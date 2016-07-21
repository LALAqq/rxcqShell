package com.example.lisi.androidshell;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import static java.lang.Math.abs;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Intent intent = getIntent();
        int x = intent.getIntExtra("x", 0);
        int y = intent.getIntExtra("y", 0);
        int h = intent.getIntExtra("h", 100);
        int w = intent.getIntExtra("w", 100);
        Bitmap a = BitmapFactory.decodeFile("/sdcard/screen.png");
        Bitmap bit = Bitmap.createBitmap(a, x, y, w, h);
        ((ImageView)findViewById(R.id.img)).setImageBitmap(bit);
        int[] pixels = new int[bit.getWidth()*bit.getHeight()];//保存所有的像素的数组，图片宽×高
        bit.getPixels(pixels,0,bit.getWidth(),0,0,bit.getWidth(),bit.getHeight());
        int aa = 0, bb = 0;
        int cnt = 0;
        int sum = 0;
        for(int i = 0; i < pixels.length; i++){
            int clr = pixels[i];
            int  red   = (clr & 0x00ff0000) >> 16;  //取高两位
            int  green = (clr & 0x0000ff00) >> 8; //取中两位
            int  blue  =  clr & 0x000000ff; //取低两位
            Log.i("aaaa", "r="+red+"g="+green+",b="+blue);
            int zz = (int)((red + green + blue)/3);
            sum += abs(zz -red) + abs(zz - green) + abs(zz - blue);
            if(abs(zz -red) < 5 && abs(zz - green) < 5 && abs(zz - blue) < 5){
                aa += 1;
            }else {
                bb += 1;
            }
        }
        float pl = sum / pixels.length;
        Log.i("bbbb", "aa=" + aa + " bb="+ bb + "pl=" + pl);
    }
}
