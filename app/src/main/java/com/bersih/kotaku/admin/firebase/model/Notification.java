package com.bersih.kotaku.admin.firebase.model;

public class Notification extends Model {
    public String userID;
    public String title;
    public String type;
    public String payload;
    public boolean isRead;
    public long createdAt;
}