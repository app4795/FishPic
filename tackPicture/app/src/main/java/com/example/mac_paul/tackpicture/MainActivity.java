package com.example.mac_paul.tackpicture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    ImageView img;
    ImageView img2;
    ImageView img3;
    Bitmap temp;
    private ArrayList<Bitmap> bitmapsget=new ArrayList<Bitmap>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img=(ImageView)findViewById(R.id.imageView);
        img2=(ImageView)findViewById(R.id.imageView2);
        img3=(ImageView)findViewById(R.id.imageView3);
        Intent intent = this.getIntent();
        if(intent!=null)
        {
            temp=intent.getParcelableExtra("bitimg0");
            img.setImageBitmap(temp);
            temp=intent.getParcelableExtra("bitimg1");
            img2.setImageBitmap(temp);
            //temp=intent.getParcelableExtra("bitimg2");
            //img3.setImageBitmap(temp);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
