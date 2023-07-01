package com.example.ecommerceshop.qui.cart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerceshop.R;
import com.example.ecommerceshop.databinding.AdapterItemOnCartBinding;
import com.example.ecommerceshop.databinding.FragmentCartBinding;
import com.example.ecommerceshop.nhan.ProfileCustomer.orders.history_orders.HistoryOrder;
import com.example.ecommerceshop.qui.homeuser.Product;
import com.example.ecommerceshop.qui.payment.PaymentActivity;
import com.example.ecommerceshop.qui.product_detail.Cart;
import com.example.ecommerceshop.qui.product_detail.ProductDetailActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.sql.Array;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class CartFragment extends Fragment {

    View mView;
    FragmentCartBinding mFragmentCartBinding;
    FirebaseUser mCurrentUser;
    private long totalMoney;

    private List<ShopProductCart> mShopListProductCarts;
    private ShopProductCartAdapter shopProductCartAdapter;

    private List<ProductCart> listSelectedProductCart;
    int numSelectedCart = 0;
    private HistoryOrder historyOrder;
    public CartFragment() {}

    public CartFragment(HistoryOrder ho) {
        historyOrder = ho;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentCartBinding = FragmentCartBinding.inflate(inflater, container, false);
        mView = mFragmentCartBinding.getRoot();

        init();
        iListener();
        return mView;
    }

    private void init() {
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        listSelectedProductCart = new ArrayList<>();
        shopProductCartAdapter = new ShopProductCartAdapter(getContext(), new IClickProductCartItemListener() {
            @Override
            public void sendParentAdapter(boolean b, ProductCart productCart) {

            }

            @Override
            public void addListSelectedItem(ProductCart productCart) {

                addSelectedItemCart(productCart);
            }

            @Override
            public void removeListSelectedItem(ProductCart productCart) {
                removeSelectedItemCart(productCart);
            }

            @Override
            public void checkAllCheckbox() {

            }

            @Override
            public void sendInfoProduct(ProductCart productCart) {

            }

            @Override
            public void showProductDetail(ProductCart productCart) {
                showActivityProductDetail(productCart);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mFragmentCartBinding.rcvCart.setLayoutManager(linearLayoutManager);
        mFragmentCartBinding.rcvCart.setAdapter(shopProductCartAdapter);
        mShopListProductCarts = new ArrayList<>();
        shopProductCartAdapter.setData(mShopListProductCarts);
//        if(historyOrder != null)
//            loadHistoryOrderToCart(historyOrder);
        setListShopProductCarts();
    }

    private void showActivityProductDetail(ProductCart productCart) {
        String shopId = productCart.getShopId();
        String productId = productCart.getProductId();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users/" + shopId + "/Shop/Products/" + productId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                Intent intent = new Intent(getContext(), ProductDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", product);
                intent.putExtras(bundle);
                getContext().startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addSelectedItemCart(ProductCart productCart) {
        listSelectedProductCart.add(productCart);
        addTotalMoney(productCart);

        if (listSelectedProductCart.size() > 1) {
            return;
        }
        if (listSelectedProductCart.size() > 0) {

            showButtonDelete();
        }
    }

    private void removeSelectedItemCart(ProductCart productCart) {
        listSelectedProductCart.remove(productCart);
        subTotalMoney(productCart);
        if (listSelectedProductCart.size() == 0) {
            hideButtonDelete();
        }
    }

    private void setListShopProductCarts() {
        List<String> listShopId = new ArrayList<>();
        Map<String, ArrayList<ProductCart>> mapProductCart = new HashMap<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users/" + mCurrentUser.getUid() + "/Customer/Cart");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Cart cart = snapshot.getValue(Cart.class);
                if (cart != null) {
                    if (!listShopId.contains(cart.getShopId())) {
                        listShopId.add(cart.getShopId());
                        DatabaseReference refShop = FirebaseDatabase.getInstance().getReference("Users/" + cart.getShopId() + "/Shop");
                        refShop.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String shopName = snapshot.child("ShopInfos").child("shopName").getValue(String.class);
                                Log.e("name", shopName);
                                mapProductCart.put(cart.getShopId(), new ArrayList<ProductCart>());
                                DatabaseReference refProduct = snapshot.child("Products/" + cart.getProductId()).getRef();
                                refProduct.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Product product = snapshot.getValue(Product.class);
                                        if (product != null) {
                                            String productName = product.getProductName();
                                            String brand = product.getProductBrand();
                                            String productCategory = product.getProductCategory();
                                            int productPrice = product.getProductPrice();
                                            int productDiscountPrice = product.getProductDiscountPrice();
                                            String uri = product.getUriList().get(0);
                                            ProductCart productCart = new ProductCart(cart.getCartId(), cart.getProductId(), productName, cart.getProductQuantity(),
                                                    productPrice, productDiscountPrice, uri, cart.getShopId(), shopName, brand, productCategory);
                                            mapProductCart.get(cart.getShopId()).add(productCart);
                                            mShopListProductCarts.add(new ShopProductCart(cart.getShopId(), shopName, mapProductCart.get(cart.getShopId())));
                                            shopProductCartAdapter.notifyDataSetChanged();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {
                        DatabaseReference refShop = FirebaseDatabase.getInstance().getReference("Users/" + cart.getShopId() + "/Shop");
                        refShop.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String shopName = snapshot.child("ShopInfos").child("shopName").getValue(String.class);
                                DatabaseReference refProduct = snapshot.child("Products/" + cart.getProductId()).getRef();
                                refProduct.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Product product = snapshot.getValue(Product.class);
                                        if (product != null) {
                                            String productName = product.getProductName();
                                            String brand = product.getProductBrand();
                                            String productCategory = product.getProductCategory();
                                            int productPrice = product.getProductPrice();
                                            int productDiscountPrice = product.getProductDiscountPrice();
                                            String uri = product.getUriList().get(0);
                                            ProductCart productCart = new ProductCart(cart.getCartId(), cart.getProductId(), productName, cart.getProductQuantity(), productPrice, productDiscountPrice, uri, cart.getShopId(), shopName, brand, productCategory);
                                            for (ShopProductCart shopProductCart : mShopListProductCarts) {
                                                if (shopProductCart.getShopId().equals(cart.getShopId())) {
                                                    Log.e("qui", "Giong");
                                                    shopProductCart.getProductCarts().add(productCart);
                                                    break;
                                                }
                                            }
                                            shopProductCartAdapter.notifyDataSetChanged();

                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                listSelectedProductCart.clear();
                mFragmentCartBinding.tvTotalMoney.setText(getPrice(0));
                Cart cart = snapshot.getValue(Cart.class);
                if (cart == null || mShopListProductCarts == null || mShopListProductCarts.isEmpty())
                    return;
                for (ShopProductCart shopProductCart : mShopListProductCarts) {
                    if (shopProductCart.getShopId().equals(cart.getShopId())) {
                        for (ProductCart productCart : shopProductCart.getProductCarts()) {
                            if (cart.getCartId().equals(productCart.getProductCardId())) {
                                productCart.setProductQuantity(cart.getProductQuantity());
                                shopProductCartAdapter.notifyDataSetChanged();
                                return;
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Cart cart = snapshot.getValue(Cart.class);
                if (cart == null || mShopListProductCarts == null || mShopListProductCarts.isEmpty())
                    return;
                for (ShopProductCart shopProductCart : mShopListProductCarts) {
                    if (shopProductCart.getShopId().equals(cart.getShopId())) {
                        if (shopProductCart.getProductCarts().size() > 1) {
                            for (ProductCart productCart : shopProductCart.getProductCarts()) {
                                if (cart.getCartId().equals(productCart.getProductCardId())) {
                                    shopProductCart.getProductCarts().remove(productCart);
                                    shopProductCartAdapter.notifyDataSetChanged();
                                    return;
                                }
                            }
                        } else {
                            mShopListProductCarts.remove(shopProductCart);
                            shopProductCartAdapter.notifyDataSetChanged();
                            return;
                        }

                    }
                }
            }


            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void iListener() {

        mFragmentCartBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_dialog_ok_cancel);
                Window window = dialog.getWindow();
                if (window == null) return;
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams windowAttributes = window.getAttributes();
                windowAttributes.gravity = Gravity.CENTER;

                window.setAttributes(windowAttributes);
                dialog.setCancelable(true);
                TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
                TextView tvOk = dialog.findViewById(R.id.tv_ok);
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                tvOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        dialog.dismiss();
                        numSelectedCart = 0;
                        deleteCart();

                    }
                });
                dialog.show();


            }
        });
        mFragmentCartBinding.btnBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        mFragmentCartBinding.btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (ProductCart productCart:listSelectedProductCart){
                    if (productCart.getShopId().equals(mCurrentUser.getUid())){
                        final Dialog dialog = new Dialog(getContext());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.layout_dialog_ok_cancel);
                        Window window = dialog.getWindow();
                        if (window == null) return;
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        WindowManager.LayoutParams windowAttributes = window.getAttributes();
                        windowAttributes.gravity = Gravity.CENTER;

                        window.setAttributes(windowAttributes);
                        dialog.setCancelable(true);
                        TextView tvContent = dialog.findViewById(R.id.tv_content);
                        tvContent.setText("Không thể mua hàng thuộc Shop của bạn");
                        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
                        tvCancel.setVisibility(View.GONE);
                        TextView tvOk = dialog.findViewById(R.id.tv_ok);

                        tvOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();

                            }
                        });
                        dialog.show();
                        return;
                    }
                }
                Intent intent = new Intent(getContext(), PaymentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("listSelectedCart", (ArrayList<? extends Parcelable>) listSelectedProductCart);
                intent.putExtras(bundle);
                getContext().startActivity(intent);
            }
        });
    }

    private void showButtonDelete() {
        mFragmentCartBinding.btnDelete.setVisibility(View.VISIBLE);
        mFragmentCartBinding.btnDelete.startAnimation(AnimationUtils.loadAnimation(
                getContext(),
                R.anim.move_left
        ));
    }

    private void hideButtonDelete() {
        mFragmentCartBinding.btnDelete.startAnimation(AnimationUtils.loadAnimation(
                getContext(),
                R.anim.move_right
        ));
        mFragmentCartBinding.btnDelete.setVisibility(View.GONE);
    }

    private void deleteCart() {
        if (numSelectedCart == listSelectedProductCart.size()) {
            hideButtonDelete();
            listSelectedProductCart.clear();
            mFragmentCartBinding.tvTotalMoney.setText(getPrice(0));
            return;
        }
        ProductCart productCart = listSelectedProductCart.get(numSelectedCart);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users/" + mCurrentUser.getUid() + "/Customer/Cart");
        ref.child(productCart.getProductCardId()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                numSelectedCart++;
                deleteCart();
            }
        });
    }

    public String getPrice(long price) {
        long res = price;
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        String str1 = currencyVN.format(res);
        return str1;
    }

    public void addTotalMoney(ProductCart productCart) {
//        totalMoney = 0;
//        mFragmentCartBinding.tvTotalMoney.setText(getPrice(totalMoney));
//
//        for (ProductCart productCart1 : listSelectedProductCart) {
//            Log.e("Name2",productCart1.getProductName());
//            if (productCart1.getProductDiscountPrice() == 0) {
//                totalMoney += productCart1.getProductPrice() * productCart1.getProductQuantity();
//                mFragmentCartBinding.tvTotalMoney.setText(getPrice(totalMoney));
//            } else {
//                totalMoney += (productCart1.getProductPrice() - productCart1.getProductDiscountPrice()) * productCart1.getProductQuantity();
//                mFragmentCartBinding.tvTotalMoney.setText(getPrice(totalMoney));
//            }
//        }
//        Log.e("Total Money",mFragmentCartBinding.tvTotalMoney.getText().toString());
        if (productCart.getProductDiscountPrice() == 0) {
            totalMoney += productCart.getProductPrice() * productCart.getProductQuantity();
            mFragmentCartBinding.tvTotalMoney.setText(getPrice(totalMoney));
        } else {
            totalMoney += (productCart.getProductPrice() - productCart.getProductDiscountPrice()) * productCart.getProductQuantity();
            mFragmentCartBinding.tvTotalMoney.setText(getPrice(totalMoney));
        }
        mFragmentCartBinding.tvTotalMoney.setText(getPrice(totalMoney));
    }
    public void subTotalMoney(ProductCart productCart) {
//        totalMoney = 0;
//        mFragmentCartBinding.tvTotalMoney.setText(getPrice(totalMoney));
//
//        for (ProductCart productCart1 : listSelectedProductCart) {
//            Log.e("Name2",productCart1.getProductName());
//            if (productCart1.getProductDiscountPrice() == 0) {
//                totalMoney += productCart1.getProductPrice() * productCart1.getProductQuantity();
//                mFragmentCartBinding.tvTotalMoney.setText(getPrice(totalMoney));
//            } else {
//                totalMoney += (productCart1.getProductPrice() - productCart1.getProductDiscountPrice()) * productCart1.getProductQuantity();
//                mFragmentCartBinding.tvTotalMoney.setText(getPrice(totalMoney));
//            }
//        }
//        Log.e("Total Money",mFragmentCartBinding.tvTotalMoney.getText().toString());
        if (productCart.getProductDiscountPrice() == 0) {
            totalMoney -= productCart.getProductPrice() * productCart.getProductQuantity();
            mFragmentCartBinding.tvTotalMoney.setText(getPrice(totalMoney));
        } else {
            totalMoney -= (productCart.getProductPrice() - productCart.getProductDiscountPrice()) * productCart.getProductQuantity();
            mFragmentCartBinding.tvTotalMoney.setText(getPrice(totalMoney));
        }
        mFragmentCartBinding.tvTotalMoney.setText(getPrice(totalMoney));
    }

//    public void checkUI(ShopProductCart shopProductCart) {
//        List<String> cartIds = new ArrayList<>();
//        for (ProductCart productCart:listSelectedProductCart){
//            cartIds.add(productCart.getProductCardId());
//        }
//        for (ProductCart productCart:shopProductCart.getProductCarts()){
//            if (!cartIds.contains(productCart.getProductCardId())){
//                listSelectedProductCart.add(productCart);
//                addTotalMoney(productCart);
//            }
//        }
//    }
}