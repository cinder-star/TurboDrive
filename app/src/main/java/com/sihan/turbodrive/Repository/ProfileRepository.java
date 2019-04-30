package com.sihan.turbodrive.Repository;

import android.content.SharedPreferences;
import android.widget.TextView;

import com.sihan.turbodrive.Domain.Profile;

public interface ProfileRepository {
    void getProfile(String email, TextView username);

    void addProfile(Profile profile);

    void deleteProfile(Profile profile);

    void setUserName(String email, SharedPreferences sharedPreferences);
}