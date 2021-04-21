package reactor.singlethread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author cgg
 * @version 1.0
 * @date 2021/4/20
 */
public class EchoHandler implements Runnable {

    private final SocketChannel socketChannel;


    private final SelectionKey selectionKey;


    private final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

    static final int RECEIVE = 0, SENDING = 1;

    int state = RECEIVE;

    public EchoHandler(SocketChannel socketChannel, Selector selector) throws IOException {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);
        //先获取事件，再注册事件类型
        this.selectionKey = this.socketChannel.register(selector, 0);
        //attach表示将本实例handler以附件形式添加到事件中
        this.selectionKey.attach(this);
        this.selectionKey.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }


    @Override
    public void run() {
        try {
            if (this.state == SENDING) {
                //将bytebuffer读取的数据写入到
                socketChannel.write(byteBuffer);
                System.out.println("Handler 读取bytebuffer 数据并写入通道 ：" + new String(byteBuffer.array()));
                //切换为写入模式
                byteBuffer.clear();
                this.selectionKey.interestOps(SelectionKey.OP_READ);
                this.state = RECEIVE;
            } else if (this.state == RECEIVE) {
                int length = 0;
                while (socketChannel.read(byteBuffer) > 0) {
                    System.out.println("Handler 读取 通道数据并写入buffer ：" + new String(byteBuffer.array()));
                }
                //切换为读取模式
                byteBuffer.flip();
                //读完后，注册write就绪事件
                this.selectionKey.interestOps(SelectionKey.OP_WRITE);
                //读完后，进入发送状态
                this.state = SENDING;
            }
            //处理完毕,不能关闭需要重复使用
//            this.selectionKey.cancel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
