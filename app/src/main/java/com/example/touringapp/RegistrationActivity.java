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


public class RegistrationActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passField;
    private Button regButton;
    private TextView txtsignin;

    private ProgressDialog dialog;

    //Firebase
    private FirebaseAuth Auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Auth=FirebaseAuth.getInstance();

        dialog=new ProgressDialog(this);

        registration();
    }

    private void registration() {
        emailField = findViewById(R.id.email_reg);
        passField = findViewById(R.id.password_reg);
        regButton = findViewById(R.id.reg_btn);
        txtsignin = findViewById(R.id.account_existed);

        regButton.setOnClickListener(new View.OnClickListener() {
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

                Auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Registration Completed", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        } else {
                            dialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Registration Failed! Try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        txtsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }
}