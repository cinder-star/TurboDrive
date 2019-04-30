package com.sihan.turbodrive.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.sihan.turbodrive.Domain.Profile;
import com.sihan.turbodrive.R;
import com.sihan.turbodrive.Service.ProfileService;

public class SignUpActivity extends AppCompatActivity {

    private EditText username, email, password, confirmPassword;
    private Button signUpButton;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Register");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        bindWidgets();
        bindListeners();
    }


    private void bindWidgets() {
        username = findViewById(R.id.sign_up_username);
        email = findViewById(R.id.sign_up_Email);
        password = findViewById(R.id.sign_up_Password);
        confirmPassword = findViewById(R.id.sign_up_confirm_Password);
        signUpButton = findViewById(R.id.email_sign_up);
        progressDialog = new ProgressDialog(this);
    }

    private void bindListeners() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate(email,password,confirmPassword)) {
                    createUserAndLogin();
                }
            }
        });
    }

    private void createUserAndLogin() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.setTitle("Completing registration and logging in...");
                    progressDialog.show();
                    logIn();
                }
                else{
                    Toast.makeText(SignUpActivity.this,"Registration Failed!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void logIn() {
        final Profile profile = new Profile();
        profile.setEmail(email.getText().toString());
        profile.setUserName(username.getText().toString());
        profile.setPasswordHash(Integer.toString(password.getText().toString().hashCode()));
        final ProfileService profileServerService = new ProfileService();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(profile.getEmail(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            profileServerService.addProfile(profile);
                            progressDialog.dismiss();
                            startActivity(new Intent(SignUpActivity.this, DriveActivity.class));
                        }
                        else{
                            Toast.makeText(SignUpActivity.this,getString(R.string.error),Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });

    }

    private boolean validate(EditText email, EditText password, EditText confirmPassword){
        boolean value = true;

        if(TextUtils.isEmpty(email.getText().toString())){
            value = false;
            email.setError("Email can't be empty!");
        }

        if(TextUtils.isEmpty(password.getText().toString()) || TextUtils.isEmpty(confirmPassword.getText().toString())){
            value = false;
            if(TextUtils.isEmpty(password.getText().toString())) password.setError("Password can't be empty!");
            if(TextUtils.isEmpty(confirmPassword.getText().toString())) password.setError("Password can't be empty!");
        }
        else if(!password.getText().toString().equals(confirmPassword.getText().toString())){
            value = false;
            password.setError("Passwords are not same!");
            confirmPassword.setError("Passwords are not same!");
        }
        return value;
    }
}
