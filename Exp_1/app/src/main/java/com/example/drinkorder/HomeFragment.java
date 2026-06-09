package com.example.drinkorder;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页Fragment — 展示饮品列表
 * 支持按名称搜索（实时）和按分类筛选（下拉框）
 */
public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private DatabaseHelper dbHelper;
    private List<Product> productList;
    private EditText etSearch;
    private Spinner spinnerCategory;
    private String currentCategory = "全部";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = new DatabaseHelper(getContext());

        recyclerView = view.findViewById(R.id.recycler_view);
        etSearch = view.findViewById(R.id.et_search);
        spinnerCategory = view.findViewById(R.id.spinner_category);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupCategorySpinner();
        loadProducts();

        // 实时搜索：输入即搜索（TextWatcher）
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    /** 初始化分类筛选下拉框 */
    private void setupCategorySpinner() {
        String[] categories = {"全部", "咖啡", "奶茶", "果茶"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCategory = categories[position];
                loadProducts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /** 加载商品列表（根据当前选中的分类） */
    private void loadProducts() {
        if (currentCategory.equals("全部")) {
            productList = dbHelper.getAllProducts();
        } else {
            productList = dbHelper.getProductsByCategory(currentCategory);
        }
        productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);

        // 点击商品项跳转到详情页
        productAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Product product = productList.get(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }
        });
    }

    /** 按名称搜索商品（关键词为空时恢复全部列表） */
    private void searchProducts(String keyword) {
        if (keyword.isEmpty()) {
            loadProducts();
        } else {
            productList = dbHelper.searchProducts(keyword);
            productAdapter = new ProductAdapter(productList);
            recyclerView.setAdapter(productAdapter);
            productAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Product product = productList.get(position);
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("product_id", product.getId());
                    startActivity(intent);
                }
            });
        }
    }
}
