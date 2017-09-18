package com.dinsmooth;

import com.dinsmooth.common.exception.DinSmoothRuntimeException;
import com.dinsmooth.emay.SmsUtils;
import com.dinsmooth.emay.dto.SmsIdAndMobileAndContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        LOGGER.debug("Hello World!");
//        testBalance();
//        testSend();
//        testBatch();
//        testPersonality();
    }

    private static void testSend(){
        try {
            SmsUtils.sendSingle("18600181386", "床前明月光, 疑是地上霜!");
        }catch (DinSmoothRuntimeException e){
            LOGGER.error("有错误发生",e);
        }
    }

    private static void testBalance(){
        try {
            SmsUtils.getBalance();
        }catch (DinSmoothRuntimeException e){
            LOGGER.error("有错误发生",e);
        }
    }

    private static void testBatch(){
        try {
            SmsUtils.sendBatchOnly( "床前明月光, 疑是地上霜!","18600181386","18811153834");
        }catch (DinSmoothRuntimeException e){
            LOGGER.error("有错误发生",e);
        }
    }

    private static void testPersonality(){
        try {
            SmsIdAndMobileAndContent content1 = new SmsIdAndMobileAndContent("","18600181386","床前明月光, 疑是地上霜;");
            SmsIdAndMobileAndContent content2 = new SmsIdAndMobileAndContent("","18811153834","举头望明月, 低头思故乡;");
            SmsUtils.sendPersonality(content1,content2);
        }catch (DinSmoothRuntimeException e){
            LOGGER.error("有错误发生",e);
        }
    }
}
