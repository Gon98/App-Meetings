package com.example.myproject;

import android.content.Intent;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private Button etRegisterButton;
    private TextView etShowHidePass;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.name);
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        etRegisterButton = findViewById(R.id.registerButton);
        etShowHidePass = findViewById(R.id.ShowHidePass);

        //Show ou Hide password
        etShowHidePass.setVisibility(View.GONE);
        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
             if(etPassword.getText().length() > 0){
                 etShowHidePass.setVisibility(View.VISIBLE);
             } else {
                 etShowHidePass.setVisibility(View.GONE);
             }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
       etShowHidePass.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
             if(etShowHidePass.getText() == "SHOW"){
               etShowHidePass.setText("HIDE");
               etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
               etPassword.setSelection(etPassword.length());
             } else{
                 etShowHidePass.setText("SHOW");
                 etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                 etPassword.setSelection(etPassword.length());
             }
          }
     });
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        registerUser();
    }


    //Verificação dos dados vazios
    private boolean isStringEmpty(String inputString){
        if (inputString.isEmpty() || inputString == null){
            return true;
        }else {
            return false;
        }
    }



    private void registerUser(){
  etRegisterButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        final String name = etName.getText().toString();
        final String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        //Se o email e a pass estiverem vazios && se a pass < 8 -> Tosta(ERRO)
        if(isStringEmpty(email) || isStringEmpty(password) || isStringEmpty(name)){
            Toast.makeText(RegisterActivity.this, getString(R.string.blank_spaces), Toast.LENGTH_SHORT).show();

        } else if(password.length() < 8){
            Toast.makeText(RegisterActivity.this, "Your password is weak!", Toast.LENGTH_SHORT).show();
        } else{
            //Vai registar o user na BD
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                //Verifica se o email é valido
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();
                        user.sendEmailVerification();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        createUserDatabase(name, email);
                        Toast.makeText(RegisterActivity.this, "Verify your email to continue!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.d(TAG, "onComplete: error: " + task.getException().toString());
                        Toast.makeText(RegisterActivity.this, "Couldn`t create an account with these credentials!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }
});

    }
    //Função para criar um novo utilizador
    private void createUserDatabase(String name, String email){
     String userID = mAuth.getCurrentUser().getUid();

     User user = new User(name, email);
     mDatabase.child("user").child(userID).setValue(user);
    }
}
