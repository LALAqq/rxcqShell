package com.example.lisi.androidshell;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.helpers.XMLFilterImpl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ShellService extends Service {
    Process mProcess;
    DataOutputStream mInput;
    InputStreamReader mOutput, mError;
    private static int FINISH_END = 1;
    private static int TEST_IS_DEAD = 2;
    private static int END_BACK = 3;
    private static int SIGN_IN_REWARD = 4;
    private static int SELECTED_HERO = 3;
    private Intent mIntent;
    private GameOCR mOrc;
    private boolean running = true;
    private String mLastAction = "";
    private int changeHeros = 0;
    private int changeServer = 0;
    private int signServer = 2;
    private boolean haveFlight = true;
    private long  endMineTimeStamp = 0;
    private boolean  onlySign = false;
    private int mLevel = 0;
    private int mScreenWidth;

    private Handler mExcuteHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(!running){
                return;
            }
            if(msg.what == FINISH_END) {
                String action = mIntent.getStringExtra("action");
                ArrayList<Integer> xianlu = mIntent.getIntegerArrayListExtra("xianlu");
                String cmd = getCmd(action, xianlu);
                exce(cmd);
            }else if(msg.what == TEST_IS_DEAD){
                if (true) {
                    ArrayList<Integer> xianlu = mIntent.getIntegerArrayListExtra("xianlu");
                    String cmd = getCmd("getback", xianlu);
                    exce(cmd);
                }else{
                    String cmd = ShellCmd.selectHeroInGame() + "echo selected_hero;\n";
                    exce(cmd);
                }
            }else if(msg.what == END_BACK){
                String cmd = getCmd("test", null);
                exce(cmd);
            }else if(msg.what == SIGN_IN_REWARD){
                Log.i("orc last action", mLastAction);
                boolean doAction = false;
                if(mLastAction.equals("begin")){
                    doAction = actionCheckIn();
                } else if(mLastAction.equals("check_in") || mLastAction.equals("out_game")) {
                    doAction = actionSelectHero();
                }else if(mLastAction.equals("select_hero")){
                    doAction = actionGetReward();
                }else if(mLastAction.equals("get_reward")){
                    doAction = actionReadResolved();
                }else if(mLastAction.equals("ready_resolve")){
                    doAction = actionResolved();
                }else if(mLastAction.equals("resolved") || mLastAction.equals("dead_back")){
                    Bitmap mp = mOrc.screenCap();
                    if(mLevel == 0) {
                        mLevel = mOrc.getHeroLevel(mp);
                    }
                    if(mLevel > 30 && !onlySign) {
                        doAction = actionGoToMine();
                    }else{
                        doAction = actionOutGame();
                    }
                }else  if(mLastAction.equals("to_sg")){
                    doAction = actionSGToMine();
                }else if(mLastAction.equals("to_mine")) {
                    doAction = actionGoInMine();
                }else if(mLastAction.equals("go_in_mine")){
                    doAction = actionMine();
                }else if(mLastAction.equals("check_out")){
                    doAction = actionCheckOut();
                }
                if(doAction == false){
                    Toast.makeText(ShellService.this, "adAction Fail", Toast.LENGTH_LONG);
                    actionCycle();
                }
            }
        }
    };
    private WindowManager windowManager;
    private LinearLayout mContainer;
    private LinearLayout mFloatView;
    private WindowManager.LayoutParams paramsF;
    private static  ShellService instance;
    private TextView mT;
    private boolean _isMoving=false;

    private void actionCycle(){
        int i = 0;
        mLevel = 0;
        while(i<5) {
            i += 1;
            Bitmap bm = mOrc.screenCap();
            if (mOrc.get(bm, mOrc.IN_HH)) {
                String cmd = ShellCmd.closeHH();
                exce(cmd);
            }
            if (mOrc.isClose2(bm)) {
                String cmd = ShellCmd.closeDialog();
                exce(cmd);
            }
            if (mOrc.isClose1(bm) || mOrc.get(bm, mOrc.READY_RESOLVE) || mOrc.get(bm, mOrc.IN_ACTIVITY) || mOrc.get(bm, mOrc.IN_WORLD_MAP)) {
                String cmd = ShellCmd.closeWindow();
                exce(cmd);
            }
            boolean haveNewTitle = false;
            if (mOrc.get(bm, mOrc.GET_NEW_TITLE)) {
                String cmd = ShellCmd.closeTitle();
                exce(cmd);
                haveNewTitle = true;
            }
            if (mOrc.get(bm, mOrc.IN_TIP)) {
                String cmd = ShellCmd.closeTip();
                exce(cmd);
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bm = mOrc.screenCap();
            if (mOrc.get(bm, mOrc.IS_DEAD)) {
                String cmd = ShellCmd.getBack();
                cmd = cmd + "echo sign_in_reward;\n";
                mLastAction = "dead_back";
                exce(cmd);
                return;
            } else if (mOrc.get(bm, mOrc.IN_CHECK_IN)) {
                actionCheckIn();
                return;
            } else if (mOrc.get(bm, mOrc.IN_SELECT_HERO)) {
                actionSelectHero();
                return;
            } else if (mOrc.get(bm, mOrc.GET_SIGN_IN_REWARD)) {
                actionGetReward();
                return;
            } else if (mOrc.get(bm, mOrc.READY_TO_MINE) || mOrc.get(bm, mOrc.END_MINE)) {
                actionGoInMine();
                return;
            } else if (mOrc.get(bm, mOrc.IN_GJ)) {
                actionGetReward();
                return;
            } else if (mOrc.get(bm, mOrc.IN_GAME)) {
                if(mLevel == 0){
                    mLevel = mOrc.getHeroLevel(bm);
                }
                if (mLevel > 30 && !onlySign) {
                    if (!haveNewTitle) {
                        actionGoToMine();
                    } else {
                        actionGetReward();
                    }
                } else {
                    actionOutGame();
                    return;
                }
                return;
            }
        }
    }
    private boolean actionCheckIn(){
        int i = 0;
        Toast.makeText(ShellService.this, "进入小区", Toast.LENGTH_LONG).show();
        while(i < 10) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Bitmap bm = mOrc.screenCap();
            if(mOrc.get(bm, mOrc.IN_CHECK_IN)){
                String cmd = ShellCmd.checkIn(signServer);
                cmd += "echo sign_in_reward;\n";
                mLastAction = "check_in";
                changeServer += 1;
                exce(cmd);
                return true;
            }
            i += 1;
        }
        return false;
    }
    private boolean actionSelectHero(){
        Toast.makeText(ShellService.this, "选择人物", Toast.LENGTH_LONG).show();
        int i = 0;
        haveFlight = true;
        endMineTimeStamp = 0;
        mLevel = 0;
        while (i < 5) {
            i = i + 1;
            Bitmap mp = mOrc.screenCap();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (mOrc.get(mp, mOrc.IN_SELECT_HERO)) {
                String cmd;
                if(changeHeros >=4) {
                    cmd = ShellCmd.checkOut();
                    cmd = cmd + "echo sign_in_reward;\n";
                    mLastAction = "check_out";
                }else {
                    cmd = ShellCmd.selectHeroInGame();
                    cmd = cmd + "echo sign_in_reward;\n";
                    mLastAction = "select_hero";
                    changeHeros += 1;
                }
                exce(cmd);
                return true;
            }
        }
        return  false;
    }
    private boolean actionGetReward(){
        Toast.makeText(ShellService.this, "签到奖励", Toast.LENGTH_LONG).show();
        int i = 0;
        while(i < 5) {
            i = i + 1;
            Bitmap mp = mOrc.screenCap();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (mOrc.get(mp, mOrc.IN_GAME)) {
                String cmd1 = ShellCmd.getSignInReward();
                exce(cmd1);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Bitmap mp2 = mOrc.screenCap();
                if(mOrc.get(mp2, mOrc.GET_NEW_TITLE)){
                    String cmd = ShellCmd.closeTitle();
                    exce(cmd);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String cmd = ShellCmd.getReward();
                cmd = cmd + "echo sign_in_reward;\n";
                mLastAction = "get_reward";
                exce(cmd);
                return true;
            }
        }
        return false;
    }
    private boolean actionReadResolved(){
        Toast.makeText(ShellService.this, "准备分解", Toast.LENGTH_LONG).show();

        int i = 0;
        while(i < 20) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i = i + 1;
            Bitmap mp = mOrc.screenCap();
            if (mOrc.get(mp, mOrc.IN_GAME)) {
                String c1 = ShellCmd.clickReward();
                exce(c1);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(mLevel == 0) {
                     mLevel = mOrc.getHeroLevel(mp);
                    Log.i("orc level", String.valueOf(mLevel));
                }
                String c2;
                if(mLevel > 0) {
                    c2 = ShellCmd.toResolved(mLevel);
                    c2 = c2 + "echo sign_in_reward;\n";
                    mLastAction = "ready_resolve";
                }else {
                    c2 = "echo sign_in_reward;\n";
                    mLastAction = "resolved";
                }
                exce(c2);
                return true;
            }
        }
        return false;
    }
    private boolean actionResolved(){
        Toast.makeText(ShellService.this, "分解", Toast.LENGTH_LONG).show();
        int i = 0;
        while(i < 5) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i = i + 1;
            Bitmap mp2 = mOrc.screenCap();
            if(mOrc.get(mp2, mOrc.READY_RESOLVE)){
                String cmd = ShellCmd.resolved(mLevel);
                cmd = cmd + "echo sign_in_reward;\n";
                mLastAction = "resolved";
                exce(cmd);
                return true;
            }
        }
        return false;
    }
    private boolean actionGoToMine(){
        Toast.makeText(ShellService.this, "去往矿区", Toast.LENGTH_LONG).show();
        int i = 0;
        while(i < 10) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i = i + 1;
            Bitmap mp = mOrc.screenCap();
            if (mOrc.get(mp, mOrc.IN_GAME)) {
                String cmd;
                haveFlight = false;
                cmd = ShellCmd.WalktoSG();
                mLastAction = "to_sg";
                cmd = cmd + "echo sign_in_reward;\n";
                exce(cmd);
                return true;
            }
        }
        return false;
    }
    private  boolean actionSGToMine(){
        Toast.makeText(ShellService.this, "去往山谷矿区一层", Toast.LENGTH_LONG).show();
        int i = 0;
        while(i < 10) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i = i + 1;
            Bitmap mp = mOrc.screenCap();
            if (mOrc.get(mp, mOrc.IN_SG)) {
                String cmd = ShellCmd.WalktoMine();
                mLastAction = "to_mine";
                cmd = cmd + "echo sign_in_reward;\n";
                exce(cmd);
                return true;
            }
        }
        return false;
    }
    private boolean actionGoInMine(){
        Toast.makeText(ShellService.this, "进入挖矿", Toast.LENGTH_LONG).show();
        int i = 0;
        while (i < 5) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i = i + 1;
            Bitmap mp = mOrc.screenCap();
            if (mOrc.get(mp, mOrc.READY_TO_MINE)) {
                String cmd = ShellCmd.inMine();
                cmd = cmd + "echo sign_in_reward;\n";
                mLastAction = "go_in_mine";
                exce(cmd);
                return true;
            }else if(mOrc.get(mp, mOrc.IS_DEAD)){
                String cmd = ShellCmd.getBack();
                cmd = cmd + "echo sign_in_reward;\n";
                mLastAction = "dead_back";
                exce(cmd);
                return true;
            }else if(mOrc.get(mp, mOrc.GET_TASK)){
                String cmd = ShellCmd.clickTask();
                cmd = cmd + "echo sign_in_reward;\n";
                mLastAction = "dead_back";
                exce(cmd);
                return true;
            }else if(mOrc.get(mp, mOrc.IN_WORLD_MAP)){
                String cmd = ShellCmd.closeWindow();
                cmd = cmd + "echo sign_in_reward;\n";
                mLastAction = "dead_back";
                exce(cmd);
                return true;
            }else if(mOrc.get(mp, mOrc.END_MINE)){
                String c = ShellCmd.endMine();
                exce(c);
                String cmd = ShellCmd.outGame();
                cmd = cmd + "echo sign_in_reward;\n";
                mLastAction = "out_game";
                exce(cmd);
                return true;
            }else if(haveFlight && android.os.Build.VERSION.SDK_INT >= 18 && (!mOrc.get(mp, mOrc.READY_TO_MINE) && !mOrc.get(mp, mOrc.IS_DEAD))){
                mLastAction = "dead_back";
                haveFlight = false;
                mExcuteHandler.sendEmptyMessage(SIGN_IN_REWARD);
                return true;
            }
        }
        return false;
    }
    private boolean actionMine(){
        Toast.makeText(ShellService.this, "开始挖矿", Toast.LENGTH_LONG).show();
        boolean hasInMine = false;
        int i = 0;
        while (i < 10) {
            i = i + 1;
            Bitmap mp2 = mOrc.screenCap();
            if(mOrc.get(mp2, mOrc.IN_MINE)){
                long now = System.currentTimeMillis();
                if(now > endMineTimeStamp){
                    endMineTimeStamp  = now + 30 * 60 * 1000;
                }
                String c = ShellCmd.Mine();
                exce(c);
                mp2 = mOrc.screenCap();
                while(mOrc.get(mp2, mOrc.IN_MINE)){
                    hasInMine = true;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(mOrc.get(mp2, mOrc.IN_TIP)){
                        String cmd = ShellCmd.closeTip();
                        exce(cmd);
                    }
                    if(mOrc.get(mp2, mOrc.IS_DEAD)){
                        String ca = ShellCmd.getBack();
                        ca = ca + "echo sign_in_reward;\n";
                        mLastAction = "dead_back";
                        exce(ca);
                        return true;
                    }
                    mp2 = mOrc.screenCap();
                }
            }else if(mOrc.get(mp2, mOrc.END_MINE)){
                String c = ShellCmd.endMine();
                exce(c);
                String cmd = ShellCmd.outGame();
                cmd = cmd + "echo sign_in_reward;\n";
                mLastAction = "out_game";
                exce(cmd);
                return true;
            }
            long now = System.currentTimeMillis();
            if(now > endMineTimeStamp){
                break;
            }
            mp2 = mOrc.screenCap();
            if(!mOrc.get(mp2, mOrc.IN_MINE) && hasInMine){
                break;
            }
        }
        long now = System.currentTimeMillis();
        if(now < endMineTimeStamp){
            Bitmap mp = mOrc.screenCap();
            if(mOrc.get(mp, mOrc.IS_DEAD)){
                String ca = ShellCmd.getBack();
                exce(ca);
            }
            if(mOrc.get(mp, mOrc.GET_NEW_TITLE)){
                String ca = ShellCmd.closeTitle();
                exce(ca);
            }
            if(mOrc.get(mp, mOrc.IN_ACTIVITY)){
                String ca = ShellCmd.closeWindow();
                exce(ca);
            }
            String ca = ShellCmd.clickReward();
            return  actionGoToMine();
        }else{
            Bitmap mp = mOrc.screenCap();
            if(mOrc.get(mp, mOrc.IS_DEAD)){
                String ca = ShellCmd.getBack();
                exce(ca);
            }
            return actionOutGame();
        }
    }
    private void actionClickReward(){
        String cmd = ShellCmd.clickReward();
        exce(cmd);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private  boolean actionOutGame(){
        actionClickReward();
        String cmd = ShellCmd.outGame();
        cmd = cmd + "echo sign_in_reward;\n";
        mLastAction = "out_game";
        exce(cmd);
        return true;
    }
    private boolean actionCheckOut(){
        Toast.makeText(ShellService.this, "退出小区", Toast.LENGTH_LONG).show();
        mLevel = 0;
        if(changeServer < signServer) {
            int i = 0;
            while (i < 10) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i = i + 1;
                Bitmap mp = mOrc.screenCap();
                if (mOrc.get(mp, mOrc.IN_CHECK_IN)) {
                    String cmd = ShellCmd.checkIn(signServer);
                    cmd = cmd + "echo sign_in_reward;\n";
                    mLastAction = "check_in";
                    changeServer += 1;
                    changeHeros = 0;
                    haveFlight = true;
                    exce(cmd);
                    return true;
                }
            }
        }else{
            running = false;
            return true;
        }
        return false;
    }

    public ShellService() {
        initProcess();
    }
    private void createFloatView(){
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        mContainer = new LinearLayout(this);
        mContainer.setOrientation(LinearLayout.HORIZONTAL);

        int type = WindowManager.LayoutParams.TYPE_PHONE;
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < 25) {
            type = WindowManager.LayoutParams.TYPE_TOAST;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        windowManager.addView(mContainer, params);

        mFloatView = new LinearLayout(this);
        LinearLayout.LayoutParams a = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mT = new TextView(this);
        mT.setText("启动");
        mT.setBackgroundColor(getResources().getColor(R.color.grey));
        mFloatView.addView(mT, a);
        mContainer.addView(mFloatView, a);
        paramsF = params;

        instance = this;

        mFloatView.setOnTouchListener(new View.OnTouchListener() {

            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override public boolean onTouch(View v, MotionEvent event) {



                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        _isMoving = false;
                        initialX = paramsF.x;
                        initialY = paramsF.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        _isMoving = true;
                        paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                        paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(mContainer, paramsF);
                        break;
                }
                return false;
            }
        });

        mFloatView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!_isMoving) {
                    if(running){
                        running = false;
                        mT.setText("启动");
                    }else{
                        running = true;
                        ArrayList<Integer> xianlu = mIntent.getIntegerArrayListExtra("xianlu");
                        String action = mIntent.getStringExtra("action");
                        String cmd = getCmd(action, xianlu);
                        exce(cmd);
                        mT.setText("停止");
                    }
                }
            }
        });
        
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public int onStartCommand(Intent intent, int flags, int startId) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.news_gift)  // the status icon
                .setTicker("")  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("脚本正在运行")  // the label of the entry
                .setContentText("永不crash")  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();
        startForeground(1234, notification);
        if(intent != null) {
            mIntent = intent;
        }
        ArrayList<Integer> xianlu = mIntent.getIntegerArrayListExtra("xianlu");
        String action = mIntent.getStringExtra("action");
        String cmd = getCmd(action, xianlu);
        exce(cmd);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        windowManager.removeView(mContainer);
    }

    private String getCmd(String action,ArrayList<Integer> xianlu ){
        if(!running){
            return "";
        }
        String ret = "";
        if(action.equals("restart_self")){
            ret = ShellCmd.restartSelf();
        }if(action.equals("restart_game")){
            ret = ShellCmd.restartRXCQ();
        }else if(action.equals("fire")){
            ret = ShellCmd.getFireCmdLeftDown(xianlu.get(0));
            ret += "echo end_cmd\n";
        }else if(action.equals("get_money")){
            ret = ShellCmd.getMoneyLeftDown(xianlu);
            ret += "echo end_cmd\n";
        }else if(action.equals("get_money_alone")){
            ret = ShellCmd.getMoneyAlone(xianlu);
            ret += "echo end_cmd\n";
        }else if(action.equals("getback")){
            ret = ShellCmd.getBack(xianlu.get(0));
            ret += "echo end_cmd\n";
        }else if(action.equals("test")){
            changeHeros = 0;
            changeServer = 0;
            signServer = xianlu.get(0);
            onlySign = false;
            if(signServer > 4){
                onlySign = true;
                signServer = signServer - 4;
            }
            mLastAction = "begin";
            ret = "echo sign_in_reward\n";
        }else if(action.equals("kill")){
            ret = ShellCmd.getKill(xianlu.get(0));
            ret += "echo end_cmd\n";
        }
        return ret;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void initProcess(){
        try {
            mOrc = new GameOCR();
            mProcess = Runtime.getRuntime().exec("su");
            mInput = new DataOutputStream(mProcess.getOutputStream());
            mOutput = new InputStreamReader(mProcess.getInputStream());
            mError = new InputStreamReader(mProcess.getErrorStream());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getOutPut();
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getError();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exce(String cmd){
        try {
            Log.i("orc execut Shell", cmd);
            mInput.write(cmd.getBytes());
            mInput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getOutPut(){
        try {
            BufferedReader br = new BufferedReader(mOutput);
            String line;
            while ((line = br.readLine()) != null){
                Log.i("orc Shell log Output", line);
                if (line.trim().equals("end_cmd")){
                    Message ms = new Message();
                    ms.what = FINISH_END;
                    ms.obj = new String(line);
                    mExcuteHandler.sendMessage(ms);
                } else if (line.trim().equals("end_cap")){
                    Message ms = new Message();
                    ms.what = TEST_IS_DEAD;
                    ms.obj = new String(line);
                    mExcuteHandler.sendMessage(ms);
                }else if (line.trim().equals("end_back")){
                    Message ms = new Message();
                    ms.what = END_BACK;
                    ms.obj = new String(line);
                    mExcuteHandler.sendMessage(ms);
                }else if (line.trim().equals("sign_in_reward")){
                    Message ms = new Message();
                    ms.what = SIGN_IN_REWARD;
                    ms.obj = new String(line);
                    mExcuteHandler.sendMessage(ms);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getError(){
        try {
            BufferedReader br = new BufferedReader(mError);
            String line;
            while ((line = br.readLine()) != null){
                Log.i("Shell log Error", line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}