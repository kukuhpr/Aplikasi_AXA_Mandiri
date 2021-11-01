package com.example.mobilderekuser.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.mobilderekuser.Common.Common;
import com.example.mobilderekuser.MainActivity;
import com.example.mobilderekuser.Model.DriverInfoModel;
import com.example.mobilderekuser.Model.UsahaModel;
import com.example.mobilderekuser.Model.UserModel;
import com.example.mobilderekuser.R;
import com.example.mobilderekuser.Utils.UserUtils;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

/*import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;*/

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SplashScreenActivity extends AppCompatActivity {

     int REQUEST_LOCATION = 88;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private ProgressDialog progressDialog;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersInfoRef;

    private ArrayList<UserModel> userModelsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Buat Fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        usersInfoRef = firebaseDatabase.getReference(Common.USERS_INFO_REFERENCE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu sebentar ya...");
        progressDialog.setCanceledOnTouchOutside(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
               // Log.d("USERCURRENT", "checkUser: "+firebaseUser.getUid());
                if (firebaseUser == null) {
                    // user not logged in

                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    checkUserFromFirebase();
                }


            }
        }, 1500);
    }

    private void location() {

        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(5000);
        request.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(request);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());
        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);

                //do you task here with your location
                checkUserFromFirebase();

            } catch (ApiException e){

                switch (e.getStatusCode()){
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException)e;
                            resolvableApiException.startResolutionForResult(SplashScreenActivity.this,REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException sendIntentException) {

                        }
                        break;
                    // when device doesn't have location feature
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }

                e.printStackTrace();
            }
        });

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

    private void checkUserFromFirebase() {
        userModelsList = new ArrayList<>();
        usersInfoRef.child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String accountType = "" + snapshot.child("accountType").getValue();
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        UsahaModel usahaModel = snapshot.getValue(UsahaModel.class);
                        DriverInfoModel driverInfoModel = snapshot.getValue(DriverInfoModel.class);
                        userModelsList.add(userModel);
                        if (accountType.equals(Common.RIDER_INFO_REFERENCES)) {
                            goToHomeActivity(userModel);
                        } else if (accountType.equals(Common.PEMILIK_INFO_REFERENCES)) {
                            goToHOmeUsahaActivity(usahaModel);
                        } else {
                            gotoHomeDriverActivity(driverInfoModel);
                        }

                        /*if (accountType.equals(Common.RIDER_INFO_REFERENCES)) {
                            //checkUserFromFirebase();
                            progressDialog.dismiss();
                            FirebaseInstanceId.getInstance()
                                    .getInstanceId()
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Erorr", "" + e.getMessage());
                                            Toast.makeText(SplashScreenActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                        @Override
                                        public void onSuccess(InstanceIdResult instanceIdResult) {
                                            Log.d("TOKEN", instanceIdResult.getToken());
                                            UserUtils.updateToken(SplashScreenActivity.this, instanceIdResult.getToken());
                                        }
                                    });
                            startActivity(new Intent(SplashScreenActivity.this, HomeUserActivity.class));
                            finish();
                        } else if (accountType.equals(Common.PEMILIK_INFO_REFERENCES)) {
                            FirebaseInstanceId.getInstance()
                                    .getInstanceId()
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Erorr", "" + e.getMessage());
                                            Toast.makeText(SplashScreenActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                        @Override
                                        public void onSuccess(InstanceIdResult instanceIdResult) {
                                            Log.d("TOKEN", instanceIdResult.getToken());
                                            UserUtils.updateToken(SplashScreenActivity.this, instanceIdResult.getToken());
                                        }
                                    });
                            startActivity(new Intent(SplashScreenActivity.this, HomeUsahaActivity.class));
                            finish();

                        } else {
                            FirebaseInstanceId.getInstance()
                                    .getInstanceId()
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Erorr", "" + e.getMessage());
                                            Toast.makeText(SplashScreenActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                        @Override
                                        public void onSuccess(InstanceIdResult instanceIdResult) {
                                            Log.d("TOKEN", instanceIdResult.getToken());
                                            UserUtils.updateToken(SplashScreenActivity.this, instanceIdResult.getToken());
                                        }
                                    });
                            startActivity(new Intent(SplashScreenActivity.this, HomeSupirActivity.class));
                            finish();
                            //gotoHomeDriverActivity(driverInfoModel);
                        }*/

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SplashScreenActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToHOmeUsahaActivity(UsahaModel usahaModel) {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SplashScreenActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        Log.d("TOKEN", instanceIdResult.getToken());
                        UserUtils.updateToken(SplashScreenActivity.this, instanceIdResult.getToken());
                    }
                });
        Common.currentUserUsaha = usahaModel;
        startActivity(new Intent(SplashScreenActivity.this, HomeUsahaActivity.class));
        finish();
    }



    private void goToHomeActivity(UserModel userModel) {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SplashScreenActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        Log.d("TOKEN", instanceIdResult.getToken());
                        UserUtils.updateToken(SplashScreenActivity.this, instanceIdResult.getToken());
                    }
                });
        Common.currentUser = userModel;
        startActivity(new Intent(SplashScreenActivity.this, HomeUserActivity.class));
        finish();
    }

    private void gotoHomeDriverActivity(DriverInfoModel driverInfoModel) {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SplashScreenActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        Log.d("TOKEN", instanceIdResult.getToken());
                        UserUtils.updateToken(SplashScreenActivity.this, instanceIdResult.getToken());
                    }
                });
        Common.currentDriver = driverInfoModel;
        startActivity(new Intent(SplashScreenActivity.this, HomeSupirActivity.class));
        finish();
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

    /*private void showRegisterLayout() {
        startActivity(new Intent(SplashScreenActivity.this, RegistretionUserActivity.class));
    }*/



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