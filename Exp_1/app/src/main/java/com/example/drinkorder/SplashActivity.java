package com.example.drinkorder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 启动页 — 应用入口
 * 显示2秒后自动跳转：已登录→主页，未登录→登录页
 */
public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 延迟2秒后检查登录状态并跳转
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                int userId = prefs.getInt("user_id", -1);
                Intent intent;
                if (userId != -1) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_DELAY);
    }
}
