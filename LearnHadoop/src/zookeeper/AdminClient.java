package zookeeper;

import java.util.Date;

import org.slf4j.LoggerFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

public class AdminClient implements Watcher {
  static {
    // Logger.getRootLogger().setLevel(Level.ERROR);
    Logger root = Logger.getRootLogger();
    root.setLevel(Level.OFF);
  }
  ZooKeeper zk;
  String hostPort;

  AdminClient(String hostPort) {
    this.hostPort = hostPort;
  }

  void start() throws Exception {
    zk = new ZooKeeper(hostPort, 15000, this);
  }

  void listState() throws KeeperException, InterruptedException {
    try {
      System.out.println("Master:");
      Stat stat = new Stat();
      byte[] masterData = zk.getData("/master", false, stat);
      Date startDate = new Date(stat.getCtime());
      System.out.println("Master: " + new String(masterData) + " since " + startDate);
    } catch (NoNodeException e) {
      System.out.println("\tNo Master\n");
    }

    try {
      System.out.println("Workers:");
      for (String w : zk.getChildren("/workers", false)) {
        byte[] data = zk.getData("/workers" + w, false, null);
        String state = new String(data);
        System.out.println("\t" + w + ": " + state);
      }
    } catch (NoNodeException e) {
      System.out.println("\tNo Workers\n");
    }

    try {
      System.out.println("Tasks:");
      for (String t : zk.getChildren("/tasks", this)) {
        System.out.println("\t" + t);
      }
    } catch (NoNodeException e) {
      System.out.println("No Tasks\n");
    }
  }

  @Override
  public void process(WatchedEvent e) {
    System.out.println(e);
  }

  public static void main(String[] args) throws Exception {
    AdminClient c = new AdminClient("localhost:2181");
    c.start();
    c.listState();
  }
}
