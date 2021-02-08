package com.example.convertrgbtogray;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    Button btn;
    ImageView iv;

    //take bitmap and bitmap drawable to get image form image view
    BitmapDrawable drawable;
    Bitmap bitmap;
    String imageString="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //binding
        btn=(Button)findViewById(R.id.summit);
        iv=(ImageView)findViewById(R.id.image_view);


        if(!Python.isStarted())
            Python.start(new AndroidPlatform(this));

        final Python py=Python.getInstance();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get image from image view
                drawable=(BitmapDrawable)iv.getDrawable();
                bitmap=drawable.getBitmap();
                imageString=getStringImage(bitmap);

                //imageString we get encoded iamge string
                //pass this string in python script

                //call .py file
                PyObject pyo=py.getModule("myscript");
                //call module in .py file
                PyObject obj=pyo.callAttr("main",imageString);
                //return value
                String str=obj.toString();


                //convert bytearray
                byte[]data=android.util.Base64.decode(str, Base64.DEFAULT);
                //conver to bitmap
                Bitmap bmp= BitmapFactory.decodeByteArray(data,0,data.length);

                //set this bitmap to imageView2
                iv.setImageBitmap(bmp);
            }
        });

    }

    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        //store in bytearray
        byte[] imageBytes=baos.toByteArray();
        String encodedImage=android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}