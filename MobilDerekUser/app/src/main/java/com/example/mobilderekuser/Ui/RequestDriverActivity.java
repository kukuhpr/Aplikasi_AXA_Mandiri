package com.example.mobilderekuser.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mobilderekuser.Common.Common;
import com.example.mobilderekuser.Model.DriverGeoModel;
import com.example.mobilderekuser.Model.EventBus.DeclineRequestAndRemoveTripFromDriver;
import com.example.mobilderekuser.Model.EventBus.DeclineRequestFromDriver;
import com.example.mobilderekuser.Model.EventBus.DriverAcceptTripEvent;
import com.example.mobilderekuser.Model.EventBus.DriverCompleteTripEvent;
import com.example.mobilderekuser.Model.EventBus.LoadTripDetailEvent;
import com.example.mobilderekuser.Model.EventBus.SelectedPlaceEvent;
import com.example.mobilderekuser.Model.EventBus.ShowNotificationFinishTrip;
import com.example.mobilderekuser.Model.TripPlanModel;
import com.example.mobilderekuser.R;
import com.example.mobilderekuser.Remote.IMapboxAPI;
import com.example.mobilderekuser.Remote.RetrofitClient;
import com.example.mobilderekuser.Utils.UserUtils;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tapadoo.alerter.Alerter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.xmlpull.v1.sax2.Driver;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.*;
import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class RequestDriverActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

    //View KONFIRMASI DEREK LAYOUT
    @BindView(R.id.confirm_derek_layout)
    CardView layout_confirm_derek;
    @BindView(R.id.btn_confirm_derek)
    Button confirm_derek_Btn;
    @BindView(R.id.txt_distance)
    TextView txt_distance;


    // View KONFIRMASI PICKUP LAYOUT
    @BindView(R.id.confirm_pickup_layout)
    CardView layout_confirm_pickup;
    @BindView(R.id.txt_address_pickup)
    TextView address_pickup_Txt;
    @BindView(R.id.btn_confirm_jemput)
    Button confirm_jemput_Btn;

    @BindView(R.id.fill_maps)
    View fill_maps;

    @BindView(R.id.finding_driver_layout)
    CardView finding_driver;

    @BindView(R.id.activity_main)
    RelativeLayout main_layout;


    //LAYOUT ACCEPT RIDE
    @BindView(R.id.driver_info_layout)
    CardView driver_info_layout;
    @BindView(R.id.img_driver)
    CircleImageView img_driver;
    @BindView(R.id.nama_usaha)
    TextView nama_usaha;
    @BindView(R.id.txt_hargaJalan)
    TextView txt_hargaTotal;
    @BindView(R.id.txt_driver_name)
    TextView txt_driver_name;
    @BindView(R.id.txt_rating)
    TextView txt_rating;
    @BindView(R.id.txt_noHp_Driver)
    TextView txt_noHp_Driver;
    @BindView(R.id.img_call_driver)
    ImageView img_call_driver;
    @BindView(R.id.img_message_driver)
    ImageView img_message_driver;
    @BindView(R.id.whatsApp)
    LinearLayout whatsApp_layout;
    @BindView(R.id.call_driver)
    LinearLayout call_driver_layout;

    private FloatingActionButton btn_back;

    private MapView mapView;
    private MapboxMap mapboxMap;

    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationComponent locationComponent;
    private MapboxDirections client;
    private DirectionsRoute currentRoute;

    private static final int REQUEST_CODE_AUTOCOMPLETE = 7171;
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 15000L; // 15 sec
    /*private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;*/
    private static final long DEFAULT_MAX_WAIT_TIME = 10000;  // 10 sec
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";

    private static final String ICON_USER_MARKER = "icon-user-marker";
    private static final String ICON_DURATION_MARKER = "icon-duration-marker";
    private static final String ICON_USER_FINISH_MARKER = "icon-user-finish-marker";
    private static final String ICON_USER_PICK_MARKER = "icon-user-pick-marker";

    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";


    private SelectedPlaceEvent selectedPlaceEvent;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ValueAnimator valueAnimator;
    private static final int DESIRED_NUM_OF_SPINS = 5;
    private static final int DESIRED_SECONDS_PER_ONE_FULL_360_SPIN = 40;
    private LatLng originLatLng, destinationLatLng, userLatLng;

    private Marker originMarker, destinationMarker, userPickMarker;

    String time, distance;
    private double jarak, waktu;
    private String st, wkt;
    private int jauh,harga, totalHarga;

    private double lokasiLat, lokasiLon;
    private Location locationSaya;
    private DriverGeoModel lastDriverCall;

    String startLocation="";
    String endLocation="";
    String originLocation="";

    private GeoJsonSource source;


    @Override
    protected void onStart() {
        mapView.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
        mapView.onStop();
        if (EventBus.getDefault().hasSubscriberForEvent(SelectedPlaceEvent.class))
            EventBus.getDefault().removeStickyEvent(SelectedPlaceEvent.class);
        if (EventBus.getDefault().hasSubscriberForEvent(DeclineRequestFromDriver.class))
            EventBus.getDefault().removeStickyEvent(DeclineRequestFromDriver.class);
        if (EventBus.getDefault().hasSubscriberForEvent(DriverAcceptTripEvent.class))
            EventBus.getDefault().removeStickyEvent(DriverAcceptTripEvent.class);
        if (EventBus.getDefault().hasSubscriberForEvent(DeclineRequestAndRemoveTripFromDriver.class))
            EventBus.getDefault().removeStickyEvent(DeclineRequestAndRemoveTripFromDriver.class);
        if (EventBus.getDefault().hasSubscriberForEvent(DriverCompleteTripEvent.class))
            EventBus.getDefault().removeStickyEvent(DriverCompleteTripEvent.class);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onDriverCompleteTrip(DriverCompleteTripEvent event) {

        Common.showNotification(this, new Random().nextInt(),
                "Derek Selesai",
                "Derekmu: "+event.getTripKey()+" telah sampai tujuan",
                null);
        startActivity(new Intent(this,TripDetailActivity.class));
        EventBus.getDefault().postSticky(new LoadTripDetailEvent(event.getTripKey()));
        //
        finish();
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onDriverAcceptEvent(DriverAcceptTripEvent event) {

        //Get Trip Information
        FirebaseDatabase.getInstance().getReference(Common.TRIP)
                .child(event.getTripIp())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            TripPlanModel tripPlanModel = snapshot.getValue(TripPlanModel.class);
                            mapboxMap.clear();
                            fill_maps.setVisibility(View.GONE);
                            finding_driver.setVisibility(View.GONE);
                            if (valueAnimator != null) valueAnimator.end();
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(mapboxMap.getCameraPosition().target)
                                    .tilt(0f)
                                    .zoom(mapboxMap.getCameraPosition().zoom)
                                    .build();
                            mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            //Get Routes

                            Point driverLocat = Point.fromLngLat(tripPlanModel.getCurrentLng(), tripPlanModel.getCurrentlat());

                            LatLng destiLocat = new LatLng(tripPlanModel.getCurrentlat(),tripPlanModel.getCurrentLng());

                            LatLng originLoc = new LatLng(Double.parseDouble(tripPlanModel.getOrigin().split(",")[0]),
                                    Double.parseDouble(tripPlanModel.getOrigin().split(",")[1]));
                            Point originLocat = Point.fromLngLat(originLoc.getLongitude(), originLoc.getLatitude());

                            Log.d("DRIVERLOCATION", "" + driverLocat);
                            Log.d("ORIGINLOCATION", "" + originLoc);

                            getRoute(mapboxMap,originLocat,driverLocat);

                            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                    .include(originLoc)
                                    .include(destiLocat)
                                    .build();



                            addMarkerPost(originLoc);

                            addDriverMarker(destiLocat);



                            mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,160));
                            mapboxMap.moveCamera(CameraUpdateFactory.zoomTo(mapboxMap.getCameraPosition().zoom-1));

                            initDriverForMoving(event.getTripIp(),tripPlanModel);

                            layout_confirm_pickup.setVisibility(View.GONE);
                            layout_confirm_derek.setVisibility(View.GONE);
                            driver_info_layout.setVisibility(View.VISIBLE);

                            //Load poto Driver
                            Glide.with(RequestDriverActivity.this)
                                    .load(tripPlanModel.getDriverInfoModel().getProfileImage())
                                    .into(img_driver);
                            txt_driver_name.setText(tripPlanModel.getDriverInfoModel().getNamaSupir());
                            txt_noHp_Driver.setText(tripPlanModel.getDriverInfoModel().getNoHpSupir());
                            nama_usaha.setText(tripPlanModel.getDriverInfoModel().getNamaUsaha());
                            txt_hargaTotal.setText("Rp "+tripPlanModel.getHargaPerjalanan());
                            Log.d("NAMAUSAHA",""+tripPlanModel.getHargaPerjalanan());

                            whatsApp_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String nomorCoba = "082137138344";
                                    openWhatsApp("+62"+tripPlanModel.getDriverInfoModel().getNoHpSupir());
                                }
                            });

                            call_driver_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    openCall(tripPlanModel.getDriverInfoModel().getNoHpSupir());
                                }
                            });




                            /*Toast.makeText(RequestDriverActivity.this, "Supir menerima "+tripPlanModel.getDriverInfoModel().getNamaSeller(),
                                    Toast.LENGTH_SHORT).show();*/

                        }else{
                            Toast.makeText(RequestDriverActivity.this, getString(R.string.trip_not_found)+event.getTripIp(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(RequestDriverActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void openCall(String noHp) {
        Uri uri = Uri.parse("tel:"+noHp);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        startActivity(intent);
    }

    private void openWhatsApp(String phoneNumber) {
        String url = "https://api.whatsapp.com/send?phone=" + phoneNumber;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }


    private void getRoute(MapboxMap mapboxMap, Point originLocat, Point driverLocat) {

        client = MapboxDirections.builder()
                .origin(driverLocat)
                .destination(originLocat)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
                .accessToken(getString(R.string.mapbox_access_token))
                .build();
        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                Timber.d("Responde code:" + response.code());
                if (response.body() == null) {
                    Toast.makeText(RequestDriverActivity.this, "No routes found, make sure you set the right user and access token.", Toast.LENGTH_SHORT).show();
                } else if (response.body().routes().size() < 1) {
                    Toast.makeText(RequestDriverActivity.this, "No routes found", Toast.LENGTH_SHORT).show();
                }

                currentRoute = response.body().routes().get(0);

                if (mapboxMap != null) {
                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            source = style.getSourceAs(ROUTE_SOURCE_ID);

                            if (source != null) {
                                source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
                            }


                            currentRoute = response.body().routes().get(0);
                            jarak = currentRoute.distance();
                            if (jarak < 1000) {
                                st = getTwoDigitOnlyAfterDecimal(currentRoute.distance()) + "\nMeter";
                                /*int harga = 2000 * Integer.parseInt(getTwoDigitOnlyAfterDecimal(currentRoute.distance()));
                                Toast.makeText(RequestDriverActivity.this, "harga : "+harga, Toast.LENGTH_SHORT).show();
                                Log.d("HARGA"," "+harga);*/
                            } else {
                                st = getTwoDigitOnlyAfterDecimal((currentRoute.distance() / 1000)) + "\nKm";
                            }

                            Log.e("DISTANCE_LOCATION", st);

                            waktu = currentRoute.duration();


                            if (waktu < (60 * 60)) {
                                wkt = getTwoDigitOnlyAfterDecimal((currentRoute.duration() / (60))) + "\nMenit.";
                            } else {
                                wkt = getTwoDigitOnlyAfterDecimal((currentRoute.duration() / (60 * 60))) + "\njam.";
                            }
                            Log.e("TIME_LOCATION", wkt);


                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Timber.e("Error: " + t.getMessage());
                Toast.makeText(RequestDriverActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initDriverForMoving(String tripIp, TripPlanModel tripPlanModel) {

        FirebaseDatabase.getInstance()
                .getReference(Common.TRIP)
                .child(tripIp)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        if (snapshot.exists()) { // Check null
                            TripPlanModel newData = snapshot.getValue(TripPlanModel.class);

                            LatLng driverOldPosition = new LatLng(tripPlanModel.getCurrentlat(), tripPlanModel.getCurrentLng());
                            Log.d("OLDPOST", "" + driverOldPosition);

                            LatLng driverNewLocation = new LatLng(newData.getCurrentlat(), newData.getCurrentLng());
                            Log.d("NEWPOST", "" + driverNewLocation);
                            ValueAnimator markerAnimator = ValueAnimator.ofObject(new TypeEvaluator<LatLng>() {
                                @Override
                                public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                                    return new LatLng(startValue.getLatitude() + (endValue.getLatitude() - startValue.getLatitude()) * fraction,
                                            startValue.getLongitude() + (endValue.getLongitude() - startValue.getLongitude()) * fraction);
                                }
                            }, new LatLng[]{driverOldPosition, driverNewLocation});
                            markerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    if (destinationMarker != null) {
                                        destinationMarker.setPosition((LatLng) animation.getAnimatedValue());
                                    }
                                }
                            });
                            markerAnimator.setDuration(7500);
                            markerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                            markerAnimator.start();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(RequestDriverActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void addDriverMarker(LatLng destiLocat) {
        destinationMarker = mapboxMap.addMarker(new MarkerOptions().position(destiLocat)
                .icon(IconFactory.getInstance(RequestDriverActivity.this).fromResource(R.drawable.derek_mobil)));
    }

    private void addPickupMarkerWithDuration(String wkt, LatLng originLoc) {
        Bitmap icon = Common.createIconWithDuration(this,wkt);
        Icon icon1 = IconFactory.recreate(ICON_DURATION_MARKER, icon);
        originMarker = mapboxMap.addMarker(new MarkerOptions().icon(icon1).position(originLoc));
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onSelectedPlaceEvent(SelectedPlaceEvent event){
        selectedPlaceEvent = event;
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onDeclineRequestEvent(DeclineRequestFromDriver event){
        if (lastDriverCall != null){
            Common.driversFound.get(lastDriverCall.getKey()).setDecline(true);
            //Driver has been decline request, just find new driver
            findNearbyDriver(selectedPlaceEvent);
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onDeclineRequestAndRemoveTripEvent(DeclineRequestAndRemoveTripFromDriver event){
        if (lastDriverCall != null){
            Common.driversFound.get(lastDriverCall.getKey()).setDecline(true);
            //Driver has been decline request, just finish the activity
            finish();

        }
    }

    private RequestDriverActivity.MyRequestDriverActivityCallback callback =
            new RequestDriverActivity.MyRequestDriverActivityCallback(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_request_driver);


        initViews();
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    private void initViews() {
       /* iMapboxAPI = RetrofitClient.getInstance().create(IMapboxAPI.class);*/

        //LAYOUT KONFIRMASI DEREK
        layout_confirm_derek = findViewById(R.id.confirm_derek_layout);
        confirm_derek_Btn = findViewById(R.id.btn_confirm_derek);
        txt_distance = findViewById(R.id.txt_distance);

        //LAYOUT KONFIRMASI PICKUP
        layout_confirm_pickup = findViewById(R.id.confirm_pickup_layout);
        address_pickup_Txt = findViewById(R.id.txt_address_pickup);
        confirm_jemput_Btn = findViewById(R.id.btn_confirm_jemput);
        /*btn_confirm = findViewById(R.id.search_btn);*/

        fill_maps = findViewById(R.id.fill_maps);
        btn_back = findViewById(R.id.btnBack);

        // LAYOUT finding_driver
        finding_driver = findViewById(R.id.finding_driver_layout);

        //MAIN LAYOUT
        main_layout = findViewById(R.id.activity_main);

        //LAYOUT ACCEPT RIDE
        driver_info_layout = findViewById(R.id.driver_info_layout);
        img_driver = findViewById(R.id.img_driver);
        nama_usaha = findViewById(R.id.nama_usaha);
        txt_hargaTotal = findViewById(R.id.txt_hargaJalan);
        txt_driver_name = findViewById(R.id.txt_driver_name);
        txt_noHp_Driver = findViewById(R.id.txt_noHp_Driver);
        txt_rating = findViewById(R.id.txt_rating);
        img_call_driver = findViewById(R.id.img_call_driver);
        img_message_driver = findViewById(R.id.img_message_driver);
        whatsApp_layout = findViewById(R.id.whatsApp);
        call_driver_layout = findViewById(R.id.call_driver);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        confirm_derek_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_confirm_pickup.setVisibility(View.VISIBLE);
                layout_confirm_derek.setVisibility(View.GONE);

                if (client != null) {
                    client.cancelCall();
                }

                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                                .target(new LatLng(selectedPlaceEvent.getOriginPoint().latitude(),
                                        selectedPlaceEvent.getOriginPoint().longitude()))
                                .zoom(17)
                                .build()), 4000);
            }
        });

        confirm_jemput_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MapsTowNowActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                mapboxMap.removeMarker(originMarker);
                mapboxMap.removeMarker(destinationMarker);
                mapboxMap.clear();


                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                                .target(new LatLng(selectedPlaceEvent.getOriginPoint().latitude(),
                                        selectedPlaceEvent.getOriginPoint().longitude()))
                                .zoom(17)
                                .build()), 4000);



                addMarkerWithPulseAnimation(selectedPlaceEvent);
            }
        });


    }



    private void addMarkerWithPulseAnimation(SelectedPlaceEvent selectedPlaceEvent) {
        layout_confirm_pickup.setVisibility(View.GONE);
        fill_maps.setVisibility(View.VISIBLE);
        finding_driver.setVisibility(View.VISIBLE);


        originMarker = mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(selectedPlaceEvent.getOriginPoint().latitude(),
                        selectedPlaceEvent.getOriginPoint().longitude()))
                .setIcon(IconFactory.getInstance(RequestDriverActivity.this)
                        .fromResource(R.drawable.icon_location_user)));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        startMapCameraSpinningAnimation(selectedPlaceEvent);

    }

    private void startMapCameraSpinningAnimation(SelectedPlaceEvent selectedPlaceEvent) {
        if (valueAnimator != null){
            valueAnimator.cancel();
        }
        valueAnimator = ValueAnimator.ofFloat(0,DESIRED_NUM_OF_SPINS*360);
        valueAnimator.setDuration(DESIRED_NUM_OF_SPINS*DESIRED_NUM_OF_SPINS*DESIRED_NUM_OF_SPINS*1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setStartDelay(100);
        valueAnimator.addUpdateListener(vAnimator->{
            Float newBearingValue = (Float)vAnimator.getAnimatedValue();
            mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(selectedPlaceEvent.getOriginPoint().latitude(),selectedPlaceEvent.getOriginPoint().longitude()))
                    .zoom(17f)
                    .tilt(45f)
                    .bearing(newBearingValue)
                    .build()));
        });
        valueAnimator.start();

        findNearbyDriver(selectedPlaceEvent);
    }

    private void findNearbyDriver(SelectedPlaceEvent selectedPlaceEvent) {
        if (Common.driversFound.size() > 0){

            float min_distance= 0;
            DriverGeoModel foundDriver = null;
            Location currentRiderLocation = new Location("");
            currentRiderLocation.setLatitude(selectedPlaceEvent.getOriginPoint().latitude());
            currentRiderLocation.setLongitude(selectedPlaceEvent.getOriginPoint().longitude());
            for (String key:Common.driversFound.keySet()){
                Location driverLocation = new Location("");
                driverLocation.setLatitude(Common.driversFound.get(key).getGeoLocation().latitude);
                driverLocation.setLongitude(Common.driversFound.get(key).getGeoLocation().longitude);

                //COMPARE 2 LOKASI
                if (min_distance == 0) {
                    min_distance = driverLocation.distanceTo(currentRiderLocation); // PERTAMA DEFAULT MIN LOKASI

                    if (!Common.driversFound.get(key).isDecline()) { // if not decline before

                        foundDriver = Common.driversFound.get(key);
                        break; // Exit loop because we found driver
                    } else
                        continue; // if already decline before, just skip and continue

                } else if (driverLocation.distanceTo(currentRiderLocation) < min_distance) {

                    //if have any driver smaller min_distance. just get it
                    min_distance = driverLocation.distanceTo(currentRiderLocation); // PERTAMA DEFAULT MIN LOKASI

                    if (!Common.driversFound.get(key).isDecline()) { // if not decline before

                        foundDriver = Common.driversFound.get(key);
                        break; // Exit loop because we found driver
                    } else
                        continue; // if already decline before, just skip and continue
                }



            }
            // After loop
            if (foundDriver != null){
                UserUtils.sendRequestToDriver(this,main_layout,st,foundDriver,selectedPlaceEvent);
                lastDriverCall = foundDriver;
                /*Toast.makeText(this, new StringBuilder("Found driver: ")
                                .append(foundDriver.getDriverInfoModel().getNamaSupir())
                                .append(foundDriver.getDriverInfoModel().getNamaSupir()),
                        Toast.LENGTH_LONG).show();*/
                Log.d("Nemu Driver :",""+foundDriver.getDriverInfoModel().getNamaSupir());

            }
            else {
                Alerter.create(RequestDriverActivity.this)
                        .setTitle("Supir Menolak ")
                        .setText(" Supir derek menolak pesanan Anda!")
                        .enableProgress(false)
                        .setBackgroundColorInt(getResources().getColor(R.color.orange_200))
                        .enableSwipeToDismiss()
                        .setIcon(R.drawable.truck_icon)
                        .setIconColorFilter(0)
                        .show();
                //Toast.makeText(this, ""+getString(R.string.no_drivers_accept_request), Toast.LENGTH_SHORT).show();
                lastDriverCall = null;
                finish();
            }

        } else {
            //Not found
            Toast.makeText(this, ""+getString(R.string.drivers_not_found), Toast.LENGTH_LONG).show();
            lastDriverCall=null;
            finish();
        }
    }

    @Override
    public void onMapReady(@NonNull @NotNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull @NotNull Style style) {

                MarkerViewManager markerViewManager = new MarkerViewManager(mapView, mapboxMap);
                Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_location_on_24, null);
                Bitmap mBitmap = BitmapUtils.getBitmapFromDrawable(drawable);
                /*myLocationComponent(style);*/
                //loadAvailableDriver();
                /*initSearchEdt();*/

                style.addImage(symbolIconId, mBitmap);
                setUpSource(style);
                setupLayer(style);
                drawPath(selectedPlaceEvent);
            }
        });
    }

    private void drawPath(SelectedPlaceEvent selectedPlaceEvent) {
        client = MapboxDirections.builder()
                .origin(selectedPlaceEvent.getOriginPoint())
                .destination(selectedPlaceEvent.getDestinationPoint())
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
                .accessToken(getString(R.string.mapbox_access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                Timber.d("Responde code:"+ response.code());
                if (response.body() == null) {
                    Toast.makeText(RequestDriverActivity.this, "No routes found, make sure you set the right user and access token.", Toast.LENGTH_SHORT).show();
                } else if (response.body().routes().size() < 1){
                    Toast.makeText(RequestDriverActivity.this, "No routes found", Toast.LENGTH_SHORT).show();
                }

                currentRoute = response.body().routes().get(0);

                if (mapboxMap != null) {
                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            source = style.getSourceAs(ROUTE_SOURCE_ID);

                            if (source != null) {
                                source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
                            }

                            originLatLng = new LatLng(selectedPlaceEvent.getOriginPoint().latitude(),
                                    selectedPlaceEvent.getOriginPoint().longitude());
                            destinationLatLng = new LatLng(selectedPlaceEvent.getDestinationPoint().latitude(),
                                    selectedPlaceEvent.getDestinationPoint().longitude());


                            currentRoute = response.body().routes().get(0);
                            jarak = currentRoute.distance();
                            Log.d("JARAK",""+jarak*150);
                            if (jarak < 1000){
                                st = getTwoDigitOnlyAfterDecimal(currentRoute.distance())+" Meter";
                            } else {
                                st = getTwoDigitOnlyAfterDecimal((currentRoute.distance()/1000))+" Km";
                            }

                            Log.e("DISTANCE_LOCATION",st);

                            waktu = currentRoute.duration();

                            if(waktu < (60*60)){
                                wkt = getTwoDigitOnlyAfterDecimal((currentRoute.duration()/(60)))+"\nMenit.";
                            }else {
                                wkt = getTwoDigitOnlyAfterDecimal((currentRoute.duration()/(60*60)))+"\njam.";
                            }
                            Log.e("TIME_LOCATION",wkt);

                            Log.d("TAG",""+originLatLng);

                            namaLokasiOrigin(originLatLng, wkt);

                            namaLokasiDestination(destinationLatLng, st, jarak);

                           /* //Update here
                            selectedPlaceEvent.setOriginAddress();*/



                            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                    .include(originLatLng)
                                    .include(destinationLatLng)
                                    .build();

                            addOriginMarker(wkt);
                            txt_distance.setText(st);

                            addDestinationMarker(st);
                            mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,160));
                            mapboxMap.moveCamera(CameraUpdateFactory.zoomTo(mapboxMap.getCameraPosition().zoom-1));

                            
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Timber.e("Error: " + t.getMessage());
                Toast.makeText(RequestDriverActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void namaLokasiDestination(LatLng destination, String st, double jarak) {

        MapboxGeocoding reverseGeocoding = MapboxGeocoding.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .query(Point.fromLngLat(destination.getLongitude(),destination.getLatitude()))
                .geocodingTypes(GeocodingCriteria.TYPE_POI)
                .build();
        reverseGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                List<CarmenFeature> results = response.body().features();

                List<CarmenFeature> chosenLocationString = results;

                if (results.size() > 0){
                    // Carmenfeature
                    CarmenFeature feature;

                    Point firsResultPoint = results.get(0).center();

                    feature=results.get(0);

                    endLocation+=feature.placeName();
                    endLocation = endLocation.replace(", Dhaka, Bangladesh", ".");
                    Log.d("COBA DISINI",""+endLocation);
                    selectedPlaceEvent.setDestinationAddress(endLocation);
                    selectedPlaceEvent.setDestinationPoint(Point.fromLngLat(destination.getLongitude(),destination.getLatitude()));

                    selectedPlaceEvent.setDistanceValue(st);
                    //
                    selectedPlaceEvent.setJarakValue(jarak);


                } else {
                    Toast.makeText(RequestDriverActivity.this, "Tidak menemukan nama lokasi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                Toast.makeText(RequestDriverActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void namaLokasiOrigin(LatLng originLatLng, String wkt) {
        MapboxGeocoding reverseGeocoding = MapboxGeocoding.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .query(Point.fromLngLat(originLatLng.getLongitude(),originLatLng.getLatitude()))
                .geocodingTypes(GeocodingCriteria.TYPE_POI)
                .build();
        reverseGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                List<CarmenFeature> results = response.body().features();

                List<CarmenFeature> chosenLocationString = results;

                if (results.size() > 0){
                    // Carmenfeature
                    CarmenFeature feature;

                    Point firsResultPoint = results.get(0).center();

                    feature=results.get(0);

                    startLocation+=feature.placeName();
                    startLocation=startLocation.replace(", Dhaka, Bangladesh",".");
                    Log.d("COBA DISINI",""+startLocation);
                    address_pickup_Txt.setText(startLocation);
                    selectedPlaceEvent.setOriginAddress(startLocation);
                    selectedPlaceEvent.setOriginPoint(Point.fromLngLat(originLatLng.getLongitude(),originLatLng.getLatitude()));
                    selectedPlaceEvent.setDurationValue(wkt);


                } else {
                    Toast.makeText(RequestDriverActivity.this, "Tidak menemukan nama lokasi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                Toast.makeText(RequestDriverActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMarkerPost(LatLng originLoc) {

        View view = getLayoutInflater().inflate(R.layout.pickup_info_with_duration_windows,null);
        //TextView txt_time = view.findViewById(R.id.txt_duration);

        IconGenerator iconGenerator = new IconGenerator(this);
        iconGenerator.setContentView(view);
        iconGenerator.setBackground(new ColorDrawable(Color.TRANSPARENT));
        Bitmap icon = iconGenerator.makeIcon();
        String IconMarkerUser;
        Icon icon1 = IconFactory.recreate(ICON_USER_PICK_MARKER, icon);

        Log.d("USERPICK ",""+originLoc);


        userPickMarker = mapboxMap.addMarker(new MarkerOptions()
                .icon(icon1)
                .position(originLoc));

    }




    private void addDestinationMarker(String distance) {
        View view = getLayoutInflater().inflate(R.layout.destination_info_window, null);

        TextView txt_distance = (TextView) view.findViewById(R.id.txt_distance);
        txt_distance.setText(distance);

        IconGenerator iconGenerator = new IconGenerator(this);
        iconGenerator.setContentView(view);
        iconGenerator.setBackground(new ColorDrawable(Color.TRANSPARENT));
        Bitmap icon = iconGenerator.makeIcon();
        String IconMarkerUser;
        Icon icon1 = IconFactory.recreate(ICON_USER_FINISH_MARKER, icon);

        destinationLatLng = new LatLng(selectedPlaceEvent.getDestinationPoint().latitude(),
                selectedPlaceEvent.getDestinationPoint().longitude());

        destinationMarker = mapboxMap.addMarker(new MarkerOptions()
                .icon(icon1)
                .position(destinationLatLng));
    }

    private void addOriginMarker(String time) {
        View view = getLayoutInflater().inflate(R.layout.origin_info_window, null);

        TextView txt_time = (TextView) view.findViewById(R.id.txt_time);

        txt_time.setText(time);

        //CREATE ICON MARKER
        IconGenerator iconGenerator = new IconGenerator(this);
        iconGenerator.setContentView(view);
        iconGenerator.setBackground(new ColorDrawable(Color.TRANSPARENT));
        Bitmap icon = iconGenerator.makeIcon();
        String IconMarkerUser;
        Icon icon1 = IconFactory.recreate(ICON_USER_MARKER, icon);

        originLatLng = new LatLng(selectedPlaceEvent.getOriginPoint().latitude(),
                selectedPlaceEvent.getOriginPoint().longitude());

        originMarker = mapboxMap.addMarker(new MarkerOptions()
                .icon(icon1)
                .position(originLatLng));


    }

    private String getTwoDigitOnlyAfterDecimal(double rawDigits) {
        String twoDigitsDecimalNo = "no data";
        if(rawDigits != 0){
            twoDigitsDecimalNo = String.format("%.2f", rawDigits);
        }
        return twoDigitsDecimalNo;
    }



    private class MyRequestDriverActivityCallback implements LocationEngineCallback<LocationEngineResult>{

        private final WeakReference<RequestDriverActivity> myLocationToMarkerWeakReference;

        MyRequestDriverActivityCallback(RequestDriverActivity activity){
            this.myLocationToMarkerWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult result) {
            RequestDriverActivity locationMarkerActivity = myLocationToMarkerWeakReference.get();

            if (locationMarkerActivity != null) {
                Location location = result.getLastLocation();

/*
                Location previousLocation, currentLocation = null;

*/


                double latitude = location.getLatitude();
                double longitude = location.getLongitude();


                /*locationSaya = new Location("");
                locationSaya.setLatitude(latitude);
                locationSaya.setLongitude(longitude);*/



                if (location == null) {

                    return;
                }


                if (locationMarkerActivity.mapboxMap != null && result.getLastLocation() != null) {
                    locationMarkerActivity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());

                }

            }

        }

        @Override
        public void onFailure(@NonNull @NotNull Exception exception) {

        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void myLocationComponent(@NonNull Style loadedMapStyle) {

        //Check if permisiion are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Create and customize the LocationComponent's options
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .elevation(5)
                    .accuracyAlpha(.6f)
                    .accuracyColor(Color.RED)
                    .foregroundDrawable(R.drawable.ic_baseline_directions_car_24)
                    .build();

// Get an instance of the component
            locationComponent = mapboxMap.getLocationComponent();

            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build();

// Activate with options
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
            initLocationEngine();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
        /*loadAvailableDriver();*/
    }



    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[] {0f, -8f})
        ));

        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(getColor(R.color.orange_200)));

        loadedMapStyle.addLayer(routeLayer);

// Add the red marker icon image to the map
        loadedMapStyle.addImage(RED_PIN_ICON_ID, BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.ic_baseline_directions_car_24)));

// Add the red marker icon SymbolLayer to the map
        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                iconImage(RED_PIN_ICON_ID),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[] {0f, -9f})));
    }

    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);
            //destination = Point.fromLngLat(point.getLongitude(), point.getLatitude());

            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
            // Then retrieve and update the source designated for showing a selected location's symbol layer icon

            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[]{Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }

                }
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (client != null) {
            client.cancelCall();
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Enable Location", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted){
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    myLocationComponent(style);
                }
            });
        }else {
            Toast.makeText(this, "Permission Required", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}