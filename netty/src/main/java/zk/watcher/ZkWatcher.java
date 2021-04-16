package zk.watcher;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

public class ZkWatcher implements Watcher {
    private CountDownLatch countDownLatch;

    public ZkWatcher(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }


    public void process(WatchedEvent watchedEvent) {
        System.out.println("接收到事件"+watchedEvent.toString());

        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            countDownLatch.countDown();
        }
    }
}
