import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Random;

/**
 * Created by Administrator on 2019/10/28 19:38.
 */
public class ByteBufTrial {
    public static void main(String[] args) {

        ByteBufTrial byteBufTrial = new ByteBufTrial();

//        create();

//        searchIndex();//查询索引
//        write();//写操作
//        read();//读操作

//        loop();
        reRead();//反复读

//        refer();

    }

    private static void read() {

        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeBytes("ab".getBytes());


        //方式一：逐字节读取
//        while (byteBuf.isReadable()) System.out.println(byteBuf.readByte());

        //方式二：以字节数组为单位读取
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes)
                .release();
        for (byte b : bytes) System.out.println(b);


    }

    private static void create() {

        ByteBuf byteBuf = Unpooled.buffer(4);

        byteBuf.writeInt(1);
        System.out.println(byteBuf.capacity());
        byteBuf.writeInt(1);
        System.out.println(byteBuf.capacity());


    }

    private static void refer() {

        ByteBuf byteBuf = Unpooled.buffer();
        System.out.println(byteBuf.refCnt());
        byteBuf.release();
        System.out.println(byteBuf.refCnt());

    }

    private static void reRead() {

        //方式一：手动设置读指针
//        ByteBuf byteBuf = Unpooled.buffer();
//        byteBuf.writeInt(25);
//        while (byteBuf.isReadable()) System.out.println(byteBuf.readByte());
//
//        byteBuf.readerIndex(0);
//        while (byteBuf.isReadable()) System.out.println(byteBuf.readByte());


        //方式二：
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt(25);

        byteBuf.markReaderIndex();
        while (byteBuf.isReadable()) System.out.println(byteBuf.readByte());

        byteBuf.resetReaderIndex();
        while (byteBuf.isReadable()) System.out.println(byteBuf.readByte());


    }

    private static void loop() {

        ByteBuf byteBuf = Unpooled.buffer();
        Random random = new Random();
        for (int i = 0; i < 25; i++) {
            byteBuf.writeInt(random.nextInt(255));

        }

        //遍历
//        byteBuf.forEachByte(b -> {
//            System.out.println(b);
//            return true;//返回真才会继续遍历
//        });


        //遍历读
        while (byteBuf.isReadable()) System.out.println(byteBuf.readInt());


    }

    private static void write() {

        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt(1);//写整型，4字节数据
        System.out.println(byteBuf);

    }

    private static void searchIndex() {
        ByteBuf byteBuf = Unpooled.buffer();
        System.out.println("writableBytes is " + byteBuf.writableBytes());

        byteBuf.writeByte(11);
        System.out.println(byteBuf);
        byteBuf.writeByte(23);
        System.out.println(byteBuf);

        System.out.println(byteBuf.bytesBefore((byte) 23));
        System.out.println(byteBuf.indexOf(byteBuf.readerIndex(), byteBuf.writerIndex(), (byte) 23));
    }
}
