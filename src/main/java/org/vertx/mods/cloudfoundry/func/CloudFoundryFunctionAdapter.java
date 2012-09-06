package org.vertx.mods.cloudfoundry.func;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.vertx.java.core.json.JsonObject;
import org.vertx.mods.cloudfoundry.CloudFoundryFunction;

public class CloudFoundryFunctionAdapter implements CloudFoundryFunction {

  private String messageType;

  protected CloudFoundryFunctionAdapter(String messageType) {
    this.messageType = messageType;
  }

  @Override
  public String messageType() {
    return messageType;
  }

  @Override
  public JsonObject reply(CloudFoundryClient client, JsonObject message) {
    return message;
  }

}
