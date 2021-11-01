package com.example.mobilderekuser.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilderekuser.Adapter.AdapterPemilikUsaha;
import com.example.mobilderekuser.Common.Common;
import com.example.mobilderekuser.MainActivity;
import com.example.mobilderekuser.Model.DriverInfoModel;
import com.example.mobilderekuser.Model.UsahaModel;
import com.example.mobilderekuser.Model.UserModel;
import com.example.mobilderekuser.R;
import com.example.mobilderekuser.Utils.UserUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
/*import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;*/
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private Button btnLoginUser;
    private TextView tvNoAccount, tvForgotPass;
    private TextInputEditText etLogin_emailUser, etLogin_passUser;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersInfoRef;

    private ArrayList<UsahaModel> arrayList;
    private AdapterPemilikUsaha adapterPemilikUsaha;

    String uidPemilik, deliveryPemilik, namaUsaha, pemilikUsaha;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Init View
        etLogin_emailUser = findViewById(R.id.edtLogin_email_User);
        etLogin_passUser = findViewById(R.id.edtLog_pass_User);
        tvForgotPass = findViewById(R.id.tv_forgotPass);
        tvNoAccount = findViewById(R.id.tv_noAccount);
        btnLoginUser = findViewById(R.id.btn_LoginUser);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersInfoRef = firebaseDatabase.getReference(Common.USERS_INFO_REFERENCE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu sebentar ya...");
        progressDialog.setCanceledOnTouchOutside(false);


        btnLoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        tvNoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPilihRegister();
                //startActivity(new Intent(LoginActivity.this, RegistretionUserActivity.class));
            }
        });

    }

    private void showPilihRegister() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(LoginActivity.this);


        View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_pilih_register, null);
        CardView cv_customer = view.findViewById(R.id.user_cv);
        CardView cv_supirDerek = view.findViewById(R.id.supir_cv);
        CardView cv_pemilikUsaha = view.findViewById(R.id.usaha_cv);

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        /*final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setView(view);*/

        /*final AlertDialog dialog = builder.create();
        dialog.show();*/

        cv_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistretionUserActivity.class));
                bottomSheetDialog.dismiss();
                //dialog.dismiss();
            }
        });

        cv_supirDerek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAllPemilikUsaha();
                bottomSheetDialog.dismiss();
            }
        });

        cv_pemilikUsaha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistretionUsahaActivity.class));
                bottomSheetDialog.dismiss();
            }
        });

    }

    private void loadAllPemilikUsaha() {

        arrayList = new ArrayList<>();

        usersInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()){

                    uidPemilik = ""+ds.child("uid").getValue();
                    String accountType = ""+ds.child("accountType").getValue();
                    Log.d("TAG", "onDataChange: "+uidPemilik+ "tipe akun : "+accountType);
                    if (accountType.equals(Common.PEMILIK_INFO_REFERENCES)){

                        usersInfoRef.orderByChild("uid").equalTo(uidPemilik)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        arrayList.clear();
                                        for (DataSnapshot ds: snapshot.getChildren()){

                                            UsahaModel usahaModel = ds.getValue(UsahaModel.class);

                                            deliveryPemilik = ""+ds.child("deliveryPemilik").getValue();
                                            namaUsaha = ""+ds.child("tokoPemilik").getValue();



                                            showDialogPemilikUsaha(usahaModel);

                                            Log.d("ID Pemilik : ",""+uidPemilik);
                                            Log.d("HARGA : ",""+deliveryPemilik);
                                            Log.d("NAMA USAHA : ",""+namaUsaha);
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
                Toast.makeText(LoginActivity.this, "Gagal ambil data pemilik usaha", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void showDialogPemilikUsaha(UsahaModel usahaModel) {
        arrayList = new ArrayList<>();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_pilih_usaha, null);

        RecyclerView pilihSupir_RV = view.findViewById(R.id.pilihSupir_RV);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(view);

        arrayList.add(usahaModel);
        adapterPemilikUsaha = new AdapterPemilikUsaha(this,arrayList);

        pilihSupir_RV.setAdapter(adapterPemilikUsaha);

        dialog = builder.create();
        dialog.show();

    }

    private String emailRider, passRider;

    private void loginUser() {
        progressDialog.setMessage("Login...");
        progressDialog.show();
        emailRider = etLogin_emailUser.getText().toString().trim();
        passRider = etLogin_passUser.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(emailRider).matches()) {
            etLogin_passUser.setError("Email Anda salah...");
            Toast.makeText(this, "Salah email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(passRider)) {
            etLogin_passUser.setError("Masukan password Anda...");
            Toast.makeText(this, "Masukan password...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (passRider.length() < 6) {
            etLogin_passUser.setError("Password terlalu pendek...");
            Toast.makeText(this, "Password terlalu pendek", Toast.LENGTH_SHORT).show();
            return;
        }


        loginEmailPass(emailRider, passRider);



    }

    private void loginEmailPass(String emailRider, String passRider){

        firebaseAuth.signInWithEmailAndPassword(emailRider, passRider)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        makeMeOnLLine();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d("Erorr", "" + e.getMessage());
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void makeMeOnLLine() {

        progressDialog.setMessage("Mengecek pengguna...");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "true");

        //Update to db
        usersInfoRef.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //NANTI DIMASUKIN KE SPLASH SCREEN
                        //Update Token
                       /* FirebaseInstanceId.getInstance()
                                .getInstanceId()
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Erorr", "" + e.getMessage());
                                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                    @Override
                                    public void onSuccess(InstanceIdResult instanceIdResult) {
                                        Log.d("TOKEN", instanceIdResult.getToken());
                                        UserUtils.updateToken(LoginActivity.this, instanceIdResult.getToken());
                                    }
                                });*/
                        // SAMPE SINI
                        //Update sukses
                        checkUserType();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Eror_disinir", "" + e.getMessage());
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserType() {

        usersInfoRef.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String accountType = "" + ds.child("accountType").getValue();
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            DriverInfoModel driverInfoModel = snapshot.getValue(DriverInfoModel.class);
                            if (accountType.equals(Common.RIDER_INFO_REFERENCES)) {
                                //checkUserFromFirebase();
                                progressDialog.dismiss();
                                /*FirebaseInstanceId.getInstance()
                                        .getInstanceId()
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("Erorr", "" + e.getMessage());
                                                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                            @Override
                                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                                Log.d("TOKEN", instanceIdResult.getToken());
                                                UserUtils.updateToken(LoginActivity.this, instanceIdResult.getToken());
                                            }
                                        });
                                startActivity(new Intent(LoginActivity.this, HomeUserActivity.class));
                                finish();*/
                                gotoHomePelangganActivity(userModel);
                            } else if (accountType.equals(Common.PEMILIK_INFO_REFERENCES)) {
                                FirebaseInstanceId.getInstance()
                                        .getInstanceId()
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("Erorr", "" + e.getMessage());
                                                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                            @Override
                                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                                Log.d("TOKEN", instanceIdResult.getToken());
                                                UserUtils.updateToken(LoginActivity.this, instanceIdResult.getToken());
                                            }
                                        });
                                startActivity(new Intent(LoginActivity.this, HomeUsahaActivity.class));
                                finish();
                            } else {
                                /*FirebaseInstanceId.getInstance()
                                        .getInstanceId()
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("Erorr", "" + e.getMessage());
                                                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                            @Override
                                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                                Log.d("TOKEN", instanceIdResult.getToken());
                                                UserUtils.updateToken(LoginActivity.this, instanceIdResult.getToken());
                                            }
                                        });
                                startActivity(new Intent(LoginActivity.this, HomeSupirActivity.class));
                                finish();*/
                                gotoHomeDriverActivity(driverInfoModel);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {


            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {

                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {

                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {

                        return true;
                    }
                }
            }
        }

        return false;

    }

    private void gotoHomePelangganActivity(UserModel userModel) {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        Log.d("TOKEN", instanceIdResult.getToken());
                        UserUtils.updateToken(LoginActivity.this, instanceIdResult.getToken());
                    }
                });
        Common.currentUser = userModel;
        startActivity(new Intent(LoginActivity.this, HomeUserActivity.class));
        finish();
    }

    private void gotoHomeDriverActivity(DriverInfoModel driverInfoModel) {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        Log.d("TOKEN", instanceIdResult.getToken());
                        UserUtils.updateToken(LoginActivity.this, instanceIdResult.getToken());
                    }
                });
        Common.currentDriver = driverInfoModel;
        startActivity(new Intent(LoginActivity.this, HomeSupirActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAllPermission();
        if (ActivityCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            // when permission is granted
        } else {
            // when permission is denied
            ActivityCompat.requestPermissions(LoginActivity.this
                    ,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},23);
        }
    }

    private void checkAllPermission() {


        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {

            }
        };
        TedPermission.create()
                .setPermissionListener(permissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1002;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "READ_PHONE_STATE Denied", Toast.LENGTH_SHORT)
                            .show();
                } else {
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}