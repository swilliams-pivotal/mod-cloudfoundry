package org.vertx.mods.cloudfoundry.func;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudInfo;
import org.vertx.java.core.json.JsonObject;

public class ShowCloudInfo extends CloudFoundryFunctionAdapter {

  public ShowCloudInfo() {
    super("cloud-info");
  }

  @Override
  public JsonObject reply(CloudFoundryClient client, JsonObject message) {
    CloudInfo cloudInfo = client.getCloudInfo();

    JsonObject reply = new JsonObject();
    reply.putBoolean("allowDebug", cloudInfo.getAllowDebug());
    reply.putString("authorizationEndpoint", cloudInfo.getAuthorizationEndpoint());
    reply.putString("description", cloudInfo.getDescription());
    reply.putString("support", cloudInfo.getSupport());
    reply.putString("user", cloudInfo.getUser());
    reply.putString("version", cloudInfo.getVersion());
    reply.putNumber("build", cloudInfo.getBuild());

    JsonObject limits = new JsonObject();
    limits.putNumber("maxApps", cloudInfo.getLimits().getMaxApps());
    limits.putNumber("maxTotalMemory", cloudInfo.getLimits().getMaxServices());
    limits.putNumber("maxTotalMemory", cloudInfo.getLimits().getMaxTotalMemory());
    limits.putNumber("maxUrisPerApp", cloudInfo.getLimits().getMaxUrisPerApp());

    reply.putObject("limits", limits);

    return reply;
  }

}
