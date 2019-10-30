import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.net.SocketAddress;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by Administrator on 2019/10/28 17:24.
 */
public class ClientTrial {

    ChannelFuture channelFuture;

    public static void main(String[] args) {

        ClientTrial nettyTrial = new ClientTrial();
        nettyTrial.client();
    }

    private void client() {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
//                            ch.pipeline().addLast(new ChannelInboundHandler());
//                            ch.pipeline().addLast(new OutboundHandler(), new ChannelInboundHandler());
//                            ch.pipeline().addLast(new ChannelInboundHandler(), new OutboundHandler());
//                            ch.pipeline().addLast(new ChannelInboundHandler(), new RequestEncoder());
                            ch.pipeline().addLast(new RequestEncoder(), new ResponseDecoder(), new ClientHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect("192.168.0.126", 5454).sync();

            channelFuture.channel().closeFuture()
                    .addListener(future -> System.out.println("closed!"))
                    .sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

    }

    /**
     * 编码器
     * 负责把主处理器写到服务端的对象编码为字节传输给服务端
     */
    class RequestEncoder extends MessageToByteEncoder<String> {

        @Override
        protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".encode~~");
            System.out.println("ctx is " + ctx);
            System.out.println("msg is " + msg);
            System.out.println("out is " + out);

            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeCharSequence(msg, UTF_8);
            ctx.write(byteBuf);

        }
    }

    /**
     * 解码器
     * 负责把服务端发送的字节封装为对象交给主处理器
     */
    class ResponseDecoder extends ByteToMessageDecoder {

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".decode~~");
            System.out.println("ctx is " + ctx);
            System.out.println("in is " + in);
            System.out.println("out is " + out);


            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);//读取服务端写过来的字节
            out.add(new String(bytes));//字节封装为对象，并保存到容器，后面主控制器将作为参数传递给服务端
        }
    }


    /**
     * 主处理器
     * 负责发送数据和接收数据
     */
    class ClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".channelActive~~");
            System.out.println("ctx is " + ctx);

            String request = "request";
            ctx.writeAndFlush(request)//发送数据到服务端
            .addListener(future -> System.out.println("flushed!"));
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".channelRead~~");
            System.out.println("ctx is " + ctx);
            System.out.println("msg is " + msg);//打印解码器封装的对象

        }
    }




    class OutboundHandler extends ChannelOutboundHandlerAdapter {

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
        public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".bind~~");
            System.out.println("ctx is " + ctx);
            System.out.println("localAddress is " + localAddress);
            System.out.println("promise is " + promise);



            super.bind(ctx, localAddress, promise);
        }


        @Override
        public void read(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".read~~");
            System.out.println("ctx is " + ctx);

            super.read(ctx);
        }

        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".connect~~");
            System.out.println("ctx is " + ctx);
            System.out.println("remoteAddress is " + remoteAddress);
            System.out.println("localAddress is " + localAddress);
            System.out.println("remoteAddress is " + remoteAddress);
            System.out.println("promise is " + promise);

            super.connect(ctx, remoteAddress, localAddress, promise);
        }

        @Override
        public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".disconnect~~");
            System.out.println("ctx is " + ctx);
            System.out.println("promise is " + promise);

            super.disconnect(ctx, promise);
        }

        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".close~~");
            System.out.println("ctx is " + ctx);
            System.out.println("promise is " + promise);

            super.close(ctx, promise);
        }


        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".write~~");
            System.out.println("ctx is " + ctx);
            System.out.println("msg is " + msg);
            System.out.println("promise is " + promise);

            super.write(ctx, msg, promise);
        }

        @Override
        public void flush(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".flush~~");
            System.out.println("ctx is " + ctx);

            super.flush(ctx);
        }
    }


    class ChannelInboundHandler extends ChannelInboundHandlerAdapter {
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
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".channelActive~~");
            System.out.println("ctx is " + ctx);

            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeCharSequence("rqst", UTF_8);

//            ctx.writeAndFlush(byteBuf)
            ctx.channel().writeAndFlush(byteBuf)
            .addListener(future -> { System.out.println("flushed!"); });


//            super.channelActive(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".channelInactive~~");
            System.out.println("ctx is " + ctx);

//            super.channelInactive(ctx);
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".exceptionCaught~~");

//            super.exceptionCaught(ctx, cause);
        }


        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            System.out.println("~~" + getClass().getSimpleName() + ".channelRead~~");
            System.out.println("ctx is " + ctx);
            System.out.println("msg is " + msg);

            ByteBuf byteBuf = (ByteBuf) msg;

            System.out.println(byteBuf.readableBytes());

            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            System.out.println(new String(bytes, UTF_8));

        }


        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".channelReadComplete~~");
            System.out.println("ctx is " + ctx);

//            super.channelReadComplete(ctx);
        }
    }

}
