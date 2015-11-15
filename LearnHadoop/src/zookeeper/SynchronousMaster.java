package zookeeper;

import java.io.IOException;
import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class SynchronousMaster implements Watcher {

  Logger logger = Logger.getRootLogger();
  {
    logger.setLevel(Level.ERROR);
  }

  ZooKeeper zk;
  String hostPort;
  String serverId = Integer.toHexString(new Random().nextInt());
  boolean isLeader = false;

  SynchronousMaster(String hostPort) {
    this.hostPort = hostPort;
  }

  void startZK() throws IOException {
    zk = new ZooKeeper(hostPort, 15000, this);
  }

  void stopZK() throws InterruptedException {
    zk.close();
  }

  public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
    SynchronousMaster m = new SynchronousMaster("127.0.0.1:2181");
    m.startZK();
    m.runForMaster();
    if(m.isLeader){
      System.out.println("I'm the leader");
      Thread.sleep(60000);
    }else{
      System.out.println(Thread.currentThread().getName() + " : Someone else is the leader");
    }
    m.stopZK();
  }

  @Override
  public void process(WatchedEvent e) {
    System.out.println(zk.getSessionId() + " " + e);
  }

  void runForMaster() throws KeeperException, InterruptedException {
    while (true) {
      try {
        zk.create("/master", serverId.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        isLeader = true;
        break;
      } catch (ConnectionLossException e) {
        if(checkMaster()) break;
      } catch (NodeExistsException e) {
        isLeader = false;
        break;
      }
    }
  }

  boolean checkMaster() throws KeeperException, InterruptedException{
    while(true){
      try{
        byte[] data = zk.getData("/master", false, null);
        isLeader = new String(data).equals(serverId);
        return true;
      }catch(NoNodeException e){
        return false;
      }catch(ConnectionLossException e){
        
      }
    }
  }
}
