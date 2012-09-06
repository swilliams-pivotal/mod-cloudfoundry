package org.vertx.mods.cloudfoundry.msg;

import org.vertx.java.core.json.JsonObject;

public abstract class AbstractMessage {

  private String type;

  private JsonObject json;

  public AbstractMessage(String type) {
    this.type = type;
    this.json = new JsonObject().putString("type", type);
  }

  public String getType() {
    return type;
  }

  public JsonObject message() {
    return json;
  }

}
