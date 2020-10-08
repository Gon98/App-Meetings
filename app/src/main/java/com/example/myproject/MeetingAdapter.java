package com.example.myproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static androidx.core.content.ContextCompat.startActivity;
import static java.sql.DriverManager.println;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.ViewHolder>{

    private List<Meeting> meetingList;
    private Context context;
    private int itemPostition;
    private DatabaseReference mDatabase;


    public MeetingAdapter(List<Meeting> MeetingList, Context context){
        this.meetingList = MeetingList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout, parent, false);

        return new ViewHolder(itemLista);
    }


    //Vai buscar os dados e passar para a row_layout
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        itemPostition = position;
        final Meeting removeItem = meetingList.get(itemPostition);
        final Meeting meeting = meetingList.get(itemPostition);
        holder.companyTxt.setText(meeting.getCompany());
        holder.dataTimeTxt.setText(meeting.getDataTime());


        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MeetingActivity.class);
                intent.putExtra("Company", meeting.company);
                intent.putExtra("DataTime", meeting.dataTime);
                intent.putExtra("Parameters", meeting.parameters);
                // manda posição  position
                intent.putExtra("RemoveItem", removeItem.toString());
                context.startActivity(intent);
            }
        });

         //Aapagar os dados
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position);
            }
        });

    }

     //Funçao para apagar dados
    public void removeItem(int postitionDelete){
        Intent intent = new Intent(context, HomeActivity.class);
        Meeting removeItem = meetingList.get(postitionDelete);
        removeItem.RemoverMeeting();
        notifyItemRemoved(postitionDelete);
        context.startActivity(intent);
    }

    //Numero de rows da tabela
    @Override
    public int getItemCount() {
        return meetingList.size();
    }


    //Definir as variaveis e as respetivas ligações ao layout
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView companyTxt;
        private TextView dataTimeTxt;
        ConstraintLayout mainLayout;
        private Button deleteButton;
        private Button editButton;

        public ViewHolder(View itemView) {
            super(itemView);
            companyTxt = itemView.findViewById(R.id.textView_company);
            dataTimeTxt = itemView.findViewById(R.id.textView_dataTime);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            deleteButton = itemView.findViewById(R.id.btn_delete);
            editButton = itemView.findViewById(R.id.ViewButton);
        }
    }





}

