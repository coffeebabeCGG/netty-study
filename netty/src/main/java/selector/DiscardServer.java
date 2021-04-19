package selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author cgg
 * @version 1.0
 * @date 2021/4/19
 */
public class DiscardServer {


    private static void discardServer(String host, int port) {
        try {
            //新建serverChannel
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            //绑定连接
            serverSocketChannel.bind(new InetSocketAddress(host, port));
            //设置为同步非阻塞
            serverSocketChannel.configureBlocking(false);
            //新建选择器
            Selector selector = Selector.open();
            //将ServerChannel注册到选择器中,注册事件为接收连接事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            //轮询处理事件, 注册事件一直大于0，会一直轮询，当有就绪，SelectionKey事件会返回，内部轮询会根据就绪事件类型处理
            while (selector.select() > 0) {
                //获取选择器监听的选择事件集合
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    //处理accept事件
                    if (selectionKey.isAcceptable()) {
                        //通过事件api获取对应通道
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        //设置为非阻塞
                        socketChannel.configureBlocking(false);
                        //将连接后的通道再次注册到选择器中
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    } else if (selectionKey.isReadable()) {
                        //通过事件api获取对应通道
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        //设置为非阻塞
                        socketChannel.configureBlocking(false);
                        //新建Buffer，准备处理连接后通道的数据
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        //处理获取连接后通道的数据
                        //将通道中的数据读取并写入到buffer
                        while (socketChannel.read(byteBuffer) > 0) {
                            //写入完成后， 切换buffer模式为读取
                            byteBuffer.flip();
                            //简单输出
                            System.out.println("disacrd server 接收数据：=====》" + new String(byteBuffer.array()));
                            //切换buffer为写入模式，准备进行下一轮写入（因为buffer有大小设置，一次并不一定读完通道中的数据）
                            byteBuffer.clear();
                        }
                        //关闭连接通道
                        socketChannel.close();
                    }
                    //移除事件
                    iterator.remove();
                }
            }
            //关闭服务端通道
            serverSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        discardServer("127.0.0.1", 2550);
    }
}
