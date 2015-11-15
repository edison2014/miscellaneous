package zookeeper;

import java.io.IOException;
import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class AsynchronousMaster implements Watcher {

  Logger logger = Logger.getRootLogger();
  {
    logger.setLevel(Level.ERROR);
  }

  ZooKeeper zk;
  String hostPort;
  String serverId = Integer.toHexString(new Random().nextInt());
  boolean isLeader = false;

  AsynchronousMaster(String hostPort) {
    this.hostPort = hostPort;
  }

  void startZK() throws IOException {
    zk = new ZooKeeper(hostPort, 15000, this);
  }

  void stopZK() throws InterruptedException {
    zk.close();
  }

  public static void main(String[] args) throws Exception {
    SynchronousMaster m = new SynchronousMaster("127.0.0.1:2181");
    try {
      m.startZK();
      m.runForMaster();
      if (m.isLeader) {
        System.out.println("I'm the leader");
        Thread.sleep(60000);
      } else {
        System.out.println(Thread.currentThread().getName() + " : Someone else is the leader");
      }
    } catch (Exception e) {
      m.stopZK();
      throw e;
    }
  }

  @Override
  public void process(WatchedEvent e) {
    System.out.println(zk.getSessionId() + " " + e);
  }

  StringCallback masterCreateCallback = new StringCallback() {
    public void processResult(int rc, String path, Object ctx, String name) {
      switch (Code.get(rc)) {
        case CONNECTIONLOSS:
          checkMaster();    // mutual recursive call; isLeader default is false;
          return;
        case OK:
          isLeader = true;
          break;
        default:
          isLeader = false;
      }
      System.out.println("I'm" + (isLeader ? "" : "not ") + "the leader");
    }
  };
  void runForMaster() {
    zk.create("/master", serverId.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
        masterCreateCallback, null);
  }


  DataCallback masterCheckCallback = new DataCallback() {
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
      switch (Code.get(rc)) {
        case CONNECTIONLOSS:
          checkMaster();
          return;
        case NONODE:
          runForMaster();
          return;
      }
    }
  };

  void checkMaster() {
    zk.getData("/master", false, masterCheckCallback, null);
  }
}
