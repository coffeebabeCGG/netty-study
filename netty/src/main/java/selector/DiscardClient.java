package selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * @author cgg
 * @version 1.0
 * @date 2021/4/19
 */
public class DiscardClient {

    /**
     * 1.0 是一个控制台输入创建一个客户端连接，没有保持长连接，浪费资源
     *
     * @param host 主机地址
     * @param port 通信端口
     */
    private static void discardClient(String host, int port) {
        try {

            //读取控制台写入到bytebuffer
            Scanner scanner = new Scanner(System.in);
            while (true) {
                //读取控制台输入，以回车结束
                String line = scanner.nextLine();
                //新建客户端channel
                SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
                //设置客户端连接为非阻塞
                socketChannel.configureBlocking(false);
                //进行自旋，等待连接完成
                while (!socketChannel.finishConnect()) {
                    System.out.println("waiting...");
                }
                //新建buffer
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                //将数据写入到buffer
                byteBuffer.put(line.getBytes());
                //将buffer的数据写入到通道
                byteBuffer.flip();
                socketChannel.write(byteBuffer);
                //发送通道的数据
                socketChannel.shutdownOutput();
                //关闭连接
                socketChannel.close();
                byteBuffer.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        discardClient("127.0.0.1", 2550);
    }
}
