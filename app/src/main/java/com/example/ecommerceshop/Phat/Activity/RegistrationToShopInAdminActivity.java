package com.example.ecommerceshop.Phat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ecommerceshop.Phat.Model.RequestShop;
import com.example.ecommerceshop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationToShopInAdminActivity extends AppCompatActivity {

    ImageView backbtn;
    CircleImageView avatarShop;
    TextView shopName, shopDescription, shopEmail, shopPhone, shopAddress,regisDate;
    AppCompatButton btnRefuse;
    Button btnAllow;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    String id;
    RequestShop requestShop= new RequestShop();
    boolean isdelete=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_to_shop_in_admin);
        id=getIntent().getStringExtra("id");
        initUI();
        if(!isdelete){
            LoadRequestDetail();
        }
        
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
            }
        });
        btnRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationToShopInAdminActivity.this);
                builder.setTitle("Refuse...").setMessage("Are you sure you want to refuse this shop ?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                isdelete=true;
                                deleteRequest(id);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                return;
                            }
                        })
                        .show();
            }
        });
    }

    private void uploadData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(id).child("Shop").child("ShopInfos").setValue(requestShop).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users");
                databaseReference1.child(id).child("Shop").child("shopId").setValue(id).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(RegistrationToShopInAdminActivity.this, "Allow successfully!", Toast.LENGTH_SHORT).show();
                        isdelete=true;
                        deleteRequest(id);
                    }
                });
            }
        });
    }

    private void deleteRequest(String requestId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Requests");
        reference.child(requestId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistrationToShopInAdminActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private String getDate(long timestamp){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(timestamp);
    }
    private void LoadRequestDetail() {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Requests");
        databaseReference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                
                requestShop=snapshot.getValue(RequestShop.class);
                if(requestShop!=null){
                    Glide.with(getApplicationContext()).load(Uri.parse(requestShop.getShopAvt())).into(avatarShop);
                    shopName.setText(requestShop.getShopName());
                    shopEmail.setText(requestShop.getShopEmail());
                    shopPhone.setText(requestShop.getShopPhone());
                    shopDescription.setText(requestShop.getShopDescription());
                    shopAddress.setText(requestShop.getShopAddress());
                    regisDate.setText(getDate(Long.parseLong(requestShop.getTimestamp())));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegistrationToShopInAdminActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUI() {
        backbtn=findViewById(R.id.backbtn);
        avatarShop=findViewById(R.id.avatarShop);
        shopName=findViewById(R.id.shopName);
        shopEmail=findViewById(R.id.shopEmail);
        shopDescription=findViewById(R.id.shopDescription);
        shopPhone=findViewById(R.id.shopPhone);
        shopAddress=findViewById(R.id.shopAddress);
        regisDate=findViewById(R.id.regisDate);
        btnRefuse=findViewById(R.id.btnRefuse);
        btnAllow=findViewById(R.id.btnAllow);
        progressDialog=new ProgressDialog(RegistrationToShopInAdminActivity.this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth= FirebaseAuth.getInstance();
    }
}