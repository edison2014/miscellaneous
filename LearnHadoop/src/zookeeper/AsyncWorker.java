package zookeeper;

import java.io.IOException;
import java.util.*;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.*;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Stat;
import org.slf4j.*;

public class AsyncWorker implements Watcher {

	private static final Logger LOG = LoggerFactory
			.getLogger(AsyncWorker.class);

	ZooKeeper zk;
	String hostPort;
	String serverId = Integer.toHexString(new Random().nextInt());

	AsyncWorker(String hostPort) {
		this.hostPort = hostPort;
	}

	void startZK() throws IOException {
		zk = new ZooKeeper(hostPort, 15000, this);
	}

	@Override
	public void process(WatchedEvent arg0) {
		LOG.info(arg0 + ", " + hostPort);
	}

	void register(){
		zk.create("/workers/worker-" + serverId, "Idle".getBytes(),
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
				createWorkerCallback, null);
	}

	StringCallback createWorkerCallback = new StringCallback() {
		public void processResult(int rc, String path, Object ctx, String name) {
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				register();
				break;
			case OK:
				LOG.info("Registered successfully: " + serverId);
				break;
			case NODEEXISTS:
				LOG.warn("Already registered: " + serverId);
				break;
			default:
				LOG.error("Something went wrong: "
						+ KeeperException.create(Code.get(rc), path));
			}
		}
	};

	StatCallback statusUpdateCallback = new StatCallback() {
		public void processResult(int rc, String path, Object ctx, Stat stat){
			switch(Code.get(rc)) {
			case CONNECTIONLOSS:
				updateStatus((String)ctx);
				return;
			}
		}
	};

	private String status;
	
	synchronized private void updateStatus(String status) {
		if(status == this.status){
			zk.setData("/workers/worker-" + serverId, status.getBytes(), -1, statusUpdateCallback, status);
		}
	}
	
	public void setStatus(String status){
		this.status = status;
		updateStatus(status);
	}
	
	public static void main(String[] args) throws IOException, KeeperException,
			InterruptedException {
		AsyncWorker w = new AsyncWorker("127.0.0.1");
		w.startZK();
		w.register();
		Thread.sleep(30000);
	}
}
