package com.example.mobilderekuser.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mobilderekuser.Adapter.AdapterSupirDerek;
import com.example.mobilderekuser.Common.Common;
import com.example.mobilderekuser.Model.DriverInfoModel;
import com.example.mobilderekuser.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class LihatSupirUsahaActivity extends AppCompatActivity {

    //UI
    private FloatingActionButton btn_back;
    private RecyclerView supir_RV;
    private LinearLayout layout_empty;

    private ArrayList<DriverInfoModel> driverInfoList;
    private AdapterSupirDerek adapterSupirDerek;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_supir_usaha);

        init();
        loadMyInfo();

    }

    private void init() {

        //init Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // UI
        btn_back = findViewById(R.id.btnBack);
        supir_RV = findViewById(R.id.supir_RV);


        layout_empty = findViewById(R.id.layout_empty_rv);
        layout_empty.setVisibility(View.VISIBLE);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });


    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Common.USERS_INFO_REFERENCE);
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String uidPemilik = ""+ds.child("uid").getValue();

                            loadSemuaSupir(uidPemilik);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LihatSupirUsahaActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadSemuaSupir(String uidPemilik) {

        driverInfoList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Common.USERS_INFO_REFERENCE);
        ref.orderByChild("accountType").equalTo("Supir").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                // Clear list sebelum dimasukkan
                driverInfoList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    DriverInfoModel driverInfoModel = ds.getValue(DriverInfoModel.class);

                    String pemilikUid = ""+ds.child("idPemilik").getValue();

                    if (pemilikUid.equals(uidPemilik)){

                        driverInfoList.add(driverInfoModel);

                        if (driverInfoList.isEmpty()){
                            // BUAT NAMBAH LAYOUT KLO KOSONG DATANYA
                            Toast.makeText(LihatSupirUsahaActivity.this, "Data Driver Kosong", Toast.LENGTH_SHORT).show();
                        } else {
                            // NAMPILIN DATANYA KLO ADA
                            supir_RV.setVisibility(View.VISIBLE);
                            layout_empty.setVisibility(View.GONE);
                            // Setup Adapter
                            adapterSupirDerek = new AdapterSupirDerek(LihatSupirUsahaActivity.this, driverInfoList);

                            //Set adapter to RV
                            supir_RV.setAdapter(adapterSupirDerek);

                        }
                    }


                }


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(LihatSupirUsahaActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}