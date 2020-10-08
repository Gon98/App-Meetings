package com.example.myproject;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Meeting {
     String idMeeting;
    String company;
    String dataTime;
    String parameters;


    //Construtor
    public Meeting(String idMeeting, String company, String dataTime, String parameters) {
        this.idMeeting = idMeeting;
        this.company = company;
        this.dataTime = dataTime;
        this.parameters = parameters;
    }


    public String getIdMeeting() {
        return idMeeting;
    }

    public void setIdMeeting(String idMeeting) {
        this.idMeeting = idMeeting;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public Meeting() {}
    @Override
    public String toString() {
        return "Meeting " +
                "\nCompany: " + company +
                "\nData/Time: " + dataTime;
    }

    //Função apagar dados
    public void RemoverMeeting(){
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference postagemRef = firebaseRef.child("meeting")
                .child(currentuser)
                .child(getIdMeeting());
        postagemRef.removeValue();

    }
}
