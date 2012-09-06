package org.vertx.mods.cloudfoundry.func;

import java.util.List;

import org.cloudfoundry.client.lib.CloudApplication;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

public class ListApplications extends CloudFoundryFunctionAdapter {

  public ListApplications() {
    super("applications");
  }

  @Override
  public JsonObject reply(CloudFoundryClient client, JsonObject message) {
    List<CloudApplication> applications = client.getApplications();

    JsonObject reply = new JsonObject();
    JsonArray array = new JsonArray();
    for (CloudApplication application : applications) {
      JsonObject single = new JsonObject();
      single.putString("name", application.getName());
      single.putNumber("instances", application.getInstances());
      single.putNumber("memory", application.getMemory());
      single.putString("state", application.getState().toString());
      array.addObject(single);
    }

    reply.putArray(messageType(), array);
    return reply;
  }

}
