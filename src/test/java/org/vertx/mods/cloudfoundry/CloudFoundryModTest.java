package org.vertx.mods.cloudfoundry;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.test.TestVerticle;
import org.vertx.java.test.VertxTestBase;
import org.vertx.java.test.junit.VertxJUnit4ClassRunner;
import org.vertx.java.test.utils.QueueReplyHandler;


@RunWith(VertxJUnit4ClassRunner.class)
@TestVerticle(main="deployer.js")
public class CloudFoundryModTest extends VertxTestBase {

  private final long TIMEOUT = Long.getLong("vertx.test.timeout", 15L);

  private final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

  private Set<String> handlers = new HashSet<>();

  @Before
  public void setup() throws Exception {

    super.setAwaitTimeout(TIMEOUT);

    String handler = registerHandler("vertx.deployments", new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> event) {
        System.out.printf("deployed: %s %n", event.body.encode());
      }});

    handlers.add(handler);

    lightSleep(2000L); // Why?
  }

  @Test
  public void testEcho() throws Exception {

    final LinkedBlockingQueue<JsonObject> queue = new LinkedBlockingQueue<>();

    JsonObject message = new JsonObject().putString("type", "echo");
    send(queue, message);

    try {
      JsonObject answer = queue.poll(TIMEOUT, TIME_UNIT);

      System.out.println("answer:" + answer);
      System.out.printf("For %s sent:%s got: %s, reply matched? %s %n", CloudFoundryMod.ADDRESS, message, answer, message.equals(answer));

      Assert.assertTrue(message.equals(answer));

    } catch (InterruptedException e) {
      //
    }
  }

  @Test
  public void testErrorMessage() throws Exception {

    final LinkedBlockingQueue<JsonObject> queue = new LinkedBlockingQueue<>();

    JsonObject message = new JsonObject().putString("type", "foo");
    send(queue, message);

    try {
      JsonObject answer = queue.poll(TIMEOUT, TIME_UNIT);

      System.out.println("answer:" + answer);
      System.out.printf("For %s sent:%s got: %s, reply matched? %s %n", CloudFoundryMod.ADDRESS, message, answer, message.equals(answer));

      JsonObject error = new JsonObject();
      error.putString("error", "unknown function: foo");
      error.putObject("original-request", message);

      Assert.assertTrue(error.equals(answer));

    } catch (InterruptedException e) {
      //
    }
  }

  @Test
  public void testListApplications() throws Exception {

    final LinkedBlockingQueue<JsonObject> queue = new LinkedBlockingQueue<>();

    JsonObject message = new JsonObject().putString("type", "applications");
    send(queue, message);

    try {
      JsonObject answer = queue.poll(TIMEOUT, TIME_UNIT);
      System.out.printf("Reply %s %n", answer.encode());
      Assert.assertNotSame(new JsonObject(), answer);

    } catch (InterruptedException e) {
      //
    }
  }

  @Test
  public void testListServiceConfigurations() throws Exception {
    final LinkedBlockingQueue<JsonObject> queue = new LinkedBlockingQueue<>();

    JsonObject message = new JsonObject().putString("type", "service-configurations");
    send(queue, message);

    try {
      JsonObject answer = queue.poll(TIMEOUT, TIME_UNIT);
      System.out.printf("Reply %s %n", answer.encode());
      Assert.assertNotSame(new JsonObject(), answer);

    } catch (InterruptedException e) {
      //
    }
  }

  @Test
  public void testListServices() throws Exception {

    final LinkedBlockingQueue<JsonObject> queue = new LinkedBlockingQueue<>();

    JsonObject message = new JsonObject().putString("type", "services");
    send(queue, message);

    try {
      JsonObject answer = queue.poll(TIMEOUT, TIME_UNIT);
      System.out.printf("Reply %s %n", answer.encode());

    } catch (InterruptedException e) {
      //
    }
  }

  @Test
  public void testShowCloudInfo() throws Exception {

    final LinkedBlockingQueue<JsonObject> queue = new LinkedBlockingQueue<>();

    JsonObject message = new JsonObject().putString("type", "cloud-info");
    send(queue, message);

    try {
      JsonObject answer = queue.poll(TIMEOUT, TIME_UNIT);
      System.out.printf("Reply %s %n", answer.encode());
      Assert.assertNotSame(new JsonObject(), answer);

    } catch (InterruptedException e) {
      //
    }
  }

  @After
  public void teardown() throws Exception {
    unregisterHandlers(handlers);
  }

  private void send(final LinkedBlockingQueue<JsonObject> queue, JsonObject message) {
    getEventBus().send(CloudFoundryMod.ADDRESS, message, new QueueReplyHandler<JsonObject>(queue, TIMEOUT, TIME_UNIT));
  }


}
