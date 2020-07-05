package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    EditText username;
    EditText name;
    EditText email;
    EditText password;
    TextView already;
    Button register;
    String txt_username;
    String txt_name;
    String txt_email;
    String txt_password;
    FirebaseAuth mAuth;
    FirebaseDatabase db;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        already = findViewById(R.id.alreadyRegistered);
        register = findViewById(R.id.register);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        final AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
        final ProgressDialog pd = new ProgressDialog();





        already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txt_username = username.getText().toString();
                txt_name = name.getText().toString();
                txt_email = email.getText().toString();
                txt_password = password.getText().toString();

                if(TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_name) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(Register.this, "Fill all fields to proceed", Toast.LENGTH_SHORT).show();
                }
                else if(txt_password.length() < 6){
                    Toast.makeText(Register.this, "Password Length too short", Toast.LENGTH_SHORT).show();
                }
                else{
                    pd.setDialog(builder, "Please wait...",true);
                    mAuth.createUserWithEmailAndPassword(txt_email, txt_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                HashMap<String, Object> hm = new HashMap<>();
                                hm.put("username", txt_username);
                                hm.put("name", txt_name);
                                hm.put("email", txt_email);
                                hm.put("password", txt_password);
                                hm.put("uid", mAuth.getUid());
                                hm.put("bio","");
                                hm.put("imageURL","default");
                                db.getReference().child("Users").child(mAuth.getUid()).setValue(hm);
                                pd.setDialog(builder, false);
                                Intent intent = new Intent(Register.this, Home.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                                finish();

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("ERROR REGISTRATION", e.toString());
                        }
                    });

                }
            }
        });

    }



}