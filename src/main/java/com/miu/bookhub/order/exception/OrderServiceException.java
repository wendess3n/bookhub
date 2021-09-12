package com.miu.bookhub.order.exception;

import com.miu.bookhub.global.exception.ServiceException;

public class OrderServiceException extends ServiceException {

    public OrderServiceException(String message) {
        super(message);
    }

    public OrderServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
