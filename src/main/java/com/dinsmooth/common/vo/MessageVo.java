package com.dinsmooth.common.vo;

/**
 *
 */
public class MessageVo {
    private boolean success;// 处理是否成功

    private String message;

    private Object data;

    public MessageVo() {
        //default
    }

    public MessageVo(boolean success) {
        this.success = success;
    }

    public MessageVo(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public MessageVo(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MessageVo{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
