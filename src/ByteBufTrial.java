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

//        loop();
//        rw();

        refer();

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

    private static void rw() {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt(1);
        byteBuf.writeInt(25);

        System.out.println(byteBuf.readInt());
        System.out.println(byteBuf.readInt());
        byteBuf.readerIndex(0);
        System.out.println(byteBuf.readInt());
        System.out.println(byteBuf.readInt());

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
