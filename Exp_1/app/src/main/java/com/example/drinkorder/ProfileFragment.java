package com.example.drinkorder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * 个人中心Fragment
 * 展示当前用户的用户名、昵称、角色，提供退出登录功能
 */
public class ProfileFragment extends Fragment {
    private TextView tvUsername, tvNickname, tvRole;
    private Button btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUsername = view.findViewById(R.id.tv_username);
        tvNickname = view.findViewById(R.id.tv_nickname);
        tvRole = view.findViewById(R.id.tv_role);
        btnLogout = view.findViewById(R.id.btn_logout);

        // 从SharedPreferences读取当前用户信息
        SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", getActivity().MODE_PRIVATE);
        String username = prefs.getString("username", "");
        String nickname = prefs.getString("nickname", "");
        String role = prefs.getString("role", "");

        tvUsername.setText("用户名：" + username);
        tvNickname.setText("昵称：" + nickname);
        tvRole.setText("角色：" + ("admin".equals(role) ? "管理员" : "普通用户"));

        // 退出登录：清除session → 跳转登录页
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("user_prefs", getActivity().MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }
}
