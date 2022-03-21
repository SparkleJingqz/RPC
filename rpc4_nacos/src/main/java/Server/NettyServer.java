package Server;

import Server.Balancer.LoadBalancer;
import Server.Balancer.RoundRobinLoadBalancer;
import Server.Coder.CommonDecoder;
import Server.Coder.CommonEncoder;
import Server.Enumeration.BalancerType;
import Server.Error.RpcError;
import Server.Exception.RpcException;
import Server.Handler.NettyServerHandler;
import Server.Registry.*;
import Server.Serializer.CommonSerializer;
import Server.Serializer.JsonSerializer;
import Server.Serializer.KryoSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 实现NIO的Netty传输
 */
public class NettyServer implements RpcServer{

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);
    private CommonSerializer serializer;
    private String host;
    private int port;
    private RemoteRegistry remoteRegistry;
    private ServiceRegistry serviceRegistry;


    public NettyServer(String host, int port) {
        serviceRegistry = new DefaultServiceRegistry();
        this.host = host;
        this.port = port;
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new CommonEncoder(serializer));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            //服务端启动前调用addClearAllHook方法保证服务端关闭时会自动注销之前注册服务，保证注册表中的存在服务都有与之关联的在线服务器
            ShutDownHook.getInstance().addClearAllHook();
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 将服务保存在本地的注册表同时注册到Nacos上
     * @param service
     * @param serviceClass
     * @param <T>
     */
    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceRegistry.register(service);
        remoteRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
    }

    public void setSerializer(int code) {
        this.serializer = CommonSerializer.getByCode(code);
    }

    public void setBalancer(int code) {
        this.remoteRegistry = new NacosRemoteRegistry(code);
    }

}
