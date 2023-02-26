package com.bersih.kotaku.admin.firebase.model;

public class Subscription extends Model {
    public String userID;
    public String packID;
    public String packName;
    public String fullName;
    public String addressName;
    public String addressDetails;
    public double price;
    public String paymentReceipt;
    public String receiverBank;
    public String receiverAccountName;
    public String receiverAccountNumber;
    public String status;
    public long registerAt;
    public long validUntil;
    public long lastPicker = 0;
}
