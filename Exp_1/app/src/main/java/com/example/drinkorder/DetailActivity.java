package com.example.drinkorder;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品详情页
 * 展示饮品完整信息（图、名、分类、价格、评分、描述），提供"加入购物车"功能
 */
public class DetailActivity extends AppCompatActivity {
    private ImageView ivProduct, ivBack;
    private TextView tvName, tvCategory, tvPrice, tvRating, tvDescription;
    private Button btnAddToCart;
    private RadioGroup rgSweetness, rgIce;
    private CheckBox cbPearl, cbCoconut, cbRedBean, cbPudding, cbGrassJelly;
    private DatabaseHelper dbHelper;
    private int productId;
    private int userId;

    private static final double TOPPING_PRICE_NORMAL = 2.0;
    private static final double TOPPING_PRICE_PUDDING = 3.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dbHelper = new DatabaseHelper(this);

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
        rgSweetness = findViewById(R.id.rg_sweetness);
        rgIce = findViewById(R.id.rg_ice);
        cbPearl = findViewById(R.id.topping_pearl);
        cbCoconut = findViewById(R.id.topping_coconut);
        cbRedBean = findViewById(R.id.topping_red_bean);
        cbPudding = findViewById(R.id.topping_pudding);
        cbGrassJelly = findViewById(R.id.topping_grass_jelly);

        loadProductDetail();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });
    }

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

    private void addToCart() {
        Product product = dbHelper.getProductById(productId);
        if (product == null) return;

        String sweetness = getSelectedSweetness();
        String ice = getSelectedIce();
        List<String> selectedToppings = getSelectedToppings();
        String toppingsStr = String.join(",", selectedToppings);
        double toppingsPrice = calculateToppingsPrice(selectedToppings);

        dbHelper.addToCart(userId, productId, 1, product.getPrice(),
                sweetness, ice, toppingsStr, toppingsPrice);
        Toast.makeText(this, "已加入购物车", Toast.LENGTH_SHORT).show();
    }

    private String getSelectedSweetness() {
        int id = rgSweetness.getCheckedRadioButtonId();
        if (id == R.id.sweet_full) return "全糖";
        if (id == R.id.sweet_70) return "七分糖";
        if (id == R.id.sweet_50) return "半糖";
        if (id == R.id.sweet_30) return "三分糖";
        if (id == R.id.sweet_0) return "无糖";
        return "七分糖";
    }

    private String getSelectedIce() {
        int id = rgIce.getCheckedRadioButtonId();
        if (id == R.id.ice_extra) return "多冰";
        if (id == R.id.ice_normal) return "正常冰";
        if (id == R.id.ice_less) return "少冰";
        if (id == R.id.ice_no) return "去冰";
        if (id == R.id.ice_hot) return "热饮";
        return "正常冰";
    }

    private List<String> getSelectedToppings() {
        List<String> toppings = new ArrayList<>();
        if (cbPearl.isChecked()) toppings.add("珍珠");
        if (cbCoconut.isChecked()) toppings.add("椰果");
        if (cbRedBean.isChecked()) toppings.add("红豆");
        if (cbPudding.isChecked()) toppings.add("布丁");
        if (cbGrassJelly.isChecked()) toppings.add("仙草");
        return toppings;
    }

    private double calculateToppingsPrice(List<String> toppings) {
        double price = 0;
        for (String t : toppings) {
            if (t.equals("布丁")) {
                price += TOPPING_PRICE_PUDDING;
            } else {
                price += TOPPING_PRICE_NORMAL;
            }
        }
        return price;
    }
}
