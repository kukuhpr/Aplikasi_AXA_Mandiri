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

import com.example.mobilderekuser.Common.Common;
import com.example.mobilderekuser.Model.UsahaModel;
import com.example.mobilderekuser.R;
import com.example.mobilderekuser.Utils.UserUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistretionUsahaActivity extends AppCompatActivity implements LocationListener {

    private CircleImageView profilePemilik_IV;
    private TextInputEditText et_namaPemilik, et_noHpPemilik, et_emailPemilik, et_passPemilik, et_conPassPemilik,
            et_countryPemilik, et_statePemilik, et_cityPemilik,
            et_alamatPemilik, et_deliverPemilik, et_tokoPemilik;
    private Button btn_registerPemilik;
    private ImageButton btnGps;
    private ImageButton back_btn;

    //permission constants
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    //imaage pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private LocationManager locationManager;
    private double latitude = 0.0, longitude = 0.0;

    //permission arrays
    private String[] locationPermissions;
    private String[] cameraPermissions;
    private String[] storagePermissions;

    //image picked uri
    private Uri image_uri;

    //database
    private FirebaseAuth firebaseAuth;


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersInfoRef;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registretion_usaha);

        initViews();

    }

    private void initViews() {
        profilePemilik_IV = findViewById(R.id.iv_profileRider);
        et_namaPemilik = findViewById(R.id.edt_namaSeller);
        et_tokoPemilik = findViewById(R.id.edt_tokoSeller);
        et_noHpPemilik = findViewById(R.id.edt_noHpSeller);
        et_deliverPemilik = findViewById(R.id.edt_deliverySeller);
        et_emailPemilik = findViewById(R.id.edt_emailSeller);
        et_passPemilik = findViewById(R.id.edt_passSeller);
        et_conPassPemilik = findViewById(R.id.edt_conpassSeller);
        et_countryPemilik = findViewById(R.id.edt_countrySeller);
        et_statePemilik = findViewById(R.id.edt_stateSeller);
        et_cityPemilik = findViewById(R.id.edt_citySeller);
        et_alamatPemilik = findViewById(R.id.edt_alamatSeller);
        btn_registerPemilik = findViewById(R.id.btn_RegisterSeller);
        btnGps = findViewById(R.id.btn_Gps);
        back_btn = findViewById(R.id.btnBack);

        //init permission array
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //init Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersInfoRef = firebaseDatabase.getReference(Common.USERS_INFO_REFERENCE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu sebentar ya...");
        progressDialog.setCanceledOnTouchOutside(false);

        profilePemilik_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        btn_registerPemilik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
        btnGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()) {
                    detectLocation();
                } else {
                    requestLocationPermission();
                }
            }
        });
    }

    private String namaPemilik, noHpPemilik, emailPemilik, passPemilik, conPassPemilik, countryPemilik, statePemilik, cityPemilik,
            alamatPemilik, deliveryPemilik, tokoPemilik;

    private void inputData() {

        namaPemilik = et_namaPemilik.getText().toString().trim();
        noHpPemilik = et_noHpPemilik.getText().toString().trim();
        emailPemilik = et_emailPemilik.getText().toString().trim();
        passPemilik = et_passPemilik.getText().toString().trim();
        conPassPemilik = et_conPassPemilik.getText().toString().trim();
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
        if (TextUtils.isEmpty(deliveryPemilik)) {
            et_deliverPemilik.setError("Masukan harga per kilometer derek Anda...");
            Toast.makeText(this, "Masukan harga per kilometer derek Anda...", Toast.LENGTH_SHORT).show();
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
        if (passPemilik.length() < 6) {
            et_passPemilik.setError("Masukan password Anda dengan 6 karakter...");
            Toast.makeText(this, "Masukan password Anda dengan 6 karakter...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!passPemilik.equals(conPassPemilik)) {
            et_conPassPemilik.setError("Masukan password Anda dengan sesuai...");
            Toast.makeText(this, "Masukan password Anda dengan sesuai...", Toast.LENGTH_SHORT).show();
            return;
        }

        bikinAkun();
    }

    private void bikinAkun() {

        if (image_uri == null){

        } else {

            progressDialog.setMessage("Membuat akun...");
            progressDialog.show();

            //create account
            firebaseAuth.createUserWithEmailAndPassword(emailPemilik, passPemilik)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            saverFirebaseData();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegistretionUsahaActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void saverFirebaseData() {

        if (image_uri == null){

        } else {

            progressDialog.setMessage("Membuat akun...");
            progressDialog.show();
            final String timeStamp = "" + System.currentTimeMillis();

            // Save dengan foto
            String filePathAndName = "profile_images/" + "" + firebaseAuth.getUid();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadPotoUri = uriTask.getResult();

                            if (uriTask.isSuccessful()) {

                                //Setup to save Data
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid", "" + firebaseAuth.getUid());
                                hashMap.put("emailPemilik", "" + emailPemilik);
                                hashMap.put("namaPemilik", "" + namaPemilik);
                                hashMap.put("tokoPemilik", "" + tokoPemilik);
                                hashMap.put("noHpPemilik", "" + noHpPemilik);
                                hashMap.put("passPemilik", "" + passPemilik);
                                hashMap.put("conPassPemilik", "" + conPassPemilik);
                                hashMap.put("countryPemilik", "" + countryPemilik);
                                hashMap.put("statePemilik", "" + statePemilik);
                                hashMap.put("cityPemilik", "" + cityPemilik);
                                hashMap.put("alamatPemilik", "" + alamatPemilik);
                                hashMap.put("deliveryPemilik", "" + deliveryPemilik);
                                hashMap.put("latitude", "" + latitude);
                                hashMap.put("longitude", "" + longitude);
                                hashMap.put("timeStamp", "" + timeStamp);
                                hashMap.put("accountType", "Pemilik");
                                hashMap.put("online", "true");
                                hashMap.put("profileImage", "" + downloadPotoUri);


                                //save to db
                                /*DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");*/
                                usersInfoRef.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // dn updated
                                                progressDialog.dismiss();
                                                goToHOmeUsahaActivity();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // failed update db
                                                progressDialog.dismiss();
                                                Toast.makeText(RegistretionUsahaActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegistretionUsahaActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void goToHOmeUsahaActivity() {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistretionUsahaActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        Log.d("TOKEN", instanceIdResult.getToken());
                        UserUtils.updateToken(RegistretionUsahaActivity.this, instanceIdResult.getToken());
                    }
                });
        //Common.currentUserUsaha = usahaModel;
        startActivity(new Intent(RegistretionUsahaActivity.this, HomeUsahaActivity.class));
        finish();
    }

    private boolean checkLocationPermission() {

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);
    }

    private void showImagePickDialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(RegistretionUsahaActivity.this);

        View view = LayoutInflater.from(RegistretionUsahaActivity.this).inflate(R.layout.dialog_pick_photo, null);
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

        /*String[] options = {"Camera", "Gallery"};
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
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

    private void detectLocation() {
        Toast.makeText(this, "Tunggu sebentar...", Toast.LENGTH_SHORT).show();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        findAddress();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(this, "Please turn on GPS...", Toast.LENGTH_SHORT).show();
    }

    private void findAddress() {
        // find address, city, country statte

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getSubAdminArea();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();

            //set alamat
            et_countryPemilik.setText(country);
            et_statePemilik.setText(state);
            et_cityPemilik.setText(city);
            et_alamatPemilik.setText(address);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                // get picked image
                image_uri = data.getData();
                profilePemilik_IV.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                profilePemilik_IV.setImageURI(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}