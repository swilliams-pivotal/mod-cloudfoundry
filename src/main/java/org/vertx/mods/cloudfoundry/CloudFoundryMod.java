/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vertx.mods.cloudfoundry;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudFoundryException;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

public class CloudFoundryMod extends BusModBase implements Handler<Message<JsonObject>> {

  public static final String ADDRESS = "vertx.cloudfoundry.api";

  private Map<String, String> config = new HashMap<>();

  private Map<String, CloudFoundryFunction> functions = new HashMap<>();

  private CloudFoundryClient client;

  private String handlerId;

  @Override
  public void start() {

    super.start();

    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

    try {
      loadFunctions();

      configureAndLogin();

      long handlerRegistrationTimeout = super.getOptionalLongConfig("handlerRegistrationTimeout", 5000L);

      CountDownLatch latch = new CountDownLatch(1);
      SimpleAsyncResultHandler result = new SimpleAsyncResultHandler(latch);
      this.handlerId = eb.registerHandler(ADDRESS, this, result);

      try {
        latch.await(handlerRegistrationTimeout, TimeUnit.MILLISECONDS);
        if (!result.succeeded()) {
          throw new RuntimeException("Handler registration for " + ADDRESS + " did not succeed before " + handlerRegistrationTimeout + "ms elapsed");
        }
      } catch (InterruptedException e) {
        // ignored
      }

      eb.publish("vertx.deployments", new JsonObject().putString("deployment", "vertx.cloudfoundry-v1.0"));

    } catch (MalformedURLException e) {
      throw new RuntimeException(e);

    } catch (CloudFoundryException e) {
      throw new RuntimeException(e);
    }
  }

  private void loadFunctions() {
    ClassLoader loader = getClass().getClassLoader();
    ServiceLoader<CloudFoundryFunction> services = 
        ServiceLoader.load(CloudFoundryFunction.class, loader);
    for (CloudFoundryFunction function : services) {
      functions.put(function.messageType(), function);
    }
  }

  private void configureAndLogin() throws MalformedURLException {
    String email = getOptionalStringConfig("cf-email", System.getProperty("cloudfoundry.email"));
    config.put("email", email);

    String password = getOptionalStringConfig("cf-password", System.getProperty("cloudfoundry.password"));
    config.put("password", password);

    String apiUrl = getOptionalStringConfig("cf-url", "https://api.cloudfoundry.com");
    config.put("apiUrl", apiUrl);

    this.client = new CloudFoundryClient(email, password, apiUrl);

    String token = client.login();
    config.put("token", token);
  }

  @Override
  public void handle(final Message<JsonObject> event) {

    final String type = event.body.getString("type");

    // TODO offload this blocking operation to another thread?
    // Does this make sense, or should this mod be a worker?

    if (functions.containsKey(type)) {
      if (vertx.isWorker()) {
        System.out.println("worker thread");
      }

      if (vertx.isEventLoop()) {
        System.out.println("event loop thread");
        vertx.runOnLoop(new Handler<Void>() {
          @Override
          public void handle(Void ignored) {
            handleTypedMessage(type, event);
          }
        });
      }
      else {
        handleTypedMessage(type, event);
      }

    }
    else {
      // FIXME remove this, put some sensible logging in
      System.out.println("json: " + event.body.encode());
      event.reply(new JsonObject().putString("error", "unknown function: " + type));
    }
  }

  private void handleTypedMessage(String type, Message<JsonObject> event) {
    CloudFoundryFunction function = functions.get(type);
    JsonObject reply = function.reply(client, event.body);
    event.reply(reply);
  }

  @Override
  public void stop() throws Exception {

    if (handlerId != null) {
      eb.unregisterHandler(handlerId);
    }

    if (client != null) {
      client.logout();
    }

    super.stop();
  }

}
