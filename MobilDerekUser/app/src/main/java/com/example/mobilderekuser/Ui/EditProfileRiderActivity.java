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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mobilderekuser.Common.Common;
import com.example.mobilderekuser.Model.UserModel;
import com.example.mobilderekuser.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
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
import com.mapbox.android.core.permissions.PermissionsListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileRiderActivity extends AppCompatActivity implements PermissionsListener {

    private FloatingActionButton back_btn;
    private TextInputEditText et_namaUser, et_noHpUser, et_emailUser;
    private CircleImageView profileUser_IV;
    private Button btn_simpan;

    ProgressDialog progressDialog;

    //permission constants

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    //imaage pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private double latitude =0.0, longitude=0.0;

    //permission arrays
    private String[] cameraPermissions;
    private String[] storagePermissions;

    //image picked uri
    private Uri image_uri;

    private ArrayList<UserModel> userModelsList;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference usersInfoRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_rider);

        init();


    }



    private void init() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        usersInfoRef = firebaseDatabase.getReference(Common.USERS_INFO_REFERENCE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu sebentar...");
        progressDialog.setCanceledOnTouchOutside(false);

        back_btn = findViewById(R.id.btnBack);
        profileUser_IV = findViewById(R.id.iv_profileRider);
        et_namaUser = findViewById(R.id.edt_namaRider);
        et_noHpUser = findViewById(R.id.edt_noHpRider);
        et_emailUser = findViewById(R.id.edt_emailRider);
        btn_simpan = findViewById(R.id.btnSimpan);


        checkUser();

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
        
        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
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
            loadMyInfo();
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
                                String nama = ""+ ds.child("namaUser").getValue();
                                String noHp = ""+ ds.child("noHpUser").getValue();
                                String email = ""+ ds.child("emailUser").getValue();
                                String profileImage = ""+ ds.child("profileImage").getValue();
                                et_namaUser.setText(nama);
                                et_noHpUser.setText(noHp);
                                et_emailUser.setText(email);
                                Glide.with(getApplicationContext())
                                        .load(profileImage)
                                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                                        .fitCenter()
                                        .into(profileUser_IV);
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

    private String namaUser, noHpUser, emailUser;

    private void inputData() {
        namaUser = et_namaUser.getText().toString().trim();
        noHpUser = et_noHpUser.getText().toString().trim();
        emailUser = et_emailUser.getText().toString().trim();

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
        simpanUpdateRider();
    }

    private void simpanUpdateRider() {
        progressDialog.setMessage("Mengubah data Anda...");
        progressDialog.show();

        if (image_uri == null){
            // Tidak ada Gambar
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid", "" + firebaseAuth.getUid());
            hashMap.put("emailUser", "" + emailUser);
            hashMap.put("namaUser", "" + namaUser);
            hashMap.put("noHpUser", "" + noHpUser);
            hashMap.put("accountType", "" + Common.RIDER_INFO_REFERENCES);

            //save To DB
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USERS_INFO_REFERENCE);
            reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // berhasil
                            progressDialog.dismiss();
                            Alerter.create(EditProfileRiderActivity.this)
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
                            Toast.makeText(EditProfileRiderActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                hashMap.put("emailUser", "" + emailUser);
                                hashMap.put("namaUser", "" + namaUser);
                                hashMap.put("noHpUser", "" + noHpUser);
                                hashMap.put("accountType", "" + Common.RIDER_INFO_REFERENCES);
                                hashMap.put("online", "true");
                                hashMap.put("profileImage", "" + downloadPotoUri);

                                //save to db
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USERS_INFO_REFERENCE);
                                reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // db updated
                                                progressDialog.dismiss();
                                                Alerter.create(EditProfileRiderActivity.this)
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
                                                Toast.makeText(EditProfileRiderActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(EditProfileRiderActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }


//  BUAT POTO PROFIL
    private void showImagePickDialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(EditProfileRiderActivity.this);

        View view = LayoutInflater.from(EditProfileRiderActivity.this).inflate(R.layout.dialog_pick_photo, null);
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

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
                        Toast.makeText(this, "Permission camera dibutuhkan", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean galleryAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (galleryAccepted && storageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Permission galeri dibutuhkan", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Silahkan cek permision", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {

    }
}