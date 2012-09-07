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
package org.vertx.mods.cloudfoundry.func;

import java.util.List;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.ServiceConfiguration;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

/**
 * @author swilliams
 *
 */
public class ListServiceConfigurations extends CloudFoundryFunctionAdapter {

  public ListServiceConfigurations() {
    super("service-configurations");
  }

  @Override
  public JsonObject reply(CloudFoundryClient client, JsonObject message) {
    List<ServiceConfiguration> serviceConfigurations = client.getServiceConfigurations();

    JsonObject reply = new JsonObject();
    JsonArray array = new JsonArray();
    for (ServiceConfiguration serviceConfiguration : serviceConfigurations) {
      JsonObject single = new JsonObject();
      single.putString("description", serviceConfiguration.getDescription());
      single.putString("type", serviceConfiguration.getType());
      single.putString("vendor", serviceConfiguration.getVendor());
      single.putString("version", serviceConfiguration.getVersion());
      array.addObject(single);
    }
    reply.putArray(messageType(), array);
    return reply;
  }

}
