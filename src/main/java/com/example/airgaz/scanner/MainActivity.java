package com.example.airgaz.scanner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.support.v4.app.Fragment;

import dmax.dialog.SpotsDialog;
import io.github.mthli.knife.KnifeText;

public class MainActivity extends FragmentActivity implements Tab1.OnFragmentInteractionListener,Tab2.OnFragmentInteractionListener {

    private String pictureImagePath = "";
    static final int camcode=100;
    static final int storecode=101;
    Uri uri;
    Intent CropIntent;
    ImageView ime;
    Bitmap bmap;

    Button im,btn,txt,more_vert;
    ProgressDialog dialog;
    EditText text;
    String str;
    Bundle gameData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        im=findViewById(R.id.btn_camera);
        btn=findViewById(R.id.btn_gallery);
        txt=findViewById(R.id.btn_text);
        ime=findViewById(R.id.imageView);
        more_vert=findViewById(R.id.morevert);
        dialog = new ProgressDialog(this);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Text"));
        tabLayout.addTab(tabLayout.newTab().setText("Image"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager)findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                im.startAnimation(myAnim);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                im.startAnimation(myAnim);
               CheckPerm();
               OpenCamera();

            }
        });

        im.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this,"Camera", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                btn.startAnimation(myAnim);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                btn.startAnimation(myAnim);
                CheckPerm();
                OpenGallery();
            }
        });

        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this,"Gallery", Toast.LENGTH_SHORT).show();
                return true;
            }
        });


        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMessage("Hang On While We Extract....");
                dialog.show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 3000);
                final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                txt.startAnimation(myAnim);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                txt.startAnimation(myAnim);
                gameData = getIntent().getExtras();
                if (gameData != null )
                {

                    String team = gameData.getString("croppedimagepath");
                    Bitmap myBitmap = BitmapFactory.decodeFile(team);
                    bmap=myBitmap;

                }
                TextExtractor(bmap);

            }
        });

       txt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this,"Text Extractor", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        more_vert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 3000);
                final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                more_vert.startAnimation(myAnim);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                more_vert.startAnimation(myAnim);

                    showMenu(v);

            }
        });

        more_vert.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this,"More Options", Toast.LENGTH_SHORT).show();
                return true;
            }
        });





    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){

            case 0: File imgFile = new  File(pictureImagePath);
                    if(imgFile.exists())
                    {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        ime.setImageBitmap(myBitmap);
                        bmap=myBitmap;

                        Tab2 fragment=new Tab2();
                        Bundle args=new Bundle();
                        args.putString("Image",pictureImagePath);
                        fragment.setArguments(args);
                        FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                        ft2.replace(R.id.idlinear, fragment);
                        ft2.commitNowAllowingStateLoss();


                    }
            break;
           /////////////////////////////////////////////////////////////////////////

            case 1:    try
                        {
                            Uri imageUri = data.getData();
                            InputStream imageStream = getContentResolver().openInputStream(imageUri);
                            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            ime.setImageBitmap(selectedImage);
                            bmap=selectedImage;

                            String path=getRealPathFromURI(imageUri);

                            Tab2 fragment=new Tab2();
                            Bundle args=new Bundle();
                            args.putString("Image",path);
                            fragment.setArguments(args);
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.idlinear, fragment);
                            ft2.commitNowAllowingStateLoss();


                        }
                        catch (FileNotFoundException e)
                        {
                             e.printStackTrace();
                        }
            break;
            ////////////////////////////////////////////////////////////////////////////////////
        }


    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static Bitmap toGrayscale(Bitmap srcImage) {

        Bitmap bmpGrayscale = Bitmap.createBitmap(srcImage.getWidth(), srcImage.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bmpGrayscale);
        Paint paint = new Paint();

        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(srcImage, 0, 0, paint);

        return bmpGrayscale;
    }

    public void TextExtractor(Bitmap path)
    {
        // Bitmap bitmap=BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.sample);
        path=toGrayscale(path);
        TextRecognizer textRecognizer=new TextRecognizer.Builder(getApplicationContext()).build();
        if(!textRecognizer.isOperational())
        {
            Toast.makeText(MainActivity.this, "unable to recognize", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(MainActivity.this, " recognizing...", Toast.LENGTH_SHORT).show();
            Frame frame=new Frame.Builder().setBitmap(path).build();
            SparseArray<TextBlock> items=textRecognizer.detect(frame);
            StringBuilder stringBuilder=new StringBuilder();
            for(int i=0;i<items.size();i++)
            {
                TextBlock item=items.valueAt(i);
                stringBuilder.append(item.getValue());
                stringBuilder.append("\n");
            }

            str=stringBuilder.toString();
            Tab1 fragment=new Tab1();
            Bundle args=new Bundle();
            args.putString("textdata",stringBuilder.toString().trim());
            fragment.setArguments(args);
            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
            ft2.replace(R.id.linear, fragment);
            ft2.commitNowAllowingStateLoss();
        }



    }

    public void OpenCamera()
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/scannerpic" + imageFileName;
        File file = new File(pictureImagePath);
        Uri outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, 0);
    }

    public void OpenGallery()
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    public void CheckPerm()
    {
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    camcode);
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    storecode);
        }

    }

    public void showMenu(View v)
    {
        PopupMenu popup = new PopupMenu(this,v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_main, popup.getMenu());
        popup.show();
    }
}
