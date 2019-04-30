package com.sihan.turbodrive.Repository;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sihan.turbodrive.Domain.Profile;

import java.util.Objects;

public class ProfileServerRepository implements ProfileRepository{
    private static final String DB_COLLECTION_NAME = "User";
    private static final String LOG_TAG = "TurboDrive";

    private FirebaseFirestore db;

    public ProfileServerRepository() {
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void getProfile(String email, final TextView username) {
        DocumentReference documentReference = db.collection(DB_COLLECTION_NAME).document(email);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Profile profile = document.toObject(Profile.class);
                        if(profile == null){
                            profile = new Profile("null","null","null");
                        }
                        username.setText(profile.getUserName());
                    } else {
                        Log.e("error","no document");
                    }
                } else {
                    Log.e("error", Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        });
    }

    @Override
    public void addProfile(final Profile profile) {
        db.collection(DB_COLLECTION_NAME)
                .document(profile.getEmail())
                .set(profile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(LOG_TAG, "Profile saved successfully to FireStore: " + profile);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(LOG_TAG, "Failed to save profile: " + profile, e);
                    }
                });
    }

    @Override
    public void deleteProfile(Profile profile) {

    }

    public void setUserName(String email, final SharedPreferences sharedPreferences){
        DocumentReference documentReference = db.collection(DB_COLLECTION_NAME).document(email);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Profile profile = document.toObject(Profile.class);
                        if(profile == null){
                            profile = new Profile("null","null","null");
                        }
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username",profile.getUserName());
                        editor.commit();
                    } else {
                        Log.e("error","no document");
                    }
                } else {
                    Log.e("error", Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        });
    }
}
