package com.xuxiang.vertx.example.sharedata;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;

/**
 * 用于接收消息
 *
 * @author xuxiang
 */
public class BarVerticle extends AbstractVerticle {
  private EventBus eventBus;
  private AsyncMap<String, String> mymap;

  @Override
  public void start() throws Exception {
    // 初始化 event bus
    eventBus = vertx.eventBus();

    SharedData sharedData = vertx.sharedData();
    sharedData.<String, String>getClusterWideMap("mymap", res -> {
      if (res.succeeded()) {
        System.out.println("get cluster socket successful.");
        mymap = res.result();
        vertx.setPeriodic(2000, id -> {
          mymap.get("xx", as -> {
            System.out.println("get xx from map. " + as.result());
          });
        });

      }
    });


  }


  public static void main(String[] args) {
    JsonObject zkConfig = configureClusterManager();
    ClusterManager zookeeperClusterManager = new ZookeeperClusterManager(zkConfig);

    VertxOptions options = configureVertx(zookeeperClusterManager);
    Vertx.clusteredVertx(options, res -> {
      if (res.succeeded()) {
        Vertx vertx = res.result();
        vertx.deployVerticle("com.xuxiang.vertx.example.sharedata.BarVerticle");
        System.out.println("BarVerticle verticle deployed");
      }
    });
  }

  /**
   * create configuration object to be used to configure the Zookeeper cluster manager
   *
   * @return JsonObject representing Zookeeper configuration
   */
  private static JsonObject configureClusterManager() {
    JsonObject zkConfig = new JsonObject();
    zkConfig.put("zookeeperHosts", "10.15.3.5:2181,10.15.3.6:2181,10.15.3.7:2181");
    return zkConfig;
  }

  /**
   * Specifies Vert.x instance configuration, this is essential for clustering on multiple separate machines and
   * Docker containers in order to make Event Bus send/consume messages appropriately
   *
   * @param clusterManager represents the cluster manager
   * @return VertxOptions object to be used in deployment
   */
  private static VertxOptions configureVertx(ClusterManager clusterManager) {
    VertxOptions options = new VertxOptions()
      .setClusterManager(clusterManager)
      .setEventBusOptions(new EventBusOptions()
        .setClustered(true)
//        .setHost("10.15.3.2") // 集群通信使用的 ip
        .setPort(22402)); // 集群通信端口
    return options;
  }
}
