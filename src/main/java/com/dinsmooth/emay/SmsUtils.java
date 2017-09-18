package com.dinsmooth.emay;

import com.dinsmooth.common.exception.DinSmoothRuntimeException;
import com.dinsmooth.common.jackson2.ObjectMapperFactory;
import com.dinsmooth.common.utils.HttpClientUtils;
import com.dinsmooth.common.vo.MessageVo;
import com.dinsmooth.emay.dto.*;
import com.dinsmooth.emay.utils.AesUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author HanHongmin 2017-09-18
 */
public class SmsUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmsUtils.class);
    private static final String SINGLE_URL = "http://bjmtn.b2m.cn/inter/sendSingleSMS";//发送单条
    private static final String BALANCE_URL = "http://bjmtn.b2m.cn/inter/getBalance";//获取余额
    private static final String BATCH_PERSONALITY_URL = "http://bjmtn.b2m.cn/inter/sendPersonalitySMS";//批量发送,可以每条内容不一样
    private static final String BATCH_ONLY_URL = "http://bjmtn.b2m.cn/inter/sendBatchOnlySMS";// 批量发送统一内容
    private static final int VALID_PERIOD = 60;//60秒有效

    private static EmayConfig config = null;

    static{
        Yaml yaml = new Yaml();
        try(InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("emay.yaml")){
            config = yaml.loadAs(is, EmayConfig.class);
            LOGGER.debug("读取配置: {}", config);
        } catch (IOException e) {
            LOGGER.error("出错啦",e);
        }
    }

    private SmsUtils(){
        //empty
    }

    /**
     * 发送单挑短信
     * @param mobile 手机号码
     * @param content 短信内容
     * @return 结果信息
     */
    public static MessageVo sendSingle(final String mobile,final String content){
        String signedContent = config.getSign() + content;
        SmsSingleRequest request = new SmsSingleRequest();
        request.setContent(signedContent);
        request.setMobile(mobile);
        String uuid = UUID.randomUUID().toString();
        String customId = uuid.replace("-","");
        request.setCustomSmsId(customId);
        request.setRequestTime(System.currentTimeMillis());
        request.setRequestValidPeriod(VALID_PERIOD);

        byte[] requestData = encryptRequest(request);
        String result = doRequest(SINGLE_URL,requestData);
        LOGGER.debug("sendSingle 结果内容: {}",result);
        return new MessageVo(true);
    }

    /**
     * 批量发送短信, 短信内容一致, 无自定义短信id
     * @param content 短信内容
     * @param mobiles 手机号码集合
     * @return 结果信息
     */
    public static MessageVo sendBatchOnly(String content,String... mobiles){
        if(mobiles==null||mobiles.length>500){
            throw new DinSmoothRuntimeException("请检查批量发送的手机号数量");
        }
        String signedContent = config.getSign() + content;
        SmsBatchOnlyRequest request = new SmsBatchOnlyRequest();
        request.setContent(signedContent);
        request.setMobiles(mobiles);
        request.setRequestTime(System.currentTimeMillis());
        request.setRequestValidPeriod(VALID_PERIOD);

        byte[] requestData = encryptRequest(request);
        String result = doRequest(BATCH_ONLY_URL,requestData);
        LOGGER.debug("sendBatchOnly 结果内容: {}",result);
        return new MessageVo(true);
    }

    /**
     * 批量发送短信, 短信内容可以不一致, 自定义短信id 用32位
     * @param mobiles 手机号码、短信ID、短信内容集合; 短信内容将被加上签名
     * @return 结果信息
     */
    public static MessageVo sendPersonality(SmsIdAndMobileAndContent... mobiles){
        if(mobiles==null||mobiles.length>500){
            throw new DinSmoothRuntimeException("请检查批量发送的手机号数量");
        }
        for(SmsIdAndMobileAndContent siamac:mobiles){
            siamac.setContent(config.getSign()+siamac.getContent());
        }
        SmsPersonalityRequest request = new SmsPersonalityRequest();
        request.setSmses(mobiles);
        request.setRequestTime(System.currentTimeMillis());
        request.setRequestValidPeriod(VALID_PERIOD);

        byte[] requestData = encryptRequest(request);
        String result = doRequest(BATCH_PERSONALITY_URL,requestData);
        LOGGER.debug("sendPersonality 结果内容: {}",result);
        return new MessageVo(true);
    }

    /**
     * 获取短信剩余条数
     * @return 结果信息
     */
    public static MessageVo getBalance(){
        BaseRequest request = new BaseRequest();
        request.setRequestTime(System.currentTimeMillis());
        request.setRequestValidPeriod(VALID_PERIOD);

        byte[] requestData = encryptRequest(request);
        String result = doRequest(BALANCE_URL,requestData);
        LOGGER.debug("getBalance 结果内容: {}",result);
        return new MessageVo(true);
    }


    //======================================
    private static byte[] encryptRequest(BaseRequest request){
        try {
            ObjectMapper om = ObjectMapperFactory.getSimpleMapper();
            byte[] jsonByte = om.writeValueAsBytes(request);
            return AesUtils.encrypt(config.getSecretKey().getBytes(),jsonByte);
        } catch (JsonProcessingException e) {
            LOGGER.error("Json 处理异常", e);
            throw new DinSmoothRuntimeException(e);
        }
    }

    private static String doRequest(final String url,final byte[] postData){
        try(CloseableHttpClient client = HttpClientUtils.getHttpClient()){
            HttpPost post = new HttpPost(url);
            post.setHeader("appId", config.getAppId());
            HttpEntity entity = new ByteArrayEntity(postData);
            post.setEntity(entity);

            HttpResponse response = client.execute(post);
            int status = response.getStatusLine().getStatusCode();
            LOGGER.debug("获取余额状态码: {}", status);
            Header header = response.getFirstHeader("result");
            if(header!=null){
                String res = header.getValue();
                LOGGER.debug("结果头信息: {}",res);
            }else{
                LOGGER.debug("没有找到结果头信息");
            }

            byte[] result = EntityUtils.toByteArray(response.getEntity());
            byte[] resultPlain = AesUtils.decrypt(config.getSecretKey().getBytes(),result);
            return new String(resultPlain);
        } catch (IOException e) {
            LOGGER.error("请求亿美接口异常", e);
            throw new DinSmoothRuntimeException(e);
        }
    }
}
