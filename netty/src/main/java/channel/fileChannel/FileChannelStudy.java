package channel.fileChannel;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author cgg
 * @version 1.0
 * @date 2021/4/15
 */
public class FileChannelStudy {
    public static void main(String[] args) {
        nioCopyFile("F:\\StudyFiles\\netty-study\\1.txt", "F:\\StudyFiles\\netty-study\\2.txt");
    }

    public static void nioCopyFile(String srcPath, String objPath) {
        Long startTime = System.currentTimeMillis();
        File src = new File(srcPath);
        File obj = new File(objPath);
        boolean isObjCreated = true;
        if (!obj.exists()) {
            try {
                isObjCreated = obj.createNewFile();
            } catch (IOException e) {
                isObjCreated = false;
                e.printStackTrace();
            }
        }
        if (isObjCreated) {
            try {
                //创建文件输入流
                FileInputStream fileInputStream = new FileInputStream(src);
                //获取文件输入流Channel通道
                FileChannel fileInputChannel = fileInputStream.getChannel();
                //创建NIO Buffer
                ByteBuffer byteBuffer = ByteBuffer.allocate(512);
                //创建文件输出流
                FileOutputStream fileOutputStream = new FileOutputStream(obj);
                //获取文件输出流的通道
                FileChannel fileOutputChannel = fileOutputStream.getChannel();
                int length = -1;
                //从通道读取数据到Buffer
                while ((length = fileInputChannel.read(byteBuffer)) != -1) {
                    //转换Buffer为可读取状态
                    byteBuffer.flip();
                    int outLength = 0;
                    //从Buffer中将数据写入通道
                    while ((outLength = fileOutputChannel.write(byteBuffer)) != 0) {
                        System.out.println("从Buffer往通道中写入的数据大小：" + outLength);
                    }
                    //清除Buffer，变成可写入状态
                    byteBuffer.clear();
                }
                //强制将通道中的数据刷到磁盘
                fileOutputChannel.force(true);

                fileOutputChannel.close();
                fileOutputStream.close();
                fileInputChannel.close();
                fileInputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Long endTime = System.currentTimeMillis();
        System.out.println("复制时间：" + (endTime - startTime));

    }

}
