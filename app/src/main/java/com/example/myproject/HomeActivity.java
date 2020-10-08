package com.example.myproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;

    //ListView lvMeeting;
    ArrayList<Meeting> arrayList = new ArrayList<Meeting>();
    ArrayAdapter<String> arrayAdapter;
    private RecyclerView recyclerView;
    private MeetingAdapter adaptermeeting;
    private List<Meeting> meetingList = new ArrayList<>();




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDatabase = FirebaseDatabase.getInstance().getReference("meeting");
        FirebaseUser meeting = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView =  findViewById(R.id.myRecyclerView);



        // config o adaptar
        adaptermeeting = new MeetingAdapter(meetingList, this);

        //config recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        LinearLayoutManager mLayoutManager = (LinearLayoutManager) layoutManager;
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adaptermeeting);


        DatabaseReference meetingtRef = mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        meetingtRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              // arrayList.clear();
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Meeting meetingNew = dados.getValue(Meeting.class);
                    meetingList.add(meetingNew);
                    Log.d("Get Data", "ms2:"+ meetingNew.getCompany() + meetingNew.getDataTime());

                }

                adaptermeeting.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("failed: " , "msg:" + databaseError.getMessage());
            }
        });


        //HomeActivity -----> ProfileActivity (profileButton)
        TextView btnProfile =  findViewById(R.id.profileButton);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        //HomeActivity -----> MeetingActivity (createButton)
        TextView btnMeeting =  findViewById(R.id.createButton);
        btnMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MeetingActivity.class);
                startActivity(intent);
            }
        });


    }

    //Modal para dar sing out
    public void showAlertDialog(View v) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Android Studio");
        alert.setMessage("Want to sign out?");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                FirebaseAuth.getInstance().signOut();
                finish();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
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
