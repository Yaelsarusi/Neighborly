package com.example.neighborly;

import java.io.Serializable;

public class RequestModel implements Serializable {

    private boolean resolved;
    private String requestId;
    private String requestMsg;
    private String requestUserId;
    private String itemRequested;


    public RequestModel() {
    }

    public RequestModel(String requestId, boolean resolved, String requestUserId, String requestMsg, String itemRequested) {
        this.requestId = requestId;
        this.resolved = resolved;
        this.requestUserId = requestUserId;
        this.requestMsg = requestMsg;
        this.itemRequested = itemRequested;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestUserId() {
        return requestUserId;
    }

    public void setRequestUserId(String requestUserId) {
        this.requestUserId = requestUserId;
    }

    public String getRequestMsg() {
        return requestMsg;
    }

    public void setRequestMsg(String requestMsg) {
        this.requestMsg = requestMsg;
    }

    public String getItemRequested() {
        return  this.itemRequested;
    }

    public String getItemPresentedName() {
        return  itemRequested.substring(0,1).toUpperCase() + itemRequested.substring(1).toLowerCase();
    }

    public void setItemRequested(String itemRequested) {
        this.itemRequested = itemRequested;
    }

    int getImage() {
        return 0;
    }
}
