package com.example.drinkorder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/** 商品列表适配器 — 在RecyclerView中展示饮品卡片，支持点击跳转详情 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvName.setText(product.getName());
        holder.tvCategory.setText(product.getCategory());
        holder.tvPrice.setText("¥" + product.getPrice());
        holder.tvRating.setText(String.valueOf(product.getRating()));
        holder.ivImage.setImageResource(product.getImageResId());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvCategory, tvPrice, tvRating;

        public ProductViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvRating = itemView.findViewById(R.id.tv_rating);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
