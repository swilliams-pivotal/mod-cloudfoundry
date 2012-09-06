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
