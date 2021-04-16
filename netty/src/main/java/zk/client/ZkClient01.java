package zk.client;

import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.zookeeper.ZooKeeper;
import zk.watcher.ZkWatcher;

import java.util.concurrent.CountDownLatch;

public class ZkClient01 {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] rgs) {
        //创建一个zk客户端连接
        try {
            ZooKeeper zookeeper = new ZooKeeper("127.0.0.1:2181", 5000, new ZkWatcher(countDownLatch));

            System.out.println(zookeeper.getState());

            countDownLatch.await();

            System.out.println("客户端已连接。。。");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
