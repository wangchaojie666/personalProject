package retrymechanism;

import cn.hutool.http.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.io.File;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class ThreadPoolService {
    //核心线程数
    private static final int DEFAULT_CORE_SIZE = 10;
    //最大线程数
    private static final int MAX_QUEUE_SIZE = 20;
    private static final int QUEUE_INIT_MAX_SIZE = 500;
    private volatile static ThreadPoolExecutor executor;

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolService.class);

    public ThreadPoolService(){

    }

    //获取单例的线程池对象
    public static ThreadPoolExecutor getInstance(){
        if (executor == null){
            synchronized(ThreadPoolService.class){
                if (executor == null){
                    executor = new ThreadPoolExecutor(DEFAULT_CORE_SIZE,MAX_QUEUE_SIZE,Integer.MAX_VALUE, TimeUnit.MILLISECONDS,
                            new LinkedBlockingDeque<Runnable>(QUEUE_INIT_MAX_SIZE), Executors.defaultThreadFactory());
                }
            }
        }
        return executor;
    }

    public void execute(Runnable runnable){
        if (runnable == null){
            return;
        }
        executor.execute(runnable);
    }

    //从线程队列中移除对象
    public void cancle(Runnable runnable){
        if (executor != null){
            executor.getQueue().remove(runnable);
        }
    }

    @Test
    public void test(){
        ThreadPoolExecutor executor = ThreadPoolService.getInstance();
        AtomicInteger num = new AtomicInteger(1);
        CompletableFuture.runAsync(()->{
            while (num.get()<=3){
                try {
                    Thread.sleep(10*1000);
                }catch (InterruptedException interruptedException){
                    interruptedException.printStackTrace();
                }

                try {
                    Boolean result = this.operate1();
                    if (result){
                        num.set(4);
                    }
                }catch (Exception exception){
                    exception.printStackTrace();
                    num.getAndIncrement();
                }
            }
        },executor);
    }

    public Boolean operate1(){
        logger.info("operate1 开始");
        new File("wcj.txt");
        String result = HttpUtil.get("www.baidu.com",5000);
        System.out.println(result);
        logger.info(result);
        if (StringUtils.isNotEmpty(result)){
            return Boolean.TRUE;
        }
        logger.info("operate1 结束");
        return Boolean.FALSE;
    }

    @Retryable(value = SocketTimeoutException.class,maxAttempts = 10,backoff = @Backoff(value = 3000L),listeners = {"myRetryListener"})
    public Boolean operate2(){
        logger.info("operate2 开始");
        String result = HttpUtil.get("www.baidu.com",5000);
        System.out.println(result);
        logger.info(result);
        if (StringUtils.isNotEmpty(result)){
            return Boolean.TRUE;
        }
        logger.info("operate2 结束");
        return Boolean.FALSE;
    }
    //将该类中使用了@Retryable的方法的类注入到其他类中，然后调用该方法

}
