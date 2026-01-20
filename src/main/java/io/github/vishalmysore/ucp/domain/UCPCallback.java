package io.github.vishalmysore.ucp.domain;

import com.t4a.detect.ActionCallback;
import com.t4a.detect.ActionState;

public class UCPCallback implements ActionCallback {

    @Override
    public void setContext(Object obj) {

    }

    @Override
    public Object getContext() {
        return null;
    }

    @Override
    public String getType() {
        return "";
    }

    @Override
    public String setType(String type) {
        return "";
    }

    @Override
    public void sendtStatus(String status, ActionState state) {

    }
}
