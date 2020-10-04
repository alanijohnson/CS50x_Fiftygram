package edu.harvard.cs50.fiftygram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.RequestOptions;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.gpu.BrightnessFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.ContrastFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.PixelationFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SepiaFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SketchFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.ToonFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private ImageView imageView;
    private Button saveButton;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        saveButton = findViewById(R.id.saveButton);
        saveButtonClickable(false);

        // get permission from user to save images
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
    }

    public void choosePhoto(View v){
        // create an intent that opens documents specifically images
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    public void savePhoto(View v){
        //get image as bitmap from view
        if(imageView.getDrawable() == null){
            saveButtonClickable(false);
            return;
        }
        Log.d("save photo", String.valueOf(System.currentTimeMillis()));
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmapDrawable.getBitmap(),
                "Fiftygram_"+String.valueOf(System.currentTimeMillis()),"");
        saveButtonClickable(false);
    }

    public void applySepia(View v){
        apply(new SepiaFilterTransformation());
    }

    public void applyToon(View v){
        apply(new ToonFilterTransformation());
    }

    public void applySketch(View v){
        apply(new SketchFilterTransformation());
    }

    public void applyBlur(View v){
        apply(new BlurTransformation());
    }

    public void applyContrast(View v){
        apply(new ContrastFilterTransformation());
    }

    public void applyPixelation(View v){
        apply(new PixelationFilterTransformation());
    }

    public void applyVignette(View v){
        apply(new VignetteFilterTransformation(new PointF(0.5f,0.5f), new float[]{0.1f, 0.1f, 0.1f}, 0.2f, 0.8f));
    }

    public void applyBrightness(View v){
        apply(new BrightnessFilterTransformation(0.3f));
    }

    public void apply(Transformation<Bitmap> filter){
        Glide
                .with(this)
                .load(image)
                .apply(RequestOptions.bitmapTransform(filter))
                .into(imageView);
        saveButtonClickable(true);
    }

    private void saveButtonClickable(boolean b){

        Log.d("Save btn","Button is Clickable - "+ String.valueOf(b));
        int color;

        if(b){
            color = ContextCompat.getColor(getApplicationContext(),R.color.colorSave_click);
        } else{
            color = ContextCompat.getColor(getApplicationContext(),R.color.colorSave_noClick);
        }
        Log.d("Save btn", String.valueOf(color));
        saveButton.setBackgroundColor(color);
        saveButton.setClickable(b);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){

            try {
                // get path of data
                Uri uri = data.getData();
                // special class allowing file operations
                // open read only and wrapper for file descriptor
                // ParcelFileDescriptor
                // FileDescriptor
                ParcelFileDescriptor parcelFileDescriptor =
                        getContentResolver().openFileDescriptor(uri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                // return bitmap from file descriptor and set in imageView
                image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                imageView.setImageBitmap(image);
                //saveButtonClickable(true);
                // close file descriptor
                parcelFileDescriptor.close();
            } catch (FileNotFoundException e) {
                Log.e("cs50", "File not found", e);
            } catch (IOException e){
                Log.e("cs50", "IOException", e);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}