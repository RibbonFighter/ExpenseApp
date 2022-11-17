package com.example.touringapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.touringapp.Model.Trip;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class TripDashFragment extends Fragment {

    //floating button
    private FloatingActionButton fab_create_outer;
    private FloatingActionButton fab_create_btn;

    //floating button text view
    private TextView fab_create_txt;

    //datepicker
    private DatePickerDialog picker;

    //firebase
    private FirebaseAuth auth;
    private DatabaseReference tripDatabase;

    //animation
    private Animation fadeOpen,fadeClose;

    //boolean
    private boolean isOpen=false;

    //recycler
    private RecyclerView trip_recycler_list;

    //adapter
    private FirebaseRecyclerAdapter tripAdapter;

    //update text
    private EditText editTrip;
    private EditText editDestination;
    private EditText editDate;
    private EditText editDesc;
    private RadioGroup editRiskRadioGroup;
    private RadioButton yesChoice;
    private RadioButton noChoice;

    //btn of upd and del
    private Button btnUpdate;
    private Button btnDelete;

    //trip value
    private String name;
    private String destination;
    private String date;
    private String description;
    private String risk;
    private String post_Key;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_trip_dash, container, false);

        //database
        auth=FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();

        tripDatabase = FirebaseDatabase.getInstance().getReference().child("TripData").child(uid);


        //connect recycler inc and exp
        trip_recycler_list=myView.findViewById(R.id.recycler_trip_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        trip_recycler_list.setHasFixedSize(true);
        trip_recycler_list.setLayoutManager(linearLayoutManager);

        //connect float btn to layout
        fab_create_outer=myView.findViewById(R.id.open_create_form_btn);
        fab_create_btn=myView.findViewById(R.id.create_float_btn);

        //connect float text
        fab_create_txt=myView.findViewById(R.id.create_float_text);

        //connect recycler
        trip_recycler_list=myView.findViewById(R.id.recycler_trip_list);

        //animation
        fadeOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_anim_open);
        fadeClose= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_anim_close);

        fab_create_outer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTripInsert();
                if (isOpen) {
                    fab_create_btn.startAnimation(fadeClose);
                    fab_create_btn.setClickable(false);

                    fab_create_txt.startAnimation(fadeClose);
                    fab_create_txt.setClickable(false);
                    isOpen=false;
                } else {
                    fab_create_btn.startAnimation(fadeOpen);
                    fab_create_btn.setClickable(true);

                    fab_create_txt.startAnimation(fadeOpen);
                    fab_create_txt.setClickable(true);
                    isOpen=true;
                }
            }
        });

        FirebaseRecyclerOptions<Trip> tripOptions =
            new FirebaseRecyclerOptions.Builder<Trip>()
                    .setQuery(tripDatabase,Trip.class)
                    .build();

        tripAdapter = new FirebaseRecyclerAdapter<Trip, tripViewwHolder>(tripOptions) {
            @Override
            protected void onBindViewHolder(@NonNull tripViewwHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Trip model) {
                holder.setTrip(model.getName());
                holder.setDestination(model.getDestination());
                holder.setDate(model.getDate());
                holder.setRisk(model.getRisk());
                holder.setDesc(model.getDesc());

                holder.tripView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_Key=getRef(position).getKey();

                        name=model.getName();
                        destination=model.getDestination();
                        date=model.getDate();
                        risk=model.getRisk();
                        description= model.getDesc();

                        tripDataUpdate();
                    }
                });
            }


            @NonNull
            @Override
            public tripViewwHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new tripViewwHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_trip_data,parent,false));
            }
        };
        trip_recycler_list.setAdapter(tripAdapter);

        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();
        tripAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        tripAdapter.stopListening();
    }

    //Floating button animation
    private void ftAnimation(){
        if (isOpen) {
            fab_create_btn.startAnimation(fadeClose);
            fab_create_btn.setClickable(false);

            fab_create_txt.startAnimation(fadeClose);
            fab_create_txt.setClickable(false);
            isOpen=false;
        } else {
            fab_create_btn.startAnimation(fadeOpen);
            fab_create_btn.setClickable(true);

            fab_create_txt.startAnimation(fadeOpen);
            fab_create_txt.setClickable(true);
            isOpen=true;
        }
    }

    private void addTripInsert(){
        fab_create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tripDataInsert();
            }
        });
    }


    public void tripDataInsert(){
        AlertDialog.Builder mydialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.insert_trip_form, null);
        mydialog.setView(myview);
        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        //edit text
        EditText editTrip = myview.findViewById(R.id.trip_edit);
        EditText editDestination = myview.findViewById(R.id.destination_edit);
        EditText editDate = myview.findViewById(R.id.datepicker_trip);
        EditText editDescription = myview.findViewById(R.id.desc_edit);

        //radio button for risk
        RadioGroup editRiskRadioGroup = myview.findViewById(R.id.risk_radio_group);
        RadioButton radioYes = myview.findViewById(R.id.yesChoice);
        RadioButton radioNo = myview.findViewById(R.id.noChoice);
        editRiskRadioGroup.clearCheck();

        Button save_btn=myview.findViewById(R.id.save_btn);
        Button cancel_btn=myview.findViewById(R.id.cancel_btn);

        //setting datepicker for edit text
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(TripDashFragment.super.getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayofMonth) {
                        editDate.setText(dayofMonth + "/" + (month+1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                //entered data
                String triptxt = editTrip.getText().toString().trim();
                String destination = editDestination.getText().toString().trim();
                String date = editDate.getText().toString().trim();
                String description = editDescription.getText().toString().trim();

                if (TextUtils.isEmpty(triptxt)) {
                    editTrip.setError("Trip is required!");
                    editTrip.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(destination)) {
                    editDestination.setError("Destination is required!");
                    editDestination.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(date)) {
                    editDate.setError("Date is required!");
                    editDate.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(description)) {
                    editDescription.setText("No Description.");
                    editDescription.requestFocus();
                    return;
                }

                String txtRisk = "";
                if (radioYes.isChecked()){
                    txtRisk= "Yes";
                } else if(radioNo.isChecked()) {
                    txtRisk="No";
                } else {
                    txtRisk = "Risk haven't been choose yet";
                }
                String id = tripDatabase.push().getKey();

                Trip trip = new Trip(triptxt,destination, date, txtRisk, description);
                tripDatabase.child(id).setValue(trip);
                Toast.makeText(getActivity(), "Trip added successfully!", Toast.LENGTH_SHORT).show();
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

    public void tripDataUpdate(){
        android.app.AlertDialog.Builder myDialog = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_trip_form,null);
        myDialog.setView(myview);

        //text
        editTrip = myview.findViewById(R.id.trip_edit);
        editDestination = myview.findViewById(R.id.desc_edit);
        editDate = myview.findViewById(R.id.datepicker_trip);
        editDesc = myview.findViewById(R.id.desc_edit);

        //radio
        editRiskRadioGroup = myview.findViewById(R.id.risk_radio_group);
        yesChoice = myview.findViewById(R.id.yesChoice);
        noChoice = myview.findViewById(R.id.noChoice);
        editRiskRadioGroup.clearCheck();

        //btn
        btnUpdate=myview.findViewById(R.id.update_btn);
        btnDelete=myview.findViewById(R.id.delete_btn);

        android.app.AlertDialog alertDialog = myDialog.create();

        //setting datepicker for edit text
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(TripDashFragment.super.getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayofMonth) {
                        editDate.setText(dayofMonth + "/" + (month+1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = editTrip.getText().toString().trim();
                destination = editDestination.getText().toString().trim();
                date = editDate.getText().toString().trim();
                description = editDesc.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    editTrip.setError("Trip is required!");
                    editTrip.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(destination)) {
                    editDestination.setError("Destination is required!");
                    editDestination.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(date)) {
                    editDate.setError("Date is required!");
                    editDate.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(description)) {
                    editDesc.setText("No Description.");
                    editDesc.requestFocus();
                    return;
                }

                risk = "";
                if (yesChoice.isChecked()){
                    risk= "Yes";
                } else if(noChoice.isChecked()) {
                    risk="No";
                } else {
                    risk = "Risk haven't been choose yet";
                }


                Trip trip = new Trip(name, destination,date,risk,description,post_Key);

                tripDatabase.child(post_Key).setValue(trip);
                alertDialog.dismiss();;
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tripDatabase.child(post_Key).removeValue();

                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }


    public static class tripViewwHolder extends RecyclerView.ViewHolder{

        View tripView;
        public tripViewwHolder(@NonNull View itemView) {
            super(itemView);
            tripView = itemView;
        }

        private void setTrip(String trip){
            TextView mTrip = tripView.findViewById(R.id.trip_txt_name);
            mTrip.setText(trip);
        }

        private void setDestination(String destination){
            TextView mDestination = tripView.findViewById(R.id.destination_txt_trip);
            mDestination.setText(destination);
        }

        private void setDate(String date){
            TextView mDate = tripView.findViewById(R.id.date_txt_trip);
            mDate.setText(date);
        }

        private void setRisk(String risk){
            TextView mRisk = tripView.findViewById(R.id.risk_txt_trip);
            mRisk.setText(risk);
        }
        private void setDesc(String desc){
            TextView mDesc = tripView.findViewById(R.id.desc_txt_trip);
            mDesc.setText(desc);
        }
    }

}