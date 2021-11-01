package com.example.mobilderekuser.Ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilderekuser.Adapter.AdapterPemilikUsaha;
import com.example.mobilderekuser.Callback.IFirebaseFailedListener;
import com.example.mobilderekuser.Callback.IFirebaseTripDetailListener;
import com.example.mobilderekuser.Callback.IFirebaseUsahaListener;
import com.example.mobilderekuser.Common.Common;
import com.example.mobilderekuser.Model.DriverInfoModel;
import com.example.mobilderekuser.Model.EventBus.LoadPemilikUsaha;
import com.example.mobilderekuser.Model.EventBus.LoadTripDetailEvent;
import com.example.mobilderekuser.Model.TripPlanModel;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tapadoo.alerter.Alerter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterSupirActivity extends AppCompatActivity implements IFirebaseFailedListener, IFirebaseUsahaListener {

    //UI
    private ImageButton btn_back;
    private Spinner spinnerUsaha;
    private CircleImageView iv_profileDriver;
    private TextInputEditText et_namaDriver, et_noHpDriver, et_emailDriver, et_passdriver, et_conPassDriver, et_namaPemilik, et_namaUsaha, et_hargaPemilik;
    private Button btn_registerDriver, btn_PilihPemilikUsaha;

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

    private ArrayList<UsahaModel> arrayList;
    private AdapterPemilikUsaha adapterPemilikUsaha;

    UsahaModel usahaModel;
    //image picked uri
    private Uri image_uri;

    //database
    private FirebaseAuth firebaseAuth;

    IFirebaseUsahaListener pemilikUsahaListener;
    IFirebaseFailedListener failedListener;


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersInfoRef;

    ProgressDialog progressDialog;

    String uidPemilik, deliveryPemilik, namaUsaha, pemilikUsaha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_supir);

        init();

    }


    private void init() {
        pemilikUsahaListener = this;
        failedListener = this;

        // UI
        btn_back = findViewById(R.id.btnBack);
        iv_profileDriver = findViewById(R.id.iv_profileSeller);
        et_namaDriver = findViewById(R.id.edt_namaSeller);
        et_namaUsaha = findViewById(R.id.edt_tokoSeller);
        et_namaPemilik = findViewById(R.id.edt_namaPemilik);
        et_hargaPemilik = findViewById(R.id.edt_deliverySeller);
        et_noHpDriver = findViewById(R.id.edt_noHpSeller);
        et_emailDriver = findViewById(R.id.edt_emailSeller);
        et_passdriver = findViewById(R.id.edt_passSeller);
        et_conPassDriver = findViewById(R.id.edt_conpassSeller);
        btn_registerDriver = findViewById(R.id.btn_RegisterSeller);


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

    private String namaSeller, noHpSeller, emailSeller, passSeller, conPassSeller, namaUsahaDerek, namaPemilik, harga, pemilikId;

    private void inputData() {

        namaSeller = et_namaDriver.getText().toString().trim();
        noHpSeller = et_noHpDriver.getText().toString().trim();
        emailSeller = et_emailDriver.getText().toString().trim();
        passSeller = et_passdriver.getText().toString().trim();
        conPassSeller = et_conPassDriver.getText().toString().trim();
        namaUsahaDerek = et_namaUsaha.getText().toString().trim();
        namaPemilik = et_namaPemilik.getText().toString().trim();
        harga = et_hargaPemilik.getText().toString().trim();


        if (TextUtils.isEmpty(namaSeller)) {
            et_namaDriver.setError("Masukan nama lengkap Anda...");
            Toast.makeText(this, "Masukan nama lengkap...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(noHpSeller)) {
            et_noHpDriver.setError("Masukan nomor telfon Anda...");
            Toast.makeText(this, "Masukan nomor telfon Anda...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailSeller).matches()) {
            et_emailDriver.setError("Masukan email Anda...");
            Toast.makeText(this, "Masukan email Anda...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (passSeller.length() < 6) {
            et_passdriver.setError("Masukan password Anda dengan 6 karakter...");
            Toast.makeText(this, "Masukan password Anda dengan 6 karakter...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!passSeller.equals(conPassSeller)) {
            et_conPassDriver.setError("Masukan password Anda dengan sesuai...");
            Toast.makeText(this, "Masukan password Anda dengan sesuai...", Toast.LENGTH_SHORT).show();
            return;
        }

        createAccount();

    }

    private void createAccount() {

        if (image_uri == null) {

            Alerter.create(RegisterSupirActivity.this)
                    .setTitle("Tarik.in")
                    .setText("Masukan foto Anda")
                    .enableProgress(false)
                    .setBackgroundColorInt(getResources().getColor(R.color.orange_200))
                    .enableSwipeToDismiss()
                    .setIcon(R.drawable.logo_tarikin)
                    .setIconColorFilter(0)
                    .show();

        } else {
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
                            Toast.makeText(RegisterSupirActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void saverFirebaseData() {
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
                            hashMap.put("idPemilik", "" + pemilikId);
                            hashMap.put("price", "" + harga);
                            hashMap.put("namaUsaha", "" + namaUsahaDerek);
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
                                            gotoHomeDriverActivity();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // failed update db
                                            progressDialog.dismiss();
                                            Toast.makeText(RegisterSupirActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(RegisterSupirActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void gotoHomeDriverActivity() {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterSupirActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        Log.d("TOKEN", instanceIdResult.getToken());
                        UserUtils.updateToken(RegisterSupirActivity.this, instanceIdResult.getToken());
                    }
                });
        //Common.currentDriver = driverInfoModel;
        startActivity(new Intent(RegisterSupirActivity.this, HomeSupirActivity.class));
        finish();
    }

    private void loadAllPemilikUsaha() {

        arrayList = new ArrayList<>();

        usersInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {

                    uidPemilik = "" + ds.child("uid").getValue();
                    String accountType = "" + ds.child("accountType").getValue();
                    Log.d("TAG", "onDataChange: " + uidPemilik + "tipe akun : " + accountType);
                    if (accountType.equals(Common.PEMILIK_INFO_REFERENCES)) {

                        usersInfoRef.orderByChild("uid").equalTo(uidPemilik)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        arrayList.clear();
                                        for (DataSnapshot ds : snapshot.getChildren()) {

                                            UsahaModel usahaModel = ds.getValue(UsahaModel.class);

                                            deliveryPemilik = "" + ds.child("deliveryPemilik").getValue();
                                            namaUsaha = "" + ds.child("tokoPemilik").getValue();


                                            Log.d("ID Pemilik : ", "" + uidPemilik);
                                            Log.d("HARGA : ", "" + deliveryPemilik);
                                            Log.d("NAMA USAHA : ", "" + namaUsaha);
                                        }



                                        /*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RegisterSupirActivity.this,R.layout.style_spinner,arrayList);
                                        spinnerUsaha.setAdapter(arrayAdapter);*/


                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(RegisterSupirActivity.this, "Gagal ambil data pemilik usaha", Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onUsahaListener(UsahaModel usahaModel) {

        Log.d("TAG", "onUsahaListener: " + usahaModel.getNamaPemilik());

        pemilikId = usahaModel.getUid();

        Log.d("TAG", "onUsahaListener: " + pemilikId);

        et_namaUsaha.setText(usahaModel.getTokoPemilik());
        et_namaPemilik.setText(usahaModel.getNamaPemilik());
        et_hargaPemilik.setText(usahaModel.getDeliveryPemilik());


    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onLoadPemilikUsahaEvent(LoadPemilikUsaha event) {
        FirebaseDatabase.getInstance()
                .getReference(Common.USERS_INFO_REFERENCE)
                .child(event.getIdUsaha())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UsahaModel model = snapshot.getValue(UsahaModel.class);
                            pemilikUsahaListener.onUsahaListener(model);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        failedListener.onFirebaseLoadFailed("Tidak menemukan Key dari perjalanan Anda");
                    }
                });
        /*usersInfoRef.child("uid").equalTo(event.getIdUsaha())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            UsahaModel model = snapshot.getValue(UsahaModel.class);
                            pemilikUsahaListener.onUsahaListener(model);
                        } else {
                            failedListener.onFirebaseLoadFailed("Tidak menemukan Key");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });*/
    }

    /*@Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onLoadPemilikUsaha(LoadPemilikUsaha event) {

        usersInfoRef.orderByChild("uid").equalTo(event.getTripUsaha())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            UsahaModel model = snapshot.getValue(UsahaModel.class);
                            pemilikUsahaListener.onUsahaListener(model);
                        } else {
                            failedListener.onFirebaseLoadFailed("Tidak menemukan Key");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });



    }*/


    private void showDialogPemilikUsaha(UsahaModel usahaModel) {
        arrayList = new ArrayList<>();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_pilih_usaha, null);

        RecyclerView pilihSupir_RV = view.findViewById(R.id.pilihSupir_RV);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(view);

        arrayList.add(usahaModel);
        adapterPemilikUsaha = new AdapterPemilikUsaha(this, arrayList);

        pilihSupir_RV.setAdapter(adapterPemilikUsaha);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showImagePickDialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(RegisterSupirActivity.this);

        View view = LayoutInflater.from(RegisterSupirActivity.this).inflate(R.layout.dialog_pick_photo, null);
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

    @Override
    protected void onStart() {

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {

        if (EventBus.getDefault().hasSubscriberForEvent(LoadPemilikUsaha.class))
            EventBus.getDefault().removeStickyEvent(LoadPemilikUsaha.class);
        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show();
    }
}