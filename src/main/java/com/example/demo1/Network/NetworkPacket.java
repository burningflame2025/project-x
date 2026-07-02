package com.example.demo1.Network;

import java.io.Serializable;

public class NetworkPacket implements Serializable {
    private static final long serialVersionUID = 1L;

    private RequestType requestType;
    private Object data;
    private String statusMessage;
    private boolean isSuccess;

    public NetworkPacket(RequestType requestType, Object data) {
        this.requestType = requestType;
        this.data = data;
        this.isSuccess = false;
        this.statusMessage = "";
    }

    public NetworkPacket(RequestType requestType, Object data, boolean isSuccess, String statusMessage) {
        this.requestType = requestType;
        this.data = data;
        this.isSuccess = isSuccess;
        this.statusMessage = statusMessage;
    }

    public RequestType getRequestType() { return requestType; }
    public void setRequestType(RequestType requestType) { this.requestType = requestType; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    public boolean isSuccess() { return isSuccess; }
    public void setSuccess(boolean success) { isSuccess = success; }
}