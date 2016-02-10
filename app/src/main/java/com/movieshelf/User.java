package com.movieshelf;

import java.io.Serializable;

/**
 * Created by kshitij.sharma on 2/10/2016.
 */
public class User implements Serializable{
    private String id;
    private String password;
    private String name;
    private String loginType;
    //Facebook params
    private String fbId;
    private String fbEmailId;
    private String fbName;
    private String fbProfile;
    private String firstName;
    private String lastName;
    private String fbLink;
    private String gender;
    private String dob;
    private String fbLocale;
    private String fbTimeZone;
    private String fbUpdatedTime;
    private String fbVerified;
    private String fbUserActionsVideos;
    private String fbAgeRange;
    private String fbHometown;
    //Google Params
    private String gpId;
    private String gpEmailId;
    private String gpName;
    private String gpProfileImage;
    private String gpAgeRange;
    private String gpLanguage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFbEmailId() {
        return fbEmailId;
    }

    public void setFbEmailId(String fbEmailId) {
        this.fbEmailId = fbEmailId;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getGpEmailId() {
        return gpEmailId;
    }

    public void setGpEmailId(String gpEmailId) {
        this.gpEmailId = gpEmailId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getFbName() {
        return fbName;
    }

    public void setFbName(String fbName) {
        this.fbName = fbName;
    }

    public String getFbProfile() {
        return fbProfile;
    }

    public void setFbProfile(String fbProfile) {
        this.fbProfile = fbProfile;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFbLink() {
        return fbLink;
    }

    public void setFbLink(String fbLink) {
        this.fbLink = fbLink;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getFbLocale() {
        return fbLocale;
    }

    public void setFbLocale(String fbLocale) {
        this.fbLocale = fbLocale;
    }

    public String getFbTimeZone() {
        return fbTimeZone;
    }

    public void setFbTimeZone(String fbTimeZone) {
        this.fbTimeZone = fbTimeZone;
    }

    public String getFbUpdatedTime() {
        return fbUpdatedTime;
    }

    public void setFbUpdatedTime(String fbUpdatedTime) {
        this.fbUpdatedTime = fbUpdatedTime;
    }

    public String getFbVerified() {
        return fbVerified;
    }

    public void setFbVerified(String fbVerified) {
        this.fbVerified = fbVerified;
    }

    public String getFbUserActionsVideos() {
        return fbUserActionsVideos;
    }

    public void setFbUserActionsVideos(String fbUserActionsVideos) {
        this.fbUserActionsVideos = fbUserActionsVideos;
    }

    public String getFbAgeRange() {
        return fbAgeRange;
    }

    public void setFbAgeRange(String fbAgeRange) {
        this.fbAgeRange = fbAgeRange;
    }

    public String getFbHometown() {
        return fbHometown;
    }

    public void setFbHometown(String fbHometown) {
        this.fbHometown = fbHometown;
    }

    public String getGpId() {
        return gpId;
    }

    public void setGpId(String gpId) {
        this.gpId = gpId;
    }

    public String getGpName() {
        return gpName;
    }

    public void setGpName(String gpName) {
        this.gpName = gpName;
    }

    public String getGpProfileImage() {
        return gpProfileImage;
    }

    public void setGpProfileImage(String gpProfileImage) {
        this.gpProfileImage = gpProfileImage;
    }

    public String getGpAgeRange() {
        return gpAgeRange;
    }

    public void setGpAgeRange(String gpAgeRange) {
        this.gpAgeRange = gpAgeRange;
    }

    public String getGpLanguage() {
        return gpLanguage;
    }

    public void setGpLanguage(String gpLanguage) {
        this.gpLanguage = gpLanguage;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
