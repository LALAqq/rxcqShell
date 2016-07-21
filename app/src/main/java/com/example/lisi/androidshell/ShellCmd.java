package com.example.lisi.androidshell;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by lisi on 16/6/21.
 */
public class ShellCmd {

    private static ArrayList<String> SXL = new ArrayList<String>() {
        {
            add("input tap 220 130; input tap 440 280;sleep 1;");
            add("input tap 220 130; input tap 540 280;sleep 1;");
            add("input tap 220 130; input tap 680 280;sleep 1;");
            add("input tap 220 130; input tap 820 280;sleep 1;");
            add("input tap 220 130; input tap 440 360;sleep 1;");
            add("input tap 220 130; input tap 540 360;sleep 1;");
            add("input tap 220 130; input tap 680 360;sleep 1;");
            add("input tap 220 130; input tap 820 360;sleep 1;");
            add("input tap 220 130; input tap 440 440;sleep 1;");
            add("input tap 220 130; input tap 540 440;sleep 1;");
        }
    };

    private static ArrayList<String> CSER = new ArrayList<String>() {
        {
            add("input tap 500 240; sleep 0.1;\n");
            add("input tap 720 240; sleep 0.1;\n");
            add("input tap 940 240; sleep 0.1;\n");
            add("input tap 500 300; sleep 0.1;\n");
            add("input tap 720 300; sleep 0.1;\n");
            add("input tap 940 300; sleep 0.1;\n");
            add("input tap 500 330; sleep 0.1;\n");
            add("input tap 720 330; sleep 0.1;\n");
            add("input tap 940 330; sleep 0.1;\n");
        }
    };

    private static ArrayList<String> SXLS = new ArrayList<String>() {
        {
            add("input tap 220 130;  sleep 0.4; input tap 440 280;sleep 1;");
            add("input tap 220 130;  sleep 0.4; input tap 540 280;sleep 1;");
            add("input tap 220 130;  sleep 0.4; input tap 680 280;sleep 1;");
            add("input tap 220 130;  sleep 0.4; input tap 820 280;sleep 1;");
            add("input tap 220 130;  sleep 0.4; input tap 440 360;sleep 1;");
            add("input tap 220 130;  sleep 0.4; input tap 540 360;sleep 1;");
            add("input tap 220 130;  sleep 0.4; input tap 680 360;sleep 1;");
            add("input tap 220 130;  sleep 0.4; input tap 820 360;sleep 1;");
            add("input tap 220 130;  sleep 0.4; input tap 440 440;sleep 1;");
            add("input tap 220 130;  sleep 0.4; input tap 540 440;sleep 1;");
        }
    };

    public static String getFireCmdLeftDown(int xl) {
        String mCmd = "";
        mCmd += "y=(600 550 500 450 400 350 300);\n" +
                "x=(970 840 710 580 450 320 290);\n" +
                SXL.get(xl) + "\n" +
                "for t in ${y[*]}\n" +
                "do\n" +
                "for i in ${x[*]}\n" +
                "do \n" +
                " input tap $i $t;\n" +
                "sleep 0.5;\n" +
                "done\n" +
                "if [ $t -le 400 ]\n" +
                "then\n" +
                "echo 200 $t\n" +
                "input tap 200 $t;\n" +
                "sleep 1;\n" +
                "fi\n" +
                "done\n" +
                "input tap 650 250;\n" +
                "sleep 0.5;\n" +
                "input tap 580 250;\n" +
                "sleep 0.5;\n" +
                "input tap 510 250;\n" +
                "sleep 0.5;\n" +
                "input tap 430 250;\n" +
                "sleep 0.5;\n" +
                "input tap 370 250;\n" +
                "sleep 0.5;\n";
        return mCmd;
    }

    public static String getMoneyLeftDown(ArrayList<Integer> xianlu) {
        String mCmd = "input tap 750 580;sleep 1;\n";
        mCmd += "y=(630 630 450 630 615 600 585 570 555 540 525 630);\n" +
                "x=(200 410 430 410 200 420 200 380 200 350 220 200);\n" +
                "s=(1 3.3 3.5 3.5 3.3 3.3 3.2 2.6 2.6 2.2 1.9 2.5);\n" +
                "function xunluo(){\n" +
                "sleep 1;\n" +
                " input tap 1200 360;\n" +
                "sleep ";
        if (xianlu.size() == 1) {
            mCmd += "30;\n";
        } else if (xianlu.size() == 2) {
            mCmd += "15;\n";
        } else {
            mCmd += "5;\n";
        }
        mCmd += "input tap 1200 50;\n" +
                "sleep 1;\n" +
                "t=0\n" +
                "while(($t<${#y[@]}))\n" +
                "do\n" +
                " input tap ${x[$t]} ${y[$t]};\n" +
                "sleep ${s[$t]};\n" +
                "t=$t+1\n" +
                "done\n" +
                " input tap 1100 50;\n sleep 0.5\n" +
                "}\n";
        for (Integer i : xianlu) {
            mCmd = mCmd + SXL.get(i) + "\nxunluo\n";
        }
        return mCmd;
    }

    public static String getMoneyAlone(ArrayList<Integer> xianlu) {
        String mCmd = "";
        mCmd = "input tap 750 490;sleep 1;\n" +
                "y=(630 450 630);\n" +
                "x=(410 430 410);\n" +
                "s=(1 3.5 3.5);\n" +
                "function xunluo(){\n" +
                "sleep 0.5;\n";
        mCmd += "input tap 1200 50;\n" +
                "sleep 0.5;\n" +
                "t=0\n" +
                "while(($t<${#y[@]}))\n" +
                "do\n" +
                "input tap ${x[$t]} ${y[$t]};\n" +
                "sleep ${s[$t]};\n" +
                "t=$t+1\n" +
                "done\n" +
                "input tap 1100 50;\n" +
                "}\n";
        for (Integer i : xianlu) {
            mCmd = mCmd + SXL.get(i) + "\nxunluo\n";
        }
        return mCmd;
    }

    public static String getTestCmd() {

        return "sleep 1;\n screencap -p /sdcard/screen.png;\n chmod 777 /sdcard/screen.png;\n echo end_cap;\n";
    }

    public static String getBack() {
        String ret = "input tap 750 450; sleep 1.5;";
        return ret;
    }

    public static String getBack(int xl) {
        String ret = "sleep 7; input tap 750 450; sleep 1.5; input tap 450 450; sleep 0.7; input tap 450 400;\n";
        if (xl != 0) {
            ret += " sleep 1;\n" + SXLS.get(xl) + "\n";
        }
        return ret;
    }

    public static String getKill(int miaoshu) {
        return "d=1;\n while(($d<" + String.valueOf(miaoshu) + "))\n do\n input tap 1200 360; sleep 1;\n d=$d+1;\ndone\n";
    }

    public static String checkIn(int c) {
        return "input tap 650 510;\nsleep 1;\n input tap 650 480; sleep 0.5;\n" + CSER.get(c - 1) + "input tap 730 600;\n sleep 2;\ninput tap 730 600;\n";
    }

    public static String selectHeroInGame() {
        return "input swipe 600 350 750 350;\n sleep 0.2;\n input tap 650 600;\n sleep 3;\n";
    }

    public static String getSignInReward() {
        return "input tap 750 600;\n";
    }

    public static String getReward() {
        return  " input tap 1110 99; sleep 0.1;\n" +
                " input tap 650 500; sleep 3;\n" +
                " input tap 650 360; sleep 1;\n" +
                " input tap 1160 90; sleep 0.1;\n";
    }

    public static String closeActivity(){
        return "input tap 1160 110;\n sleep 0.1;\n";
    }

    public static String AcceptTask(){
        return "input tap 850 400;\n sleep 0.1;\n";
    }

    public static String outGame(){
        return " input tap 650 360; sleep 1; \n" +
                " input tap 650 700; sleep 1; \n" +
                " input tap 1000 700; sleep 1; \n" +
                " input tap 620 60; sleep 0.5;\n" +
                " input tap 350 360; sleep 0.5;\n";
    }

    public static String checkOut(){
        return "input tap 200 630;\n";
    }

    public static String closeTitle(){
        return "input tap 750 450;\n";
    }

    public static String clickReward(){
        return  "input tap 900 580; sleep 0.1\n" +
                "input tap 900 580; sleep 0.1\n" +
                "input tap 900 580; sleep 0.1\n" +
                "input tap 900 580; sleep 0.1\n" +
                "input tap 900 580; sleep 0.1\n" +
                "input tap 900 580; sleep 0.1\n";
    }

    public static String toResolved(int level){
        String ret = "input tap 650 360; sleep 0.4;\n" +
                "input tap 650 700; sleep 0.4;\n" +
                "input tap 550 700; sleep 0.4;\n";
        if(level >= 50) {
            ret += "input tap 200 500;\n sleep 3;\n";
        }else if(level >= 40){
            ret += "input tap 250 500;\n sleep 3;\n";
        }else if(level >= 30) {
            ret += "input tap 300 500;\n sleep 3;\n";
        } else{
            ret += "input tap 450 500;\n sleep 3;\n";
        }
        return ret;
    }

    public  static String resolved(int level){

        String ret =   "input tap 900 580; sleep 0.1\n" +
                "input tap 900 580; sleep 0.1\n" +
                "input tap 900 580; sleep 0.1\n" +
                "input tap 900 580; sleep 0.1\n" +
                "input tap 900 580; sleep 0.1\n" +
                "input tap 900 550; sleep 0.3;\n";
        if (level > 40){
            ret += "input tap 600 200; sleep 0.3;\n";
        }
        ret +=  "input tap 650 550; sleep 2;\n" +
                "input tap 550 450; sleep 1;\n" +
                "input tap 900 450; sleep 1.5; \n" +
                "input tap 900 450; sleep 1.5;\n" +
                "input tap 1150 50; sleep 0.5; \n" +
                "input tap 650 360;\n";
        return  ret;
    }

    public static String toMine(){
        return "input tap 950 200;\n sleep 2;" +
                "input swipe 650 460 650 260;" +
                "sleep 1;\n" +
                "input swipe 650 460 650 260;" +
                "sleep 1;\n" +
                "input swipe 650 460 650 260;" +
                "sleep 1;\n" +
                "input swipe 650 460 650 260;" +
                "sleep 3;\n" +
                "input swipe 650 460 650 550;" +
                "input tap 745 120;\n" +
                "sleep 2;\n" +
                "input tap 500 200;\n" +
                "sleep 6;\n";

    }

    public static String WalktoMine(){
        return "input tap 1200 60; sleep 0.4;\n" +
                "input tap 960 200; sleep 3;\n" +
                "input tap 1110 50; sleep 4;\n";
    }

    public static String WalktoSG(){
        return "input tap 1200 60; sleep 3;\n" +
                "input tap 1000 60; sleep 1.5;\n" +
                "input tap 750 400; sleep 0.5;\n" +
                "input tap 750 400; sleep 0.5;\n" +
                "input tap 480 490; sleep 4;\n" +
                "input tap 1200 60; sleep 1.5;\n" +
                "input tap 930 60; sleep 0.4;\n" +
                "input swipe 960 600 960 200; sleep 0.4;\n" +
                "input swipe 960 600 960 200; sleep 0.4;\n" +
                "input swipe 960 600 960 200; sleep 0.4;\n" +
                "input swipe 960 600 960 200; sleep 0.4;\n" +
                "input tap 960 550; sleep 0.1;\n" +
                "input tap 960 550; sleep 0.1;\n" +
                "input tap 1110 50; sleep 19;";
    }

    public static String clickTask(){
        return  "input tap 900 400;\n";
    }

    public static String inMine(){
        return  "input tap 500 400; sleep 3;\n";
    }

    public static String Mine(){
            return "input swipe 150 600 150 700;\ninput swipe 150 600 150 700;\n" +
                "input swipe 150 600 150 700;\ninput swipe 150 600 150 700;\n" +
                "input swipe 150 600 150 700;\ninput swipe 150 600 150 700;\n" +
                "input swipe 150 600 150 700;\ninput swipe 150 600 150 700;\n" +
                "input swipe 150 600 150 700;\ninput swipe 150 600 150 700;\n" +
                "input swipe 150 600 150 700;\ninput swipe 150 600 150 700;\n" +
                "input swipe 150 600 150 700;\ninput swipe 150 600 150 700;\n" +
                "input swipe 150 600 150 700;\ninput swipe 150 600 150 700;\n" +
                "input swipe 150 600 150 700;\ninput swipe 150 600 150 700;\n" +
                "input swipe 150 600 150 700;\ninput swipe 150 600 150 700;\n" +
                "input swipe 150 600 150 700;\ninput swipe 150 600 150 700;\n" +
                "input swipe 150 600 150 700;\ninput swipe 150 600 150 700  ;\n" + "input tap 950 640;\n";
    }

    public static String endMine(){
        return  "input tap 920 150;\n sleep 0.5;";
    }

    public static String closeWindow(){
        return "input tap 1148 33;\n";
    }

    public static String closeDialog(){
        return "input tap 913 159;\n";
    }

    public static String closeTip(){
        return "input tap 750 490;\n";
    }

    public static String closeHH(){
        return "input tap 750 480;\n";
    }


    public static String restartRXCQ() {
        return "am force-stop com.tencent.tmgp.rxcq;\n sleep 5;\n am start com.tencent.tmgp.rxcq/.AppActivity\n";
    }

    public static String restartSelf(){
        return "am force-stop com.example.lisi.androidshell;\n sleep 5;\n am start com.example.lisi.androidshell/.MainActivity\n";
    }

}
