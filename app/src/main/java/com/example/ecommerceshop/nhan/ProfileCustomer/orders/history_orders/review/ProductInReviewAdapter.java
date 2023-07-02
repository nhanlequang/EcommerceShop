package com.example.ecommerceshop.nhan.ProfileCustomer.orders.history_orders.review;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceshop.R;
import com.example.ecommerceshop.nhan.Model.Product;
import com.example.ecommerceshop.nhan.Model.Review;
import com.example.ecommerceshop.nhan.ProfileCustomer.orders.history_orders.HistoryOrder;
import com.example.ecommerceshop.nhan.ProfileCustomer.orders.history_orders.HistoryOrdersAdapter;
import com.example.ecommerceshop.nhan.ProfileCustomer.orders.history_orders.HistoryProductsInOrderAdapter;
import com.example.ecommerceshop.nhan.ProfileCustomer.orders.history_orders.IClickHistoryOrderListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ProductInReviewAdapter extends RecyclerView.Adapter<ProductInReviewAdapter.ReviewViewholder> {

    private Context context;
    private ArrayList<Product> productList;
    private ArrayList<String> uriList;
    private IClickProductInReviewListener mClickProductInReviewListener;
    public ProductInReviewAdapter(Context context, ArrayList<Product> productList, IClickProductInReviewListener clickListener) {
        this.context = context;
        this.productList = productList;
        this.mClickProductInReviewListener = clickListener;
    }
    @NonNull
    @Override
    public ProductInReviewAdapter.ReviewViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_write_rating_product, parent,false);
        return new ProductInReviewAdapter.ReviewViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductInReviewAdapter.ReviewViewholder holder, int position) {
        Product product = productList.get(position);
        Picasso.get().load(Uri.parse(product.getProductAvatar())).into(holder.iv_ProductAvatar);
        holder.tv_ProductName.setText(product.getProductName());
        holder.btn_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickProductInReviewListener.RemoveReview(product);
            }
        });
        holder.rb_Rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                mClickProductInReviewListener.RatingBarChange(ratingBar, v, b, product);
            }
        });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ReviewViewholder extends RecyclerView.ViewHolder{
        ImageView iv_ProductAvatar;
        TextView tv_ProductName;
        ImageView btn_Close;
        RatingBar rb_Rating;
        EditText et_Comment;
        RecyclerView rv_ListImage;
        LinearLayout btn_AddImage;
        public ReviewViewholder(@NonNull View itemView) {
            super(itemView);
            iv_ProductAvatar = itemView.findViewById(R.id.iv_ProductAvatar);
            tv_ProductName = itemView.findViewById(R.id.tv_ProductName);
            btn_Close = itemView.findViewById(R.id.btn_Close);
            rb_Rating = itemView.findViewById(R.id.rb_Rating);
            et_Comment = itemView.findViewById(R.id.et_Comment);
            rv_ListImage = itemView.findViewById(R.id.rv_ListImage);
            btn_AddImage = itemView.findViewById(R.id.btn_AddImage);
        }
    }
}