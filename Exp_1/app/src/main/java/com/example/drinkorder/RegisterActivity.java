package com.example.drinkorder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 注册页面
 * 校验输入合法性 → 检查用户名唯一性 → 写入数据库（密码由DatabaseHelper自动加密）
 */
public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etPassword, etConfirmPassword, etNickname;
    private Button btnRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etNickname = findViewById(R.id.et_nickname);
        btnRegister = findViewById(R.id.btn_register);

        // 注册按钮 — 校验 → 查重 → 插入数据库
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();
                String nickname = etNickname.getText().toString().trim();

                // 输入校验
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(RegisterActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "请确认密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 检查用户名是否已被注册
                if (dbHelper.isUsernameExists(username)) {
                    Toast.makeText(RegisterActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 昵称可选，未填写则默认使用用户名
                if (TextUtils.isEmpty(nickname)) {
                    nickname = username;
                }

                long result = dbHelper.insertUser(username, password, "user", nickname);
                if (result != -1) {
                    Toast.makeText(RegisterActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
