package com.example.drinkorder;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 商品详情页
 * 展示饮品完整信息（图、名、分类、价格、评分、描述），提供"加入购物车"功能
 */
public class DetailActivity extends AppCompatActivity {
    private ImageView ivProduct, ivBack;
    private TextView tvName, tvCategory, tvPrice, tvRating, tvDescription;
    private Button btnAddToCart;
    private DatabaseHelper dbHelper;
    private int productId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dbHelper = new DatabaseHelper(this);

        // 获取当前登录用户的ID
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        productId = getIntent().getIntExtra("product_id", -1);

        ivProduct = findViewById(R.id.iv_product);
        ivBack = findViewById(R.id.iv_back);
        tvName = findViewById(R.id.tv_name);
        tvCategory = findViewById(R.id.tv_category);
        tvPrice = findViewById(R.id.tv_price);
        tvRating = findViewById(R.id.tv_rating);
        tvDescription = findViewById(R.id.tv_description);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);

        loadProductDetail();

        // 返回按钮
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 加入购物车 → 写入Cart表
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product = dbHelper.getProductById(productId);
                if (product != null) {
                    dbHelper.addToCart(userId, productId, 1, product.getPrice());
                    Toast.makeText(DetailActivity.this, "已加入购物车", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /** 根据productId加载并展示商品详情 */
    private void loadProductDetail() {
        Product product = dbHelper.getProductById(productId);
        if (product != null) {
            tvName.setText(product.getName());
            tvCategory.setText(product.getCategory());
            tvPrice.setText("¥" + product.getPrice());
            tvRating.setText(String.valueOf(product.getRating()));
            tvDescription.setText(product.getDescription());
            ivProduct.setImageResource(product.getImageResId());
        }
    }
}
