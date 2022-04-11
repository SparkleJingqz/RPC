package Hook;


import Factory.ThreadPoolFactory;
import Utils.NacosUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * 关闭服务通知类，当服务端一个Nacos服务要关闭时调用NacosUtils中的方法
 */
@Slf4j
public class ShutDownHook {
    private final ExecutorService threadPool = ThreadPoolFactory.createDefaultThreadPool("shutdown-hook");
    //懒汉式单例 - 使用时加载
    private static ShutDownHook shutdownHook;

    private ShutDownHook(){}

    public static ShutDownHook getInstance() {
        //1重校验，判断当前实例是否为null，若不为null直接返回
        if (shutdownHook == null) {
            //2.若为null，则并发进入的线程只能有一个实际参与创建工作，添加同步锁
            synchronized (ShutDownHook.class) {
                //3.经过1重校验进入的线程若此前有线程已经创建完毕shutdownHook则不需创建
                if (shutdownHook == null) {
                    shutdownHook = new ShutDownHook();
                }
            }
        }
        return shutdownHook;
    }

    /**
     * Runtime对象-JVM运行时环境，调用其addShutdownHook方法增加钩子函数 -> 创建新线程调用clearRegistry方法完成注销工作<br>
     * addShutdownHook方法添加的线程再JVM关闭前被调用完成注销工作 -> 保证每次客户端调用的都是在线的服务端信息
     */
    public void addClearAllHook() {
        log.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtils.clearRegistry();
            threadPool.shutdown();
        }));
    }

}
