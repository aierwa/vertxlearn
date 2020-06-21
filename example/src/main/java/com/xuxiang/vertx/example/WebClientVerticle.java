package com.xuxiang.vertx.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.codec.BodyCodec;

/**
 * @author xuxiang
 */
public class WebClientVerticle extends AbstractVerticle {
  private HttpRequest<JsonObject> request;

  @Override
  public void start() throws Exception {

    request = WebClient.create(vertx)
      .get(443, "icanhazdadjoke.com", "/")
      .ssl(true)
      .putHeader("Accept", "application/json")
      .as(BodyCodec.jsonObject())
      .expect(ResponsePredicate.SC_OK);

    // 每隔 3 s
    vertx.setPeriodic(3000, id -> fetchJoke());
  }

  private void fetchJoke(){
    // 异步
//    request.send(asyncResult -> {
//      if (asyncResult.succeeded()) {
//        System.out.println(asyncResult.result().body().getString("joke"));
//        System.out.println("🤣");
//        System.out.println();
//      }
//    });

    // 测试阻塞，会给出 event loop 警告，以及 Thread blocked 异常
    System.out.println("start");
    vertx.executeBlocking(promise -> {
      for (int i = 0; i < 10000000; i++) {
        if (i == 50000) {
          System.out.println(50000);
        }
//        MD5Util.toMD5String("" + i);
      }
      promise.complete("done!");
    }, as -> {
      System.out.println(as.result());
    });
    System.out.println("some other code");

  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new WebClientVerticle());
  }
}
