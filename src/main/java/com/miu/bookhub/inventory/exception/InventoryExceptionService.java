package com.miu.bookhub.inventory.exception;

import com.miu.bookhub.global.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InventoryExceptionService extends ServiceException {

    public InventoryExceptionService(String message) {
        super(message);
    }

    public InventoryExceptionService(String message, Throwable cause) {
        super(message, cause);
    }
}
