package com.example.mobilderekuser.Ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mobilderekuser.Common.Common;
import com.example.mobilderekuser.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileUserActivity extends AppCompatActivity implements LocationListener {

    private FloatingActionButton back_btn;
    private LinearLayout layout_edit_supir, layout_edit_usaha;
    ProgressDialog progressDialog;

    /*LAYOUT EDIT USAHA*/
    private CircleImageView profilePemilik_IV;
    private TextInputEditText et_namaPemilik, et_noHpPemilik, et_emailPemilik,
            et_countryPemilik, et_statePemilik, et_cityPemilik,
            et_alamatPemilik, et_deliverPemilik, et_tokoPemilik;
    private ImageButton gps_btn;
    private Button simpan_btn;

    /*LAYOUT EDIT SUPIR*/
    private CircleImageView profileSupir_IV;
    private TextInputEditText et_namSupir, et_noHpSupir, et_emailSupir;
    private Button simpan_supir_btn;

    //permission constants
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    //imaage pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;


    //permission arrays
    private String[] locationPermissions;
    private String[] cameraPermissions;
    private String[] storagePermissions;

    private LocationManager locationManager;
    private double latitude = 0.0, longitude = 0.0;

    //image picked uri
    private Uri image_uri;

    private FirebaseAuth firebaseAuth;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersInfoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_user);

        initVews();

    }

    private void initVews() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        usersInfoRef = firebaseDatabase.getReference(Common.USERS_INFO_REFERENCE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu sebentar ya...");
        progressDialog.setCanceledOnTouchOutside(false);

        back_btn = findViewById(R.id.btnBack);
        layout_edit_supir = findViewById(R.id.edit_supir_layout);
        layout_edit_usaha = findViewById(R.id.edit_usaha_layout);

        /*LAYOUT EDIT USAHA*/
        profilePemilik_IV = findViewById(R.id.iv_profilePemilik);
        et_namaPemilik = findViewById(R.id.edt_namaSeller);
        et_noHpPemilik = findViewById(R.id.edt_noHpSeller);
        et_emailPemilik = findViewById(R.id.edt_emailSeller);
        et_tokoPemilik = findViewById(R.id.edt_tokoSeller);
        et_countryPemilik = findViewById(R.id.edt_countrySeller);
        et_statePemilik = findViewById(R.id.edt_stateSeller);
        et_cityPemilik = findViewById(R.id.edt_citySeller);
        et_alamatPemilik = findViewById(R.id.edt_alamatSeller);
        gps_btn = findViewById(R.id.btn_Gps);
        et_deliverPemilik = findViewById(R.id.edt_deliverySeller);
        simpan_btn = findViewById(R.id.btn_simpanSeller);

        /*LAYOUT EDIT SUPIR*/
        profileSupir_IV = findViewById(R.id.iv_profileSupir);
        et_namSupir = findViewById(R.id.edt_namaSupir);
        et_noHpSupir = findViewById(R.id.edt_noHpSupir);
        et_emailSupir = findViewById(R.id.edt_emailSupir);
        simpan_supir_btn = findViewById(R.id.btn_RegisterSupir);

        loadMyInfo();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        /*LAYOUT EDIT USAHA*/

        profilePemilik_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        gps_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()) {
                    detectLocation();
                } else {
                    requestLocationPermission();
                }
            }
        });

        simpan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDataUsaha();
            }
        });



        /*LAYOUT EDIT SUPIR*/

        profileSupir_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        simpan_supir_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });

    }

    private void loadMyInfo() {
        usersInfoRef.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){


                            String accountType = ""+ds.child("accountType").getValue();
                            if (accountType.equals("Supir")){
                                String nama = ""+ ds.child("namaSupir").getValue();
                                String noHp = ""+ ds.child("noHpSupir").getValue();
                                String email = ""+ ds.child("emailSupir").getValue();
                                String profileImage = ""+ ds.child("profileImage").getValue();
                                layout_edit_supir.setVisibility(View.VISIBLE);
                                layout_edit_usaha.setVisibility(View.GONE);

                                et_namSupir.setText(nama);
                                et_noHpSupir.setText(noHp);
                                et_emailSupir.setText(email);
                                layout_edit_supir.setVisibility(View.VISIBLE);
                                layout_edit_usaha.setVisibility(View.GONE);

                                try{
                                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_baseline_account_circle_24)
                                            .into(profileSupir_IV);
                                } catch (Exception e){
                                    profileSupir_IV.setImageResource(R.drawable.ic_baseline_account_circle_24);
                                }
                            } else {
                                String nama = ""+ ds.child("namaPemilik").getValue();
                                String noHp = ""+ ds.child("noHpPemilik").getValue();
                                String emailPemilik = ""+ ds.child("emailPemilik").getValue();
                                String tokoPemilik = ""+ ds.child("tokoPemilik").getValue();
                                String countryPemilik = ""+ ds.child("countryPemilik").getValue();
                                String statePemilik = ""+ ds.child("statePemilik").getValue();
                                String cityPemilik = ""+ ds.child("cityPemilik").getValue();
                                String alamatPemilik = ""+ ds.child("alamatPemilik").getValue();
                                String deliveryPemilik = ""+ ds.child("deliveryPemilik").getValue();
                                String profileImage = ""+ ds.child("profileImage").getValue();

                                et_namaPemilik.setText(nama);
                                et_noHpPemilik.setText(noHp);
                                et_emailPemilik.setText(emailPemilik);
                                et_tokoPemilik.setText(tokoPemilik);
                                et_countryPemilik.setText(countryPemilik);
                                et_statePemilik.setText(statePemilik);
                                et_cityPemilik.setText(cityPemilik);
                                et_alamatPemilik.setText(alamatPemilik);
                                et_deliverPemilik.setText(deliveryPemilik);

                                layout_edit_supir.setVisibility(View.GONE);
                                layout_edit_usaha.setVisibility(View.VISIBLE);

                                Glide.with(getApplicationContext())
                                        .load(profileImage)
                                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                                        .fitCenter()
                                        .into(profilePemilik_IV);
                            }



                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(EditProfileUserActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /*UNTUK UPDATE DATA PEMILIK USAHA DEREK*/
    private String namaPemilik, noHpPemilik, emailPemilik, passPemilik, conPassPemilik, countryPemilik, statePemilik, cityPemilik,
            alamatPemilik, deliveryPemilik, tokoPemilik;

    private void inputDataUsaha() {

        namaPemilik = et_namaPemilik.getText().toString().trim();
        noHpPemilik = et_noHpPemilik.getText().toString().trim();
        emailPemilik = et_emailPemilik.getText().toString().trim();
        countryPemilik = et_countryPemilik.getText().toString().trim();
        statePemilik = et_statePemilik.getText().toString().trim();
        cityPemilik = et_cityPemilik.getText().toString().trim();
        alamatPemilik = et_alamatPemilik.getText().toString().trim();
        deliveryPemilik = et_deliverPemilik.getText().toString().trim();
        tokoPemilik = et_tokoPemilik.getText().toString().trim();

        //Validation
        if (TextUtils.isEmpty(namaPemilik)) {
            et_namaPemilik.setError("Masukan nama lengkap Anda...");
            Toast.makeText(this, "Masukan nama lengkap...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(noHpPemilik)) {
            et_noHpPemilik.setError("Masukan nomor telfon Anda...");
            Toast.makeText(this, "Masukan nomor telfon Anda...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(countryPemilik)) {
            et_countryPemilik.setError("Klik tombol GPS untuk memasukkan alamat Anda...");
            Toast.makeText(this, "Klik tombol GPS untuk memasukkan alamat Anda...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(statePemilik)) {
            et_statePemilik.setError("Klik tombol GPS untuk memasukkan alamat Anda...");
            Toast.makeText(this, "Klik tombol GPS untuk memasukkan alamat Anda...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(cityPemilik)) {
            et_cityPemilik.setError("Klik tombol GPS untuk memasukkan alamat Anda...");
            Toast.makeText(this, "Klik tombol GPS untuk memasukkan alamat Anda...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(alamatPemilik)) {
            et_alamatPemilik.setError("Klik tombol GPS untuk memasukkan alamat Anda...");
            Toast.makeText(this, "Klik tombol GPS untuk memasukkan alamat Anda...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(deliveryPemilik)) {
            et_deliverPemilik.setError("Masukan biaya setiap kilometer derek Anda...");
            Toast.makeText(this, "mMasukan biaya setiap kilometer derek Anda...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(tokoPemilik)) {
            et_tokoPemilik.setError("Masukan nama usaha derek Anda...");
            Toast.makeText(this, "Masukan nama usaha derek Anda...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (latitude == 0.0 || longitude == 0.0) {
            Toast.makeText(this, "Klik tombol GPS untuk memasukkan alamat Anda...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailPemilik).matches()) {
            et_emailPemilik.setError("Masukan email Anda...");
            Toast.makeText(this, "Masukan email Anda...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(deliveryPemilik)) {
            et_deliverPemilik.setError("Masukan harga per kilometer derek Anda...");
            Toast.makeText(this, "Masukan harga per kilometer derek Anda...", Toast.LENGTH_SHORT).show();
            return;
        }


        simpanUpdateUsaha();
    }

    private void simpanUpdateUsaha() {

        final String timeStamp = "" + System.currentTimeMillis();
        progressDialog.setMessage("Mengubah data Anda...");
        progressDialog.show();

        if (image_uri == null){
            // Tidak ada Gambar
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid", "" + firebaseAuth.getUid());
            hashMap.put("namaPemilik", "" + namaPemilik);
            hashMap.put("tokoPemilik", "" + tokoPemilik);
            hashMap.put("noHpPemilik", "" + noHpPemilik);
            hashMap.put("countryPemilik", "" + countryPemilik);
            hashMap.put("statePemilik", "" + statePemilik);
            hashMap.put("cityPemilik", "" + cityPemilik);
            hashMap.put("alamatPemilik", "" + alamatPemilik);
            hashMap.put("deliveryPemilik", "" + deliveryPemilik);
            hashMap.put("latitude", "" + latitude);
            hashMap.put("longitude", "" + longitude);
            hashMap.put("accountType", "Pemilik");
            hashMap.put("online", "true");

            //save To DB
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USERS_INFO_REFERENCE);
            reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // berhasil
                            checkAllSupir();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // gagal
                            finish();
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        } else {
            // Dengan foto

            // upload dulu
            String filePathAndName = "profile_images/" + "" +firebaseAuth.getUid();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // get uri of upload poto
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadPotoUri = uriTask.getResult();

                            if (uriTask.isSuccessful()) {

                                //setup to save data
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid", "" + firebaseAuth.getUid());
                                hashMap.put("namaPemilik", "" + namaPemilik);
                                hashMap.put("tokoPemilik", "" + tokoPemilik);
                                hashMap.put("noHpPemilik", "" + noHpPemilik);
                                hashMap.put("countryPemilik", "" + countryPemilik);
                                hashMap.put("statePemilik", "" + statePemilik);
                                hashMap.put("cityPemilik", "" + cityPemilik);
                                hashMap.put("alamatPemilik", "" + alamatPemilik);
                                hashMap.put("deliveryPemilik", "" + deliveryPemilik);
                                hashMap.put("latitude", "" + latitude);
                                hashMap.put("longitude", "" + longitude);
                                hashMap.put("accountType", "Pemilik");
                                hashMap.put("online", "true");
                                hashMap.put("profileImage", "" + downloadPotoUri);

                                //save to db
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USERS_INFO_REFERENCE);
                                reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // db updated
                                                checkAllSupir();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // failed update db
                                                progressDialog.dismiss();
                                                Toast.makeText(EditProfileUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                finish();finish();
                                            }
                                        });

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void checkAllSupir() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Common.USERS_INFO_REFERENCE);
        ref.orderByChild("idPemilik").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                // Clear list sebelum dimasukkan

                for (DataSnapshot ds: snapshot.getChildren()){
                    String uidSupir = ds.getKey();
                    Log.d("UIDSUPIR",""+uidSupir);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("namaUsaha", "" + tokoPemilik);
                    hashMap.put("price", "" + deliveryPemilik);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USERS_INFO_REFERENCE);
                    reference.child(uidSupir).updateChildren(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // berhasil
                                    progressDialog.dismiss();
                                    Alerter.create(EditProfileUserActivity.this)
                                            .setTitle("Tarik.in")
                                            .setText("Data Anda berhasil Diubah...")
                                            .enableProgress(false)
                                            .setBackgroundColorInt(getResources().getColor(R.color.orange_200))
                                            .enableSwipeToDismiss()
                                            .setIcon(R.drawable.logo_tarikin)
                                            .setIconColorFilter(0)
                                            .show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // gagal
                                    finish();
                                    progressDialog.dismiss();
                                    Toast.makeText(EditProfileUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }



            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(EditProfileUserActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /*UNTUK UPDATE DATA SUPIR DEREK*/
    /*UNTUK UPDATE DATA DRIVER*/
    private String namaUser, noHpUser, emailUser;

    private void inputData() {
        namaUser = et_namSupir.getText().toString().trim();
        noHpUser = et_noHpSupir.getText().toString().trim();
        emailUser = et_emailSupir.getText().toString().trim();

        if (TextUtils.isEmpty(namaUser)) {
            Toast.makeText(this, "Enter Name...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(noHpUser)) {
            Toast.makeText(this, "Enter Phone Number...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailUser).matches()) {
            Toast.makeText(this, "Enter Email...", Toast.LENGTH_SHORT).show();
            return;
        }
        simpanUpdateSupir();
    }

    private void simpanUpdateSupir() {

        progressDialog.setMessage("Mengubah data Anda...");
        progressDialog.show();

        if (image_uri == null){
            // Tidak ada Gambar
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid", "" + firebaseAuth.getUid());
            hashMap.put("emailSupir", "" + emailUser);
            hashMap.put("namaSupir", "" + namaUser);
            hashMap.put("noHpSupir", "" + noHpUser);

            //save To DB
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USERS_INFO_REFERENCE);
            reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // berhasil
                            progressDialog.dismiss();
                            Alerter.create(EditProfileUserActivity.this)
                                    .setTitle("Tarik.in")
                                    .setText("Data Anda berhasil Diubah...")
                                    .enableProgress(false)
                                    .setBackgroundColorInt(getResources().getColor(R.color.orange_200))
                                    .enableSwipeToDismiss()
                                    .setIcon(R.drawable.logo_tarikin)
                                    .setIconColorFilter(0)
                                    .show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // gagal
                            finish();
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Dengan foto

            // upload dulu
            String filePathAndName = "profile_images/" + "" + firebaseAuth.getUid();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // get uri of upload poto
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadPotoUri = uriTask.getResult();

                            if (uriTask.isSuccessful()) {

                                //setup to save data
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid", "" + firebaseAuth.getUid());
                                hashMap.put("emailSupir", "" + emailUser);
                                hashMap.put("namaSupir", "" + namaUser);
                                hashMap.put("noHpSupir", "" + noHpUser);
                                hashMap.put("profileImage", "" + downloadPotoUri);

                                //save to db
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USERS_INFO_REFERENCE);
                                reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // db updated
                                                progressDialog.dismiss();
                                                Alerter.create(EditProfileUserActivity.this)
                                                        .setTitle("Tarik.in")
                                                        .setText("Data Anda berhasil Diubah...")
                                                        .enableProgress(false)
                                                        .setBackgroundColorInt(getResources().getColor(R.color.orange_200))
                                                        .enableSwipeToDismiss()
                                                        .setIcon(R.drawable.logo_tarikin)
                                                        .setIconColorFilter(0)
                                                        .show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // failed update db
                                                progressDialog.dismiss();
                                                Toast.makeText(EditProfileUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                finish();finish();
                                            }
                                        });

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }




    //  BUAT POTO PROFIL
    private void showImagePickDialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(EditProfileUserActivity.this);

        View view = LayoutInflater.from(EditProfileUserActivity.this).inflate(R.layout.dialog_pick_photo, null);
        LinearLayout galery_layout = view.findViewById(R.id.layout_gallery);
        LinearLayout camera_layout = view.findViewById(R.id.layout_camera);

        bottomSheetDialog.setContentView(view);

        bottomSheetDialog.show();
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        galery_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkStoragePermission()) {
                    // acc permission
                    pickFromGallery();
                } else {
                    // not acc permission
                    requestStoragePermission();
                }
                bottomSheetDialog.dismiss();
            }
        });

        camera_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCameraPermission()) {
                    // acc permision
                    pickFromCamera();
                } else {
                    // not acc
                    requestCameraPermission();
                }
                bottomSheetDialog.dismiss();
            }
        });

        /*//to display in dialog
        String[] options = {"Camera", "Gallery"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            //kamera klik
                            if (checkCameraPermission()) {
                                // acc permision
                                pickFromCamera();
                            } else {
                                // not acc
                                requestCameraPermission();
                            }
                        } else {
                            //galery klik
                            if (checkStoragePermission()) {
                                // acc permission
                                pickFromGallery();
                            } else {
                                // not acc permission
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();*/
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                // get picked image
                image_uri = data.getData();
                profilePemilik_IV.setImageURI(image_uri);
                profileSupir_IV.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                profilePemilik_IV.setImageURI(image_uri);
                profileSupir_IV.setImageURI(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /*UNTUK UPDATE DATA LOKASI PEMILIK USAHA DEREK*/
    private boolean checkLocationPermission() {

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);
    }

    private void detectLocation() {
        Toast.makeText(this, "Tunggu sebentar...", Toast.LENGTH_SHORT).show();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        findAddress();

    }

    private void findAddress() {
        // find address, city, country statte

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            String state = addresses.get(0).getAdminArea();
            String city = addresses.get(0).getSubAdminArea();
            String country = addresses.get(0).getCountryName();

            //set alamat
            et_countryPemilik.setText(country);
            et_statePemilik.setText(state);
            et_cityPemilik.setText(city);
            et_alamatPemilik.setText(address);
        } catch (Exception e) {
            Log.d("ERROR_EDITPROFILE",""+e.getMessage());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        //GPS DISABLE
        Toast.makeText(this, "Nyalakan GPS...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {
                        // permisson allowed
                        detectLocation();
                    } else {
                        //permission denied
                        Toast.makeText(this, "Location is neessary", Toast.LENGTH_SHORT).show();

                    }


                }
            }
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "Camera permissions are necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Storage permissions is necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}