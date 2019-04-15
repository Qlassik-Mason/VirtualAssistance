package com.example.qlassik_mason.virtualassistance.AccountActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qlassik_mason.virtualassistance.R;
import com.example.qlassik_mason.virtualassistance.entity.ChatMessage;
import com.example.qlassik_mason.virtualassistance.feature.MainActivity;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity{

    private TextView createAccount;
    private EditText userEmail;
    private EditText userPassword;
    private Button signIn;
    private DatabaseReference myDatabase;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private CheckBox checkBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();

        RegisterActivity registerActivity = new RegisterActivity();



        firebaseAuth = FirebaseAuth.getInstance();
        myDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        userEmail = (EditText)findViewById(R.id.user_email);
        userPassword = (EditText)findViewById(R.id.user_password);
        signIn = (Button)findViewById(R.id.sign_in_button);
        createAccount = (TextView)findViewById(R.id.dont_have_account);
        checkBox = (CheckBox)findViewById(R.id.remember_me);
        Paper.init(this);
        progressDialog = new ProgressDialog(this);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMeToRegisterActivity();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogInUser();
            }
        });



        }

    private void LogInUser(){
        final String email = userEmail.getText().toString().trim();
        final String password = userPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"pasword is required",Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.setTitle("LogIn");
            progressDialog.setMessage("please Wait,checking your credentials");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        if(checkBox.isChecked()){
                            Paper.book().write(prevalent.UserEmailKey,email);
                            Paper.book().write(prevalent.UserPasswordKey,password);
                        }
                        final String user_id = firebaseAuth.getCurrentUser().getUid();
                        myDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(user_id)){
                                    progressDialog.dismiss();
                                    Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    prevalent.currentOnLineUser = user_id;
                                    startActivity(homeIntent);
                                    Toast.makeText(LoginActivity.this,"you have succesfully logged in",Toast.LENGTH_LONG).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    else{
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this,errorMessage,Toast.LENGTH_LONG).show();

                        Intent loginIntent = new Intent(LoginActivity.this,LoginActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(loginIntent);
                    }
                }
            });
        }
    }

    private void SendMeToRegisterActivity(){
        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }


}
