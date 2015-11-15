package zookeeper;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher;

public class SimpleMaster implements Watcher {
  
  Logger logger = Logger.getRootLogger();
  {
    logger.setLevel(Level.ERROR);
  }

  ZooKeeper zk;
  String hostPort;
  
  SimpleMaster(String hostPort){
    this.hostPort = hostPort;
  }
  
  void startZK() throws IOException{
    zk = new ZooKeeper(hostPort, 15000, this);
  }
  
  void stopZK() throws InterruptedException{
    zk.close();
  }
  
  public static void main(String[] args) throws IOException, InterruptedException {
    SimpleMaster m = new SimpleMaster("127.0.0.1:2181");
    m.startZK();
    Thread.sleep(10000000);
    m.stopZK();
  }

  @Override
  public void process(WatchedEvent e) {
    System.out.println(zk.getSessionId() + " " + e); 
  }

}
