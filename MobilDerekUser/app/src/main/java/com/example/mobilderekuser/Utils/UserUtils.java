package com.example.mobilderekuser.Utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mobilderekuser.Common.Common;
import com.example.mobilderekuser.Model.DriverGeoModel;
import com.example.mobilderekuser.Model.EventBus.NotifyToRiderEvent;
import com.example.mobilderekuser.Model.EventBus.SelectedPlaceEvent;
import com.example.mobilderekuser.Model.FCMResponse;
import com.example.mobilderekuser.Model.FCMSendData;
import com.example.mobilderekuser.Model.RetrofitFCMClient;
import com.example.mobilderekuser.Model.TokenModel;
import com.example.mobilderekuser.R;
import com.example.mobilderekuser.Remote.IFCMService;
import com.example.mobilderekuser.Ui.MapsTowNowActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.internal.impl.net.pablo.FindCurrentPlacePabloResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class UserUtils {

    public static void updateToken(Context context, String token) {

        TokenModel tokenModel = new TokenModel(token);

        FirebaseDatabase.getInstance()
                .getReference(Common.TOKEN_REFERENCE)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(tokenModel)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
    }

    public static void sendRequestToDriver(Context context, RelativeLayout main_layout, String distance, DriverGeoModel foundDriver, SelectedPlaceEvent selectedPlaceEvent) {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

        //GET TOKEN
        FirebaseDatabase
                .getInstance()
                .getReference(Common.TOKEN_REFERENCE)
                .child(foundDriver.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            TokenModel tokenModel = snapshot.getValue(TokenModel.class);
                            Map<String, String> notificationData = new HashMap<>();
                            notificationData.put(Common.NOTI_TITLE, Common.REQUEST_DRIVER_TITLE);
                            notificationData.put(Common.NOTI_CONTENT, "This message represent for request driver action");
                            notificationData.put(Common.RIDER_KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());

                            notificationData.put(Common.RIDER_PICKUP_LOCATION_STRING, selectedPlaceEvent.getOriginAddress());
                            notificationData.put(Common.RIDER_PICKUP_LOCATION, new StringBuilder("")
                                    .append(selectedPlaceEvent.getOriginPoint().latitude())
                                    .append(",")
                                    .append(selectedPlaceEvent.getOriginPoint().longitude())
                                    .toString());

                            // New Information for Driver
                            notificationData.put(Common.RIDER_DISTANCE_VALUE, selectedPlaceEvent.getDistanceValue());
                            notificationData.put(Common.RIDER_JARAK_VALUE, String.valueOf(selectedPlaceEvent.getJarakValue()));
                            notificationData.put(Common.RIDER_TIME_VALUE,selectedPlaceEvent.getDurationValue());

                            notificationData.put(Common.RIDER_DESTINATION_STRING, selectedPlaceEvent.getDestinationAddress());
                            notificationData.put(Common.RIDER_DESTIONATION, new StringBuilder("")
                                    .append(selectedPlaceEvent.getDestinationPoint().latitude())
                                    .append(",")
                                    .append(selectedPlaceEvent.getDestinationPoint().longitude())
                                    .toString());

                            FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(), notificationData);
                            //Log.d("2. SENDRC :",""+chosenLatLng);

                            Log.d("3. SENDRC :", "" + notificationData);

                            compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<FCMResponse>() {
                                        @Override
                                        public void accept(FCMResponse fcmResponse) throws Exception {
                                            if (fcmResponse.getSuccess() == 0) {
                                                compositeDisposable.clear();
                                                Toast.makeText(context, context.getString(R.string.request_driver_failed), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            compositeDisposable.clear();
                                            Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }));

                        } else {
                            Toast.makeText(context, context.getString(R.string.token_not_found), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }



    public static void sendAcceptRequestToRider(View view, Context context, String key, String tripNumberId) {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

        //GET TOKEN
        FirebaseDatabase
                .getInstance()
                .getReference(Common.TOKEN_REFERENCE)
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            TokenModel tokenModel = snapshot.getValue(TokenModel.class);
                            Map<String, String> notificationData = new HashMap<>();
                            notificationData.put(Common.NOTI_TITLE, Common.REQUEST_DRIVER_ACCEPT);
                            notificationData.put(Common.NOTI_CONTENT, "This message represent for driver action accept request");
                            notificationData.put(Common.DRIVER_KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());
                            notificationData.put(Common.TRIP_KEY, tripNumberId);


                            FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(), notificationData);
                            Log.d("TOKENDRIVER :", "" + tokenModel.getToken());

                            compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<FCMResponse>() {
                                        @Override
                                        public void accept(FCMResponse fcmResponse) throws Exception {
                                            if (fcmResponse.getSuccess() == 0) {
                                                compositeDisposable.clear();
                                                Toast.makeText(context, context.getString(R.string.accept_failed), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            compositeDisposable.clear();
                                            Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }));

                        } else {
                            compositeDisposable.clear();
                            Toast.makeText(context, context.getString(R.string.token_not_found), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        compositeDisposable.clear();
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public static void sendNotifyToRider(Context context, View view, String key) {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

        //GET TOKEN
        FirebaseDatabase
                .getInstance()
                .getReference(Common.TOKEN_REFERENCE)
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            TokenModel tokenModel = snapshot.getValue(TokenModel.class);
                            Map<String, String> notificationData = new HashMap<>();
                            notificationData.put(Common.NOTI_TITLE, context.getString(R.string.driver_arrived));
                            notificationData.put(Common.NOTI_CONTENT, context.getString(R.string.your_driver_arrived));
                            notificationData.put(Common.DRIVER_KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());
                            notificationData.put(Common.RIDER_KEY, key);


                            FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(), notificationData);
                            Log.d("TOKENDRIVER :", "" + tokenModel.getToken());

                            compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<FCMResponse>() {
                                        @Override
                                        public void accept(FCMResponse fcmResponse) throws Exception {
                                            if (fcmResponse.getSuccess() == 0) {
                                                compositeDisposable.clear();
                                                Toast.makeText(context, context.getString(R.string.accept_failed), Toast.LENGTH_SHORT).show();
                                            } else {
                                                EventBus.getDefault().postSticky(new NotifyToRiderEvent());
                                            }
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            compositeDisposable.clear();
                                            Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }));

                        } else {
                            compositeDisposable.clear();
                            Toast.makeText(context, context.getString(R.string.token_not_found), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        compositeDisposable.clear();
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public static void sendCompleteTripToRider(View view, Context context, String key, String tripNumberId) {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);


        //Update success, Send notification to Rider app
        FirebaseDatabase
                .getInstance()
                .getReference(Common.TOKEN_REFERENCE)
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            TokenModel tokenModel = snapshot.getValue(TokenModel.class);
                            Map<String, String> notificationData = new HashMap<>();
                            notificationData.put(Common.NOTI_TITLE, Common.REQUEST_COMPLETE_TRIP);
                            notificationData.put(Common.NOTI_CONTENT, "This message represent for action driver complete trip");
                            notificationData.put(Common.TRIP_KEY, tripNumberId);


                            FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(), notificationData);
                            Log.d("TOKENDRIVER :", "" + tokenModel.getToken());

                            compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<FCMResponse>() {
                                        @Override
                                        public void accept(FCMResponse fcmResponse) throws Exception {
                                            if (fcmResponse.getSuccess() == 0) {
                                                compositeDisposable.clear();
                                                Toast.makeText(context, context.getString(R.string.complete_trip_failed), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, context.getString(R.string.complete_trip_success), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            compositeDisposable.clear();
                                            Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }));

                        } else {
                            compositeDisposable.clear();
                            Toast.makeText(context, context.getString(R.string.token_not_found), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        compositeDisposable.clear();
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }



    public static void sendDeclineRequest(View view, Context context, String key) {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

        //GET TOKEN
        FirebaseDatabase
                .getInstance()
                .getReference(Common.TOKEN_REFERENCE)
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            TokenModel tokenModel = snapshot.getValue(TokenModel.class);
                            Map<String, String> notificationData = new HashMap<>();
                            notificationData.put(Common.NOTI_TITLE, Common.REQUEST_DRIVER_DECLINE);
                            notificationData.put(Common.NOTI_CONTENT, "This message represent for driver action decline request");
                            notificationData.put(Common.DRIVER_KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());


                            FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(), notificationData);
                            Log.d("TOKENDRIVER :", "" + tokenModel.getToken());

                            compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<FCMResponse>() {
                                        @Override
                                        public void accept(FCMResponse fcmResponse) throws Exception {
                                            if (fcmResponse.getSuccess() == 0) {
                                                compositeDisposable.clear();
                                                Toast.makeText(context, context.getString(R.string.decline_failed), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, context.getString(R.string.decline_success), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            compositeDisposable.clear();
                                            Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }));

                        } else {
                            compositeDisposable.clear();
                            Toast.makeText(context, context.getString(R.string.token_not_found), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        compositeDisposable.clear();
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public static void sendDeclineAndRemoveTripRquest(View view, Context context, String key, String tripNumberId) {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

        //First, remove trip from firebase
        FirebaseDatabase.getInstance()
                .getReference(Common.Trip)
                .child(tripNumberId)
                .removeValue()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Delete success, Send notification to Rider app
                        FirebaseDatabase
                                .getInstance()
                                .getReference(Common.TOKEN_REFERENCE)
                                .child(key)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            TokenModel tokenModel = snapshot.getValue(TokenModel.class);
                                            Map<String, String> notificationData = new HashMap<>();
                                            notificationData.put(Common.NOTI_TITLE, Common.REQUEST_DRIVER_DECLINE_AND_REMOVE_TRIP);
                                            notificationData.put(Common.NOTI_CONTENT, "This message represent for driver action decline request");
                                            notificationData.put(Common.DRIVER_KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());


                                            FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(), notificationData);
                                            Log.d("TOKENDRIVER :", "" + tokenModel.getToken());

                                            compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                                    .subscribeOn(Schedulers.newThread())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new Consumer<FCMResponse>() {
                                                        @Override
                                                        public void accept(FCMResponse fcmResponse) throws Exception {
                                                            if (fcmResponse.getSuccess() == 0) {
                                                                compositeDisposable.clear();
                                                                Toast.makeText(context, context.getString(R.string.decline_failed), Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(context, context.getString(R.string.decline_success), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }, new Consumer<Throwable>() {
                                                        @Override
                                                        public void accept(Throwable throwable) throws Exception {
                                                            compositeDisposable.clear();
                                                            Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }));

                                        } else {
                                            compositeDisposable.clear();
                                            Toast.makeText(context, context.getString(R.string.token_not_found), Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                        compositeDisposable.clear();
                                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });
    }



}
