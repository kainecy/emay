package com.dinsmooth.common.utils;

import com.dinsmooth.common.exception.DinSmoothRuntimeException;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/** Http工具
 * Created by HanHongmin on 15/6/29.
 */
public class HttpClientUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtils.class);
    private static PoolingHttpClientConnectionManager cm = null;

    private HttpClientUtils() {
        //empty
    }

    public static CloseableHttpClient getHttpClient() {
        try {
            if(cm==null){
                TrustStrategy trustStrategy = new TrustSelfSignedStrategy();

                SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(trustStrategy).build();
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                        sslContext, NoopHostnameVerifier.INSTANCE);

                ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();

                Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                        .<ConnectionSocketFactory> create().register("http",plainSF).register("https", sslsf)
                        .build();

                cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
                // 将最大连接数增加到200
                cm.setMaxTotal(200);
                // 将每个路由基础的连接增加到20
                cm.setDefaultMaxPerRoute(20);
                //将目标主机的最大连接数增加到50
                cm.setValidateAfterInactivity(60000);
            }

            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(5000)
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .build();

            return HttpClients.custom()
                    .setConnectionManager(cm).setConnectionManagerShared(true)
                    .setDefaultRequestConfig(defaultRequestConfig)
                    .build();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            throw new DinSmoothRuntimeException(e);
        }
    }

    /**
     * 简单的网络请求
     * @param urlString Get请求地址
     * @return 返回内容
     */
    public static String doGetRequest(String urlString){
        try(CloseableHttpClient client = HttpClientUtils.getHttpClient()){
            HttpGet get = new HttpGet(urlString);
            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, Consts.UTF_8);
            }else{
                LOGGER.error("网络请求状态异常 - {}: {}",response.getStatusLine().getStatusCode(), urlString);
                return "";
            }
        } catch (IOException e) {
            LOGGER.error("网络请求异常: "+urlString,e);
        }
        return "";
    }

    public static void release() {
        if (cm != null) {
            cm.shutdown();
        }
    }
}
