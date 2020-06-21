package com.xuxiang.vertx.example.sharedata;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
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
public class FooVerticle extends AbstractVerticle {
  private EventBus eventBus;
  // Invalid type: class com.xuxiang.vertx.example.sharedata.User to put in async map
//  private AsyncMap<String, User> mymap;
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
//        User user = new User("xx", 28);

        mymap.get("xx", as -> {
          System.out.println("get xx from map. " + as.result());
        });

        mymap.put("xx", "yyyyy", as -> {
          System.out.println("put xx to map.");
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
//        DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(2);
        vertx.deployVerticle("com.xuxiang.vertx.example.sharedata.FooVerticle");
        System.out.println("FooVerticle verticle deployed");
      }
    });
  }

  /**
   * create configuration object to be used to configure the Zookeeper cluster manager
   *
   * @return JsonObject representing Zookeeper configuration
   */
  private static JsonObject configureClusterManager() {
        /*
            Set zookeeperHosts property to the IPs of machines running the cluster manager, here we set it to localhost
            (127.0.0.1), but in case we have multiple machines/docker containers we have to set it on every node to the IPs
            of the machines running the cluster manager. For example:
            zkConfig.put("zookeeperHosts", "192.168.1.12"); // Zookeeper is running on this machines
            zkConfig.put("zookeeperHosts", "192.168.1.12,192.168.1.56");
         */
    JsonObject zkConfig = new JsonObject();
    zkConfig.put("zookeeperHosts", "10.15.3.5:2181,10.15.3.6:2181,10.15.3.7:2181");
//    zkConfig.put("rootPath", "io.vertx");
//    zkConfig.put("retry", new JsonObject()
//      .put("initialSleepTime", 3000)
//      .put("maxTimes", 3));
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
        /*
            the default value of the cluster host (localhost) is used here, if we want to have multiple machines/docker
            containers in our cluster we must configure the cluster host properly on each node in order for the event bus
            to send/consume messages properly between our verticles, to do so we use the method setClusterHost and give it
            this node's IP. For example:
            options.setClusterHost(192.168.1.12);
        */
    VertxOptions options = new VertxOptions()
      .setClusterManager(clusterManager)
      .setEventBusOptions(new EventBusOptions()
        .setClustered(true)
//        .setHost("10.15.3.2") // 集群通信使用的 ip
        .setPort(22401)); // 集群通信端口
    return options;
  }
}
