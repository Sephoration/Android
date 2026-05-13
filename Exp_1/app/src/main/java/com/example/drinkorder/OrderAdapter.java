package com.example.drinkorder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

/**
 * 订单列表适配器
 * 展示每条订单的编号、时间、总价和商品明细（从JSON解析）
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<OrderItem> orderItemList;

    public OrderAdapter(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderItem order = orderItemList.get(position);
        holder.tvOrderId.setText("订单号：" + order.getId());
        holder.tvOrderTime.setText("下单时间：" + order.getOrderTime());
        holder.tvTotalPrice.setText("总价：¥" + String.format("%.2f", order.getTotalPrice()));

        // 从JSON解析商品清单（格式：[{"product_name":"拿铁咖啡","quantity":2},...]）
        try {
            JSONArray itemsArray = new JSONArray(order.getItems());
            StringBuilder itemsText = new StringBuilder();
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject item = itemsArray.getJSONObject(i);
                itemsText.append(item.getString("product_name"))
                        .append(" x")
                        .append(item.getInt("quantity"))
                        .append("\n");
            }
            holder.tvItems.setText(itemsText.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderTime, tvTotalPrice, tvItems;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderTime = itemView.findViewById(R.id.tv_order_time);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvItems = itemView.findViewById(R.id.tv_items);
        }
    }
}
