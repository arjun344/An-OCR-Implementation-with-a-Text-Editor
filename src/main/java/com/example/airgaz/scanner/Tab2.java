package com.example.airgaz.scanner;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import io.github.mthli.knife.KnifeText;


public class Tab2 extends Fragment {

    ImageView imageView;
    String imgpath;
    Bitmap bmp;
    View view;
    Uri uri;
    Bitmap croppedim;
    String uriSting;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null)
        {
            if(getArguments()!=null)
            {
                imgpath=this.getArguments().getString("Image");
                bmp=BitmapFactory.decodeFile(imgpath);

            }

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.fragment_tab2, container, false);

        imageView =view.findViewById(R.id.imageView2);
        imageView.setImageBitmap(bmp);
        setupCrop();

        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {



    }

    private void setupCrop() {
        final ImageButton crop = view.findViewById(R.id.crop);

        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                crop.startAnimation(myAnim);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                crop.startAnimation(myAnim);
                saveImageFile(bmp);
                uri=Uri.fromFile(new File(uriSting));
                performCrop(uri);
            }
        });

        crop.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(), "crop", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
           // cropIntent.putExtra("aspectX", 1);
           // cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
           // cropIntent.putExtra("outputX", 128);
           // cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, 1001);
        }catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public String saveImageFile(Bitmap bitmap) {
        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }

    private String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory()
                .getPath(), "Scannertemp");
        if (!file.exists()) {
            file.mkdirs();
        }
         uriSting = (file.getAbsolutePath() + "/"
                + "temp" + ".jpg");
        Bundle gameData = new Bundle();
        gameData.putString("croppedimagepath",uriSting);
        Intent intent = getActivity().getIntent();
        intent.putExtras(gameData);
        return uriSting;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {
            if (data != null) {
                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                croppedim = extras.getParcelable("data");
                uriSting=null;

            }
        }

       else if(requestCode == 1)
        {
            Bitmap tobecropped=((BitmapDrawable)imageView.getDrawable()).getBitmap();
            saveImageFile(tobecropped);
        }

    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);


    }
}
