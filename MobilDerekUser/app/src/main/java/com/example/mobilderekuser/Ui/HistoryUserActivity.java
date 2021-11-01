package com.example.mobilderekuser.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mobilderekuser.Adapter.AdapterHistoryPerjalanan;
import com.example.mobilderekuser.Adapter.AdapterHistoryPerjalananSupir;
import com.example.mobilderekuser.Common.Common;
import com.example.mobilderekuser.Model.TripPlanModel;
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

public class HistoryUserActivity extends AppCompatActivity {

    private FloatingActionButton btn_back;
    private RecyclerView history_RV;
    private LinearLayout layout_empty;

    private ArrayList<TripPlanModel> tripPlanList;
    private AdapterHistoryPerjalanan adapterHistoryPerjalanan;
    private AdapterHistoryPerjalananSupir adapterHistoryPerjalananSupir;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_user);

        init();
        loadMyInfo();

    }



    private void init() {

        //init Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //UI
        btn_back = findViewById(R.id.btnBack);
        history_RV = findViewById(R.id.order_RV);
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
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String uid = ""+ds.child("uid").getValue();
                            String accountType = ""+ds.child("accountType").getValue();
                            if (accountType.equals(Common.RIDER_INFO_REFERENCES)){
                                loadOrderRider(uid);
                            } else if (accountType.equals(Common.PEMILIK_INFO_REFERENCES)){
                                loadTotalOrder(uid);
                            } else {
                                loadOrderDriver(uid);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(HistoryUserActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadTotalOrder(String uid) {
        tripPlanList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Common.Trip);
        ref.orderByChild("pemilik").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                //Clear list sebelum dimasukkan
                tripPlanList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    TripPlanModel tripPlanModel = ds.getValue(TripPlanModel.class);
                    tripPlanList.add(tripPlanModel);
                    Log.d("COBA INI",""+tripPlanModel);
                    if (tripPlanList.isEmpty()){
                        history_RV.setVisibility(View.GONE);

                    } else {
                        history_RV.setVisibility(View.VISIBLE);
                        layout_empty.setVisibility(View.GONE);
                        //Setup adapter
                        adapterHistoryPerjalananSupir = new AdapterHistoryPerjalananSupir(HistoryUserActivity.this, tripPlanList);

                        //Set adapter to Rv
                        history_RV.setAdapter(adapterHistoryPerjalananSupir);
                    }

                }
                long totalOrder = snapshot.getChildrenCount();
                Log.d("KERJAAN", "" + totalOrder);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void loadOrderDriver(String uid) {
        tripPlanList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Common.Trip);
        ref.orderByChild("driver").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                //Clear list sebelum dimasukkan
                tripPlanList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    TripPlanModel tripPlanModel = ds.getValue(TripPlanModel.class);

                    tripPlanList.add(tripPlanModel);
                    Log.d("COBA INI",""+tripPlanModel);
                    if (tripPlanList.isEmpty()){
                        history_RV.setVisibility(View.GONE);

                    } else {
                        history_RV.setVisibility(View.VISIBLE);
                        layout_empty.setVisibility(View.GONE);
                        //Setup adapter
                        adapterHistoryPerjalananSupir = new AdapterHistoryPerjalananSupir(HistoryUserActivity.this, tripPlanList);

                        //Set adapter to Rv
                        history_RV.setAdapter(adapterHistoryPerjalananSupir);
                    }
                }
                long totalOrder = snapshot.getChildrenCount();
                Log.d("KERJAAN",""+totalOrder);
                Log.d("SIZEKERJAAN",""+tripPlanList.size());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void loadOrderRider(String uidRider) {

        tripPlanList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Common.TRIP);
        ref.orderByChild("rider").equalTo(uidRider).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                // Clear list sebelum dimasukkan
                tripPlanList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    TripPlanModel tripPlanModel = ds.getValue(TripPlanModel.class);

                    tripPlanList.add(tripPlanModel);
                    Log.d("COBA INI",""+tripPlanModel);
                    if (tripPlanList.isEmpty()){
                        history_RV.setVisibility(View.GONE);

                    } else {
                        history_RV.setVisibility(View.VISIBLE);
                        layout_empty.setVisibility(View.GONE);
                        //Setup adapter
                        adapterHistoryPerjalanan = new AdapterHistoryPerjalanan(HistoryUserActivity.this, tripPlanList);

                        //Set adapter to Rv
                        history_RV.setAdapter(adapterHistoryPerjalanan);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

}