package com.example.drinkorder;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * 订单列表页
 * 展示当前用户的历史订单，包含订单编号、时间、商品清单和总价
 */
public class OrderActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private DatabaseHelper dbHelper;
    private List<OrderItem> orderItemList;
    private int userId;
    private TextView tvEmpty;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        dbHelper = new DatabaseHelper(this);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        recyclerView = findViewById(R.id.recycler_view);
        tvEmpty = findViewById(R.id.tv_empty);
        ivBack = findViewById(R.id.iv_back);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadOrders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }

    /** 从数据库加载当前用户的订单列表 */
    private void loadOrders() {
        orderItemList = dbHelper.getOrdersByUserId(userId);
        if (orderItemList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            orderAdapter = new OrderAdapter(orderItemList);
            recyclerView.setAdapter(orderAdapter);
        }
    }
}
