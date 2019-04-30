package com.sihan.turbodrive.Domain;

import java.util.Objects;

public class Profile {
    private String userName;
    private String email;
    private String passwordHash;

    public Profile() {
    }

    public Profile(String userName, String email, String passwordHash) {
        this.userName = userName;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile)) return false;
        Profile profile = (Profile) o;
        return getUserName().equals(profile.getUserName()) &&
                getEmail().equals(profile.getEmail()) &&
                getPasswordHash().equals(profile.getPasswordHash());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserName(), getEmail(), getPasswordHash());
    }

    @Override
    public String toString() {
        return "Profile{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                '}';
    }
}
