package HelloServiceImpl;

import HelloService.*;
import Annotation.Service;
import HelloService.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class HelloServiceImpl implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    @Override
    public String hello(HelloObject object) {
        logger.info("接收到: {}", object.getMessage());
        return "调用的返回值: id=" + object.getId();
    }

    @Override
    public String hello(HelloObject object, Integer i) {
        logger.info("接收到: {}", object.getMessage(), i);
        return "调用的返回值: id+i=" + object.getId() + i;
    }
}
