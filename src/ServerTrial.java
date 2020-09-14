import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import jdk.swing.interop.SwingInterOpUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.netty.util.CharsetUtil.UTF_8;

/**
 * Created by Administrator on 2019/10/28 17:24.
 */
public class ServerTrial {
    String ip = "192.168.0.126";
    int port = 5454;

    ChannelFuture channelFuture;

    public static void main(String[] args) {

        ServerTrial nettyTrial = new ServerTrial();

        nettyTrial.server();//测试基本功能
//        nettyTrial.pipeLine();//测试管线的事件传播
//        nettyTrial.task();//测试任务队列
//        nettyTrial.attribute();//测试管线的事件传播

//        nettyTrial.codecs();//测试基本功能


    }

    private void codecs() {

        EventLoopGroup group = new NioEventLoopGroup();

        ChannelInitializer<SocketChannel> init = new ChannelInitializer<>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                System.out.println("~~initChannel~~");
                ByteBuf delimiter = Unpooled.wrappedBuffer("o".getBytes());
                ch.pipeline().addLast(
                        new DelimiterBasedFrameDecoder(1024, delimiter),
                        new InboundHandler());
            }
        };

        try {
            new ServerBootstrap()
                    .group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(ip, port))
                    .childHandler(init)
                    .bind()
                    .channel()
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

    private void attribute() {

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.localAddress(new InetSocketAddress(ip, port));

            ChannelInitializer<SocketChannel> init = new ChannelInitializer<>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    System.out.println("~~" + getClass().getSimpleName() + ".initChannel~~");
                    System.out.println("ch is " + ch);


                    ch.pipeline().addLast(new Attrbute());
                }
            };


            serverBootstrap.childHandler(init)//增加处理器
                    .option(ChannelOption.SO_BACKLOG, 128)//配置通道
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childAttr(AttributeKey.valueOf("One"), 111);


            serverBootstrap.bind()
                    .channel()
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

    private void task() {


        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.localAddress(new InetSocketAddress(ip, port));

            ChannelInitializer<SocketChannel> init = new ChannelInitializer<>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    System.out.println("~~" + getClass().getSimpleName() + ".initChannel~~");
                    System.out.println("ch is " + ch);

                    ch.eventLoop().submit(() -> System.out.println("Task One"));

                    ch.eventLoop().schedule(() -> System.out.println("Task Two"), 2L, TimeUnit.SECONDS)
                    .addListener(future -> {
                        System.out.println(future.isSuccess());
                    });


                }
            };

            serverBootstrap.childHandler(init)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);


            serverBootstrap.bind()
                    .channel()
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

    private void pipeLine() {

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.localAddress(new InetSocketAddress(ip, port));

            ChannelInitializer<SocketChannel> init = new ChannelInitializer<>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    System.out.println("~~" + getClass().getSimpleName() + ".initChannel~~");
                    System.out.println("ch is " + ch);

                    //增加5个读处理器
                    ch.pipeline().addLast(new In("R1"));
                    ch.pipeline().addLast(new In("R2"));
                    ch.pipeline().addLast(new In("R3"));
                    ch.pipeline().addLast(new In("R4"));
                    ch.pipeline().addLast(new In("R5"));

                    //增加2个写处理器
                    ch.pipeline().addLast(new Out("W1"),
                            new Out("W2"),
                            new Out("W3"));
                }
            };


            serverBootstrap.childHandler(init)//增加处理器
                    .option(ChannelOption.SO_BACKLOG, 128)//配置通道
                    .childOption(ChannelOption.SO_KEEPALIVE, true);


            serverBootstrap.bind()
                    .channel()
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


    private void server() {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.localAddress(new InetSocketAddress(ip, port));
//            serverBootstrap.handler(new EmptyHandlerIn("xxx"));//仅在接受请求时调用一次

            ChannelInitializer<SocketChannel> init = new ChannelInitializer<>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    System.out.println("~~" + getClass().getSimpleName() + ".initChannel~~");
                    System.out.println("ch is " + ch);


                    ch.pipeline().addLast(new EmptyHandlerIn("R1"))
                            .addLast(new EmptyHandlerIn("R2"))
                            .addLast(new EmptyHandlerIn("R3"));
//                    ch.pipeline().addLast(new EmptyHandlerOut("W1"))
//                            .addLast(new EmptyHandlerOut("W2"))
//                            .addLast(new EmptyHandlerOut("W3"))
//                            .addLast(new EmptyHandlerOut("W4"));


//                    ch.pipeline().addLast(new InboundHandler());//使用单个读处理器
//                    ch.pipeline().addLast(new StringDecoder());//使用框架自带的解码器

                }
            };

            serverBootstrap.childHandler(init)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.AUTO_READ, true);

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



            //方式一：直接读取字节
//            while (byteBuf.isReadable()) System.out.println(byteBuf.readByte());

            //方式二：读取字符串
//            byteBuf.readerIndex(0);
//            System.out.println(byteBuf.readableBytes());
//            byte[] bytes = new byte[byteBuf.readableBytes()];
//            byteBuf.readBytes(bytes);
//            System.out.println(new String(bytes));




            //方式一：使用自定义处理器发送数据
            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeBytes("ok".getBytes());
            ctx.writeAndFlush(byteBuf)
                    .addListener(future -> ctx.close());//增加监听器，flush()操作后关闭通道
//                    .addListener(ChannelFutureListener.CLOSE);//增加系统自带监听器

          channelFuture.channel().close();//关闭服务端（不再服务，任何通道都不会再建立，结束main()方法）


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

    class Attrbute extends ChannelInboundHandlerAdapter {
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~handlerAdded~~");
            if (ctx.channel().hasAttr(AttributeKey.valueOf("One"))) {
                Attribute<Integer> i = ctx.channel().attr(AttributeKey.valueOf("One"));
                System.out.println(i);
            }
        }


        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("~~channelRead~~");
            if (ctx.channel().hasAttr(AttributeKey.valueOf("One"))) {
                Attribute<Integer> i = ctx.channel().attr(AttributeKey.valueOf("One"));
                System.out.println(i);
            }
        }
    }


    class In extends ChannelInboundHandlerAdapter {
        String name;

        public In(String name) {
            this.name = name;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println(this.name);
            if (name == "R3") {
                System.out.println("-------");
                ByteBuf byteBuf = Unpooled.wrappedBuffer("XXX".getBytes());
                ctx.pipeline().writeAndFlush(byteBuf);
            } else {
                ctx.fireChannelRead(msg);
            }
        }
    }

    class Out extends ChannelOutboundHandlerAdapter {
        String name;

        public Out(String name) {
            this.name = name;
        }


        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println(this.name);
            ctx.write(msg);
        }
    }

    class EmptyHandlerOut extends ChannelOutboundHandlerAdapter {
        String name;

        public EmptyHandlerOut(String name) {
            this.name = name;
        }

        @Override
        public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".bind~~");
            super.bind(ctx, localAddress, promise);
        }

        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".connect~~");
            super.connect(ctx, remoteAddress, localAddress, promise);
        }

        @Override
        public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".disconnect~~");
            super.disconnect(ctx, promise);
        }

        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".close~~");
            super.close(ctx, promise);
        }

        @Override
        public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".deregister~~");
            super.deregister(ctx, promise);
        }

        @Override
        public void read(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".read~~");
            super.read(ctx);
        }

        @Override
        public void flush(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".flush~~");
            super.flush(ctx);
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".handlerAdded~~");
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".handlerRemoved~~");
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".write~~");
            System.out.println("ctx is " + ctx);
            System.out.println("msg is " + msg);

//            if (name.equals("W3")) {
//                ByteBuf byteBuf = (ByteBuf) msg;
//                ctx.flush();
////                System.out.println("W3");
//            } else {
//                super.write(ctx, msg, promise);
//            }


            ByteBuf byteBuf = (ByteBuf) msg;
            ((ByteBuf) msg).writeBytes(UTF_8.encode(name));
            super.write(ctx, msg, promise);
        }
    }


    class EmptyHandlerIn extends ChannelInboundHandlerAdapter {

        String name;

        public EmptyHandlerIn(String name) {
            this.name = name;
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".handlerAdded~~");
//            System.out.println("ctx is " + ctx);

            super.handlerAdded(ctx);
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".handlerRemoved~~");
//            System.out.println("ctx is " + ctx);


            super.handlerRemoved(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".channelRead~~");
            System.out.println("ctx is " + ctx);
            System.out.println("msg is " + msg);


            //读数据
//            ByteBuf byteBuf = (ByteBuf) msg;
//            System.out.println(byteBuf.readByte());//逐字节读取（每个处理器仅处理一个字符）
//            super.channelRead(ctx, msg);


            //写数据
            if (name.equals("R3")) {

                //方式一：获取客户端通道，并发送数据
//                ByteBuf byteBuf = Unpooled.directBuffer();
//                byteBuf.writeBytes(UTF_8.encode("OK"));
//                ctx.channel()
//                        .writeAndFlush(byteBuf)
//                        .addListener(new ChannelFutureListener() {
//                            @Override
//                            public void operationComplete(ChannelFuture future) throws Exception {
//                                System.out.println("flush!");
//                            }
//                        });

                //方式二：把待发送的数据交给管线处理
                ctx.pipeline()//和ctx.channel()是等价的，通道写操作就是调用管线写操作
                        .writeAndFlush(Unpooled.directBuffer().writeBytes(UTF_8.encode("OK")))
                        .addListener(ChannelFutureListener.CLOSE);


            } else {
                super.channelRead(ctx, msg);
            }

        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".channelReadComplete~~");
//            System.out.println("ctx is " + ctx);

            super.channelReadComplete(ctx);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".channelActive~~");
            super.channelActive(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".channelInactive~~");
            super.channelInactive(ctx);
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".exceptionCaught~~");
            super.exceptionCaught(ctx, cause);
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".channelRegistered~~");
            super.channelRegistered(ctx);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".channelUnregistered~~");
            super.channelUnregistered(ctx);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".userEventTriggered~~");
            super.userEventTriggered(ctx, evt);
        }

        @Override
        public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + name + "|" + getClass().getSimpleName() + ".channelWritabilityChanged~~");
            super.channelWritabilityChanged(ctx);
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
