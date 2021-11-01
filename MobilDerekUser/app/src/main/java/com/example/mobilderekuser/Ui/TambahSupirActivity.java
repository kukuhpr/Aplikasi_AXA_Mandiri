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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mobilderekuser.Common.Common;
import com.example.mobilderekuser.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class TambahSupirActivity extends AppCompatActivity {

    //UI
    private ImageButton btn_back;
    private CircleImageView iv_profileDriver;
    private TextInputEditText et_namaDriver, et_noHpDriver, et_emailDriver, et_passdriver, et_conPassDriver;
    private Button btn_registerDriver;

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

    String uidPemilik, deliveryPemilik, namaUsaha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_supir);

        init();
        loadMyInfo();
    }

    private void init() {

        // UI
        btn_back = findViewById(R.id.btnBack);
        iv_profileDriver = findViewById(R.id.iv_profileSeller);
        et_namaDriver = findViewById(R.id.edt_namaSeller);
        et_noHpDriver = findViewById(R.id.edt_noHpSeller);
        et_emailDriver = findViewById(R.id.edt_emailSeller);
        et_passdriver = findViewById(R.id.edt_passSeller);
        et_conPassDriver = findViewById(R.id.edt_conpassSeller);
        btn_registerDriver = findViewById(R.id.btn_RegisterSeller);

        //init permission array
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

       /* uidPemilik = Common.currentUser.getUid();
        harga = Common.currentUser.getDeliveryPemilik();
        namaUsaha = Common.currentUser.getTokoPemilik();*/




        //init Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersInfoRef = firebaseDatabase.getReference(Common.USERS_INFO_REFERENCE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu sebentar ya...");
        progressDialog.setCanceledOnTouchOutside(false);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        iv_profileDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        btn_registerDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });

    }



    private String namaSeller, noHpSeller, emailSeller, passSeller, conPassSeller;

    private void inputData() {

        namaSeller = et_namaDriver.getText().toString().trim();
        noHpSeller = et_noHpDriver.getText().toString().trim();
        emailSeller = et_emailDriver.getText().toString().trim();
        passSeller = et_passdriver.getText().toString().trim();
        conPassSeller = et_conPassDriver.getText().toString().trim();


        if (TextUtils.isEmpty(namaSeller)) {
            Toast.makeText(this, "Enter Name...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(noHpSeller)) {
            Toast.makeText(this, "Enter Phone Number...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailSeller).matches()) {
            Toast.makeText(this, "Enter Email...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (passSeller.length() < 6) {
            Toast.makeText(this, "Password must be atleast 6 characters...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!passSeller.equals(conPassSeller)) {
            Toast.makeText(this, "Password doesn't match...", Toast.LENGTH_SHORT).show();
            return;
        }

        createAccount();

    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Common.USERS_INFO_REFERENCE);
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            uidPemilik = ""+ds.child("uid").getValue();
                            deliveryPemilik = ""+ds.child("deliveryPemilik").getValue();
                            namaUsaha = ""+ds.child("tokoPemilik").getValue();

                            Log.d("ID Pemilik : ",""+uidPemilik);
                            Log.d("HARGA : ",""+deliveryPemilik);
                            Log.d("NAMA USAHA : ",""+namaUsaha);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(TambahSupirActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createAccount() {

        progressDialog.setMessage("Membuat akun...");
        progressDialog.show();

        //create account
        firebaseAuth.createUserWithEmailAndPassword(emailSeller, passSeller)
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
                        Toast.makeText(TambahSupirActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void saverFirebaseData(){
        // save with poto
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        //name and path of poto
        String filePathAndName = "profile_images/" + "" + firebaseAuth.getUid();
        //upload poto
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

                            final String timeStamp = "" + System.currentTimeMillis();
                            //setup to save data
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("uid", "" + firebaseAuth.getUid());
                            hashMap.put("emailSupir", "" + emailSeller);
                            hashMap.put("namaSupir", "" + namaSeller);
                            hashMap.put("noHpSupir", "" + noHpSeller);
                            hashMap.put("passSupir", "" + passSeller);
                            hashMap.put("conPassSupir", "" + conPassSeller);
                            hashMap.put("idPemilik", "" + uidPemilik);
                            hashMap.put("price", "" + deliveryPemilik);
                            hashMap.put("namaUsaha", "" + namaUsaha);
                            hashMap.put("timeStamp", "" + timeStamp);
                            hashMap.put("accountType", "Supir");
                            hashMap.put("online", "false");
                            hashMap.put("profileImage", "" + downloadPotoUri);
                           /* Log.d("idPemilik",""+uidPemilik);
                            Log.d("price",""+harga);
                            Log.d("namaUsaha",""+namaUsaha);*/

                            //save to db
                            usersInfoRef.child(firebaseAuth.getUid()).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // dn updated
                                            progressDialog.dismiss();
                                            Toast.makeText(TambahSupirActivity.this, "Tambah supir derek berhasil", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // failed update db
                                            progressDialog.dismiss();
                                            Toast.makeText(TambahSupirActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(TambahSupirActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void showImagePickDialog() {

        //to display in dialog
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
                .show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                // get picked image
                image_uri = data.getData();
                iv_profileDriver.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                iv_profileDriver.setImageURI(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}