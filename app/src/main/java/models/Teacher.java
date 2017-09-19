package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by emin on 15.11.2015.
 */
public class Teacher {

    @SerializedName("userID")
    public String id;
    @SerializedName("userName")
    public String fullName;
    @SerializedName("userProfilePicture")
    public String picture;
    @SerializedName("userEmail")
    public String email;

    public Teacher(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
