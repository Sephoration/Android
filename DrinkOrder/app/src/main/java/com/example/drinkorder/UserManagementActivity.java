package com.example.drinkorder;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {
    private RecyclerView rvUsers;
    private UserAdapter adapter;
    private List<User> userList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        dbHelper = new DatabaseHelper(this);
        rvUsers = findViewById(R.id.rv_users);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        loadUsers();
    }

    private void loadUsers() {
        userList = dbHelper.getAllUsers();
        if (userList == null) {
            userList = new ArrayList<>();
        }
        adapter = new UserAdapter(userList);
        adapter.setOnDeleteClickListener(new UserAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                User user = userList.get(position);
                dbHelper.deleteUserWithData(user.getId());
                userList.remove(position);
                adapter.notifyItemRemoved(position);
                Toast.makeText(UserManagementActivity.this, "已删除用户：" + user.getUsername(), Toast.LENGTH_SHORT).show();
            }
        });
        rvUsers.setAdapter(adapter);
    }
}
