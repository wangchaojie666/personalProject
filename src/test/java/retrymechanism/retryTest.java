package retrymechanism;

import cn.hutool.http.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class retryTest {

    private static Logger logger = LoggerFactory.getLogger(retryTest.class);
    private int maxTry = 3;

    @Test
    public void test1(){
        for (int i=1;i<=maxTry;i++){
            try {
                Boolean result = this.operate1();
                if (result){
                    i = 4;
                    logger.info(result.toString());
                }
            }catch (Exception exception){
                exception.printStackTrace();
            }
        }
    }

    public Boolean operate1(){
        logger.info("operate1 开始");
        String result = HttpUtil.get("www.baidu.com",500);
        logger.info(result);
        logger.info("operate1 结束");
        if (StringUtils.isNotEmpty(result)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
