package org.vertx.mods.cloudfoundry.func;

import java.util.List;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudService;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

public class ListServices extends CloudFoundryFunctionAdapter {

  public ListServices() {
    super("services");
  }

  @Override
  public JsonObject reply(CloudFoundryClient client, JsonObject message) {
    List<CloudService> services = client.getServices();

    JsonObject reply = new JsonObject();
    JsonArray array = new JsonArray();
    for (CloudService service : services) {
      JsonObject single = new JsonObject();
      single.putString("name", service.getName());
      single.putString("tier", service.getTier());
      single.putString("type", service.getType());
      single.putString("vendor", service.getVendor());
      single.putString("version", service.getVersion());
      array.addObject(single);
    }

    reply.putArray(messageType(), array);
    return reply;
  }

}
