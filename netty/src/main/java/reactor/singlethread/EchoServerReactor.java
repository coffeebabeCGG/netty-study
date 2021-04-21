package reactor.singlethread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author cgg
 * @version 1.0
 * @date 2021/4/20
 */
public class EchoServerReactor implements Runnable {
    /**
     * 主机
     */
    private String host = "127.0.0.1";

    /**
     * 端口
     */
    private int port = 2551;

    /**
     * 选择器
     */
    private Selector selector;

    /**
     * 服务端channel
     */
    private ServerSocketChannel serverSocketChannel;

    EchoServerReactor() throws IOException {
        //初始化选择器
        selector = Selector.open();
        //初始化服务channel
        serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.bind(new InetSocketAddress(this.host, this.port));

        serverSocketChannel.configureBlocking(false);

        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        AcceptorHandler acceptorHandler = new AcceptorHandler();

        selectionKey.attach(acceptorHandler);
    }



    @Override
    public void run() {

        try {
            //获取注册的事件
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            //遍历事件集合
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                this.dispatch(selectionKey);
            }
            //清除事件集合
            selectionKeys.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatch(SelectionKey selectionKey) {
        Runnable handler = (Runnable) selectionKey.attachment();
        if (handler != null) {
            //单线程reactor直接调用了run方法，并没有start一个新的线程
            handler.run();
        }
    }

    class AcceptorHandler implements Runnable {
        @Override
        public void run() {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    //新建handler处理器
                    new EchoHandler(socketChannel, selector);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new Thread(new EchoServerReactor()).start();
    }

}
