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
public class ClientTrial {

    ChannelFuture channelFuture;

    public static void main(String[] args) {

        ClientTrial nettyTrial = new ClientTrial();

        nettyTrial.client();


    }

    private void client() {

    }

}
