package com.example.mobilderekuser.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mobilderekuser.Common.Common;
import com.example.mobilderekuser.MainActivity;
import com.example.mobilderekuser.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeUserActivity extends AppCompatActivity {

    CardView cv_help, cv_history;
    TextView txt_nama, txt_hai;
    CircleImageView iv_profile;
    RelativeLayout profile_layout;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference usersInfoRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);

        //init firebase
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        usersInfoRef = database.getReference(Common.USERS_INFO_REFERENCE);

        txt_hai = findViewById(R.id.hai_txt);
        txt_nama = findViewById(R.id.nama_txt);
        cv_help = findViewById(R.id.help_cv);
        cv_history = findViewById(R.id.history_cv);
        iv_profile = findViewById(R.id.iv_potoUser);
        profile_layout = findViewById(R.id.rl_profile_layout);



        cekPengguna();
        Common.setWelcomeMessage(txt_hai);

        profile_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeUserActivity.this, ProfileUserActivity.class));
            }
        });

        cv_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeUserActivity.this, HistoryUserActivity.class));
            }
        });

        cv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeUserActivity.this, MapsTowNowActivity.class));
            }
        });

    }

    private void cekPengguna() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            loadMyInfo();
        } else {
            startActivity(new Intent(HomeUserActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UsersInfo");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String nameUser = ""+ds.child("namaUser").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();

                            txt_nama.setText(nameUser);

                            Glide.with(getApplicationContext())
                                    .load(profileImage)
                                    .placeholder(R.drawable.ic_baseline_account_circle_24)
                                    .fitCenter()
                                    .into(iv_profile);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }


}