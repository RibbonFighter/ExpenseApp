package com.example.touringapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.touringapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class IncomeFragment extends Fragment {

    //firebase
    private FirebaseAuth auth;
    private DatabaseReference incomeDatabase;

    //recycler
    private RecyclerView recyclerView;

    //adapter
    private FirebaseRecyclerAdapter adapter;

    //Text view
    private TextView incomeTotalSum;

    //update text
    private EditText editAmount;
    private EditText editType;
    private EditText editDescription;

    //btn of upd and del
    private Button btnUpdate;
    private Button btnDelete;

    //data value
    private String type;
    private String description;
    private int amount;
    private String post_Key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_income, container, false);

        auth=FirebaseAuth.getInstance();

        FirebaseUser firebaseUser=auth.getCurrentUser();
        String uid = firebaseUser.getUid();

        incomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        recyclerView = myview.findViewById(R.id.recycler_income_id);

        incomeTotalSum=myview.findViewById(R.id.income_rs_txt);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(incomeDatabase, Data.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Data,MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Data model) {
                holder.setAmount(model.getAmount());
                holder.setType(model.getType());
                holder.setDescription(model.getDescription());
                holder.setDate(model.getDate());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_Key=getRef(position).getKey();

                        type=model.getType();
                        description=model.getDescription();
                        amount=model.getAmount();

                        updateDataItem();
                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_income_data,parent,false));
            }
        };
        recyclerView.setAdapter(adapter);

        incomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total_amount_value=0;

                for (DataSnapshot mySnapshot:snapshot.getChildren()){
                    Data data = mySnapshot.getValue(Data.class);

                    total_amount_value+=data.getAmount();

                    String total_str_amount = String.valueOf(total_amount_value);

                    incomeTotalSum.setText(total_str_amount+".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();
         adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }
        private void setType(String type){
            TextView mType = mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }
        private void setDescription(String description){
            TextView mDescription = mView.findViewById(R.id.desc_txt_income);
            mDescription.setText(description);
        }
        private void setDate(String date){
            TextView mDate = mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }
        private void setAmount(int amount){
            TextView mAmount = mView.findViewById(R.id.amount_txt_income);
            String str_amount = String.valueOf(amount);
            mAmount.setText((str_amount));
        }
    }

    private void updateDataItem(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myView = inflater.inflate(R.layout.update_data_item,null);
        myDialog.setView(myView);

        editAmount=myView.findViewById(R.id.amount_edit);
        editType=myView.findViewById(R.id.type_edit);
        editDescription=myView.findViewById(R.id.desc_edit);

        editType.setText(type);
        editType.setSelection(type.length());

        editDescription.setText(description);
        editDescription.setSelection(description.length());

        editAmount.setText(String.valueOf(amount));
        editAmount.setSelection(String.valueOf(amount).length());

        btnUpdate=myView.findViewById(R.id.update_btn);
        btnDelete=myView.findViewById(R.id.delete_btn);

        AlertDialog alertDialog = myDialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type=editType.getText().toString().trim();
                description=editDescription.getText().toString().trim();

                String updAmount= String.valueOf(amount);
                updAmount=editAmount.getText().toString().trim();
                int myAmount=Integer.parseInt(updAmount);

                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(myAmount,type,description,mDate,post_Key);

                incomeDatabase.child(post_Key).setValue(data);
                alertDialog.dismiss();;

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incomeDatabase.child(post_Key).removeValue();

                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

}