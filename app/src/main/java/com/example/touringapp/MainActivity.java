package com.example.touringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passField;
    private Button logButton;
    private TextView txtForgotpass;
    private TextView txtSignup;

    private ProgressDialog dialog;

    //Firebase
    private FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Auth=FirebaseAuth.getInstance();

        if(Auth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }

        dialog=new ProgressDialog(this);

        login();
    }

    private void login() {
        emailField=findViewById(R.id.email_login);
        passField=findViewById(R.id.password_login);
        logButton=findViewById(R.id.login_btn);
        txtForgotpass=findViewById(R.id.forget_password);
        txtSignup=findViewById(R.id.signup_regis);

        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailField.getText().toString().trim();
                String pass = passField.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    emailField.setError("Email is required!");
                    return;
                }
                if (TextUtils.isEmpty(pass)){
                    passField.setError("Password is required!");
                    return;
                }

                dialog.setMessage("Loading...");
                dialog.show();

                Auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                            Toast.makeText(MainActivity.this, "Login Successful...", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Login Failed...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
/// to registrating view
        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RegistrationActivity.class));
            }
        });

        //forgot pass view
        txtForgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ResetPassActivity.class));
            }
        });

    }
}