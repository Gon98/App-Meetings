package com.example.myproject;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private EditText etName;
    private Button editButton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etName = findViewById(R.id.editTextName);
        editButton = findViewById(R.id.editButton);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        getDatabaseUser();




    }


    //Função que vai buscar os dados do user
    private void getDatabaseUser(){

        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Vai buscar o nome a BD
        mDatabase.child("user").child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String userName = dataSnapshot.child("name").getValue().toString();

                EditText tx = findViewById(R.id.editTextName);

                tx.setText(" " + userName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Vai buscar o email a BD
        mDatabase.child("user").child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String userEmail = dataSnapshot.child("email").getValue().toString();

                TextView tx = findViewById(R.id.textViewEmail);

                tx.setText(" " + userEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //Verificação dos dados vazios
    private boolean isStringEmpty(String inputString){
        if (inputString.isEmpty() || inputString == null){
            return true;
        }else {
            return false;
        }
    }


    //Função de update
    private void updateData() {
        final String name = etName.getText().toString();
        String userID = mAuth.getCurrentUser().getUid();
        DatabaseReference myref = FirebaseDatabase.getInstance().getReference();

        myref.child("user").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef()
                        .child("name")
                        .setValue(name);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }

        });

    }

    //Modal para confirmar o editar nome
    public void showAlertDialog(View v) {

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Android Studio");
            alert.setMessage("Are you sure?");

            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    final String name = etName.getText().toString();
                    if (!isStringEmpty(name)) {

                        updateData();
                        Toast.makeText(ProfileActivity.this, "The name has been successfully changed!", Toast.LENGTH_SHORT).show();
                    } else{

                        Toast.makeText(ProfileActivity.this, getString(R.string.blank_spaces), Toast.LENGTH_SHORT).show();
                    }

                }
            });

            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {

                }
            });
            alert.create().show();

    }
}
