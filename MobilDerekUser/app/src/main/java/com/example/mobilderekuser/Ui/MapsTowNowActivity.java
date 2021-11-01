package com.example.mobilderekuser.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;


import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilderekuser.Callback.IFirebaseDriverInfoListener;
import com.example.mobilderekuser.Callback.IFirebaseFailedListener;
import com.example.mobilderekuser.Common.Common;
import com.example.mobilderekuser.Model.AnimationModel;
import com.example.mobilderekuser.Model.DriverGeoModel;
import com.example.mobilderekuser.Model.DriverInfoModel;
import com.example.mobilderekuser.Model.EventBus.DeclineRequestFromDriver;
import com.example.mobilderekuser.Model.EventBus.SelectedPlaceEvent;
import com.example.mobilderekuser.Model.EventBus.ShowNotificationFinishTrip;
import com.example.mobilderekuser.Model.GeoQueryModel;
import com.example.mobilderekuser.R;
import com.example.mobilderekuser.Remote.IMapboxAPI;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
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

import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment;

import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;

import com.rengwuxian.materialedittext.MaterialEditText;

import com.tapadoo.alerter.Alerter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class MapsTowNowActivity extends AppCompatActivity implements OnMapReadyCallback,
        PermissionsListener, IFirebaseFailedListener, IFirebaseDriverInfoListener {

    //View SEARCH LOCATION LAYOUT
    @BindView(R.id.search_location_layout)
    CardView layout_search_location;
    @BindView(R.id.welcome_txt)
    TextView txt_hai;
    @BindView(R.id.edt_tujuanRider)
    TextInputEditText edt_tujuan;

    //View KONFIRMASI DEREK LAYOUT
    @BindView(R.id.confirm_derek_layout)
    CardView layout_confirm_derek;
    @BindView(R.id.btn_confirm_derek)
    Button confirm_derek_Btn;

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

    private PlaceAutocompleteFragment autocompleteFragment;

    private MapView mapView;
    private MapboxMap mapboxMap;
    private MarkerViewManager markerViewManager;
    private Marker originMarker, destinationMarker;

    private GeoFire geoFire;
    private GeoQuery geoQuery;

    private GeoJsonSource geoJsonSource;
    private ValueAnimator animator;
    public static double pre_lat, pre_long;
    public static Marker marker;
    private double distance = 1.0;


    private Location myLocation;

    private LatLng lokasiSaya;
    private static final String ICON_USER_MARKER = "icon-user-marker";
    private static final String ICON_USER_FINISH_MARKER = "icon-user-finish-marker";

    private static final double LIMIT_RANGE = 10; // KM
    private Location previousLocation, currentLocation;

    private boolean firstTime = true;

    private Boolean driverFound = false;
    private String driverFoundID;

    //LISTENER
    IFirebaseDriverInfoListener iFirebaseDriverInfoListener;
    IFirebaseFailedListener iFirebaseFailedListener;

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
    private NavigationMapRoute navigationMapRoute;

    private static final int LOCATION_REQUEST_CODE = 100;
    private String[] locationPermissions;

    private MyMapsTowNowActivityCallback callback =
            new MyMapsTowNowActivityCallback(this);

    private CarmenFeature startLocation;
    /*private CarmenFeature pointDestination;*/
    String destinationLoct;
    LatLng chosenLatLng, originLatLng;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersInfoRef, onlineRef, driversLocationRef, currentUserRef;
    private DriverGeoModel driverGeoModel;



    private boolean isNextLaunch = false;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private double lokasiLat, lokasiLon;
    private Location locationSaya;
    private String cityName;

    private double jarak, waktu;
    private String st, wkt;

    private LatLng originLocation;

    //
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IMapboxAPI iMapboxAPI;

    private Polyline orangePolyline, greyPolyline;
    private PolylineOptions polylineOptions, orangePolylineOptions;
    private List<LatLng> polylineList;

    //EFFECT CAMERA SPINNING
    private ValueAnimator valueAnimator;
    private static final int DESIRED_NUM_OF_SPINS = 5;
    private static final int DESIRED_SECONDS_PER_ONE_FULL_360_SPIN = 40;
    private DriverGeoModel lastDriverCall;
    private LatLng posisiMe;

    private FloatingActionButton btn_back;

    @Override
    protected void onStart() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        super.onStart();
        mapView.onStart();
    }
    @Override
    protected void onStop() {
        if (EventBus.getDefault().hasSubscriberForEvent(DeclineRequestFromDriver.class))
            EventBus.getDefault().removeStickyEvent(DeclineRequestFromDriver.class);
        if (EventBus.getDefault().hasSubscriberForEvent(ShowNotificationFinishTrip.class))
            EventBus.getDefault().removeStickyEvent(ShowNotificationFinishTrip.class);
        EventBus.getDefault().unregister(this);
        super.onStop();

        mapView.onStop();
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onShowNotificationFinishTrip(ShowNotificationFinishTrip event){
        Common.showNotification(getApplicationContext(), new Random().nextInt(),
                "Derek Selesai",
                "Derekmu: "+event.getTripKey()+" telah sampai tujuan",
                null);
    }


    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onDeclineRequestEvent(DeclineRequestFromDriver event){
        if (lastDriverCall != null){
            Common.driversFound.get(lastDriverCall.getKey()).setDecline(true);
            //Driver has been decline request, just find new driver
            findNearbyDriver(posisiMe);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_maps_tow_now);


        initViews();
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


    }


    private void initViews() {
        ButterKnife.bind(this);
        // LAYOUT SEARCH
        txt_hai = findViewById(R.id.welcome_txt);
        edt_tujuan = findViewById(R.id.edt_tujuanRider);
        layout_search_location = findViewById(R.id.search_location_layout);

        //LAYOUT KONFIRMASI DEREK
        layout_confirm_derek = findViewById(R.id.confirm_derek_layout);
        confirm_derek_Btn = findViewById(R.id.btn_confirm_derek);

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

        iFirebaseFailedListener = this;
        iFirebaseDriverInfoListener = this;

//init permission array
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        markerViewManager = new MarkerViewManager(mapView, mapboxMap);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        driversLocationRef = FirebaseDatabase.getInstance().getReference(Common.DRIVERS_LOCATION_REFERENCES);
        currentUserRef = FirebaseDatabase.getInstance().getReference(Common.DRIVERS_LOCATION_REFERENCES)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        usersInfoRef = firebaseDatabase.getReference(Common.RIDER_INFO_REFERENCES);



        posisiMe = new LatLng(lokasiLat, lokasiLon);

        loadAvailableDriver();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });


    }

    private void addMarkerWithPulseAnimation() {
        layout_confirm_pickup.setVisibility(View.GONE);
        fill_maps.setVisibility(View.VISIBLE);
        finding_driver.setVisibility(View.VISIBLE);


        originMarker = mapboxMap.addMarker(new MarkerOptions()
                .position(posisiMe)
                .setIcon(IconFactory.getInstance(MapsTowNowActivity.this)
                        .fromResource(R.drawable.icon_location_user)));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationComponent.setLocationComponentEnabled(true);
        startMapCameraSpinningAnimation(mapboxMap.getCameraPosition().target);

    }

    private void startMapCameraSpinningAnimation(LatLng target) {
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
                    .target(target)
                    .zoom(17f)
                    .tilt(45f)
                    .bearing(newBearingValue)
                    .build()));
        });
        valueAnimator.start();

        findNearbyDriver(target);
    }

    private void findNearbyDriver(LatLng target) {
        if (Common.driversFound.size() > 0){

            float min_distance= 0;
            DriverGeoModel foundDriver = null;
            Location currentRiderLocation = new Location("");
            currentRiderLocation.setLatitude(target.getLatitude());
            currentRiderLocation.setLongitude(target.getLongitude());
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
                /*Toast.makeText(this, new StringBuilder("Found driver: ")
                                .append(foundDriver.getDriverInfoModel().getNamaSeller())
                                .append(foundDriver.getDriverInfoModel().getNoHpSeller()),
                        Toast.LENGTH_LONG).show();*/
                Log.d("Nemu Driver :",""+foundDriver.getDriverInfoModel().getNamaSupir());


            }
            // After loop
            if (foundDriver != null){
                //UserUtils.sendRequestToDriver(this,main_layout,foundDriver,target);
                lastDriverCall = foundDriver;
            }
            else {
                Toast.makeText(this, ""+getString(R.string.no_drivers_accept_request), Toast.LENGTH_SHORT).show();
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

                //SETUP LAYER
                /*style.addImage(symbolIconId, BitmapFactory.decodeResource(MapsTowNowActivity.this.getResources(),
                        R.drawable.ic_baseline_directions_car_24));*/
                MarkerViewManager markerViewManager = new MarkerViewManager(mapView, mapboxMap);
                Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_location_on_24, null);
                Bitmap mBitmap = BitmapUtils.getBitmapFromDrawable(drawable);

                if (checkLocationPermission()){
                    myLocationComponent(style);
                } else {
                    requestLocationPermission();
                }


                initSearchEdt();
                style.addImage(symbolIconId, mBitmap);
                setUpSource(style);
                setupLayer(style);


            }
        });
    }

    private void initSearchEdt() {
        edt_tujuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.mapbox_access_token))
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#EEEEEE"))
                                .limit(10)
                                .country("id")
                                .geocodingTypes(GeocodingCriteria.TYPE_POI_LANDMARK)
                                .build(PlaceOptions.MODE_CARDS))
                        .build(MapsTowNowActivity.this);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);


            }
        });
    }

    private class MyMapsTowNowActivityCallback implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MapsTowNowActivity> myLocationToMarkerWeakReference;

        MyMapsTowNowActivityCallback(MapsTowNowActivity activity) {
            this.myLocationToMarkerWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult result) {
            MapsTowNowActivity locationMarkerActivity = myLocationToMarkerWeakReference.get();

            if (locationMarkerActivity != null) {
                Location location = result.getLastLocation();

/*
                Location previousLocation, currentLocation = null;

*/
                if (firstTime) {
                    previousLocation = currentLocation = location;
                    firstTime = false;
                } else {
                    previousLocation = currentLocation;
                    currentLocation = location;
                }

                if (previousLocation.distanceTo(currentLocation) / 1000 <= LIMIT_RANGE) // NOT OVER RANGE
                    loadAvailableDriver();
                else {

                }

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                lokasiLat = latitude;
                lokasiLon = longitude;

                locationSaya = location;

                /*locationSaya = new Location("");
                locationSaya.setLatitude(latitude);
                locationSaya.setLongitude(longitude);*/


                if (location == null) {

                    return;
                }


                if (locationMarkerActivity.mapboxMap != null && result.getLastLocation() != null) {
                    locationMarkerActivity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());

                }
                loadAvailableDriver();
            }
        }

        @Override
        public void onFailure(@NonNull @NotNull Exception exception) {

        }
    }


    private void loadAvailableDriver() {
        // LOAD ALL DRIVER IN CITY
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(lokasiLat, lokasiLon, 1);
            cityName = addressList.get(0).getSubAdminArea();
            DatabaseReference driver_location_ref = FirebaseDatabase.getInstance()
                    .getReference(Common.DRIVERS_LOCATION_REFERENCES)
                    .child(cityName);
            GeoFire gf = new GeoFire(driver_location_ref);
            GeoQuery geoQuery = gf.queryAtLocation(new GeoLocation(lokasiLat, lokasiLon), distance);
            //Log.d(TAG, "Latitude longitude" + lokasiLat + lokasiLon);
            geoQuery.removeAllListeners();
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    if (!Common.driversFound.containsKey(key)){
                        Alerter.create(MapsTowNowActivity.this)
                                .setTitle("Supir Tersedia ")
                                .setText("Sapa supir derek di sekitar "+cityName)
                                .enableProgress(false)
                                .setBackgroundColorInt(getResources().getColor(R.color.orange_200))
                                .enableSwipeToDismiss()
                                .setIcon(R.drawable.derek_mobil)
                                .setIconColorFilter(0)
                                .show();
                        Common.driversFound.put(key,new DriverGeoModel(key,location));
                    }
                    if (!driverFound) {
                        driverFound = true;

                        //Common.driversFound.add(new DriverGeoModel(key, location));
                        Log.d("Key ", key);
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
                    /*if (!driverFound) {
                        distance++;
                        addDriverMarker();
                    }*/
                    if (distance <= LIMIT_RANGE) {
                        distance++;
                        loadAvailableDriver();
                    } else {
                        distance = 1.0;
                        addDriverMarker();
                    }
                }

                @Override
                public void onGeoQueryError(DatabaseError error) {
                    Log.d("INDEX TOAST",""+error);
                    //Toast.makeText(MapsTowNowActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            driver_location_ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull @NotNull DataSnapshot dataSnapshot, String previousChildName) {

                    // Have new Driver
                    GeoQueryModel geoQueryModel = dataSnapshot.getValue(GeoQueryModel.class);
                    GeoLocation geoLocation = new GeoLocation(geoQueryModel.getL().get(0),
                            geoQueryModel.getL().get(1));
                    DriverGeoModel driverGeoModel = new DriverGeoModel(dataSnapshot.getKey(),
                            geoLocation);
                    Location newDriverLocation = new Location("");
                    newDriverLocation.setLatitude(geoLocation.latitude);
                    newDriverLocation.setLongitude(geoLocation.longitude);
                    float newDistance = locationSaya.distanceTo(newDriverLocation) / 1000; // in KM
                    if (newDistance <= LIMIT_RANGE)
                        findDriverByKey(driverGeoModel);
                }

                @Override
                public void onChildChanged(@NonNull @NotNull DataSnapshot dataSnapshot, String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull @NotNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull @NotNull DataSnapshot dataSnapshot, String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("INDEX TOAST",""+e);
            //Toast.makeText(MapsTowNowActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void addDriverMarker() {
        if (Common.driversFound.size() > 0) {
            Observable.fromIterable(Common.driversFound.keySet())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(key -> {
                        findDriverByKey(Common.driversFound.get(key));
                    }, throwable -> {

                    }, () -> {

                    });

        } else {
            Toast.makeText(this, "" + getString(R.string.drivers_not_found), Toast.LENGTH_SHORT).show();
        }

    }

    private void findDriverByKey(DriverGeoModel driverGeoModel) {
        FirebaseDatabase.getInstance()
                .getReference(Common.DRIVER_INFO_REFERENCE)
                .child(driverGeoModel.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            driverGeoModel.setDriverInfoModel(snapshot.getValue(DriverInfoModel.class));
                            //Common.driversFound.get(driverGeoModel.getKey()).setDriverInfoModel(snapshot.getValue(DriverInfoModel.class));
                            iFirebaseDriverInfoListener.onDriverInfoLoadSuccess(driverGeoModel);
                        } else {
                            iFirebaseFailedListener.onFirebaseLoadFailed("Tidak menemukan Supir berdasarkan kunci : " + driverGeoModel.getKey());
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        iFirebaseFailedListener.onFirebaseLoadFailed(error.getMessage());
                    }
                });

    }


    @Override
    public void onFirebaseLoadFailed(String message) {
        // Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDriverInfoLoadSuccess(DriverGeoModel driverGeoModel) {
        // if already marker with this key, doesn't set again

        Common.markerList.put(driverGeoModel.getKey(),
                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(driverGeoModel.getGeoLocation().latitude,
                                driverGeoModel.getGeoLocation().longitude))
                        .title(Common.buildName(driverGeoModel.getDriverInfoModel().getNamaSupir()))
                        .snippet(driverGeoModel.getDriverInfoModel().getNamaUsaha())
                        .icon(IconFactory.getInstance(MapsTowNowActivity.this).fromResource(R.drawable.derek_mobil))));

        if (!TextUtils.isEmpty(cityName)) {
            DatabaseReference driverlocation = FirebaseDatabase.getInstance()
                    .getReference(Common.DRIVERS_LOCATION_REFERENCES)
                    .child(cityName)
                    .child(driverGeoModel.getKey());
            driverlocation.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        if (Common.markerList.get(driverGeoModel.getKey()) != null)
                            Common.markerList.get(driverGeoModel.getKey()).remove();// Remove Marker
                        Common.markerList.remove(driverGeoModel.getKey());// Remove marker info from HashMap
                        Common.driverLocationSubscribe.remove(driverGeoModel.getKey()); // REMOVE driver information too
                        /*if (Common.driversFound != null && Common.driversFound.size() > 0) // Remove local information of Driver
                            Common.driversFound.remove(driverGeoModel.getKey());*/
                        driverlocation.removeEventListener(this); // Remove event Listener
                    } else {
                        if (Common.markerList.get(driverGeoModel.getKey()) != null) {
                            GeoQueryModel geoQueryModel = snapshot.getValue(GeoQueryModel.class);
                            AnimationModel animationModel = new AnimationModel(false, geoQueryModel);
                            if (Common.driverLocationSubscribe.get(driverGeoModel.getKey()) != null) {
                                Marker currentMarker = Common.markerList.get(driverGeoModel.getKey());
                                AnimationModel oldPosition = Common.driverLocationSubscribe.get(driverGeoModel.getKey());
                                Log.d("FROMPOSITION",""+oldPosition);
                                Log.d("TOPOSITION",""+animationModel);

                                /*String from = new StringBuilder()
                                        .append(oldPosition.getGeoQueryModel().getL().get(0))
                                        .append(",")
                                        .append(oldPosition.getGeoQueryModel().getL().get(1))
                                        .toString();

                                String to = new StringBuilder()
                                        .append(animationModel.getGeoQueryModel().getL().get(0))
                                        .append(",")
                                        .append(animationModel.getGeoQueryModel().getL().get(1))
                                        .toString();*/

                                /*moveMarkerAnimation(driverGeoModel.getKey(),animationModel,currentMarker,from,to);*/
                            } else {
                                //FIRST LOCATION INIT
                                Common.driverLocationSubscribe.put(driverGeoModel.getKey(), animationModel);
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Toast.makeText(MapsTowNowActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void moveMarkerAnimation(String key, AnimationModel animationModel, Marker currentMarker, String from, String to) {
        if (animationModel.isRun()) {

            compositeDisposable.add(iMapboxAPI.getDirection("driving",
                    "less_driving",
                    from, to,
                    getString(R.string.mapbox_access_token))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(returnResult -> {
                        Log.d("API_RETURN", returnResult);

                        try {
                            // PARSE JSON
                            JSONObject jsonObject = new JSONObject(returnResult);
                            JSONArray jsonArray = jsonObject.getJSONArray("routes");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject route = jsonArray.getJSONObject(i);
                                JSONObject poly = route.getJSONObject("overview_polyline");
                                String polyline = poly.getString("points");
                                /*polylineList = Common.decodePoly(polyline);*/
                                animationModel.setPolylineList(Common.decodePoly(polyline));


                            }

                            //MOVING
                    /*handler = new Handler();
                    index = -1;
                    next = 1;*/
                            animationModel.setIndex(-1);
                            animationModel.setNext(1);

                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (animationModel.getPolylineList() != null && animationModel.getPolylineList().size() > 1) {

                                        if (animationModel.getIndex() < animationModel.getPolylineList().size() - 2) {
                                            //index++;
                                            animationModel.setIndex(animationModel.getIndex() + 1);
                                            //next = index+1;
                                            animationModel.setNext(animationModel.getIndex() + 1);
                                            //start = polylineList.get(index);
                                            animationModel.setStart(animationModel.getPolylineList().get(animationModel.getIndex()));
                                            //end = polylineList.get(next);
                                            animationModel.setEnd(animationModel.getPolylineList().get(animationModel.getNext()));
                                        }
                                        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 1);
                                        valueAnimator.setDuration(3000);
                                        valueAnimator.setInterpolator(new LinearInterpolator());
                                        valueAnimator.addUpdateListener(value -> {
                                            //v = value.getAnimatedFraction();
                                            animationModel.setV(value.getAnimatedFraction());
                                            //lat = v*end.getLatitude() + (1-v) * start.getLatitude();
                                            animationModel.setLat(animationModel.getV() * animationModel.getEnd().getLatitude() +
                                                    (1 - animationModel.getV())
                                                            * animationModel.getStart().getLatitude());
                                            //lng = v*end.getLongitude() + (1-v) *start.getLongitude();
                                            animationModel.setLng(animationModel.getV() * animationModel.getEnd().getLongitude() +
                                                    (1 - animationModel.getV())
                                                            * animationModel.getStart().getLongitude());
                                            LatLng newPos = new LatLng(animationModel.getLat(), animationModel.getLng());
                                            currentMarker.setPosition(newPos);

                                        });

                                        valueAnimator.start();
                                        if (animationModel.getIndex() < animationModel.getPolylineList().size() - 2)
                                            animationModel.getHandler().postDelayed(this, 1500);
                                        else if (animationModel.getIndex() < animationModel.getPolylineList().size() - 1) {
                                            animationModel.setRun(false);
                                            Common.driverLocationSubscribe.put(key, animationModel);
                                        }
                                    }
                                }
                            };

                            animationModel.getHandler().postDelayed(runnable, 1500);

                        } catch (Exception e) {
                            // Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
            );

        }

    }


    @SuppressWarnings({"MissingPermission"})
    private void myLocationComponent(@NonNull Style loadedMapStyle) {

        //Check if permisiion are enabled and if not request
        /*if (checkLocationPermission()) {
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .elevation(6)
                    .accuracyAlpha(.10f)
                    .accuracyColor(getColor(R.color.orange_200))
                    .foregroundDrawable(R.drawable.ic_baseline_directions_car_24)
                    .build();

// Get an instance of the component
            locationComponent = mapboxMap.getLocationComponent();

            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

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
            requestLocationPermission();
        }*/

        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Create and customize the LocationComponent's options

            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .elevation(6)
                    .accuracyAlpha(.10f)
                    .accuracyColor(getColor(R.color.orange_200))
                    .foregroundDrawable(R.drawable.ic_baseline_directions_car_24)
                    .build();

// Get an instance of the component
            locationComponent = mapboxMap.getLocationComponent();

            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

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
            ActivityCompat.requestPermissions(this,locationPermissions, LOCATION_REQUEST_CODE);
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
        loadAvailableDriver();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {
                        // permisson allowed

                    } else {
                        //permission denied
                        Toast.makeText(this, "GPS dibutuhkan...", Toast.LENGTH_SHORT).show();

                    }
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
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    myLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "Permission Required", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            /*layout_confirm_pickup.setVisibility(View.GONE);
            layout_search_location.setVisibility(View.GONE);
            layout_confirm_derek.setVisibility(View.VISIBLE);*/

            chosenLatLng = new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                    ((Point) selectedCarmenFeature.geometry()).longitude());


            Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                    locationComponent.getLastKnownLocation().getLatitude());
            Point destinationPoint = Point.fromLngLat(((Point) selectedCarmenFeature.geometry()).longitude(),
                    ((Point)selectedCarmenFeature.geometry()).latitude());;

            originLocation = new LatLng(locationComponent.getLastKnownLocation().getLatitude(),
                    locationComponent.getLastKnownLocation().getLongitude());
            LatLng destination = new LatLng(chosenLatLng.getLatitude(), chosenLatLng.getLongitude());

            startActivity(new Intent(MapsTowNowActivity.this, RequestDriverActivity.class));
            EventBus.getDefault().postSticky(new SelectedPlaceEvent(originPoint,destinationPoint,"",selectedCarmenFeature.address()));
            Log.d(TAG,"Origin : "+originPoint+", Desti : "+destinationPoint);

            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
            // Then retrieve and update the source designated for showing a selected location's symbol layer icon

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

    }

    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

    }



    @Override
    protected void onResume() {
        if (isNextLaunch){
            loadAvailableDriver();
        } else
            isNextLaunch = true;
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
        if (valueAnimator != null)
            valueAnimator.end();
        // Cancel the Directions API request
        if (client != null) {
            client.cancelCall();
        }

        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }



    private boolean checkLocationPermission(){

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this,locationPermissions, LOCATION_REQUEST_CODE);
    }


}