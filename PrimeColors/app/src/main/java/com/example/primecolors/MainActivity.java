package com.example.primecolors;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private int PICK_IMAGE_REQUEST = 1;
    private int TAKE_PICTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Removes big ActionBar from the top of the application
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);

        //Checks and asks for storage permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        //Button for opening the camera and taking a picture
        Button myCam = (Button) findViewById(R.id.takePic) ;
        myCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent3 = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                startActivityForResult(Intent3,TAKE_PICTURE);
            }

        });

        //Button to load a picture from the Gallery
        Button load = (Button) findViewById(R.id.loadPic) ;
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();

            }

        });
        }


        //Grants all the necessary permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    //Functions to choose an image from the gallery
    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //Function to find all colors in the chosen image from the gallery
    public void findColors(Bitmap b) {
        Palette p = Palette.from(b).generate();

        Palette.Swatch psVibrant = p.getVibrantSwatch();
        Palette.Swatch psVibrantLight = p.getLightVibrantSwatch();
        Palette.Swatch psVibrantDark = p.getDarkVibrantSwatch();
        Palette.Swatch psMuted = p.getMutedSwatch();
        Palette.Swatch psMutedLight = p.getLightMutedSwatch();
        Palette.Swatch psMutedDark = p.getDarkMutedSwatch();

        List<Palette.Swatch> pss;
        pss = p.getSwatches();

        for(int j = 0; j< pss.size(); j++) {
            Palette.Swatch ps = pss.get(j);
            float color = ps.getRgb();
            int population = ps.getPopulation();
            float[] hsl = ps.getHsl();
            int bodyTextColor = ps.getBodyTextColor();
            int titleTextColor = ps.getTitleTextColor();

            //Creates a new layout which contains the colors of the image as a colored rectangle and as a hexstring
            LinearLayout myLayout = findViewById(R.id.linLayout);
            RelativeLayout rLayout = new RelativeLayout(this);
            ImageView im = new ImageView(this);
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,200);
            im.setLayoutParams(parms);
            im.setBackgroundColor(ps.getRgb());
            String hexColor = String.format("#%06X", (0xFFFFFF & ps.getRgb()));
            TextView txt = new TextView(this);
            txt.setText("Color: "+hexColor);
            txt.setTextSize(20);
            txt.setTextColor(Color.WHITE);
            rLayout.addView(im);
            rLayout.addView(txt);
            myLayout.addView(rLayout);
        }
    }

    //Loads the selected Image as an Bitmap for the color scan
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                ImageView imageView = findViewById(R.id.loadedImage);
                imageView.setImageBitmap(bitmap);

                //Removes the colors in the layout from previous scans
                LinearLayout myLayout = findViewById(R.id.linLayout);
                while(myLayout.getChildCount()>3) {
                    for (int i = 0; i < myLayout.getChildCount(); i++) {
                        int deleteCnt = myLayout.getChildCount() - 1;
                        if (deleteCnt > 2) {
                            myLayout.removeViewAt(deleteCnt);
                        }

                    }
                }
                findColors(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode ==TAKE_PICTURE) {
            chooseImage();
        }
    }

    @Override
    public void onClick(View v) {
    }
}
