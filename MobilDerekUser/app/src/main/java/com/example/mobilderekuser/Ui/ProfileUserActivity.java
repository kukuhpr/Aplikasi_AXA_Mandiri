package com.example.mobilderekuser.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mobilderekuser.Common.Common;
import com.example.mobilderekuser.R;
import com.firebase.geofire.GeoFire;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUserActivity extends AppCompatActivity {

    TextView txt_nama, txt_email;
    CircleImageView iv_potoProfile;
    FloatingActionButton back_btn;

    RelativeLayout rl_logout, rl_feedback, rl_editProfile, rl_changePass, rl_pesanan;

    private GeoFire geoFire;
    private DatabaseReference driversLocationRef;
    private String locationSaya;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference riderInfoRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        riderInfoRef = database.getReference(Common.USERS_INFO_REFERENCE);

        initViews();
        checkUser();
    }

    private void initViews() {
        txt_email = findViewById(R.id.tv_emailRider);
        txt_nama = findViewById(R.id.tv_nameRider);
        iv_potoProfile = findViewById(R.id.iv_profileRider);

        rl_logout = findViewById(R.id.RL_logoutRider);
        rl_feedback = findViewById(R.id.RL_feedBackRider);
        rl_editProfile = findViewById(R.id.RL_editRider);
        rl_changePass = findViewById(R.id.RL_changePassRider);
        rl_pesanan = findViewById(R.id.RL_historyRider);

        back_btn = findViewById(R.id.btnBack);



        driversLocationRef = FirebaseDatabase.getInstance().getReference(Common.DRIVERS_LOCATION_REFERENCES)
                .child(String.valueOf(locationSaya));
        geoFire = new GeoFire(driversLocationRef);

        progressDialog = new ProgressDialog(ProfileUserActivity.this);
        progressDialog.setTitle("Tunggu sebentar ya...");
        progressDialog.setCanceledOnTouchOutside(false);

       /* txt_nama.setText(Common.buildWelcomeMessage());
        txt_email.setText(Common.buildEmailMessage());
        Common.setRiderPhoto(iv_potoProfile);*/



        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        rl_pesanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileUserActivity.this, HistoryUserActivity.class));
            }
        });

        rl_changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });



        rl_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        rl_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*AlertDialog.Builder builder = new AlertDialog.Builder(ProfileUserActivity.this);
                builder.setTitle("Keluar")
                        .setMessage(getString(R.string.logout_message))
                        .setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(getString(R.string.btn_keluar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                makeMeOffline();
                            }
                        })
                        .setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(dialog1 -> {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(getResources().getColor(R.color.orange_200));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(getResources().getColor(R.color.black));
                });
                dialog.show();*/
                makeMeOffline();
            }

        });

    }

    private void showChangePasswordDialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ProfileUserActivity.this);

        View view = LayoutInflater.from(ProfileUserActivity.this).inflate(R.layout.dialog_update_password, null);
        TextInputEditText passwordEt = view.findViewById(R.id.passwordET);
        TextInputEditText newPasswordEt = view.findViewById(R.id.newPasswordET);
        Button btnUpdatePass = view.findViewById(R.id.btn_updatePassword);

        bottomSheetDialog.setContentView(view);

        bottomSheetDialog.show();
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        /*final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileUserActivity.this);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();*/

        btnUpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validate data
                String oldPass = passwordEt.getText().toString().trim();
                String newPass = newPasswordEt.getText().toString().trim();
                if (TextUtils.isEmpty(oldPass)){
                    passwordEt.setError(""+getString(R.string.enter_current_pass));
                    Toast.makeText(ProfileUserActivity.this, ""+getString(R.string.enter_current_pass), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPass.length() <6){
                    newPasswordEt.setError(""+getString(R.string.password_length));
                    Toast.makeText(ProfileUserActivity.this, ""+getString(R.string.password_length), Toast.LENGTH_SHORT).show();
                    return;
                }
                bottomSheetDialog.dismiss();
                updatePass(oldPass, newPass);
            }
        });
    }

    private void updatePass(String oldPass, String newPass) {
        progressDialog.setMessage("Update Password...");
        progressDialog.show();

        //get Current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //before changing pass re-authentic the user
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //successfully authenticed, begin update
                        user.updatePassword(newPass)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        saveToDb(newPass);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(ProfileUserActivity.this, ""+getString(R.string.pass_failed), Toast.LENGTH_SHORT).show();

                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        progressDialog.dismiss();

                        Toast.makeText(ProfileUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void saveToDb(String newPass) {

        riderInfoRef.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            String accountType = ""+ds.child("accountType").getValue();

                            if (accountType.equals(Common.RIDER_INFO_REFERENCES)){
                                //update to db
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("passUser", "" + newPass);
                                hashMap.put("conPassUser", "" + newPass);
                                riderInfoRef.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Alerter.create(ProfileUserActivity.this)
                                                        .setTitle("Tarik.in")
                                                        .setText(getString(R.string.pass_updated))
                                                        .enableProgress(false)
                                                        .setBackgroundColorInt(getResources().getColor(R.color.orange_200))
                                                        .enableSwipeToDismiss()
                                                        .setIcon(R.drawable.logo_tarikin)
                                                        .setIconColorFilter(0)
                                                        .show();
                                                //Toast.makeText(ProfileUserActivity.this, ""+getString(R.string.pass_updated), Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ProfileUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } else if (accountType.equals(Common.PEMILIK_INFO_REFERENCES)){
                                //update to db
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("passPemilik", "" + newPass);
                                hashMap.put("conPassPemilik", "" + newPass);
                                riderInfoRef.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Alerter.create(ProfileUserActivity.this)
                                                        .setTitle("Tarik.in")
                                                        .setText(getString(R.string.pass_updated))
                                                        .enableProgress(false)
                                                        .setBackgroundColorInt(getResources().getColor(R.color.orange_200))
                                                        .enableSwipeToDismiss()
                                                        .setIcon(R.drawable.logo_tarikin)
                                                        .setIconColorFilter(0)
                                                        .show();
                                                //Toast.makeText(ProfileUserActivity.this, ""+getString(R.string.pass_updated), Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ProfileUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } else {
                                //update to db
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("passSupir", "" + newPass);
                                hashMap.put("conPassSupir", "" + newPass);
                                riderInfoRef.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Alerter.create(ProfileUserActivity.this)
                                                        .setTitle("Tarik.in")
                                                        .setText(getString(R.string.pass_updated))
                                                        .enableProgress(false)
                                                        .setBackgroundColorInt(getResources().getColor(R.color.orange_200))
                                                        .enableSwipeToDismiss()
                                                        .setIcon(R.drawable.logo_tarikin)
                                                        .setIconColorFilter(0)
                                                        .show();
                                                //Toast.makeText(ProfileUserActivity.this, ""+getString(R.string.pass_updated), Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ProfileUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

    }

    // METHOD UNTUK LOG OUT USER
    private void makeMeOffline() {
        progressDialog.setMessage("Logging out...");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "false");

        //update to db
        riderInfoRef.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseAuth.signOut();
                        progressDialog.dismiss();
                        //geoFire.removeLocation(firebaseAuth.getCurrentUser().getUid());

                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(ProfileUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else {
            loadUserInfo();
        }
    }

    private void loadUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Common.USERS_INFO_REFERENCE);
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){

                            String profileImage = ""+ds.child("profileImage").getValue();
                            String accountType = ""+ds.child("accountType").getValue();

                            if (accountType.equals(Common.RIDER_INFO_REFERENCES)){
                                //checkUserFromFirebase();
                                String nameUser = ""+ds.child("namaUser").getValue();
                                String emailUser = ""+ds.child("noHpUser").getValue();
                                txt_nama.setText(nameUser);
                                txt_email.setText(emailUser);

                                rl_editProfile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(ProfileUserActivity.this, EditProfileRiderActivity.class));
                                    }
                                });


                            } else if (accountType.equals(Common.PEMILIK_INFO_REFERENCES)){
                                String nama = ""+ ds.child("namaPemilik").getValue();
                                String noHp = ""+ ds.child("noHpPemilik").getValue();
                                txt_nama.setText(nama);
                                txt_email.setText(noHp);

                                rl_editProfile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(ProfileUserActivity.this, EditProfileUserActivity.class));
                                    }
                                });
                                rl_pesanan.setVisibility(View.GONE);

                            } else {
                                String nama = ""+ ds.child("namaSupir").getValue();
                                String noHp = ""+ ds.child("noHpSupir").getValue();
                                txt_nama.setText(nama);
                                txt_email.setText(noHp);

                                rl_editProfile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(ProfileUserActivity.this, EditProfileUserActivity.class));
                                    }
                                });
                            }

                            Glide.with(getApplicationContext())
                                    .load(profileImage)
                                    .placeholder(R.drawable.ic_baseline_account_circle_24)
                                    .fitCenter()
                                    .into(iv_potoProfile);

                            /*try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_baseline_account_circle_24).into(iv_potoProfile);
                            } catch (Exception e){
                                iv_potoProfile.setImageResource(R.drawable.ic_baseline_account_circle_24);
                            }*/
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

}