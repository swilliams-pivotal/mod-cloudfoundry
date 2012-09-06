package org.vertx.mods.cloudfoundry.func;

import java.util.List;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.ServiceConfiguration;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

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
