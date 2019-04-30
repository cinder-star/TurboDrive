package com.sihan.turbodrive.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sihan.turbodrive.R;

public class LoginActivity extends AppCompatActivity {
    private EditText email,password;
    private Button loginButton;
    private ProgressDialog progressDialog;
    private TextView signUp,recoverPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Log In");
        bindWidgets();
        bindListeners();
    }

    private void bindWidgets() {
        email = findViewById(R.id.email_value);
        password = findViewById(R.id.password_value);
        loginButton = findViewById(R.id.email_sign_in);
        signUp = findViewById(R.id.signUp);
        recoverPassword = findViewById(R.id.recover_password);
        progressDialog = new ProgressDialog(this);
    }

    private void bindListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate(email,password)) {
                    progressDialog.setTitle("Logging in...");
                    progressDialog.show();
                    logIn();
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });

        recoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RetrievePasswordActivity.class));
            }
        });
    }

    private void logIn() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            startActivity(new Intent(LoginActivity.this,DriveActivity.class));
                        }
                        else{
                            Toast.makeText(LoginActivity.this,getString(R.string.error),Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });

    }

    private boolean validate(EditText email, EditText password){
        boolean value = true;

        if(TextUtils.isEmpty(email.getText().toString())){
            value = false;
            email.setError("Email can't be empty!");
        }

        if(TextUtils.isEmpty(password.getText().toString())){
            value = false;
            if(TextUtils.isEmpty(password.getText().toString())) password.setError("Password can't be empty!");
        }
        return value;
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
