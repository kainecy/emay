package com.dinsmooth.emay;

/**
 * @author HanHongmin 2017-09-18
 */
public class EmayConfig {
    private String appId;
    private String secretKey;
    private String sign;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "EmayConfig{" +
                "appId='" + appId + '\'' +
                ", secretKey='" + secretKey + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
