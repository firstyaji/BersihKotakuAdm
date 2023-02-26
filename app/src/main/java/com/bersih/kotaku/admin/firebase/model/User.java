package com.bersih.kotaku.admin.firebase.model;

import java.util.Objects;

public class User extends Model {
    public String fullName = "";
    public String email = "";
    public String firebaseID = "";
    public String phoneNumber = "";
    public String profile = "";
    public String AddressName = "";
    public String AddressDetails = "";
    public String dayBirth = "";
    public String job = "";
    public String firebaseToken = "";
    public String defaultBank = "";
    public int gender;
    public boolean isActive;
    public long createAt;
    public long updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return gender == user.gender && isActive == user.isActive && createAt == user.createAt && updatedAt == user.updatedAt && id.equals(user.id) && fullName.equals(user.fullName) && email.equals(user.email) && firebaseID.equals(user.firebaseID) && phoneNumber.equals(user.phoneNumber) && profile.equals(user.profile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, email, firebaseID, phoneNumber, profile, gender, isActive, createAt, updatedAt);
    }
}
