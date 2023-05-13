package com.example.ecommerceshop.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ecommerceshop.Fragment.AddProductShopFragment;
import com.example.ecommerceshop.Fragment.HomePageShopFragment;
import com.example.ecommerceshop.Fragment.OrderListShopFragment;
import com.example.ecommerceshop.Fragment.ProfileShopFragment;
import com.example.ecommerceshop.Fragment.VoucherShopFragment;
import com.example.ecommerceshop.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Arrays;
import java.util.List;

public class MainShopActivity extends AppCompatActivity {

    ListView listView ;
    TextView textView;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_shop);
        ReplaceFragment(new HomePageShopFragment());
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        floatingActionButton=findViewById(R.id.fab);
        textView=findViewById(R.id.textView12);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReplaceFragment(new AddProductShopFragment());
                textView.setText("ADD PRODUCT");
            }
        });
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.homeshop:
                        ReplaceFragment(new HomePageShopFragment());
                        textView.setText("PRODUCTS");
                        break;
                    case R.id.ordersshop:
                        ReplaceFragment(new OrderListShopFragment());
                        textView.setText("ORDERS");
                        break;
                    case R.id.vouchersshop:
                        ReplaceFragment(new VoucherShopFragment());
                        textView.setText("VOUCHERS");
                        break;
                    case R.id.profile_shop:
                        ReplaceFragment(new ProfileShopFragment());
                        textView.setText("MY PROFILE");
                        break;
                }
                return true;
            }
        });
    }
    private void ReplaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

}
