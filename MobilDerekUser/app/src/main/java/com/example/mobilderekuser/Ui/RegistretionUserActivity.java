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
import com.example.mobilderekuser.Model.UserModel;
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
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistretionUserActivity extends AppCompatActivity {

    private CircleImageView profileUser_IV;
    private TextInputEditText et_namaUser, et_noHpUser, et_emailUser, et_passUser, et_conPassUser;
    private Button btn_registerUser;
    private ImageButton back_btn;

    //permission constants
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    //imaage pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;


    //permission arrays
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
        setContentView(R.layout.activity_registretion_user);

        ///Init Views
        profileUser_IV = findViewById(R.id.iv_profileRider);
        et_namaUser = findViewById(R.id.edt_namaRider);
        et_noHpUser = findViewById(R.id.edt_noHpRider);
        et_emailUser = findViewById(R.id.edt_emailRider);
        et_passUser = findViewById(R.id.edt_passRider);
        et_conPassUser = findViewById(R.id.edt_conpassRider);

        back_btn = findViewById(R.id.btnBack);
        btn_registerUser = findViewById(R.id.btn_RegisterRider);


        //init permission array
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //init Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersInfoRef = firebaseDatabase.getReference(Common.USERS_INFO_REFERENCE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu sebentar ya...");
        progressDialog.setCanceledOnTouchOutside(false);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        profileUser_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        btn_registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });

    }



    private String namaUser, noHpUser, emailUser, passUser, conPassUser;
    private void inputData() {
        namaUser = et_namaUser.getText().toString().trim();
        noHpUser = et_noHpUser.getText().toString().trim();
        emailUser = et_emailUser.getText().toString().trim();
        passUser = et_passUser.getText().toString().trim();
        conPassUser = et_conPassUser.getText().toString().trim();


        //Validation
        if (TextUtils.isEmpty(namaUser)) {
            et_namaUser.setError("Masukan nama lengkap Anda...");
            Toast.makeText(this, "Masukan nama lengkap Anda...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(noHpUser)) {
            et_noHpUser.setError("Masukan nomor telfon Anda...");
            Toast.makeText(this, "Masukan nomor telfon Anda...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailUser).matches()) {
            et_emailUser.setError("Masukan email Anda...");
            Toast.makeText(this, "Masukan email Anda...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (passUser.length() < 6) {
            et_passUser.setError("Masukan password Anda...");
            Toast.makeText(this, "Masukan password Anda...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!passUser.equals(conPassUser)) {
            et_conPassUser.setError("Masukan password Anda dengan sesuai...");
            Toast.makeText(this, "Masukan password Anda dengan sesuai...", Toast.LENGTH_SHORT).show();
            return;
        }

        bikinAkun();
    }

    private void bikinAkun() {
        progressDialog.setMessage("Membuat akun...");
        progressDialog.show();

        //create account
        firebaseAuth.createUserWithEmailAndPassword(emailUser, passUser)
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
                        Toast.makeText(RegistretionUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saverFirebaseData() {
        progressDialog.setMessage("Menyimpan data Anda...");
        progressDialog.show();

        final String timeStamp = "" + System.currentTimeMillis();

        if (image_uri == null){
            // Tidak ada Gambar
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid", "" + firebaseAuth.getUid());
            hashMap.put("emailUser", "" + emailUser);
            hashMap.put("namaUser", "" + namaUser);
            hashMap.put("noHpUser", "" + noHpUser);
            hashMap.put("passUser", "" + passUser);
            hashMap.put("conPassUser", "" + conPassUser);
            hashMap.put("timeStamp", "" + timeStamp);
            hashMap.put("accountType", "" + Common.RIDER_INFO_REFERENCES);
            hashMap.put("online", "false");

            //save To DB
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USERS_INFO_REFERENCE);
            reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // berhasil
                            progressDialog.dismiss();
                            goToHomeActivity();
                            //finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // gagal
                            finish();
                            progressDialog.dismiss();
                            Toast.makeText(RegistretionUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            //Save dengan foto
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

                                //Setup to save data
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid", "" + firebaseAuth.getUid());
                                hashMap.put("emailUser", "" + emailUser);
                                hashMap.put("namaUser", "" + namaUser);
                                hashMap.put("noHpUser", "" + noHpUser);
                                hashMap.put("passUser", "" + passUser);
                                hashMap.put("conPassUser", "" + conPassUser);
                                hashMap.put("timeStamp", "" + timeStamp);
                                hashMap.put("accountType", "" + Common.RIDER_INFO_REFERENCES);
                                hashMap.put("online", "false");
                                hashMap.put("profileImage", "" + downloadPotoUri);

                                //save to db
                                usersInfoRef.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //db updated
                                                progressDialog.dismiss();
                                                goToHomeActivity();
                                                //finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                /// failed updated to db
                                                Toast.makeText(RegistretionUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RegistretionUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void goToHomeActivity() {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistretionUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        Log.d("TOKEN", instanceIdResult.getToken());
                        UserUtils.updateToken(RegistretionUserActivity.this, instanceIdResult.getToken());
                    }
                });

        startActivity(new Intent(RegistretionUserActivity.this, HomeUserActivity.class));
        finish();
    }

    private void showImagePickDialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(RegistretionUserActivity.this);

        View view = LayoutInflater.from(RegistretionUserActivity.this).inflate(R.layout.dialog_pick_photo, null);
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
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result1;
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
                        Toast.makeText(this, "Permission kamera dibutuhkan...", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(this, "Permission galeri dibutuhkan...", Toast.LENGTH_SHORT).show();
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
                profileUser_IV.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                profileUser_IV.setImageURI(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}