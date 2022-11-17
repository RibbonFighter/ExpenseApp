package com.example.touringapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.touringapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


public class DashboardFragment extends Fragment {

    //floating button
    private FloatingActionButton fab_dash_btn;
    private FloatingActionButton fab_inc_btn;
    private FloatingActionButton fab_exp_btn;

    //floating button text view
    private TextView fab_inc_txt;
    private TextView fab_exp_txt;

    //boolean
    private boolean isOpen=false;

    //animation
    private Animation fadeOpen,fadeClose;

    //firebase
    private FirebaseAuth auth;
    private DatabaseReference incomeDatabase;
    private DatabaseReference expenseDatabase;

    //dashboard inc and exp
    private TextView total_expense_amount;
    private TextView total_income_amount;

    //recyclerview
    private RecyclerView recyclerIncome;
    private RecyclerView recyclerExpense;

    //adapter
    private FirebaseRecyclerAdapter incomeAdapter;
    private FirebaseRecyclerAdapter expenseAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview =  inflater.inflate(R.layout.fragment_dashboard, container, false);

        //database
        auth=FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();

        incomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        expenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        //connect float btn to layout
        fab_dash_btn=myview.findViewById(R.id.main_plus_float_btn);
        fab_inc_btn=myview.findViewById(R.id.income_float_btn);
        fab_exp_btn=myview.findViewById(R.id.expense_float_btn);

        //connect float text
        fab_inc_txt=myview.findViewById(R.id.income_float_text);
        fab_exp_txt=myview.findViewById(R.id.expense_float_text);

        //connect exp and inc total
        total_income_amount=myview.findViewById(R.id.income_setNum);
        total_expense_amount=myview.findViewById(R.id.expense_setNum);

        //connect recycler inc and exp
        recyclerIncome=myview.findViewById(R.id.recycler_income);
        recyclerExpense=myview.findViewById(R.id.recycler_expense);

        //animation
        fadeOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_anim_open);
        fadeClose= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_anim_close);



        fab_dash_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDataInsert();
                if(isOpen){
                    fab_inc_btn.startAnimation(fadeClose);
                    fab_exp_btn.startAnimation(fadeClose);
                    fab_inc_btn.setClickable(false);
                    fab_exp_btn.setClickable(false);

                    fab_inc_txt.startAnimation(fadeClose);
                    fab_exp_txt.startAnimation(fadeClose);
                    fab_inc_txt.setClickable(false);
                    fab_exp_txt.setClickable(false);
                    isOpen=false;
                } else {
                    fab_inc_btn.startAnimation(fadeOpen);
                    fab_exp_btn.startAnimation(fadeOpen);
                    fab_inc_btn.setClickable(true);
                    fab_exp_btn.setClickable(true);

                    fab_inc_txt.startAnimation(fadeOpen);
                    fab_exp_txt.startAnimation(fadeOpen);
                    fab_inc_txt.setClickable(true);
                    fab_exp_txt.setClickable(true);
                    isOpen=true;
                }
            }
        });

        //calc total income
        incomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int total_inc_sum = 0;

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Data data=dataSnapshot.getValue(Data.class);

                    total_inc_sum+=data.getAmount();

                    String str_inc_result=String.valueOf(total_inc_sum);

                    total_income_amount.setText(str_inc_result+".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //calc total expense
        expenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total_exp_sum = 0;

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Data data=dataSnapshot.getValue(Data.class);

                    total_exp_sum+=data.getAmount();

                    String str_exp_result=String.valueOf(total_exp_sum);
                    total_expense_amount.setText(str_exp_result+".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //recycler view
        LinearLayoutManager linearLayoutManagerInc = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        linearLayoutManagerInc.setStackFromEnd(true);
        linearLayoutManagerInc.setReverseLayout(true);
        recyclerIncome.setHasFixedSize(true);
        recyclerIncome.setLayoutManager(linearLayoutManagerInc);

        LinearLayoutManager linearLayoutManagerExp = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        linearLayoutManagerExp.setStackFromEnd(true);
        linearLayoutManagerExp.setReverseLayout(true);
        recyclerExpense.setHasFixedSize(true);
        recyclerExpense.setLayoutManager(linearLayoutManagerExp);

        FirebaseRecyclerOptions<Data> incomeOptions =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(incomeDatabase, Data.class)
                        .build();

        incomeAdapter = new FirebaseRecyclerAdapter<Data,incomeViewHolder>(incomeOptions) {
            @Override
            protected void onBindViewHolder(@NonNull incomeViewHolder holder, int position, @NonNull Data model) {
                holder.setIncomeType(model.getType());
                holder.setIncomeAmount(model.getAmount());
                holder.setIncomeDate(model.getDate());
            }


            @NonNull
            @Override
            public incomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new incomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income,parent,false));
            }
        };
        recyclerIncome.setAdapter(incomeAdapter);

        FirebaseRecyclerOptions<Data> expenseOptions =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(expenseDatabase,Data.class)
                        .build();

        expenseAdapter = new FirebaseRecyclerAdapter<Data,expenseViewHolder>(expenseOptions) {
            @Override
            protected void onBindViewHolder(@NonNull expenseViewHolder holder, int position, @NonNull Data model) {
                holder.setExpenseType(model.getType());
                holder.setExpenseAmount(model.getAmount());
                holder.setExpenseDate(model.getDate());
            }

            @NonNull
            @Override
            public expenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new expenseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense,parent,false));
            }
        };
        recyclerExpense.setAdapter(expenseAdapter);


        return myview;
    }

    //Floating button animation
    private void ftAnimation(){
        if (isOpen){

            fab_inc_btn.startAnimation(fadeClose);
            fab_exp_btn.startAnimation(fadeClose);
            fab_inc_btn.setClickable(false);
            fab_exp_btn.setClickable(false);

            fab_inc_txt.startAnimation(fadeClose);
            fab_exp_txt.startAnimation(fadeClose);
            fab_inc_txt.setClickable(false);
            fab_exp_txt.setClickable(false);
            isOpen=false;

        }else {
            fab_inc_btn.startAnimation(fadeOpen);
            fab_exp_btn.startAnimation(fadeOpen);
            fab_inc_btn.setClickable(true);
            fab_exp_btn.setClickable(true);

            fab_inc_txt.startAnimation(fadeOpen);
            fab_exp_txt.startAnimation(fadeOpen);
            fab_inc_txt.setClickable(true);
            fab_exp_txt.setClickable(true);
            isOpen=true;

        }
    }

    private void addDataInsert(){
        //float btn income
        fab_inc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incomeDataInsert();
            }
        });

        //float btn expense
        fab_exp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseDataInsert();
            }
        });
    }

    public void incomeDataInsert(){
        AlertDialog.Builder mydialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.insert_data_form, null);
        mydialog.setView(myview);
        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        EditText editAmount=myview.findViewById(R.id.amount_edit);
        EditText editType=myview.findViewById(R.id.type_edit);
        EditText editDescription=myview.findViewById(R.id.desc_edit);

        Button save_btn=myview.findViewById(R.id.save_btn);
        Button cancel_btn=myview.findViewById(R.id.cancel_btn);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = editType.getText().toString().trim();
                String amount = editAmount.getText().toString().trim();
                String description = editDescription.getText().toString().trim();


                if(TextUtils.isEmpty(type)){
                    editType.setError("Type is required!");
                    return;
                }
                if(TextUtils.isEmpty(amount)){
                    editAmount.setError("Amount is required!");
                    return;
                }
                int totalamount = Integer.parseInt(amount);

                if(TextUtils.isEmpty(description)){
                    editDescription.setError("Description is required!");
                    return;
                }

                String id = incomeDatabase.push().getKey();

                String date = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(totalamount,type,description,date,id);

                incomeDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Data added successfully!", Toast.LENGTH_SHORT).show();
                ftAnimation();
                dialog.dismiss();

            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void expenseDataInsert(){
        AlertDialog.Builder mydialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.insert_data_form, null);
        mydialog.setView(myview);
        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        EditText editAmount=myview.findViewById(R.id.amount_edit);
        EditText editType=myview.findViewById(R.id.type_edit);
        EditText editDescription=myview.findViewById(R.id.desc_edit);

        Button save_btn=myview.findViewById(R.id.save_btn);
        Button cancel_btn=myview.findViewById(R.id.cancel_btn);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtAmount=editAmount.getText().toString().trim();
                String txtType=editType.getText().toString().trim();
                String txtDesc=editDescription.getText().toString().trim();

                if(TextUtils.isEmpty(txtAmount)){
                    editType.setError("Type is required!");
                    return;
                }

                int totalexpamount = Integer.parseInt(txtAmount);
                if(TextUtils.isEmpty(txtAmount)){
                    editAmount.setError("Amount is required!");
                    return;
                }
                if(TextUtils.isEmpty(txtDesc)){
                    editDescription.setError("Description is required!");
                    return;
                }

                String id = expenseDatabase.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(totalexpamount,txtType,txtDesc,date,id);
                expenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Expense added successfully", Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        incomeAdapter.startListening();
        expenseAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        incomeAdapter.stopListening();
        expenseAdapter.stopListening();
    }

    //income data
    public static class incomeViewHolder extends RecyclerView.ViewHolder{

        View incomeView;
        public incomeViewHolder(@NonNull View itemView) {
            super(itemView);
            incomeView=itemView;
        }

        public void setIncomeType(String type){
            TextView mType= incomeView.findViewById(R.id.type_income_dash);
            mType.setText(type);
        }
        public void setIncomeAmount(int amount){
            TextView mAmount= incomeView.findViewById(R.id.amount_income_dash);
            String str_amount= String.valueOf(amount);
            mAmount.setText(str_amount);
        }
        public void setIncomeDate(String date){
            TextView mDate= incomeView.findViewById(R.id.date_income_dash);
            mDate.setText(date);
        }

    }
    //expense data
    public static class expenseViewHolder extends RecyclerView.ViewHolder{

        View expenseView;
        public expenseViewHolder(@NonNull View itemView) {
            super(itemView);
            expenseView=itemView;
        }
        public void setExpenseType(String type){
            TextView mType= expenseView.findViewById(R.id.type_expense_dash);
            mType.setText(type);
        }
        public void setExpenseAmount(int amount){
            TextView mAmount= expenseView.findViewById(R.id.amount_expense_dash);
            String str_amount= String.valueOf(amount);
            mAmount.setText(str_amount);
        }
        public void setExpenseDate(String date){
            TextView mDate= expenseView.findViewById(R.id.date_expense_dash);
            mDate.setText(date);
        }


    }

}