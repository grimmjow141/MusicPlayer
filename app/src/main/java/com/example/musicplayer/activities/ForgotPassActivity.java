package com.example.musicplayer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.musicplayer.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassActivity extends AppCompatActivity {
    private EditText email;
    private Button recover;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        email=findViewById(R.id.email);
        recover=findViewById(R.id.recoverBtn);
        auth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoverPassword();
            }
        });
    }
    private String email1;
    private void recoverPassword()
    {
        email1=email.getText().toString().trim();
        //verific daca emailul este valid
        if(!Patterns.EMAIL_ADDRESS.matcher(email1).matches())
        {
            email.setError("Invalid email");
            return;
        }
        progressDialog.setMessage("Sending instructions to reset password...");
        progressDialog.show();
        //trimie instructiunile de resetare catre mail
        auth.sendPasswordResetEmail(email1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //daca s-au trimis te trimite catre pagina de logare,si trebuie sa iti verifici mail-ul sa iti resetezi parola
                progressDialog.dismiss();
                Toast.makeText(ForgotPassActivity.this,"Password reset!Instructions sent to your email!",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ForgotPassActivity.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ForgotPassActivity.this,"Failed!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}