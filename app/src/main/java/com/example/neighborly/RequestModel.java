package com.example.neighborly;

import java.io.Serializable;

public class RequestModel implements Serializable {

    private boolean resolved;

    public RequestModel() {
    }

    public RequestModel(boolean resolved) {
        this.resolved = resolved;
    }

    // Todo: implement this
    int getImage() {
        return 0;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }
}
