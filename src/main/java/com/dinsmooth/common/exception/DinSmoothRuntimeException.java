package com.dinsmooth.common.exception;


/**
 *
 * User: HanHongmin
 */
public class DinSmoothRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public DinSmoothRuntimeException() {
        super();
    }

    public DinSmoothRuntimeException(String message) {
        super(message);
    }

    public DinSmoothRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DinSmoothRuntimeException(Throwable cause) {
        super(cause);
    }

    public DinSmoothRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
