package com.example.drinkorder;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

/**
 * 购物车页面
 * 展示用户购物车中的商品列表，支持数量修改/删除/总价计算，以及结算下单
 */
public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private DatabaseHelper dbHelper;
    private List<CartItem> cartItemList;
    private int userId;
    private TextView tvTotalPrice, tvEmpty;
    private Button btnCheckout;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        dbHelper = new DatabaseHelper(this);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        recyclerView = findViewById(R.id.recycler_view);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        tvEmpty = findViewById(R.id.tv_empty);
        btnCheckout = findViewById(R.id.btn_checkout);
        ivBack = findViewById(R.id.iv_back);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkout();
            }
        });

        loadCartItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }

    /** 加载购物车列表，空时显示空状态提示 */
    private void loadCartItems() {
        cartItemList = dbHelper.getCartItems(userId);
        if (cartItemList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            btnCheckout.setEnabled(false);
            tvTotalPrice.setText("总价：¥0.00");
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            btnCheckout.setEnabled(true);
            cartAdapter = new CartAdapter(cartItemList, dbHelper, this);
            recyclerView.setAdapter(cartAdapter);
            calculateTotalPrice();
        }
    }

    /** 计算并显示购物车总价 */
    public void calculateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItemList) {
            total += item.getTotalPrice();
        }
        tvTotalPrice.setText("总价：¥" + String.format("%.2f", total));
    }

    /** 结算：生成订单JSON → 写入Orders表 → 清空购物车 */
    private void checkout() {
        try {
            JSONArray itemsArray = new JSONArray();
            double totalPrice = 0;
            for (CartItem item : cartItemList) {
                JSONObject itemJson = new JSONObject();
                itemJson.put("product_id", item.getProductId());
                itemJson.put("product_name", item.getProductName());
                itemJson.put("quantity", item.getQuantity());
                itemJson.put("price", item.getPrice());
                itemsArray.put(itemJson);
                totalPrice += item.getTotalPrice();
            }

            dbHelper.createOrder(userId, totalPrice, itemsArray.toString());
            dbHelper.clearCart(userId);
            Toast.makeText(this, "下单成功", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "下单失败", Toast.LENGTH_SHORT).show();
        }
    }
}
