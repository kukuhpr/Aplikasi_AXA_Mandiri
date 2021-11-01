package com.example.mobilderekuser.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mobilderekuser.Adapter.AdapterSupirDerek;
import com.example.mobilderekuser.Common.Common;
import com.example.mobilderekuser.Model.DriverInfoModel;
import com.example.mobilderekuser.Model.ReviewModel;
import com.example.mobilderekuser.Model.TripPlanModel;
import com.example.mobilderekuser.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeUsahaActivity extends AppCompatActivity {

    CardView cv_lihatOrder, cv_tambahSupir;
    TextView txt_nama, txt_hai, txt_kerjaan, txt_supir, txt_rating;
    CircleImageView iv_profileUser;
    RatingBar ratingBar;

    private static final int REQUEST_CODE_AUTOCOMPLETE = 7171;
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1500L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;


    private FirebaseAuth firebaseAuth;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersInfoRef;

    ProgressDialog progressDialog;

    private ArrayList<ReviewModel> reviewModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_usaha);

        init();

    }

    private void init() {

        txt_hai = findViewById(R.id.hai_txt);
        txt_nama = findViewById(R.id.nama_txt);
        txt_kerjaan = findViewById(R.id.txt_jumlah_kerjaan);
        txt_rating = findViewById(R.id.txt_jumlah_rating);
        txt_supir = findViewById(R.id.txt_jumlah_supir);
        cv_lihatOrder = findViewById(R.id.help_cv);
        cv_tambahSupir = findViewById(R.id.booking_cv);
        iv_profileUser = findViewById(R.id.iv_potoUser);
        ratingBar = findViewById(R.id.ratingBar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        usersInfoRef = firebaseDatabase.getReference(Common.USERS_INFO_REFERENCE);

        Common.setWelcomeMessage(txt_hai);
        checkUserInfo();


        iv_profileUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeUsahaActivity.this, ProfileUserActivity.class));
            }
        });

        cv_lihatOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeUsahaActivity.this, HistoryUserActivity.class));

            }
        });

        cv_tambahSupir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeUsahaActivity.this, LihatSupirUsahaActivity.class));

            }
        });


    }

    private float ratingSum = 0;
    private void loadReviews() {

        reviewModelList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.RATING);
        reference.orderByChild("idPemilik").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        reviewModelList.clear();
                        ratingSum = 0;
                        for (DataSnapshot ds : snapshot.getChildren()){
                            float rating = Float.parseFloat(""+ds.child("ratings").getValue());
                            Log.d("JUMLAHRATING", "" + rating);
                            ratingSum = ratingSum + rating;

                            ReviewModel reviewModel = ds.getValue(ReviewModel.class);
                            reviewModelList.add(reviewModel);

                        }

                        long numbOfReview = snapshot.getChildrenCount();
                        Log.d("SUMRATING", "" + numbOfReview);
                        float rataRating = ratingSum/numbOfReview;
                        Log.d("RATING-RATING", "" + String.format("%.2f", rataRating)+ " ("+numbOfReview+")");
                        txt_rating.setText(String.format("%.2f", rataRating)+ " ("+numbOfReview+")");
                        ratingBar.setRating(rataRating);

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });


    }


    private void checkUserInfo() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        usersInfoRef.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            String accountType = "" + ds.child("accountType").getValue();

                            if (accountType.equals("Supir")) {
                                String uid = "" + ds.child("uid").getValue();
                                String nama = "" + ds.child("namaSeller").getValue();
                                String noHp = "" + ds.child("noHpSeller").getValue();
                                String email = "" + ds.child("emailSeller").getValue();
                                String profileImage = "" + ds.child("profileImage").getValue();
                                txt_nama.setText(nama);

                                Glide.with(getApplicationContext())
                                        .load(profileImage)
                                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                                        .fitCenter()
                                        .into(iv_profileUser);

                                /*try {
                                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_baseline_account_circle_24)
                                            .into(iv_profileUser);
                                } catch (Exception e) {
                                    iv_profileUser.setImageResource(R.drawable.ic_baseline_account_circle_24);
                                }*/
                            } else {
                                String uid = "" + ds.child("uid").getValue();
                                String nama = "" + ds.child("namaPemilik").getValue();
                                String noHp = "" + ds.child("noHpPemilik").getValue();
                                String tokoPemilik = "" + ds.child("tokoPemilik").getValue();
                                String countryPemilik = "" + ds.child("countryPemilik").getValue();
                                String statePemilik = "" + ds.child("statePemilik").getValue();
                                String cityPemilik = "" + ds.child("cityPemilik").getValue();
                                String alamatPemilik = "" + ds.child("alamatPemilik").getValue();
                                String deliveryPemilik = "" + ds.child("deliveryPemilik").getValue();
                                String profileImage = "" + ds.child("profileImage").getValue();

                                txt_nama.setText(nama);
                                loadTotalSupir(uid);
                                loadTotalOrder(uid);
                                loadReviews();

                                Glide.with(getApplicationContext())
                                        .load(profileImage)
                                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                                        .fitCenter()
                                        .into(iv_profileUser);

                                /*try {
                                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_baseline_account_circle_24)
                                            .into(iv_profileUser);
                                } catch (Exception e) {
                                    iv_profileUser.setImageResource(R.drawable.ic_baseline_account_circle_24);
                                }*/
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void loadTotalOrder(String uid) {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Common.Trip);
        ref.orderByChild("pemilik").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                //Clear list sebelum dimasukkan

                for (DataSnapshot ds : snapshot.getChildren()) {
                    TripPlanModel tripPlanModel = ds.getValue(TripPlanModel.class);


                }
                long totalOrder = snapshot.getChildrenCount();
                Log.d("KERJAAN", "" + totalOrder);
                txt_kerjaan.setText(String.valueOf(totalOrder));
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        /*DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Common.Trip);
        ref.orderByChild("driver").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                //Clear list sebelum dimasukkan

                for (DataSnapshot ds: snapshot.getChildren()){
                    TripPlanModel tripPlanModel = ds.getValue(TripPlanModel.class);

                }
                long totalOrder = snapshot.getChildrenCount();
                Log.d("KERJAAN",""+totalOrder);
                txt_kerjaan.setText(String.valueOf(totalOrder));
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });*/
    }

    private void loadTotalSupir(String uidPemilik) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Common.USERS_INFO_REFERENCE);
        ref.orderByChild("idPemilik").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                // Clear list sebelum dimasukkan
                long totalSupir = snapshot.getChildrenCount();
                Log.d("SUPIR", "" + totalSupir);
                txt_supir.setText(String.valueOf(totalSupir));
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(HomeUsahaActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}