package zookeeper;

import java.io.IOException;

import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class SettingUpMetadata implements Watcher {
  
  private static final Logger LOG = LoggerFactory.getLogger(SettingUpMetadata.class);
  static{
    
  }
  
  ZooKeeper zk = null;
  public void startZK() throws IOException{
    zk = new ZooKeeper("127.0.0.1:2181", 15000, this);
  }
  
  public void stopZK() throws InterruptedException {
    zk.close();
  }
  
  public void bootstrap(){
    createParent("/workers", new byte[0]);
    createParent("/assign", new byte[0]);
    createParent("/tasks", new byte[0]);
    createParent("/status", new byte[0]);
  }

  StringCallback createParentCallback = new StringCallback(){
    public void processResult(int rc, String path, Object ctx, String name){
      switch(Code.get(rc)){
        case CONNECTIONLOSS:
          createParent(path, (byte[])ctx);
          break;
        case OK:
          LOG.info("Parent created");
          break;
        case NODEEXISTS:
          LOG.info("Parent already registered: " + path);
          break;
        default :
          LOG.error("Something went wrong: ", KeeperException.create(Code.get(rc), path));
      }
    }
  };
  private void createParent(String path, byte[] data){
    zk.create(path, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, createParentCallback, data);
  }
  
  public static void main(String[] args) throws IOException, InterruptedException {
    
    SettingUpMetadata sum = new SettingUpMetadata();
    sum.startZK();
    sum.bootstrap();
    sum.stopZK();

  }

  @Override
  public void process(WatchedEvent e) {
    System.out.println(zk.getSessionId() + ":"+ e);
  }

}
