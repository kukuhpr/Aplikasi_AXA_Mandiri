package com.example.mobilderekuser.Ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mobilderekuser.Adapter.AdapterHistoryPerjalananSupir;
import com.example.mobilderekuser.Common.Common;
import com.example.mobilderekuser.Model.EventBus.DriverRequestRecevived;
import com.example.mobilderekuser.Model.EventBus.NotifyToRiderEvent;
import com.example.mobilderekuser.Model.ReviewModel;
import com.example.mobilderekuser.Model.RiderModel;
import com.example.mobilderekuser.Model.TripPlanModel;
import com.example.mobilderekuser.Model.UserModel;
import com.example.mobilderekuser.R;
import com.example.mobilderekuser.Utils.UserUtils;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kusu.library.LoadingButton;
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
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
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
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class HomeSupirActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

    //Layout Profile
    @BindView(R.id.rl_profile)
    RelativeLayout rl_profile;
    @BindView(R.id.layout_profileDriver)
    CardView layout_profileDriver;
    @BindView(R.id.txt_namaDriver)
    TextView txt_namaDriver;
    @BindView(R.id.txt_noHpDriver)
    TextView txt_noHpDriver;
    @BindView(R.id.txt_nama_perusahaan)
    TextView txt_nama_perusahaan;
    @BindView(R.id.txt_jumlah_kerjaan)
    TextView txt_kerjaan;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.txt_jumlah_rating)
    TextView txt_rating;
    @BindView(R.id.iv_potoDriver)
    CircleImageView iv_potoDriver;

    @BindView(R.id.chip_decline)
    Chip chip_decline;
    @BindView(R.id.layout_accept)
    CardView layout_accept;
    @BindView(R.id.circularProgressBar)
    CircularProgressBar circularProgressBar;
    @BindView(R.id.iv_profileImage)
    CircleImageView iv_profileImage;
    @BindView(R.id.txt_harga)
    TextView txt_harga;
    @BindView(R.id.txt_estimate_time)
    TextView txt_estimate_time;
    @BindView(R.id.txt_estimate_distance)
    TextView txt_estimate_distance;
    @BindView(R.id.root_layout)
    FrameLayout root_layout;

    //LAYOUT MULAI DEREK
    @BindView(R.id.layout_start_uber)
    CardView layout_start_uber;
    @BindView(R.id.txt_rider_name)
    TextView txt_rider_name;
    @BindView(R.id.txt_start_derek_estimate_distance)
    TextView txt_start_derek_estimate_distance;
    @BindView(R.id.txt_start_derek_estimate_time)
    TextView txt_start_derek_estimate_time;
    @BindView(R.id.img_phone_call)
    ImageView img_phone_call;
    @BindView(R.id.img_whatsapp)
    ImageView img_whatsapp;
    @BindView(R.id.btn_start_derek)
    LoadingButton btn_start_uber;
    @BindView(R.id.btn_complete_derek)
    LoadingButton btn_complete_derek;

    @BindView(R.id.layout_notify_rider)
    LinearLayout layout_notify_rider;
    @BindView(R.id.txt_notify_rider)
    TextView txt_notify_rider;
    @BindView(R.id.progress_notify)
    ProgressBar progress_notify;



    private static final int REQUEST_CODE_AUTOCOMPLETE = 7171;
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 15000L; // 15 sec
    /*private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;*/
    private static final long DEFAULT_MAX_WAIT_TIME = 10000;  // 10 sec

    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";

    private static final String TAG = "MapsTowNow";

    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationComponent locationComponent;
    private LocationLayerPlugin locationLayerPlugin;
    private Location mLastLocation;
    private DirectionsRoute currentRoute;
    private MapboxDirections client;


    String destinationLoct;
    LatLng chosenLatLng, originLatLng;

    private Map<String, Marker> markers;

    Button logout_btn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersInfoRef;
    private static DatabaseReference onlineRef;
    private DatabaseReference driversLocationRef;
    private static DatabaseReference currentUserRef;

    private MapView mapView;
    private MapboxMap mapboxMap;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private double lokasiLat, lokasiLon;
    private String locationSaya;
    private String cityName;

    private ArrayList<ReviewModel> reviewModelList;

    private LatLng originLocation, tujuanlocation;

    public static Marker marker;

    private double jarak, waktu;
    private String st, wkt, hargaDelivery;
    private int hargaTotal;

    private DriverRequestRecevived driverRequestRecevived;
    private Disposable countDownEvent;
    private String tripNumberId="";

    private boolean isTripStart=false,onlineSystemAlreadyRegister=false;

    private GeoFire pickupGeoFire, destinationGeoFire;
    private GeoQuery pickupGeoQuery, destinationGeoQuery;

    private GeoQueryEventListener pickupGeoQueryListener = new GeoQueryEventListener() {
        @Override
        public void onKeyEntered(String key, GeoLocation location) {
            btn_start_uber.setEnabled(true); // When Driver arrived pickup location, they can start derek
            UserUtils.sendNotifyToRider(getApplicationContext(),root_layout,key);
            if (pickupGeoQuery != null){

                // Remove geoFire
                pickupGeoFire.removeLocation(key);
                pickupGeoFire = null;
                pickupGeoQuery.removeAllListeners();
            }

        }

        @Override
        public void onKeyExited(String key) {
            btn_start_uber.setEnabled(false);

        }

        @Override
        public void onKeyMoved(String key, GeoLocation location) {

        }

        @Override
        public void onGeoQueryReady() {

        }

        @Override
        public void onGeoQueryError(DatabaseError error) {

        }
    };

    private GeoQueryEventListener destinationGeoQueryListener = new GeoQueryEventListener() {
        @Override
        public void onKeyEntered(String key, GeoLocation location) {
            Toast.makeText(HomeSupirActivity.this, "Anda telah sampai di tempat tujuan", Toast.LENGTH_SHORT).show();
            btn_complete_derek.setEnabled(true);
            if (destinationGeoQuery != null){
                destinationGeoFire.removeLocation(key);
                destinationGeoFire = null;
                destinationGeoQuery.removeAllListeners();
            }
        }

        @Override
        public void onKeyExited(String key) {

        }

        @Override
        public void onKeyMoved(String key, GeoLocation location) {

        }

        @Override
        public void onGeoQueryReady() {

        }

        @Override
        public void onGeoQueryError(DatabaseError error) {

        }
    };

    private CountDownTimer waiting_timer;

    ValueEventListener onlineValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            if (snapshot.exists() && currentUserRef !=null)
                currentUserRef.onDisconnect().removeValue();
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {
            Toast.makeText(HomeSupirActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private MyHomeDriverActivityCallback callback =
            new MyHomeDriverActivityCallback(this);
    private Marker finishMarker, pickupMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_home_supir);


        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        initView();

    }

    private void initView() {
        ButterKnife.bind(this);
        chip_decline = findViewById(R.id.chip_decline);
        layout_accept = findViewById(R.id.layout_accept);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        iv_profileImage = findViewById(R.id.iv_profileImage);
        txt_harga = findViewById(R.id.txt_harga);
        txt_estimate_time = findViewById(R.id.txt_estimate_time);
        txt_estimate_distance = findViewById(R.id.txt_estimate_distance);
        root_layout = findViewById(R.id.root_layout);

        //Layout profile
        rl_profile = findViewById(R.id.rl_profile);
        txt_namaDriver = findViewById(R.id.txt_namaDriver);
        txt_noHpDriver = findViewById(R.id.txt_noHpDriver);
        txt_nama_perusahaan = findViewById(R.id.txt_nama_perusahaan);
        txt_kerjaan = findViewById(R.id.txt_jumlah_kerjaan);
        txt_rating = findViewById(R.id.txt_jumlah_rating);
        ratingBar = findViewById(R.id.ratingBar);
        iv_potoDriver = findViewById(R.id.iv_potoDriver);
        layout_profileDriver = findViewById(R.id.layout_profileDriver);

        //Layout mulai derek
        layout_start_uber = findViewById(R.id.layout_start_uber);
        txt_rider_name = findViewById(R.id.txt_rider_name);
        txt_start_derek_estimate_distance = findViewById(R.id.txt_start_derek_estimate_distance);
        txt_start_derek_estimate_time = findViewById(R.id.txt_start_derek_estimate_time);
        img_phone_call = findViewById(R.id.img_phone_call);
        img_whatsapp = findViewById(R.id.img_whatsapp);
        btn_start_uber = findViewById(R.id.btn_start_derek);
        btn_complete_derek = findViewById(R.id.btn_complete_derek);

        layout_notify_rider = findViewById(R.id.layout_notify_rider);
        txt_notify_rider = findViewById(R.id.txt_notify_rider);
        progress_notify = findViewById(R.id.progress_notify);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        driversLocationRef = FirebaseDatabase.getInstance().getReference(Common.DRIVERS_LOCATION_REFERENCES)
                .child(String.valueOf(locationSaya));
        currentUserRef = driversLocationRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        usersInfoRef = firebaseDatabase.getReference(Common.USERS_INFO_REFERENCE);


        checkUser();



        rl_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(HomeSupirActivity.this, ProfileUserActivity.class));
            }
        });

        chip_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (driverRequestRecevived != null) {
                    if (TextUtils.isEmpty(tripNumberId)){
                        if (countDownEvent != null)
                            countDownEvent.dispose();
                        chip_decline.setVisibility(View.GONE);
                        layout_accept.setVisibility(View.GONE);
                        layout_profileDriver.setVisibility(View.VISIBLE);
                        mapboxMap.clear();
                        UserUtils.sendDeclineRequest(root_layout, getApplicationContext(), driverRequestRecevived.getKey());
                        driverRequestRecevived = null;

                    } else {
                        chip_decline.setVisibility(View.GONE);
                        layout_start_uber.setVisibility(View.GONE);
                        layout_profileDriver.setVisibility(View.VISIBLE);
                        mapboxMap.removeMarker(pickupMarker);
                        client.cancelCall();
                        UserUtils.sendDeclineAndRemoveTripRquest(root_layout, getApplicationContext(),
                                driverRequestRecevived.getKey(),tripNumberId);
                        tripNumberId = ""; // Set TripNumberId to empty
                        driverRequestRecevived = null;
                        makeDriverOnline(locationComponent.getLastKnownLocation());
                    }

                }

            }
        });

        btn_start_uber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (client != null) {
                    client.cancelCall();
                }
                //Cancel waiting timer
                if (waiting_timer != null) waiting_timer.cancel();
                layout_notify_rider.setVisibility(View.GONE);
                if (driverRequestRecevived != null){

                    LatLng destinationLatLng = new LatLng(
                            Double.parseDouble(driverRequestRecevived.getDestinationLocation().split(",")[0]),
                            Double.parseDouble(driverRequestRecevived.getDestinationLocation().split(",")[1])
                    );

                    Point destinatUser = Point.fromLngLat(destinationLatLng.getLongitude(),destinationLatLng.getLatitude());

                    Point driverLokasi = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                            locationComponent.getLastKnownLocation().getLatitude());

                    LatLng driverLoct = new LatLng(locationComponent.getLastKnownLocation().getLatitude(),
                            locationComponent.getLastKnownLocation().getLongitude());




                    getRoute(mapboxMap,driverLokasi, destinatUser);



                    LatLngBounds latLngBounds = new LatLngBounds.Builder()
                            .include(driverLoct)
                            .include(destinationLatLng)
                            .build();

                    finishMarker = mapboxMap.addMarker(new MarkerOptions()
                            .position(destinationLatLng)
                            .title(driverRequestRecevived.getDestinationLocationString())
                            .icon(IconFactory.getInstance(HomeSupirActivity.this).fromResource(R.drawable.icon_finish_marker)));
                    mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 160));
                    mapboxMap.moveCamera(CameraUpdateFactory.zoomTo(mapboxMap.getCameraPosition().zoom - 1));

                    createGeoFireDestinationLocation(driverRequestRecevived.getKey(),destinationLatLng);
                }
                btn_start_uber.setVisibility(View.GONE);
                chip_decline.setVisibility(View.GONE);
                btn_complete_derek.setVisibility(View.VISIBLE);
            }
        });

        btn_complete_derek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(HomeDriverActivity.this, "Complete Trip Fake Action", Toast.LENGTH_SHORT).show();
                //Pertama, update trip to done
                Map<String, Object> update_trip = new HashMap<>();
                update_trip.put("done",true);
                FirebaseDatabase.getInstance()
                        .getReference(Common.Trip)
                        .child(tripNumberId)
                        .updateChildren(update_trip)
                        .addOnSuccessListener(unused -> {

                            UserUtils.sendCompleteTripToRider(root_layout,getApplicationContext(),driverRequestRecevived.getKey(),
                                    tripNumberId);

                            //Clear map
                            mapboxMap.clear();
                            mapboxMap.removeMarker(finishMarker);
                            tripNumberId =""; // set tripNumberId to empty
                            isTripStart = false; // Return original state
                            chip_decline.setVisibility(View.GONE);

                            layout_accept.setVisibility(View.GONE);
                            circularProgressBar.setProgress(0);

                            layout_start_uber.setVisibility(View.GONE);

                            layout_notify_rider.setVisibility(View.GONE);
                            progress_notify.setProgress(0);

                            btn_complete_derek.setEnabled(false);
                            btn_complete_derek.setVisibility(View.GONE);

                            btn_start_uber.setEnabled(false);
                            btn_start_uber.setVisibility(View.VISIBLE);

                            layout_profileDriver.setVisibility(View.VISIBLE);

                            destinationGeoFire = null;
                            pickupGeoFire = null;

                            driverRequestRecevived = null;
                            makeDriverOnline(locationComponent.getLastKnownLocation());
                        })
                        .addOnFailureListener(e -> Toast.makeText(HomeSupirActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });


    }

    private void createGeoFireDestinationLocation(String key, LatLng destinationLatLng) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Common.TRIP_DESTINATION_LOCATION_REF);
        destinationGeoFire = new GeoFire(ref);
        destinationGeoFire.setLocation(key, new GeoLocation(destinationLatLng.getLatitude(), destinationLatLng.getLongitude()),
                (key1, error) -> {

                });

    }

    @Override
    public void onMapReady(@NonNull @NotNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull @NotNull Style style) {

                myLocationComponent(style);
                Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_location_on_24, null);
                Bitmap mBitmap = BitmapUtils.getBitmapFromDrawable(drawable);
                style.addImage(symbolIconId, mBitmap);
                setUpSource(style);
                setupLayer(style);
            }
        });
    }

    @SuppressWarnings( {"MissingPermission"})
    private void myLocationComponent(@NonNull Style loadedMapStyle) {

        //Check if permission are enabled and if not request
        Dexter.withContext(getBaseContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        locationComponent = mapboxMap.getLocationComponent();

                        LocationComponentOptions locationComponentOptions = LocationComponentOptions.builder(getApplicationContext())
                                .build();

                        LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions.builder(getApplicationContext(), loadedMapStyle)
                                .locationComponentOptions(locationComponentOptions)
                                .useDefaultLocationEngine(false)
                                .build();

                        locationComponent.activateLocationComponent(locationComponentActivationOptions);
                        locationComponent.setLocationComponentEnabled(true);
                        locationComponent.setCameraMode(CameraMode.TRACKING_GPS);
                        locationComponent.setRenderMode(RenderMode.COMPASS);
                        initLocationEngine();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        permissionsManager = new PermissionsManager((PermissionsListener) getApplicationContext());
                        permissionsManager.requestLocationPermissions(HomeSupirActivity.this);
                        Toast.makeText(HomeSupirActivity.this, permissionDeniedResponse.getPermissionName() + "need enabled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                }).check();

    }


    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);

        /*geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                new GeoLocation());*/
    }

    private class MyHomeDriverActivityCallback implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<HomeSupirActivity> myLocationToMarkerWeakReference;
        private DatabaseReference currentUserRef, driversLocationRef;

        MyHomeDriverActivityCallback(HomeSupirActivity activity){
            this.myLocationToMarkerWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult result) {
            HomeSupirActivity locationMarkerActivity = myLocationToMarkerWeakReference.get();

            if (locationMarkerActivity != null) {
                Location location = result.getLastLocation();

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                lokasiLat = latitude;
                lokasiLon = longitude;

                LatLng newPosition = new LatLng(location.getLatitude(),location.getLongitude());

                if (pickupGeoFire != null){ // That mean geoFire has been create on Firebase
                    pickupGeoQuery =
                            pickupGeoFire.queryAtLocation(new GeoLocation(location.getLatitude(),
                                    location.getLongitude()),Common.MIN_RANGE_PICKUP_IN_KM);
                    pickupGeoQuery.addGeoQueryEventListener(pickupGeoQueryListener);
                }

                //Destination
                if (destinationGeoFire != null){ // That mean geoFire has been create on Firebase
                    destinationGeoQuery =
                            destinationGeoFire.queryAtLocation(new GeoLocation(location.getLatitude(),
                                    location.getLongitude()),Common.MIN_RANGE_PICKUP_IN_KM);
                    destinationGeoQuery.addGeoQueryEventListener(destinationGeoQueryListener);
                }

                mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition,16f));


                if (location == null) {
                    return;
                }

                if (!isTripStart) {
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addressList;
                    try {
                        addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        String cityName = addressList.get(0).getSubAdminArea();

                        locationSaya = cityName;

                        //UPDATE LOCATION
                        driversLocationRef = FirebaseDatabase.getInstance().getReference(Common.DRIVERS_LOCATION_REFERENCES)
                                .child(String.valueOf(locationSaya));
                        currentUserRef = driversLocationRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        // UPDATE LOCATION
                        geoFire = new GeoFire(driversLocationRef);
                        geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                new GeoLocation(location.getLatitude(), location.getLongitude()),
                                new GeoFire.CompletionListener() {
                                    @Override
                                    public void onComplete(String key, DatabaseError error) {
                                        if (error != null) {

                                            Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Alerter.create(HomeSupirActivity.this)
                                                    .setTitle("Tarik.in")
                                                    .setText("Kamu online...")
                                                    .enableProgress(false)
                                                    .setBackgroundColorInt(getResources().getColor(R.color.orange_200))
                                                    .enableSwipeToDismiss()
                                                    .setIcon(R.drawable.logo_tarikin)
                                                    .setIconColorFilter(0)
                                                    .show();
                                            /*Toast.makeText(locationMarkerActivity, "Kamu online", Toast.LENGTH_SHORT).show();*/
                                        }
                                    }
                                });

                        registerOnlineSystem();
                    } catch (Exception e) {
                        Log.d("ERROR DISINI",""+e.getMessage());
                        Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    makeDriverOnline(locationComponent.getLastKnownLocation());
                } else {
                    if (!TextUtils.isEmpty(tripNumberId)) {

                        // Update location of driver
                        Map<String, Object> update_data = new HashMap<>();
                        update_data.put("currentLat", locationComponent.getLastKnownLocation().getLatitude());
                        update_data.put("currentLng", locationComponent.getLastKnownLocation().getLongitude());

                        FirebaseDatabase.getInstance()
                                .getReference(Common.Trip)
                                .child(tripNumberId)
                                .updateChildren(update_data)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        Log.d("ERROR",""+e.getMessage());
                                        Toast.makeText(HomeSupirActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                });

                    }
                }


                if (locationMarkerActivity.mapboxMap != null && result.getLastLocation() != null) {
                    locationMarkerActivity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());

                }

            }

        }

        @Override
        public void onFailure(@NonNull @NotNull Exception exception) {
            HomeSupirActivity locationToMarkerActivity = myLocationToMarkerWeakReference.get();
            if (locationToMarkerActivity != null){
                Toast.makeText(locationToMarkerActivity, exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void makeDriverOnline(Location location) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addressList;
        try {

            addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String cityName = addressList.get(0).getSubAdminArea();

            locationSaya = cityName;

            //UPDATE LOCATION
            driversLocationRef = FirebaseDatabase.getInstance().getReference(Common.DRIVERS_LOCATION_REFERENCES)
                    .child(String.valueOf(locationSaya));
            currentUserRef = driversLocationRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            // UPDATE LOCATION
            GeoFire geoFire = new GeoFire(driversLocationRef);
            geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    new GeoLocation(location.getLatitude(), location.getLongitude()),
                    new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (error != null) {

                                Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                /*Toast.makeText(locationMarkerActivity, "Kamu online", Toast.LENGTH_SHORT).show();*/
                            }
                        }
                    });

            registerOnlineSystem();
        } catch (Exception e) {
            Log.d("ERROR DISINI",""+e.getMessage());
            // Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);


            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
            // Then retrieve and update the source designated for showing a selected location's symbol layer icon

            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[] {Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }

                    chosenLatLng = new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                            ((Point) selectedCarmenFeature.geometry()).longitude());



                    Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                            locationComponent.getLastKnownLocation().getLatitude());

                    Point destinationPoint = Point.fromLngLat(chosenLatLng.getLongitude(),chosenLatLng.getLatitude());

                    // Move map camera to the selected location
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 4000);


                }
            }

        }

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
        }
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

    private void setProcessLayout(boolean isProcess) {
        int color =-1;
        if (isProcess) {
            color = ContextCompat.getColor(getApplicationContext(), R.color.darkgrey);
            circularProgressBar.setIndeterminateMode(true);
        }else{
            color = ContextCompat.getColor(getApplicationContext(), R.color.white);
            circularProgressBar.setIndeterminateMode(false);
            circularProgressBar.setProgress(0);
        }

        circularProgressBar.setIndeterminateMode(true);
        txt_estimate_time.setTextColor(color);
        txt_estimate_distance.setTextColor(color);
        txt_harga.setTextColor(color);


    }

    private void setOfflineModeForDriver(DriverRequestRecevived event, String wkt, String st) {

        UserUtils.sendAcceptRequestToRider(root_layout,getApplicationContext(),event.getKey(),tripNumberId);

        //Go to offline
        if (currentUserRef != null)
            currentUserRef.removeValue();

        setProcessLayout(false);
        layout_profileDriver.setVisibility(View.GONE);
        layout_accept.setVisibility(View.GONE);
        chip_decline.setVisibility(View.GONE);
        layout_start_uber.setVisibility(View.VISIBLE);


        isTripStart = true;

    }



    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onDriverRequestRecevived(DriverRequestRecevived event) {

        driverRequestRecevived = event;

        Point driverLokasi = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());

        LatLng driverLoct = new LatLng(locationComponent.getLastKnownLocation().getLatitude(),
                locationComponent.getLastKnownLocation().getLongitude());

        String userPickup = driverRequestRecevived.getPickupLocation();
        LatLng destinatUser = new LatLng(Double.parseDouble(userPickup.split(",")[0]),
                Double.parseDouble(userPickup.split(",")[1]));

        double jarakDesti = driverRequestRecevived.getJarakValue();
        String hargaPerJarak = hargaDelivery;

        int hargaInt = Integer.parseInt(hargaPerJarak);
        int hargaFinal = hargaInt/1000;
        int jarakDes = (int)jarakDesti;

        hargaTotal = hargaFinal*jarakDes;

        txt_harga.setText("Rp "+hargaTotal);

        Log.d("COBA_DULU","Jarak : "+jarakDesti+" harga driver: "+hargaPerJarak);


        // Log.d("destinatUser", "destinatUser : " + destinatUser);

        Point pickUser = Point.fromLngLat(destinatUser.getLongitude(), destinatUser.getLatitude());


        getRoute(mapboxMap,driverLokasi, pickUser);
        // Log.d("POINT TO PICKUP LOCT", "Driver Loct : " + driverLokasi + "\n User Loct : " + pickUser);

        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(driverLoct)
                .include(destinatUser)
                .build();

        pickupMarker = mapboxMap.addMarker(new MarkerOptions()
                .position(destinatUser)
                .title("Pickup Location")
                .icon(IconFactory.getInstance(HomeSupirActivity.this).fromResource(R.drawable.icon_location_user)));

        createGeoFirePickupLocation(event.getKey(),destinatUser);


        Point pickup = Point.fromLngLat(destinatUser.getLongitude(),destinatUser.getLatitude());

        mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 160));
        mapboxMap.moveCamera(CameraUpdateFactory.zoomTo(mapboxMap.getCameraPosition().zoom - 1));


        //Point pickupLoct = Point.fromJson(event.getPickupLocation());
        chip_decline.setVisibility(View.VISIBLE);
        layout_accept.setVisibility(View.VISIBLE);
        layout_profileDriver.setVisibility(View.GONE);

        //COUNT DOWN
        countDownEvent = Observable.interval(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(x -> {
                    circularProgressBar.setProgress(circularProgressBar.getProgress() + 1f);
                })
                .takeUntil(aLong -> aLong == 100) // 10 detik
                .doOnComplete(() -> {
                    /*circularProgressBar.setProgress(0);
                    Toast.makeText(this, "AKSI BOONGAN BOOKING DEREK", Toast.LENGTH_SHORT).show();*/

                    createTripPlane(event, wkt, st);
                    changeStatusToTugas();

                }).subscribe();


    }

    private void changeStatusToTugas() {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","tugas");

        usersInfoRef.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(HomeSupirActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void createGeoFirePickupLocation(String key, LatLng destinatUser) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(Common.TRIP_PICKUP_REF);
        pickupGeoFire = new GeoFire(ref);
        pickupGeoFire.setLocation(key, new GeoLocation(destinatUser.getLatitude(), destinatUser.getLongitude()),
                new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                        if (error != null)
                            Toast.makeText(HomeSupirActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                        else
                            Log.d("KUKUHbisa", key+" was create success on geo fire");
                    }
                });
    }

    private void createTripPlane(DriverRequestRecevived event, String wkt, String st) {
        setProcessLayout(true);
        // Sync server time with device
        FirebaseDatabase.getInstance()
                .getReference(".info/serverTimeOffset")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        long timeOffset = snapshot.getValue(Long.class);
                        long estimateTimeInMs = System.currentTimeMillis()+timeOffset;
                        String timeText = new SimpleDateFormat("dd/MM/yyyy HH:mm aa")
                                .format(estimateTimeInMs);

                        FirebaseDatabase.getInstance()
                                .getReference(Common.USERS_INFO_REFERENCE)
                                .child(event.getKey())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {

                                            UserModel riderModel = snapshot.getValue(UserModel.class);

                                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                                return;
                                            }
                                            locationComponent.setLocationComponentEnabled(true);

                                            // Create Trip Planner
                                            TripPlanModel tripPlanModel = new TripPlanModel();
                                            tripPlanModel.setDriver(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            tripPlanModel.setRider(event.getKey());
                                            tripPlanModel.setPemilik(Common.currentDriver.getIdPemilik());

                                            //Time Text
                                            tripPlanModel.setTimText(timeText);

                                            tripPlanModel.setDriverInfoModel(Common.currentDriver);
                                            tripPlanModel.setUserModel(riderModel);
                                            tripPlanModel.setOrigin(event.getPickupLocation());
                                            tripPlanModel.setOriginString(event.getPickupLocationString());
                                            tripPlanModel.setDestination(event.getDestinationLocation());
                                            tripPlanModel.setDestinationString(event.getDestinationLocationString());
                                            tripPlanModel.setDistanceValue(event.getDistanceValue());
                                            tripPlanModel.setDurationValue(event.getDurationValue());
                                            tripPlanModel.setDistancePickup(st);
                                            tripPlanModel.setDurationPickup(wkt);
                                            tripPlanModel.setHargaPerjalanan(String.valueOf(hargaTotal));

                                            tripPlanModel.setCurrentlat(locationComponent.getLastKnownLocation().getLatitude());
                                            tripPlanModel.setCurrentLng(locationComponent.getLastKnownLocation().getLongitude());

                                            tripNumberId = Common.createUniqueTripIdNumber(timeOffset);
                                            tripPlanModel.setTripId(tripNumberId);

                                            FirebaseDatabase.getInstance()
                                                    .getReference(Common.Trip)
                                                    .child(tripNumberId)
                                                    .setValue(tripPlanModel)
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull @NotNull Exception e) {
                                                            Toast.makeText(HomeSupirActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                                        }
                                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    txt_rider_name.setText(riderModel.getNamaUser());
                                                    txt_start_derek_estimate_time.setText(wkt);
                                                    txt_start_derek_estimate_distance.setText(st);
                                                    img_phone_call.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            openCall(riderModel.getNoHpUser());
                                                        }
                                                    });

                                                    img_whatsapp.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            openWhatsApp("+62"+riderModel.getNoHpUser());
                                                        }
                                                    });

                                                    setOfflineModeForDriver(event,wkt,st);
                                                }
                                            });


                                        } else {
                                            Toast.makeText(HomeSupirActivity.this, ""+getBaseContext().getString(R.string.rider_not_found)+ " "+event.getKey(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                        Toast.makeText(HomeSupirActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(HomeSupirActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void loadMyInfo() {
        usersInfoRef.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            String uid = ""+ds.child("uid").getValue();
                            String nama = ""+ ds.child("namaSupir").getValue();
                            String noHp = ""+ ds.child("noHpSupir").getValue();
                            String profileImage = ""+ ds.child("profileImage").getValue();
                            String namaUsaha = ""+ ds.child("namaUsaha").getValue();
                            hargaDelivery = ""+ ds.child("price").getValue();

                            loadTotalKerjaan(uid);
                            loadReviews(uid);

                            txt_namaDriver.setText(nama);
                            txt_noHpDriver.setText(noHp);
                            txt_nama_perusahaan.setText(namaUsaha);

                            Glide.with(getApplicationContext())
                                    .load(profileImage)
                                    .placeholder(R.drawable.ic_baseline_account_circle_24)
                                    .fitCenter()
                                    .into(iv_potoDriver);

                            /*try{
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_baseline_account_circle_24)
                                        .into(iv_potoDriver);
                            } catch (Exception e){
                                iv_potoDriver.setImageResource(R.drawable.ic_baseline_account_circle_24);
                            }*/

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private float ratingSum = 0;
    private void loadReviews(String uid) {

        reviewModelList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.RATING);
        reference.orderByChild("idSupir").equalTo(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        reviewModelList.clear();
                        ratingSum = 0;
                        for (DataSnapshot ds : snapshot.getChildren()){
                            float rating = Float.parseFloat(""+ds.child("ratings").getValue());
                            Log.d("JUMLAHRATING", "" + rating);
                            ratingSum = ratingSum + rating;

                            ReviewModel reviewModel = ds.getValue(ReviewModel.class);
                            reviewModelList.add(reviewModel);

                        }

                        long numbOfReview = snapshot.getChildrenCount();
                        Log.d("SUMRATING", "" + numbOfReview);
                        float rataRating = ratingSum/numbOfReview;
                        Log.d("RATING-RATING", "" + String.format("%.2f", rataRating)+ " ("+numbOfReview+")");
                        txt_rating.setText(String.format("%.2f", rataRating)+ " ("+numbOfReview+")");
                        ratingBar.setRating(rataRating);

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });


    }

    private void loadTotalKerjaan(String uid) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Common.Trip);
        ref.orderByChild("driver").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                //Clear list sebelum dimasukkan

                for (DataSnapshot ds: snapshot.getChildren()){
                    TripPlanModel tripPlanModel = ds.getValue(TripPlanModel.class);


                }
                long totalOrder = snapshot.getChildrenCount();
                Log.d("KERJAAN",""+totalOrder);
                txt_kerjaan.setText(String.valueOf(totalOrder));
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void getRoute(MapboxMap mapboxMap, Point driverLoct, Point destinatUser) {
        client = MapboxDirections.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .origin(driverLoct)
                .destination(destinatUser)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .build();
        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

                Log.d(TAG, "Response code: " + response.code());
                if (response.body() == null){
                    Log.e(TAG, "No routes found, check right user and access token");
                    //Toast.makeText(HomeDriverActivity.this, "No routes found, check right user and access token", Toast.LENGTH_SHORT).show();
                    return;
                } else if (response.body().routes().size() == 0) {
                    Log.e(TAG,"No routes found");
                    return;
                }
                currentRoute = response.body().routes().get(0);
                jarak = currentRoute.distance();
                if (jarak < 1000){
                    st = getTwoDigitOnlyAfterDecimal(currentRoute.distance())+" meter";
                } else {
                    st = getTwoDigitOnlyAfterDecimal((currentRoute.distance()/1000))+" km";
                }

                Log.e("DISTANCE_LOCATION",st);

                waktu = currentRoute.duration();



                if(waktu < (60*60)){
                    wkt = getTwoDigitOnlyAfterDecimal((currentRoute.duration()/(60)))+" menit";
                }else {
                    wkt = getTwoDigitOnlyAfterDecimal((currentRoute.duration()/(60*60)))+" jam";
                }

                Log.e("TIME_LOCATION",wkt);

                //duration = currentRoute.duration();
                txt_estimate_time.setText(wkt);
                txt_estimate_distance.setText(st);



                if (mapboxMap != null) {
                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull @NotNull Style style) {
                            GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

                            if (source != null) {
                                source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
                            }

                        }
                    });



                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {

            }
        });
    }

    private String getTwoDigitOnlyAfterDecimal(double rawDigits) {
        String twoDigitsDecimalNo = "no data";
        if(rawDigits != 0){
            twoDigitsDecimalNo = String.format("%.2f", rawDigits);
        }
        return twoDigitsDecimalNo;
    }


    private void registerOnlineSystem() {
        if (!onlineSystemAlreadyRegister) {
            onlineRef.addValueEventListener(onlineValueEventListener);
            onlineSystemAlreadyRegister = true;
        }
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Log.d("CURRENT USER", "checkUser: "+firebaseUser.getUid());
        if (firebaseUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            locationEngine.removeLocationUpdates(callback);
            geoFire.removeLocation(FirebaseAuth.getInstance().getCurrentUser().getUid());
            onlineRef.removeEventListener(onlineValueEventListener);
            startActivity(intent);
        }else {
            registerOnlineSystem();
            loadMyInfo();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
       /* geoFire.removeLocation(FirebaseAuth.getInstance().getCurrentUser().getUid());
        onlineRef.removeEventListener(onlineValueEventListener);*/
       /* if (handler != null && runnable != null){
            handler.removeCallbacksAndMessages(null);
        }
        if (markerIconAnimator != null){
            markerIconAnimator.cancel();
        }*/
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        registerOnlineSystem();
    }

    @Override
    protected void onStart() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onDestroy() {
        /*locationEngine.removeLocationUpdates(callback);
        geoFire.removeLocation(FirebaseAuth.getInstance().getCurrentUser().getUid());
        onlineRef.removeEventListener(onlineValueEventListener);*/

        if (EventBus.getDefault().hasSubscriberForEvent(DriverRequestRecevived.class))
            EventBus.getDefault().removeStickyEvent(DriverRequestRecevived.class);
        if (EventBus.getDefault().hasSubscriberForEvent(NotifyToRiderEvent.class))
            EventBus.getDefault().removeStickyEvent(NotifyToRiderEvent.class);
        EventBus.getDefault().unregister(this);

        if (client != null) {
            client.cancelCall();
        }

        onlineSystemAlreadyRegister=false;

        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser == null) {
               // geoFire.removeLocation(firebaseAuth.getCurrentUser().getUid());
                onlineRef.removeEventListener(onlineValueEventListener);
            } else {
                geoFire.removeLocation(firebaseAuth.getCurrentUser().getUid());
                onlineRef.removeEventListener(onlineValueEventListener);
            }

        }

        if (client != null) {
            client.cancelCall();
        }
        super.onDestroy();
        mapView.onDestroy();

    }

}