package com.example.mobilderekuser.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilderekuser.Callback.IFirebaseFailedListener;
import com.example.mobilderekuser.Callback.IFirebaseTripDetailListener;
import com.example.mobilderekuser.Common.Common;
import com.example.mobilderekuser.Model.EventBus.DeclineRequestAndRemoveTripFromDriver;
import com.example.mobilderekuser.Model.EventBus.DeclineRequestFromDriver;
import com.example.mobilderekuser.Model.EventBus.DriverAcceptTripEvent;
import com.example.mobilderekuser.Model.EventBus.DriverCompleteTripEvent;
import com.example.mobilderekuser.Model.EventBus.LoadTripDetailEvent;
import com.example.mobilderekuser.Model.EventBus.SelectedPlaceEvent;
import com.example.mobilderekuser.Model.TripPlanModel;
import com.example.mobilderekuser.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.plugins.annotation.Line;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class TripDetailActivity extends AppCompatActivity implements IFirebaseFailedListener, IFirebaseTripDetailListener {

    @BindView(R.id.txt_date)
    TextView txt_date;
    @BindView(R.id.txt_price)
    TextView txt_price;
    @BindView(R.id.txt_originPickup)
    TextView txt_originPickup;
    @BindView(R.id.txt_destinationRider)
    TextView txt_destinationRider;
    @BindView(R.id.txt_base_fare)
    TextView txt_base_fare;
    @BindView(R.id.layout_detail)
    LinearLayout layout_detail;
    @BindView(R.id.main_layout)
    LinearLayout main_layout;
    @BindView(R.id.progress_ring)
    ProgressBar progress_ring;
    @BindView(R.id.txt_distanceDetail)
    TextView txt_distanceDetail;
    @BindView(R.id.txt_durationDetail)
    TextView txt_durationDetail;
    @BindView(R.id.txt_total_fee)
    TextView txt_total_fee;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.reviewEt)
    EditText reviewEt;
    @BindView(R.id.btnSubmitReview)
    Button btnSubmitReview;
    @BindView(R.id.btnBack)
    FloatingActionButton btn_back;
    @BindView(R.id.txt_namaDriver)
    TextView txt_namaDriver;
    @BindView(R.id.txt_namaUsaha)
    TextView txt_namaUsaha;
    @BindView(R.id.iv_potoDriver)
    CircleImageView iv_potoDriver;

    IFirebaseTripDetailListener tripDetailListener;
    IFirebaseFailedListener failedListener;

    private String usahaDerekUid;
    private String ratingNumerId="";
    private FirebaseAuth firebaseAuth;

    private DatabaseReference usersInfoRef;
    private FirebaseDatabase firebaseDatabase;

    String ratings;
    String review;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        initViews();
    }

    private void initViews() {
        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();

        tripDetailListener = this;
        failedListener = this;

        txt_date = findViewById(R.id.txt_date);
        txt_price = findViewById(R.id.txt_price);
        txt_originPickup = findViewById(R.id.txt_originPickup);
        txt_destinationRider = findViewById(R.id.txt_destinationRider);
        txt_base_fare = findViewById(R.id.txt_base_fare);
        layout_detail = findViewById(R.id.layout_detail);
        main_layout = findViewById(R.id.main_layout);
        progress_ring = findViewById(R.id.progress_ring);
        txt_distanceDetail = findViewById(R.id.txt_distanceDetail);
        txt_durationDetail = findViewById(R.id.txt_durationDetail);
        txt_total_fee = findViewById(R.id.txt_total_fee);
        ratingBar = findViewById(R.id.ratingBar);
        reviewEt = findViewById(R.id.reviewEt);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        btn_back = findViewById(R.id.btnBack);
        iv_potoDriver = findViewById(R.id.iv_potoDriver);
        txt_namaDriver = findViewById(R.id.txt_namaDriver);
        txt_namaUsaha = findViewById(R.id.txt_namaUsaha);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        usersInfoRef = firebaseDatabase.getReference(Common.USERS_INFO_REFERENCE);

        checkUser();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }


    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else {
            /*loadMyInfo();*/
        }
    }

    private void loadMyInfo() {
        usersInfoRef.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){

                            String accountType = ""+ds.child("accountType").getValue();
                            if (accountType.equals(Common.RIDER_INFO_REFERENCES)){


                            } else if (accountType.equals(Common.PEMILIK_INFO_REFERENCES)){

                            } else {

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTripDetailLoadSuccess(TripPlanModel tripPlanModel) {
        String tripId = tripPlanModel.getTripId();
        usersInfoRef.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){

                            String accountType = ""+ds.child("accountType").getValue();
                            if (accountType.equals(Common.RIDER_INFO_REFERENCES)){
                                //Set Data
                                txt_date.setText(tripPlanModel.getTimText());
                                txt_originPickup.setText(tripPlanModel.getOriginString());
                                txt_destinationRider.setText(tripPlanModel.getDestinationString());
                                txt_base_fare.setText("Rp "+String.valueOf(tripPlanModel.getDriverInfoModel().getPrice())+"/Km");
                                txt_distanceDetail.setText(tripPlanModel.getDistanceValue());
                                txt_durationDetail.setText(tripPlanModel.getDurationValue());
                                txt_total_fee.setText("Rp "+tripPlanModel.getHargaPerjalanan());
                                txt_price.setText("Rp "+tripPlanModel.getHargaPerjalanan());

                                txt_namaDriver.setText(tripPlanModel.getDriverInfoModel().getNamaSupir());
                                txt_namaUsaha.setText(tripPlanModel.getDriverInfoModel().getNamaUsaha());

                                String profileImage = tripPlanModel.getDriverInfoModel().getProfileImage();

                                try{
                                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_baseline_account_circle_24)
                                            .into(iv_potoDriver);
                                } catch (Exception e){
                                    iv_potoDriver.setImageResource(R.drawable.ic_baseline_account_circle_24);
                                }


                                loadMyReview(tripId);

                                //show layout
                                layout_detail.setVisibility(View.VISIBLE);
                                progress_ring.setVisibility(View.GONE);



                                btnSubmitReview.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        inputDatReview(tripId, tripPlanModel);
                                    }
                                });

                            } else if (accountType.equals(Common.PEMILIK_INFO_REFERENCES)){
                                txt_date.setText(tripPlanModel.getTimText());
                                txt_originPickup.setText(tripPlanModel.getOriginString());
                                txt_destinationRider.setText(tripPlanModel.getDestinationString());
                                txt_base_fare.setText("Rp "+String.valueOf(tripPlanModel.getDriverInfoModel().getPrice())+"/Km");
                                txt_distanceDetail.setText(tripPlanModel.getDistanceValue());
                                txt_durationDetail.setText(tripPlanModel.getDurationValue());
                                txt_total_fee.setText("Rp "+tripPlanModel.getHargaPerjalanan());
                                txt_price.setText("Rp "+tripPlanModel.getHargaPerjalanan());

                                txt_namaDriver.setText(tripPlanModel.getUserModel().getNamaUser());
                                txt_namaUsaha.setText(tripPlanModel.getUserModel().getNoHpUser());

                                //show layout
                                layout_detail.setVisibility(View.VISIBLE);
                                progress_ring.setVisibility(View.GONE);
                                btnSubmitReview.setVisibility(View.GONE);


                                String profileImage = tripPlanModel.getUserModel().getProfileImage();
                                try{
                                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_baseline_account_circle_24)
                                            .into(iv_potoDriver);
                                } catch (Exception e){
                                    iv_potoDriver.setImageResource(R.drawable.ic_baseline_account_circle_24);
                                }
                                loadReviews(tripId);
                            } else {
                                txt_date.setText(tripPlanModel.getTimText());
                                txt_originPickup.setText(tripPlanModel.getOriginString());
                                txt_destinationRider.setText(tripPlanModel.getDestinationString());
                                txt_base_fare.setText("Rp "+String.valueOf(tripPlanModel.getDriverInfoModel().getPrice())+"/Km");
                                txt_distanceDetail.setText(tripPlanModel.getDistanceValue());
                                txt_durationDetail.setText(tripPlanModel.getDurationValue());
                                txt_total_fee.setText("Rp "+tripPlanModel.getHargaPerjalanan());
                                txt_price.setText("Rp "+tripPlanModel.getHargaPerjalanan());

                                txt_namaDriver.setText(tripPlanModel.getUserModel().getNamaUser());
                                txt_namaUsaha.setText(tripPlanModel.getUserModel().getNoHpUser());

                                //show layout
                                layout_detail.setVisibility(View.VISIBLE);
                                progress_ring.setVisibility(View.GONE);
                                btnSubmitReview.setVisibility(View.GONE);


                                String profileImage = tripPlanModel.getUserModel().getProfileImage();
                                try{
                                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_baseline_account_circle_24)
                                            .into(iv_potoDriver);
                                } catch (Exception e){
                                    iv_potoDriver.setImageResource(R.drawable.ic_baseline_account_circle_24);
                                }
                                loadReviews(tripId);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });





    }

    private void loadReviews(String tripId) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Common.RATING);
        ref.child(tripId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            //Get Review Detail
                            String uid = ""+snapshot.child("uid").getValue();
                            String ratings = ""+snapshot.child("ratings").getValue();
                            String review = ""+snapshot.child("review").getValue();
                            String timeStamp = ""+snapshot.child("timeStamp").getValue();

                            float myRating = Float.parseFloat(ratings);
                            ratingBar.setRating(myRating);
                            reviewEt.setText(review);
                            reviewEt.setEnabled(false);
                            reviewEt.setTextColor(getColor(R.color.black));
                            btnSubmitReview.setVisibility(View.GONE);
                            btnSubmitReview.setEnabled(false);

                            Log.d("COBA_RATINGS",""+ratings);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

    }

    private void loadMyReview(String tripId) {

        Log.d("TRIP_ID",""+tripId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Common.RATING);
        ref.child(tripId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            //Get Review Detail
                            String uid = ""+snapshot.child("uid").getValue();
                            String ratings = ""+snapshot.child("ratings").getValue();
                            String review = ""+snapshot.child("review").getValue();
                            String timeStamp = ""+snapshot.child("timeStamp").getValue();

                            float myRating = Float.parseFloat(ratings);
                            ratingBar.setRating(myRating);
                            reviewEt.setText(review);
                            reviewEt.setEnabled(false);
                            reviewEt.setTextColor(getColor(R.color.black));
                            btnSubmitReview.setVisibility(View.GONE);
                            btnSubmitReview.setEnabled(false);

                            Log.d("COBA_RATINGS",""+ratings);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
}

    private void inputDatReview(String tripId, TripPlanModel tripPlanModel) {
        ratings = ""+ratingBar.getRating();
        review = reviewEt.getText().toString().trim();

        String timeStamp = ""+System.currentTimeMillis();

        FirebaseDatabase.getInstance()
                .getReference(".info/serverTimeOffset")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        long timeOffset = snapshot.getValue(Long.class);
                        long estimateTimeInMs = System.currentTimeMillis()+timeOffset;
                        String timeText = new SimpleDateFormat("dd/MM/yyyy HH:mm aa")
                                .format(estimateTimeInMs);

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("idRider",""+firebaseAuth.getUid());
                        hashMap.put("ratings",""+ratings);
                        hashMap.put("review", ""+review);
                        hashMap.put("idPemilik", ""+tripPlanModel.getPemilik());
                        hashMap.put("idSupir", ""+tripPlanModel.getDriver());
                        hashMap.put("idTrip", ""+tripId);
                        hashMap.put("timeStamp",""+timeStamp);

                        ratingNumerId = Common.createUniqueTripIdNumber(timeOffset);

                        FirebaseDatabase.getInstance()
                                .getReference(Common.RATING)
                                .child(tripId)
                                .setValue(hashMap)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        Toast.makeText(TripDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                btnSubmitReview.setVisibility(View.GONE);
                                Alerter.create(TripDetailActivity.this)
                                        .setTitle("Tarik.in")
                                        .setText(getString(R.string.reviews_sukses))
                                        .enableProgress(false)
                                        .setBackgroundColorInt(getResources().getColor(R.color.orange_200))
                                        .enableSwipeToDismiss()
                                        .setIcon(R.drawable.logo_tarikin)
                                        .setIconColorFilter(0)
                                        .show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });


    }


    @Override
    protected void onStart() {

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {

        if (EventBus.getDefault().hasSubscriberForEvent(LoadTripDetailEvent.class))
            EventBus.getDefault().removeStickyEvent(LoadTripDetailEvent.class);
        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    @Override
    protected void onPostResume() {
        if (EventBus.getDefault().hasSubscriberForEvent(LoadTripDetailEvent.class))
            EventBus.getDefault().removeStickyEvent(LoadTripDetailEvent.class);
        EventBus.getDefault().unregister(this);
        super.onPostResume();
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onLoadTripDetailEvent(LoadTripDetailEvent event) {

        FirebaseDatabase.getInstance()
                .getReference(Common.TRIP)
                .child(event.getTripKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            TripPlanModel model = snapshot.getValue(TripPlanModel.class);
                            tripDetailListener.onTripDetailLoadSuccess(model);
                        } else {
                            failedListener.onFirebaseLoadFailed("Tidak menemukan Key dari perjalanan Anda");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        //failedListener.onFirebaseLoadFailed(error.getMessage());
                    }
                });

    }

}