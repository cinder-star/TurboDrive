package com.sihan.turbodrive.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.sihan.turbodrive.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        TextView email = findViewById(R.id.email_view);
        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }
}
