package com.example.drinkorder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * 购物车列表适配器
 * 管理购物车单项的展示，处理数量加减和删除操作，并回调更新总价
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItemList;
    private DatabaseHelper dbHelper;
    private CartActivity activity;

    public CartAdapter(List<CartItem> cartItemList, DatabaseHelper dbHelper, CartActivity activity) {
        this.cartItemList = cartItemList;
        this.dbHelper = dbHelper;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);
        holder.tvName.setText(item.getProductName());
        holder.tvPrice.setText("¥" + String.format("%.2f", item.getPrice() + item.getToppingsPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.ivImage.setImageResource(item.getImageResId());

        StringBuilder customText = new StringBuilder();
        if (item.getSweetness() != null && !item.getSweetness().isEmpty()) {
            customText.append(item.getSweetness());
        }
        if (item.getIce() != null && !item.getIce().isEmpty()) {
            if (customText.length() > 0) customText.append(" / ");
            customText.append(item.getIce());
        }
        if (item.getToppings() != null && !item.getToppings().isEmpty()) {
            if (customText.length() > 0) customText.append(" / ");
            customText.append("+").append(item.getToppings());
        }
        holder.tvCustom.setText(customText.toString());

        // 减少数量（最少1件）
        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = item.getQuantity();
                if (quantity > 1) {
                    quantity--;
                    item.setQuantity(quantity);
                    dbHelper.updateCartItemQuantity(item.getId(), quantity);
                    holder.tvQuantity.setText(String.valueOf(quantity));
                    if (activity != null) {
                        activity.calculateTotalPrice();
                    }
                }
            }
        });

        // 增加数量
        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = item.getQuantity();
                quantity++;
                item.setQuantity(quantity);
                dbHelper.updateCartItemQuantity(item.getId(), quantity);
                holder.tvQuantity.setText(String.valueOf(quantity));
                if (activity != null) {
                    activity.calculateTotalPrice();
                }
            }
        });

        // 删除该购物车项
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteCartItem(item.getId());
                cartItemList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItemList.size());
                if (activity != null) {
                    activity.calculateTotalPrice();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public void setActivity(CartActivity activity) {
        this.activity = activity;
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvCustom, tvPrice, tvQuantity;
        Button btnMinus, btnPlus, btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCustom = itemView.findViewById(R.id.tv_custom);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
