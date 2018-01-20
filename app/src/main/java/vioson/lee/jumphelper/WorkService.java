package vioson.lee.jumphelper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by viosonlee
 * on 2018/1/20.
 * for 工作
 */

public class WorkService extends Service {
    private static double TIMES = 1.37;//倍数
    private LinearLayout btn;
    private LinearLayout pointLayout;
    private WindowManager.LayoutParams lp1;
    private WindowManager.LayoutParams lp2;
    private WindowManager windowManager;
    private PointF startP = null;
    private PointF endP = null;
    private boolean hasBtn;
    private boolean hasLayout;
    private boolean running;
    private int screenWidth, screenHeight;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        sendBtn();
        su();
        return super.onStartCommand(intent, flags, startId);
    }

    private void init() {
        btn = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.view_btn, null);
        pointLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.view_point_layout, null);
        screenWidth = ScreenUtil.getScreenWidth(this);
        screenHeight = ScreenUtil.getScreenHeight(this);
        TIMES = TIMES * (Math.sqrt(4852800)) / Math.sqrt(screenWidth * screenWidth + screenHeight * screenHeight);
        Log.i("速度", "times:" + TIMES);
        btn.setOnClickListener(new ClickListener());
        pointLayout.setOnTouchListener(new TouchListener());
    }

    class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (running) {
                running = false;
                btn.setBackgroundResource(R.drawable.play);
                hideLayout();
            } else {
                running = true;
                btn.setBackgroundResource(R.drawable.stop);
                startP = null;
                sendLayout();
            }
        }
    }

    class TouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (startP == null) {
                    startP = new PointF(event.getX(), event.getY());
                } else {
                    endP = new PointF(event.getX(), event.getY());
                    hideLayout();
                    Log.e("Start", startP.toString());
                    Log.e("End", endP.toString());
                    Log.e("Dis", "" + getTouchTime());
                    touch();
                }
            }
            return true;
        }
    }

    private void su() {
        try {
            Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private long getTouchTime() {
        float disX = Math.abs(startP.x - endP.x);
        float disY = Math.abs(startP.y - endP.y);
        double dis = Math.sqrt(disX * disX + disY * disY);
        return (long) (dis * TIMES);
    }

    /**
     * 执行shell命令
     *
     * @param cmd
     */
    private void execShellCmd(String cmd) {

        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(
                    outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void touch() {
// input swipe 100 100 100 100 1000 //在 100 100 位置长按 1000毫秒
        execShellCmd("input swipe " + startP.x + " " + startP.y + " " + endP.x + " " + endP.y + " " + getTouchTime());
        if (running)
            new Handler(Looper.getMainLooper())
                    .postDelayed(() -> {
                        startP = null;
                        sendLayout();
                    }, getTouchTime() + 1000);
    }

    private void sendBtn() {
        if (hasBtn) return;
        if (windowManager == null) {
            windowManager = (WindowManager) this.getApplication().getSystemService(Context.WINDOW_SERVICE);
        }
        if (lp1 == null) {
            lp1 = new WindowManager.LayoutParams();
            lp1.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            lp1.format = PixelFormat.RGBA_8888;
            lp1.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            lp1.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            lp1.width = getResources().getDimensionPixelOffset(R.dimen.dp50);
            lp1.height = getResources().getDimensionPixelOffset(R.dimen.dp50);
        }
        windowManager.addView(btn, lp1);
        hasBtn = true;
    }

    private void hideBtn() {
        if (!hasBtn) return;
        hasBtn = false;
        if (windowManager != null && null != btn) {
            windowManager.removeView(btn);
        }
    }

    private void sendLayout() {
        if (hasLayout) return;
        if (windowManager == null) {
            windowManager = (WindowManager) this.getApplication().getSystemService(Context.WINDOW_SERVICE);
        }
        if (lp2 == null) {
            lp2 = new WindowManager.LayoutParams();
            lp2.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            lp2.format = PixelFormat.RGBA_8888;
            lp2.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            lp2.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            lp2.width = screenWidth;
            lp2.height = screenHeight - getResources().getDimensionPixelOffset(R.dimen.dp100);
        }
        windowManager.addView(pointLayout, lp2);
        hasLayout = true;
    }

    private void hideLayout() {
        if (!hasLayout) return;
        hasLayout = false;
        if (windowManager != null && null != pointLayout) {
            windowManager.removeView(pointLayout);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideBtn();
        hideLayout();
        hasLayout = false;
        hasBtn = false;
    }


}
