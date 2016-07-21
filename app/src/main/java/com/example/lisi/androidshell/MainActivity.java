package com.example.lisi.androidshell;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity {

    private  String mAction = "xunluo";
    private Button btn1;
    private boolean sxh = false;
    private Timer timer;
    private TimerTask timerTask;
    private int mIx;
    private ArrayList<Integer> MultiChoiceID = new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            java.lang.Process mProcess = Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            e.printStackTrace();
        }

        findViewById(R.id.fire).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MultiChoiceID.size()==0){
                    Toast toast = Toast.makeText(getApplicationContext(),"请选择路线", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, ShellService.class);
                intent.putExtra("action", "fire");
                intent.putExtra("xianlu", MultiChoiceID);
                startService(intent);
            }
        });
        findViewById(R.id.get_money).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MultiChoiceID.size()==0){
                    Toast toast = Toast.makeText(getApplicationContext(),"请选择路线", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, ShellService.class);
                intent.putExtra("action", "get_money");
                intent.putExtra("xianlu", MultiChoiceID);
                startService(intent);
            }
        });
        findViewById(R.id.get_money_alone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShellService.class);
                intent.putExtra("action", "get_money_alone");
                intent.putExtra("xianlu", MultiChoiceID);
                startService(intent);
            }
        });
        findViewById(R.id.stop_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShellService.class);
                stopService(intent);
            }
        });
        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(MainActivity.this, ShellService.class);
                intent.putExtra("action", "test");
                String x = ((EditText)findViewById(R.id.x)).getText().toString();
                int ix = Integer.valueOf(x);
                MultiChoiceID.add(0,ix);
                intent.putExtra("xianlu", MultiChoiceID);
                startService(intent);
            }
        });
        findViewById(R.id.kill).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShellService.class);
                intent.putExtra("action", "kill");
                String x = ((EditText)findViewById(R.id.x)).getText().toString();
                int ix = Integer.valueOf(x);
                MultiChoiceID.add(0,ix);
                intent.putExtra("xianlu", MultiChoiceID);
                startService(intent);
            }
        });

        final String [] nItems = {"1线","2线","3线","4线","5线","6线","7线","8线","9线","10线"};

        btn1 = (Button) findViewById(R.id.luxian);
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                MultiChoiceID.clear();
                builder.setTitle("选择路线");
                //	设置多选项
                builder.setMultiChoiceItems(nItems,
                        new boolean[]{false,false,false,false,false,false,false,false,false,false},
                        new DialogInterface.OnMultiChoiceClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
                                // TODO Auto-generated method stub
                                if (arg2) {
                                    MultiChoiceID.add(arg1);
                                    String tip = "你选择的ID为"+arg1+",值为"+nItems[arg1];
                                    Toast toast = Toast.makeText(getApplicationContext(),   tip, Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                                else {
                                    //MultiChoiceID.remove(arg1);
                                }
                            }
                        });
                //	设置确定按钮
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        String str = "";
                        int size = MultiChoiceID.size();
                        for(int i = 0; i < size; i++) {
                            str += (nItems[MultiChoiceID.get(i)]+",");
                        }
                        Toast toast = Toast.makeText(getApplicationContext(), "你选择了"+str, Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
                //	设置取消按钮
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                    }
                });

                builder.create().show();
            }
        });

        findViewById(R.id.testORC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShellService.class);
                intent.putExtra("action", "restart_self");
                startService(intent);
            }
        });

        findViewById(R.id.show_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ShellService.class);
                intent.putExtra("action", "restart_game");
                startService(intent);

            }
        });
    }

    public static  int getP(Bitmap a, int x, int y, int w, int h){
        Log.i("xywh", x + " " + y + " " + w + " " + h);
        int [] pixels = new int[w * h];
        a.getPixels(pixels,0,w, x, y, w, h);
        int sum = 0;
        for(int i = 0; i < pixels.length; i++){
            int clr = pixels[i];
            int  red   = (clr & 0x00ff0000) >> 16;  //取高两位
            int  green = (clr & 0x0000ff00) >> 8; //取中两位
            int  blue  =  clr & 0x000000ff; //取低两位
            int zz = (int)((red + green + blue)/3);
            sum += abs(zz -red) + abs(zz - green) + abs(zz - blue);
        }
        float pl = sum / pixels.length;
        return (int) pl;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
