package vioson.lee.jumphelper;

import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.root_layout).setOnClickListener(v -> Toast.makeText(this, "我被点击了", Toast.LENGTH_SHORT).show());
    }

    private void startService() {
        intent = new Intent(MainActivity.this, WorkService.class);
        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(MainActivity.this)) {
                Toast.makeText(MainActivity.this, "已开启Toucher", Toast.LENGTH_SHORT).show();
                startService(this.intent);
                moveTaskToBack(false);
            } else {
                //若没有权限，提示获取.
                this.intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                Toast.makeText(MainActivity.this, "需要取得权限以使用悬浮窗", Toast.LENGTH_SHORT).show();
                startActivity(this.intent);
            }
        } else {
            //SDK在23以下，不用管.
            startService(this.intent);
            finish();
        }
    }

    public void start(View view) {
        startService();
    }

    public void stop(View view) {
        stopService(intent);
    }
}
