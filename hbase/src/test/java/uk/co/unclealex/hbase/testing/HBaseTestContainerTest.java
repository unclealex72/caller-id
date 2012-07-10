package uk.co.unclealex.hbase.testing;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.hbase.master.HMaster;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Assert;
import org.junit.Test;

import uk.co.unclealex.hbase.testing.HBaseTestContainer.Port;

public class HBaseTestContainerTest {

  @Test
  public void test() throws Exception {
    HBaseTestContainer container = new HBaseTestContainer();
    container.start();
    HMaster master = container.getCluster().getActiveMaster();
    Assert.assertTrue("The hbase master node is not running.", master.isMasterRunning());
    final BlockingQueue<KeeperState> queue = new ArrayBlockingQueue<KeeperState>(100);
    Watcher watcher = new Watcher() {
      @Override
      public void process(WatchedEvent event) {
        try {
          queue.put(event.getState());
        }
        catch (InterruptedException e) {
          // Ignore
        }
      }
    };
    ZooKeeper zooKeeper =
        new ZooKeeper("localhost:" + container.getPorts().get(Port.ZOOKEEPER_CLIENT), 1000, watcher);
    KeeperState keeperState = queue.poll(1, TimeUnit.SECONDS);
    Assert.assertEquals("The wrong keeper state was returned.", KeeperState.SyncConnected, keeperState);
    zooKeeper.close();
    container.stop();
  }

}
