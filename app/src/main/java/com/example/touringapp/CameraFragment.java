package com.example.touringapp;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.Manifest;

import android.content.Intent;


import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


public class CameraFragment extends Fragment{

    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;

    private Button pic_btn;
    private ImageView image_pic;

    private Uri image_uri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_camera, container, false);
        pic_btn = myView.findViewById(R.id.picture_btn);
        image_pic = myView.findViewById(R.id.pic_holder);

        pic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getActivity().startActivityFromFragment(CameraFragment.this, cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });





        return myView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK && data != null) {

                    Bitmap bmp = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();



                    image_pic.setImageBitmap(bmp);

                }
            }
        }catch(Exception e){
            Toast.makeText(this.getActivity(), e+"Something went wrong", Toast.LENGTH_LONG).show();

        }
    }


}