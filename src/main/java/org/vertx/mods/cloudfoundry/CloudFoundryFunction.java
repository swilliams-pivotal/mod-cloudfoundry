package org.vertx.mods.cloudfoundry;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.vertx.java.core.json.JsonObject;

public interface CloudFoundryFunction {

  String messageType();

  JsonObject reply(CloudFoundryClient client, JsonObject message);

}
