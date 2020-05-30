package com.deha.app.model;

import com.deha.app.service.BroadcastType;
import com.deha.app.service.P2PConnections;

public class UserAdapterModel extends UserModel {

    private BroadcastType broadcastType;
    private boolean isContact;

    public UserAdapterModel(UserModel model, BroadcastType broadcastType, boolean isContact) {
        super(model);
        this.broadcastType = broadcastType;
        this.isContact = isContact;
    }

    public BroadcastType getBroadcastType() {
        return broadcastType;
    }

    public void setBroadcastType(BroadcastType broadcastType) {
        this.broadcastType = broadcastType;
    }

    public boolean isContact() {
        return isContact;
    }

    public void setContact(boolean contact) {
        isContact = contact;
    }
}
