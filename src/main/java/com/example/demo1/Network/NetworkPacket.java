package com.example.demo1.Network;

import java.io.Serializable;

public class NetworkPacket implements Serializable {
    // الزامی بودن این خط طبق مستند پروژه برای جلوگیری از خطای ناسازگاری شیء
    private static final long serialVersionUID = 1L;

    private RequestType requestType;
    private Object data; // این فیلد جنریک هست و می‌تونه User، Post یا هر چیزی باشه
    private String message; // برای فرستادن پیام‌های متنی جانبی یا متن خطاها

    public NetworkPacket(RequestType requestType, Object data) {
        this.requestType = requestType;
        this.data = data;
    }

    public NetworkPacket(RequestType requestType, Object data, String message) {
        this.requestType = requestType;
        this.data = data;
        this.message = message;
    }

    // Getters and Setters
    public RequestType getRequestType() { return requestType; }
    public void setRequestType(RequestType requestType) { this.requestType = requestType; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}