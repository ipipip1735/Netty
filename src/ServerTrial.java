import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created by Administrator on 2019/10/28 17:24.
 */
public class ServerTrial {

    ChannelFuture channelFuture;

    public static void main(String[] args) {

        ServerTrial nettyTrial = new ServerTrial();

        nettyTrial.server();


    }


    private void server() {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.localAddress(new InetSocketAddress("192.168.0.126", 5454));

            ChannelInitializer<SocketChannel> init = new ChannelInitializer<>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    System.out.println("~~" + getClass().getSimpleName() + ".initChannel~~");
                    System.out.println("ch is " + ch);


//                    ch.pipeline().addLast(new EmptyHandler());//增加空处理器
                    ch.pipeline().addLast(new InboundHandler());//增加读处理器
//                    ch.pipeline().addLast(new EmptyHandler(), new InboundHandler());//使用2个处理器
//                    ch.pipeline().addLast(new StringDecoder());//使用框架自带的解码器

                }
            };

            serverBootstrap.childHandler(init)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);


            channelFuture = serverBootstrap.bind().sync();
            channelFuture.channel()
                    .closeFuture()
                    .sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }


    class InboundHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {
            System.out.println("~~" + getClass().getSimpleName() + ".handlerAdded~~");
            System.out.println("ctx is " + ctx);
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
            System.out.println("~~" + getClass().getSimpleName() + ".handlerRemoved~~");
            System.out.println("ctx is " + ctx);

        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            System.out.println("~~" + getClass().getSimpleName() + ".channelRead~~");
            System.out.println("ctx is " + ctx);
            System.out.println("msg is " + msg);




            ByteBuf byteBuf = (ByteBuf) msg;


            //方式一：直接读取字节
//            while (byteBuf.isReadable()) System.out.println(byteBuf.readByte());

            //方式二：读取字符串
            byteBuf.readerIndex(0);
            System.out.println(byteBuf.readableBytes());
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            System.out.println(new String(bytes));





            byteBuf = Unpooled.buffer();
            byteBuf.writeBytes("ok".getBytes());

            //方式一：使用自定义处理器
            ctx.writeAndFlush(byteBuf)
                    .addListener(future -> ctx.close());//增加监听器，flush()操作后关闭通道

            //方式二：使用框架自带处理器
//            ctx.writeAndFlush(byteBuf)
//                    .addListener(ChannelFutureListener.CLOSE);

//          channelFuture.channel().close();//关闭服务端（不再服务，任何通道都不会再建立，结束main()方法）


        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".channelActive~~");
            System.out.println("ctx is " + ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".channelInactive~~");
            System.out.println("ctx is " + ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".exceptionCaught~~");
            ctx.close();
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".channelReadComplete~~");
            System.out.println("ctx is " + ctx);
        }
    }


    class EmptyHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {
            System.out.println("~~empty|" + getClass().getSimpleName() + ".handlerAdded~~");
            System.out.println("ctx is " + ctx);

        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
            System.out.println("~~empty|" + getClass().getSimpleName() + ".handlerRemoved~~");
            System.out.println("ctx is " + ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            System.out.println("~~empty|" + getClass().getSimpleName() + ".channelRead~~");
            System.out.println("ctx is " + ctx);
            System.out.println("msg is " + msg);


            ctx.fireChannelRead(msg);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~empty|" + getClass().getSimpleName() + ".channelActive~~");
            System.out.println("ctx is " + ctx);

            ctx.fireChannelActive();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~empty|" + getClass().getSimpleName() + ".channelInactive~~");
            System.out.println("ctx is " + ctx);

            ctx.fireChannelInactive();
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println("~~empty|" + getClass().getSimpleName() + ".exceptionCaught~~");
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            System.out.println("ctx is " + ctx);

            ctx.fireChannelReadComplete();
        }
    }


    /**
     * NIO模式读取数据比较麻烦，因为数据完整性无法保证
     * 一般要定义一个缓存数据，但框架自带解码器已经做了这方便面的工作
     */
    class StringDecoder extends ByteToMessageDecoder {

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".decode~~");
            System.out.println("ctx is " + ctx);
            System.out.println("msg is " + in);
            System.out.println("out is " + out);


            //utf8是变长的，这里的逻辑不知道怎么写，下面是错误的
            if (in.readableBytes() < 3) {
                return;
            }
            out.add(in.readBytes(3));

        }
    }

}
