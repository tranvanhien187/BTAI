package com.example.btai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ImageView img = findViewById(R.id.img);
        Button btn = findViewById(R.id.btn);

        SharedPreferences sharedPreferences = getSharedPreferences("AI",MODE_PRIVATE);
        String base64 = sharedPreferences.getString("BASE64",null);

        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        img.setImageBitmap(decodedByte);

        btn.setOnClickListener(view->{
            MediaStore.Images.Media.insertImage(getContentResolver(), decodedByte, base64.substring(2,10) , "yourDescription");
            MediaStore.Images.Media.insertImage(getContentResolver(), decodedByte, "yourTitle" , "yourDescription");
        });



    }
}