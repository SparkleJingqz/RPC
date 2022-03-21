package Server.Handler;

import Client.RpcRequest;
import Server.Registry.DefaultServiceRegistry;
import Server.Registry.NacosRemoteRegistry;
import Server.Registry.RemoteRegistry;
import Server.Registry.ServiceRegistry;
import Server.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 接受RpcRequest,执行调用,封装为rpcResponse返回
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;
    private static ServiceRegistry serviceRegistry;

    static {
        requestHandler = new RequestHandler();
        serviceRegistry = new DefaultServiceRegistry();
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        try {
            logger.info("服务器接收到请求: {}", rpcRequest);
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result = requestHandler.handle(rpcRequest, service);
            RpcResponse<Object> success = RpcResponse.success(result);
            ChannelFuture future = channelHandlerContext.writeAndFlush(success);
            future.addListener(ChannelFutureListener.CLOSE);
        } finally {
            //这里的作用是什么?
            ReferenceCountUtil.release(rpcRequest);
        }
    }
}
