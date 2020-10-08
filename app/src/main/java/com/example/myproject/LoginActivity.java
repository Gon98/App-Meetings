package com.example.myproject;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private  EditText etPassword;
    private TextView etShowHidePass;
    private Button confirmButton;
    private SignInButton googleButton;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 500;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        confirmButton =  findViewById(R.id.confirmButton);
        googleButton = findViewById(R.id.googleButton);
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
        //Condição para show or hide password
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

        // Configurar o Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //Clicar em criar conta e ir para a pagina de registo da app
        TextView txtRegister =  findViewById(R.id.registerLink);
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //Activar a função
        authenticateUser();

    }

    //Condição para campos null  (isStringEmpty)
    private boolean isStringEmpty(String inputString){
        if (inputString.isEmpty() || inputString == null){
            return true;
        }else {
            return false;
        }
    }
       //Vai confirmar os dados do user, quando clicar no button login
       private  void authenticateUser(){
       confirmButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String email = etEmail.getText().toString();
               String password = etPassword.getText().toString();

               //Verificação de campos vazios
               if(!isStringEmpty(email) && !isStringEmpty(password)){
                   //Verificação dos dados para autenticação do user
                   mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful()){
                               FirebaseUser user = mAuth.getCurrentUser();
                               if(user.isEmailVerified()){
                                   Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                   startActivity(intent);
                                   Toast.makeText(LoginActivity.this, getString(R.string.authentication_complete), Toast.LENGTH_LONG).show();
                               } else{
                                   Toast.makeText(LoginActivity.this, "Check your inbox and verify your email!", Toast.LENGTH_LONG).show();
                               }
                           } else{
                               Toast.makeText(LoginActivity.this, getString(R.string.authentication_failed), Toast.LENGTH_LONG).show();
                           }
                       }
                   });

               }  else{
                   Toast.makeText(LoginActivity.this, getString(R.string.blank_spaces), Toast.LENGTH_SHORT).show();
               }
           }
       });

  }
    //Vai abrir a pagina para selecionar o gmail e deixa a ActivityLogin em pausa
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // O resultado vai returnar do Intent para GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Vai registar a conta google na BD
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            Toast.makeText(LoginActivity.this, getString(R.string.authentication_complete), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.authentication_failed), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


}
