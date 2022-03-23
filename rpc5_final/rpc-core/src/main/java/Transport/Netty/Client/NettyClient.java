package Transport.Netty.Client;

import Coder.CommonDecoder;
import Coder.CommonEncoder;
import Entity.RpcRequest;
import Entity.RpcResponse;
import Registry.Remote.NacosRemoteRegistry;
import Registry.Remote.RemoteRegistry;
import Serializer.CommonSerializer;
import Serializer.KryoSerializer;
import Transport.RpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private String host;
    private int port;
    private static final Bootstrap bootstrap;
    private static RemoteRegistry remoteRegistry;
    private CommonSerializer serializer;

    static {
        remoteRegistry = new NacosRemoteRegistry();
    }

    /**
     * 静态代码块部分配置好Netty客户端,等待发送数据时启动, channel将RpcRequest写出并等待服务端返回response<br>
     * 注: 通过AttributeKey方式阻塞获取返回结果
     */
    static {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new CommonDecoder())
                                .addLast(new CommonEncoder(new KryoSerializer()))
                                .addLast(new NettyClientHandler());
                    }
                });
    }

    public NettyClient(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {

//        AtomicReference result = new AtomicReference(null);
        try {
            //修改为从远程服务注册中心获取Ip地址与端口号而非本地定义
            InetSocketAddress inetSocketAddress = remoteRegistry.lookupService(rpcRequest.getInterfaceName());

            this.host = inetSocketAddress.getHostName();
            this.port = inetSocketAddress.getPort();

            ChannelFuture future = bootstrap.connect(host, port).sync();
            logger.info("客户端连接到服务器: {}:{}", host, port);
            Channel channel = future.channel();
            if (channel != null) {
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if (future1.isSuccess()) {
                        logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    } else {
                        logger.error("发送消息时有错误发生: ", future1.cause());
                    }
                });

                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();
                return rpcResponse;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
