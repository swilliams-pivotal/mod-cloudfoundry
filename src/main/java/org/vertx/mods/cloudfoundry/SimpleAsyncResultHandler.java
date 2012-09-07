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
package org.vertx.mods.cloudfoundry;

import java.util.concurrent.CountDownLatch;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;

public class SimpleAsyncResultHandler implements AsyncResultHandler<Void> {

  private final CountDownLatch latch;

  private volatile boolean succeeded = false;

  public SimpleAsyncResultHandler(CountDownLatch latch) {
    this.latch = latch;
  }

  @Override
  public void handle(AsyncResult<Void> event) {
    this.succeeded = event.succeeded();
    latch.countDown();
  }

  public boolean succeeded() {
    return succeeded;
  }

}
