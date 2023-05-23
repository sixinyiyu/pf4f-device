package com.github.device.app;

import java.io.Serializable;

public class RegResponse implements Serializable {

    private boolean success;

    private String message = "";

    public RegResponse() {
    }

    public RegResponse(boolean regSuccess, String message) {
        this.success = regSuccess;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
