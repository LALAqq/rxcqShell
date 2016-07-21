package com.example.lisi.androidshell;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.apache.commons.io.IOUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static java.lang.Math.abs;

/**
 * Created by lisi on 16/6/28.
 */
public class GameOCR {

    private class ORCMachInfo{
        public int x, y, w, h;
        public int minf, maxf;
        public HashSet<String> matchText;


        public ORCMachInfo(int x, int y, int w, int h, int minf, int maxf, HashSet<String> matchText){
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.minf = minf;
            this.maxf = maxf;
            this.matchText = matchText;
        }
    }



    private TessBaseAPI baseApi;
    public Bitmap mScreen = Bitmap.createBitmap(720, 1280, Bitmap.Config.ARGB_8888);
    private Map<Integer,ArrayList<ORCMachInfo> > mOrcMap = new HashMap<Integer, ArrayList<ORCMachInfo>>();

    public static int IS_DEAD = 0;
    public static int IN_BIQI = 1;
    public static int IN_MENGZHONG = 2;
    public static int OPEN_DALY_ACTIVITY = 3;
    public static int READY_TO_MINE = 4;
    public static int IN_MINE = 5;
    public static int CHECKIN_BOARD = 6;
    public static int IN_SELECT_HERO = 7;
    public static int GET_SIGN_IN_REWARD = 8;
    public static int IN_ACTIVITY = 9;
    public static int GET_TASK = 10;
    public static int IN_CHECK_IN = 11;
    public static int IN_GAME = 12;
    public static int GET_NEW_TITLE = 13;
    public static int READY_RESOLVE = 14;
    public static int END_MINE = 15;
    public static int IN_TIP = 16;
    public static int IN_SG = 17;
    public static int IN_WORLD_MAP = 18;
    public static int IN_GJ = 19;
    public static int IN_HH = 20;


    private void addOrcMap(){
        mOrcMap.put(IS_DEAD, new ArrayList<ORCMachInfo>(){
            {
                add(new ORCMachInfo(275, 710, 40, 107, 140, 255, new HashSet<String>() {{add("返回城镇");}}));
            }});
        mOrcMap.put(READY_TO_MINE, new ArrayList<ORCMachInfo>(){
            {
                add(new ORCMachInfo(300, 340, 30, 180, 100, 255, new HashSet<String>() {{add("传送到矿洞三层");}}));
            }});
        mOrcMap.put(CHECKIN_BOARD, new ArrayList<ORCMachInfo>(){
            {
                add(new ORCMachInfo(540, 600, 80, 150, 150, 255, new HashSet<String>() {{add("公告");}}));
            }});
        mOrcMap.put(IN_SELECT_HERO, new ArrayList<ORCMachInfo>(){
            {
                add(new ORCMachInfo(15, 150, 30, 110, 80, 255, new HashSet<String>() {{add("当前帐号");}}));
            }});
        mOrcMap.put(GET_SIGN_IN_REWARD, new ArrayList<ORCMachInfo>(){
            {
                add(new ORCMachInfo(500, 650, 30, 110, 130, 255, new HashSet<String>() {{add("累计签到");}}));
                add(new ORCMachInfo(605, 605, 30, 150, 130, 255, new HashSet<String>() {{add("每日签到");}}));
            }});
        mOrcMap.put(IN_ACTIVITY, new ArrayList<ORCMachInfo>(){
            {
                add(new ORCMachInfo(620, 600, 40, 75, 140, 255, new HashSet<String>() {{add("活动");}}));
            }});
        mOrcMap.put(GET_TASK, new ArrayList<ORCMachInfo>(){
            {
                add(new ORCMachInfo(380, 300, 30, 120, 140, 255, new HashSet<String>() {{add("任务奖励");}}));
            }});
        mOrcMap.put(IN_CHECK_IN, new ArrayList<ORCMachInfo>(){
            {
                add(new ORCMachInfo(645, 110, 30, 110, 150, 255, new HashSet<String>() {{add("当前账号");add("当前账胃");}}));
            }});
        mOrcMap.put(IN_GAME, new ArrayList<ORCMachInfo>(){
            {
                add(new ORCMachInfo(646, 1163, 30, 30, 60, 255, new HashSet<String>() {{add("区");}}));
                add(new ORCMachInfo(680, 1120, 40, 153, 120, 255, new HashSet<String>() {{add("矿洞三层");add("比奇");add("梦中");add("比奇野外");add("奏蛇山谷");add("毒蛇山谷");add("山谷矿区一层");add("盟重");add("银杏山谷");add("银杏山谷野外");add("山谷矿区_层");add("山爸矿区_层");add("山爸矿区一层");}}));
                add(new ORCMachInfo(609, 145, 30, 30, 91, 255, new HashSet<String>() {{add("近");add("柒");add("萼");add("遍");add("叉斤〉");add("严");add("迎");add("遁");}}));
                add(new ORCMachInfo(646, 1115, 30, 80, 60, 255, new HashSet<String>() {{add("安全区");add("PK区");add("pk区");add("Pk区");add("pK区");add("激情区");}}));

            }});
        mOrcMap.put(IN_SG, new ArrayList<ORCMachInfo>(){
            {
                add(new ORCMachInfo(680, 1120, 40, 153, 120, 255, new HashSet<String>() {{add("山谷矿区一层");add("山谷矿区_层");add("山爸矿区_层");add("山爸矿区一层");}}));
            }});
        mOrcMap.put(GET_NEW_TITLE, new ArrayList<ORCMachInfo>(){
            {
                add(new ORCMachInfo(465, 560, 30, 160, 155, 240, new HashSet<String>() {{add("获得新的称号");}}));
            }});
        mOrcMap.put(READY_RESOLVE, new ArrayList<ORCMachInfo>(){
            {
                add(new ORCMachInfo(385, 795, 50, 52, 105, 255, new HashSet<String>() {{add("选择");}}));
                add(new ORCMachInfo(650, 600, 50, 80, 125, 240, new HashSet<String>() {{add("分解");}}));
            }});
        mOrcMap.put(IN_MINE, new ArrayList<ORCMachInfo>(){
            {
                add(new ORCMachInfo(680, 1120, 40, 153, 120, 255, new HashSet<String>() {{add("矿洞三层");}}));
            }});
        mOrcMap.put(END_MINE, new ArrayList<ORCMachInfo>(){
            {
                add(new ORCMachInfo(490, 342, 40, 30, 140, 255, new HashSet<String>() {{add("你");add("每");}}));
            }});
        mOrcMap.put(IN_TIP, new ArrayList<ORCMachInfo>(){
            {
                add(new ORCMachInfo(463, 600, 35, 70, 160, 255, new HashSet<String>() {{add("提示");}}));
            }});
        mOrcMap.put(IN_WORLD_MAP, new ArrayList<ORCMachInfo>(){
            {
                add(new ORCMachInfo(655, 820, 43, 110, 180, 255, new HashSet<String>() {{add("当前地图");}}));
                add(new ORCMachInfo(655, 965, 43, 110, 130, 255, new HashSet<String>() {{add("世界地图");}}));
                add(new ORCMachInfo(655, 820, 43, 110, 130, 255, new HashSet<String>() {{add("当前地图");}}));
                add(new ORCMachInfo(655, 965, 43, 110, 180, 255, new HashSet<String>() {{add("世界地图");}}));
            }});
        mOrcMap.put(IN_GJ, new ArrayList<ORCMachInfo>(){
            {
                add(new ORCMachInfo(195, 345, 35, 80, 110, 255, new HashSet<String>() {{add("经验值");}}));
                add(new ORCMachInfo(515, 585, 40, 110, 155, 255, new HashSet<String>() {{add("挂机结束");add("桂机结束");add("娃机结束");}}));
            }});
        mOrcMap.put(IN_HH, new ArrayList<ORCMachInfo>(){
            {
                add(new ORCMachInfo(460, 580, 40, 120, 150, 255, new HashSet<String>() {{add("加入行会");}}));
            }});
    }

    public GameOCR(){
        String TESSBASE_PATH = "/mnt/sdcard/";
        String DEFAULT_LANGUAGE = "chi_sim";
        baseApi = new TessBaseAPI();
        baseApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);
        addOrcMap();
    }

    public int getHeroLevel(Bitmap bm){
        if (bm == null) {
            bm = screenCap();
        }
        Bitmap ora = Bitmap.createBitmap(bm, 687, 7, 25, 25);
        Bitmap tmp  = gray2Binary(adjustPhotoRotation(ora, 270), 153, 240);
        String orcRes = orc(tmp);
        String tmp1 = orcRes.replace("飞", "1");
        String tmp2 = tmp1.replace("Z", "2");
        String tmp3 = tmp2.replace("z", "2");
        String tmp4 = tmp3.replace("O", "0");
        String tmp5 = tmp4.replace("三", "3");
        String tmp6 = tmp5.replace("m", "10");
        String tmp7 = tmp6.replace("引", "51");
        String orcLevel = tmp7.replace("o", "0");
        Log.i("orc level", orcLevel);
        ora.recycle();
        tmp.recycle();
        try {
            return Integer.valueOf(orcLevel);
        }catch (Exception e){
            return  0;
        }
    }

    public  Bitmap screenCap(){
        try {
            long a = System.currentTimeMillis();
            java.lang.Process process =  Runtime.getRuntime().exec("su");
            DataOutputStream input = new DataOutputStream(process.getOutputStream());
            InputStream outStream = process.getInputStream();
            input.write("screencap;\n exit 0\n".getBytes());
            input.flush();
            byte[] bytes = IOUtils.toByteArray(outStream);
            mScreen.copyPixelsFromBuffer(ByteBuffer.wrap(bytes).rewind());
            long b = System.currentTimeMillis();
            Log.i("use time", String.valueOf(b - a));
            input.close();
            process.destroy();
            return mScreen;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean get(Bitmap map, int cj){
        Log.i("orc cj", String.valueOf(cj));
        try {
            if(map == null){
                return false;
            }
            if(cj == IS_DEAD){
                return testDead(map);
            }
            for(ORCMachInfo info: mOrcMap.get(cj)) {
                 if(match(map, info, 270)){

                     return  true;
                 }
            }
        }catch (Exception e){
            return  false;
        }
        return false;
    }


    private boolean match(Bitmap bit, ORCMachInfo info, int rotation){
        if(bit == null){
            return false;
        }
        Bitmap ora = Bitmap.createBitmap(bit, info.x, info.y, info.w, info.h);
        Bitmap tmp  = gray2Binary(adjustPhotoRotation(ora, rotation), info.minf, info.maxf);
        String orcRes = orc(tmp);
        for(String t: info.matchText){
            if (t.equals(orcRes.trim().replaceAll("\r|\n", ""))){
                Log.i("orc get result", "true");
                ora.recycle();
                tmp.recycle();
                return true;
            }
        }
        ora.recycle();
        tmp.recycle();
        return false;
    }

    public String orc(Bitmap bm){
        baseApi.setImage(bm);
        String text = baseApi.getUTF8Text();
        Log.i("orc text", text);
        return text;
    }

    // 该函数实现对图像进行二值化处理
    private Bitmap gray2Binary(Bitmap graymap, int minf, int maxf) {
        //得到图形的宽度和长度
        int width = graymap.getWidth();
        int height = graymap.getHeight();
        //创建二值化图像
        Bitmap binarymap = null;
        binarymap = graymap.copy(Bitmap.Config.ARGB_8888, true);
        //依次循环，对图像的像素进行处理
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //得到当前像素的值
                int col = binarymap.getPixel(i, j);
                //得到alpha通道的值
                int alpha = col & 0xFF000000;
                //得到图像的像素RGB的值
                int red = (col & 0x00FF0000) >> 16;
                int green = (col & 0x0000FF00) >> 8;
                int blue = (col & 0x000000FF);
                // 用公式X = 0.3×R+0.59×G+0.11×B计算出X代替原来的RGB
                int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                //对图像进行二值化处理
                if (gray <= maxf && gray >= minf) {
                    gray = 255;
                } else {
                    gray = 0;
                }
                // 新的ARGB
                int newColor = alpha | (gray << 16) | (gray << 8) | gray;
                //设置新图像的当前像素值
                binarymap.setPixel(i, j, newColor);
            }
        }
        return binarymap;
    }

    private  Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree)
    {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            return bm1;
        } catch (OutOfMemoryError ex) {
        }
        return null;
    }

    public static boolean testDead(Bitmap a){
        int p1 = getP(a, 275, 590, 40, 110);
        int p2 = getP(a, 275, 470, 40, 110);
        int p3 = getP(a, 275, 710, 40, 110);
        Log.i("orc testDead", p1 + " " + p2 + " " + p3);
        if (p1 < 30 && ((p2 > 85 && p2 < 110) || p2 < 30 )&& p3 > 85 && p3 < 110 && (abs(p2 - p3) < 20 || abs(p2 - p1) < 20)){
            return true;
        }
        return  false;
    }


    public boolean isClose1(Bitmap a){
        int p1 = getP(a, 680, 1140, 25, 25);
        int p2 = getP(a, 682, 1143, 15, 15);
        int p3 = getP(a, 679, 1120, 25, 25);
        Log.i("orc", String.valueOf(p1 )+ " " + String.valueOf(p2) + String.valueOf(p3));
        if((p1 >= 117 && p1 <= 127) || (p2 >= 97 && p2 <= 107)||(p3 >= 110 && p3 <= 127)){
            return true;
        }
        return false;
    }//1148 33

    public boolean isClose2(Bitmap a){
        int p1 = getP(a, 553, 905, 16, 16);
        if((p1 >= 97 && p1 <= 107) ){
            return true;
        }
        return false;
    }//913 159

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
}
