package com.example.musicplayer.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import p32929.androideasysql_library.EasyDB;

public class MainActivity extends AppCompatActivity {
    private EditText emailEt,passEt;
    private Button loginBtn;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ProgressDialog progressDialog;
    public EasyDB easyDB;
    private TextView forgotPass,register;
    private static final int STORAGE_REQUEST_CODE = 300;
    private String[] storagePermissions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        emailEt=findViewById(R.id.emailEt);
        passEt=findViewById(R.id.passEt);
        loginBtn=findViewById(R.id.loginBtn);
        forgotPass=findViewById(R.id.resetPass);
        register=findViewById(R.id.register);
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ForgotPassActivity.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });
        storagePermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        //cere permisiunea storage-ului
        requestStoragePermission();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        //aici se completeaza automat campurile cu care te-ai logat anterior
        easyDB=EasyDB.init(this,"DB")
                .setTableName("USER_TABLE")
                .addColumn("email","text")
                .addColumn("password","text")
                .doneTableColumn();
        Cursor res=easyDB.getAllData();
        //aici "punem mana" efectiv pe resultatele bazei de date
        while(res.moveToNext())
        {
            emailEt.setText(res.getString(1));
            passEt.setText(res.getString(2));
        }
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }
    private String email,password;
    private void loginUser()
    {
        email=emailEt.getText().toString().trim();
        password=passEt.getText().toString().trim();
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailEt.setError("Invalid email");
            return;
        }
        if(TextUtils.isEmpty(password))
        {
            passEt.setError("Invalid password!");
            return;
        }
        progressDialog.setMessage("Logging In...");
        progressDialog.show();
        //aici se incearca logara user-ului
        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                progressDialog.dismiss();
                boolean done=easyDB.addData(1,emailEt.getText().toString())
                        .addData(2,passEt.getText().toString())
                        .doneDataAdding();
                startActivity(new Intent(getApplicationContext(),MenuActivity.class));
            }
            //bucata de cod de mai jos se apeleaza in cazul in care datele de logare sunt incorecte
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Failed!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkStoragePermission()
    {
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private  void requestStoragePermission()
    {
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case STORAGE_REQUEST_CODE:
                if(grantResults.length>0)
                {
                    boolean storageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;

                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
