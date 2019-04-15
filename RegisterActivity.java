package com.example.qlassik_mason.virtualassistance.AccountActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qlassik_mason.virtualassistance.R;
import com.example.qlassik_mason.virtualassistance.entity.ChatMessage;
import com.example.qlassik_mason.virtualassistance.feature.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.paperdb.Paper;

public class RegisterActivity extends AppCompatActivity {
    private EditText userEmail,userPassword;
    private Button signup;
    private TextView hasAccount;

    private FirebaseAuth mAuth;
    private DatabaseReference myDatabase;
    private ProgressDialog loadingBox;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

         //getSupportActionBar().hide();











        userEmail=(EditText)findViewById(R.id.user_email);
        userPassword=(EditText)findViewById(R.id.user_password);
        signup = (Button)findViewById(R.id.sign_up_button);
        hasAccount = (TextView)findViewById(R.id.already_have_account);

        mAuth = FirebaseAuth.getInstance();
        myDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        loadingBox = new ProgressDialog(this);


        hasAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToLoginActivity();
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });


    }




    private void GoToLoginActivity(){
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    private void CreateNewAccount(){
        final String email = userEmail.getText().toString().trim();
        final String password = userPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(RegisterActivity.this,"Email is required",Toast.LENGTH_SHORT).show();
        }else if(!email.contains("@")){
            Toast.makeText(RegisterActivity.this,"Invalid Email",Toast.LENGTH_SHORT).show();
        }else if(!email.contains("gmail.com") && !email.contains("ymail.com") && !email.contains("icloud.com")){
            Toast.makeText(RegisterActivity.this,"Invalid Email",Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this,"password is reuired",Toast.LENGTH_SHORT).show();
        }else if (password.length()<6){
            Toast.makeText(RegisterActivity.this,"Minimum password required is 6 characters",Toast.LENGTH_SHORT).show();
        }else{
            loadingBox.setTitle("Create Account");
            loadingBox.setMessage("please wait while we create your account");
            loadingBox.show();
            loadingBox.setCanceledOnTouchOutside(false);
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    if(task.isSuccessful()){
                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference currentUser = myDatabase.child(user_id);




                        currentUser.child("Email").setValue(email);
                        currentUser.child("password").setValue(password);




                        loadingBox.dismiss();
                        Toast.makeText(RegisterActivity.this,"Registration was succesful",Toast.LENGTH_LONG).show();

                        loadingBox.dismiss();
                        Intent homeIntent = new Intent(RegisterActivity.this,MainActivity.class);
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(homeIntent);
                        finish();
                    }
                    else{
                        loadingBox.dismiss();
                        String errorMesage = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this,"Error occured"+errorMesage,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

}
