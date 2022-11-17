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

public class ExpenseFragment extends Fragment {

    //firebase
    private FirebaseAuth auth;
    private DatabaseReference expenseDatabase;

    //recyclerview
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;

    //Text view
    private TextView expenseTotalSum;

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
        View myview = inflater.inflate(R.layout.fragment_expense, container, false);

        auth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        String uid = firebaseUser.getUid();

        expenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        recyclerView=myview.findViewById(R.id.recycler_expense_id);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity());

        expenseTotalSum=myview.findViewById(R.id.expense_rs_txt);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(expenseDatabase,Data.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Data,myViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Data model) {
                holder.setAmount(model.getAmount());
                holder.setType(model.getType());
                holder.setDescription(model.getDescription());
                holder.setDate(model.getDate());

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_Key=getRef(position).getKey();
                        type=model.getType();
                        description= model.getDescription();
                        amount=model.getAmount();

                        updateDataItem();
                    }
                });
            }

            @NonNull
            @Override
            public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new myViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_expense_data,parent,false));
            }
        };
        recyclerView.setAdapter(adapter);

        expenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total_amount_value=0;

                for (DataSnapshot myDataSnapshot:snapshot.getChildren()){
                    Data data = myDataSnapshot.getValue(Data.class);

                    total_amount_value+=data.getAmount();

                    String total_str_amount = String.valueOf(total_amount_value);

                    expenseTotalSum.setText(total_str_amount+".00");
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

    public static class myViewHolder extends RecyclerView.ViewHolder{

        View view;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
        }

        private void setType(String type){
            TextView mType = view.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }
        private void setDescription(String description){
            TextView mDescription = view.findViewById(R.id.desc_txt_expense);
            mDescription.setText(description);
        }
        private void setDate(String date){
            TextView mDate = view.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }
        private void setAmount(int amount){
            TextView mAmount = view.findViewById(R.id.amount_txt_expense);
            String str_amount = String.valueOf(amount);
            mAmount.setText((str_amount));
        }
    }

    private void updateDataItem(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myView = inflater.inflate(R.layout.update_data_item,null);
        mydialog.setView(myView);

        editAmount = myView.findViewById(R.id.amount_edit);
        editType = myView.findViewById(R.id.type_edit);
        editDescription = myView.findViewById(R.id.desc_edit);

        editType.setText(type);
        editType.setSelection(type.length());

        editDescription.setText(description);
        editDescription.setSelection(description.length());

        editAmount.setText(String.valueOf(amount));
        editAmount.setSelection(String.valueOf(amount).length());


        btnUpdate = myView.findViewById(R.id.update_btn);
        btnDelete = myView.findViewById(R.id.delete_btn);

        AlertDialog dialog = mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type= editType.getText().toString().trim();
                description= editDescription.getText().toString().trim();

                String updAmount= String.valueOf(amount);
                updAmount=editAmount.getText().toString().trim();

                int myAmount=Integer.parseInt(updAmount);
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(myAmount,type,description,mDate,post_Key);
                expenseDatabase.child(post_Key).setValue(data);
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseDatabase.child(post_Key).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();;
    }

}