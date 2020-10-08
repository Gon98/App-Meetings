package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MeetingActivity extends AppCompatActivity {

    private EditText etCompany;
    private EditText etDataTime;
    private EditText etParameters;
    private Button etMeetingButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final String TAG = "MeetingActivity";
    private String company, dataTime, parameters;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        etCompany = findViewById(R.id.company);
        etDataTime = findViewById(R.id.dataTime);
        etParameters = findViewById(R.id.parameters);
        etMeetingButton = findViewById(R.id.meetingButton);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        registerMeeting();
        getData();
        setData();


    }


    //Verificação dos dados vazios
    private boolean isStringEmpty(String inputString){
        if (inputString.isEmpty() || inputString == null){
            return true;
        }else {
            return false;
        }
    }

    //Criar uma nova reunião
    private void registerMeeting(){
        etMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String company = etCompany.getText().toString();
                final String dataTime = etDataTime.getText().toString();
                final String parameters = etParameters.getText().toString();

                //Verificar se os campos estão vazios
                if(isStringEmpty(company) || isStringEmpty(dataTime) || isStringEmpty(parameters)){
                    Toast.makeText(MeetingActivity.this, getString(R.string.blank_spaces), Toast.LENGTH_SHORT).show();


                } else{
                    //Fazer registo na BD
                    //FirebaseUser meeting = mAuth.getCurrentUser();
                    createMeetingDatabase(company, dataTime, parameters);
                    Intent intent = new Intent(MeetingActivity.this, HomeActivity.class);
                    startActivity(intent);
                    Toast.makeText(MeetingActivity.this, "The meeting was successfully registered!", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }



    //Função para criar uma nova reunião
    private void createMeetingDatabase(String company, String dataTime, String parameters){
        String userID = mAuth.getCurrentUser().getUid();
        String key = mDatabase.push().getKey();
        Meeting meeting = new  Meeting(key,company, dataTime, parameters);
        mDatabase.child("meeting").child(userID).child(key).setValue(meeting);
    }



    //Função para mandar o dados da reunião selecionada para a activity_meeting
    private void getData() {
       if(getIntent().hasExtra("Company") && getIntent().hasExtra("DataTime") && getIntent().hasExtra("Parameters")){
            company = getIntent().getStringExtra("Company");
           dataTime = getIntent().getStringExtra("DataTime");
           parameters = getIntent().getStringExtra("Parameters");
        }
    }

    private void setData(){
        etCompany.setText(company);
        etDataTime.setText(dataTime);
        etParameters.setText(parameters);
    }


}
