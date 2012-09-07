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

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudInfo;
import org.vertx.java.core.json.JsonObject;

/**
 * @author swilliams
 *
 */
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
