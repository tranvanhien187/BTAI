package com.example.btai;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.btai.remote.ConfigService;
import com.example.btai.remote.RemoteService;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private static final String[] PERMISSIONS_LIST = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_ACCESS = 102;
    static final int REQUEST_IMAGE_OPEN = 2;
    private ImageView imgPicker;
    private Button btn;
    private EditText edt;
    private File file;
    private LinearLayout layout1;
    private LinearLayout layout2;
    private LinearLayout layout3;
    private LinearLayout layout4;
    private ImageView imgStyle1;
    private ImageView imgStyle2;
    private ImageView imgStyle3;
    private ImageView imgStyle4;
    private RemoteService remoteService;

    private ProgressBar progressBar;

    private int style =-1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgPicker = findViewById(R.id.img_picker);
        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        layout4 = findViewById(R.id.layout4);

        imgStyle1 = findViewById(R.id.img_style1);
        imgStyle2 = findViewById(R.id.img_style2);
        imgStyle3 = findViewById(R.id.img_style3);
        imgStyle4 = findViewById(R.id.img_style4);

        progressBar = findViewById(R.id.progress);


        btn = findViewById(R.id.btn_create_image);
        edt = findViewById(R.id.edt_url);
        imgPicker.setOnClickListener(view->{
            getImageFromGallery();
        });


        btn.setOnClickListener(view->{

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                &&(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    &&(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            ) {
                // result  is your block of code
                callApi();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_LIST, REQUEST_ACCESS);
            }

//            service.updateProfile(id, fullName, body, other);
        });

        imgStyle1.setOnClickListener(view->{
            layout2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            layout3.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            layout4.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            layout1.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
            style = 1;
        });
        imgStyle2.setOnClickListener(view->{
            layout3.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            layout4.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            layout1.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            layout2.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
            style = 2;
        });
        imgStyle3.setOnClickListener(view->{
            layout4.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            layout1.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            layout2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            layout3.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
            style = 3;
        });
        imgStyle4.setOnClickListener(view->{
            layout1.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            layout2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            layout3.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            layout4.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
            style = 4;
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_ACCESS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                      callApi();
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {

            } else {

            }
        }
    }

    private void callApi(){
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        RequestBody styleBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), style+"");
        if(style == -1){
            Toast.makeText(this, "Vui long chon kieu anh", Toast.LENGTH_SHORT).show();
            return;
        }
        remoteService = ConfigService.getRemoteService(edt.getText().toString());

        remoteService.getArtImage(body,styleBody).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                if(response.code()==200){
                    String base64 = response.body().getResult();
                    base64 = base64.substring(2,base64.length()-3);

                    SharedPreferences sharedPreferences = getSharedPreferences("AI",MODE_PRIVATE);
                    if(sharedPreferences.edit().putString("BASE64",base64).commit()){
                        Intent intent = new Intent(MainActivity.this,ImageActivity.class);
                        startActivity(intent);
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("AAA","error" + t.getMessage());
                progressBar.setVisibility(View.GONE);

            }
        });

        progressBar.setVisibility(View.VISIBLE);
    }

    private void getImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_OPEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_OPEN && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();
            imgPicker.setImageURI(selectedImage);
            file = new File(getRealPathFromURI(selectedImage));
            Log.d("AAA","finalFile " + file);
        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "image", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}