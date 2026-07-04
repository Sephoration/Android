package com.example.drinkorder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * 主页面 — 底部导航容器
 * 首页/个人中心使用Fragment切换，购物车/订单使用Activity跳转
 */
public class MainActivity extends AppCompatActivity {
    private FrameLayout frameLayout;
    private ImageButton btnHome, btnCart, btnOrder, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = findViewById(R.id.frame_layout);
        btnHome = findViewById(R.id.btn_home);
        btnCart = findViewById(R.id.btn_cart);
        btnOrder = findViewById(R.id.btn_order);
        btnProfile = findViewById(R.id.btn_profile);

        // 默认显示首页
        replaceFragment(new HomeFragment());
        btnHome.setSelected(true);

        // 底部导航点击事件
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new HomeFragment());
                setSelectedButton(btnHome);
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                startActivity(intent);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ProfileFragment());
                setSelectedButton(btnProfile);
            }
        });
    }

    /** 替换主内容区域的Fragment */
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    /** 切换底部按钮选中状态（高亮当前Tab） */
    private void setSelectedButton(ImageButton selectedButton) {
        btnHome.setSelected(false);
        btnProfile.setSelected(false);
        selectedButton.setSelected(true);
    }
}
