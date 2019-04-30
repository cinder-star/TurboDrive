package com.sihan.turbodrive.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import com.sihan.turbodrive.Domain.Profile;
import com.sihan.turbodrive.Repository.ProfileRepository;
import com.sihan.turbodrive.Repository.ProfileServerRepository;

public class ProfileService {
    private static final boolean USE_SQLITE_REPOSITORY = false;

    private ProfileRepository repository;

    public ProfileService(Context context) {
        repository = USE_SQLITE_REPOSITORY ? new ProfileServerRepository() : new ProfileServerRepository();
    }

    public ProfileService() {
        repository = new ProfileServerRepository();
    }

    public void addProfile(Profile profile) {
        repository.addProfile(profile);
    }

    public void getProfile(String email, TextView username){
        repository.getProfile(email,username);
    }

    public void deleteProfile(Profile profile) {
        repository.deleteProfile(profile);
    }

    public void setUserName(String email, SharedPreferences sharedPreferences){
        repository.setUserName(email,sharedPreferences);
    }
}
