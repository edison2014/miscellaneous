package zookeeper;
import java.io.IOException;
import java.util.UUID;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;

public class Client implements Watcher {
  ZooKeeper zk;
  String hostPort;
  
  Client(String hostPort){
    this.hostPort = hostPort;
  }
  
  public void startZK() throws IOException{
    
    zk = new ZooKeeper(hostPort, 15000, this);
    
  }
  
  public String queueCommand(String command) throws Exception{
    String name = null;
    while(true){
      try{
        name = zk.create("/tasks/"+UUID.randomUUID().toString()+"-", command.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        return name;
      }catch(NodeExistsException e){
        throw new Exception(name + "already appears to be running");
      }catch(ConnectionLossException e){
        
      }
    }
  }
  
  public void close() throws InterruptedException{
    
    zk.close();
    
  }
  

  @Override
  public void process(WatchedEvent e){
    
    System.out.println(e);
    
  }
  
  public static void main(String[] args) throws Exception{
    Client c = new Client("127.0.0.1:2181");
    c.startZK();
    String name = c.queueCommand("some-task");
    System.out.println("Created " + name);
    // should add session ID in the node name to address duplicate task, at least once execution, connection lost and reconnect.
  }

}
