package com.example.mobilderekuser.Ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilderekuser.Common.Common;
import com.example.mobilderekuser.Model.DriverGeoModel;
import com.example.mobilderekuser.Model.EventBus.SelectedPlaceEvent;
import com.example.mobilderekuser.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
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
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.tapadoo.alerter.Alerter;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class LokasiDerekSekarangActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {


    //View SEARCH LOCATION LAYOUT
    @BindView(R.id.search_location_layout)
    CardView layout_search_location;
    @BindView(R.id.welcome_txt)
    TextView txt_hai;
    @BindView(R.id.edt_tujuanRider)
    TextInputEditText edt_tujuan;

    private MapView mapView;
    private MapboxMap mapboxMap;

    LatLng chosenLatLng, originLatLng;
    private LatLng originLocation;





    private static final int REQUEST_CODE_AUTOCOMPLETE = 7171;
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1500L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;

    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";

    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationComponent locationComponent;
    private LocationLayerPlugin locationLayerPlugin;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersInfoRef, onlineRef, driversLocationRef, currentUserRef;
    private DriverGeoModel driverGeoModel;

    public static double lat, lng;
    private double lokasiLat, lokasiLon;

    public static Marker marker;
    private double distance = 1.0;
    private LatLng lokasiSaya;
    private boolean firstTime = true;
    private Boolean driverFound = false;
    private String driverFoundID;

    private String cityName;

    private LokasiDerekSekarangActivityCallback callback =
            new LokasiDerekSekarangActivityCallback(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this,getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_lokasi_derek_sekarang);

        initViews();
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    private void initViews() {
        // LAYOUT SEARCH
        txt_hai = findViewById(R.id.welcome_txt);
        edt_tujuan = findViewById(R.id.edt_tujuanRider);
        layout_search_location = findViewById(R.id.search_location_layout);

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
                        .build(LokasiDerekSekarangActivity.this);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);


            }
        });
    }

    @Override
    public void onMapReady(@NonNull @NotNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                enableLocationComponent(style);
                getClosestDriver();
                initSearchEdt();

            }
        });
    }

    private void getClosestDriver() {
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addressList;
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        /*Log.d("Key : ", "myLocation:"+myLocation.getLatitude(),);*/
        try {
            addressList = geocoder.getFromLocation(lokasiLat, lokasiLon,1);
            cityName = addressList.get(0).getSubAdminArea();
            DatabaseReference driverLocation = database.getReference().child(Common.DRIVERS_LOCATION_REFERENCES)
                    .child(cityName);
            GeoFire geoFire = new GeoFire(driverLocation);
            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lokasiLat, lokasiLon), distance);
            geoQuery.removeAllListeners();

            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    if (!driverFound) {
                        driverFound = true;
                        driverFoundID = key;
                        //Common.driversFound.add(new DriverGeoModel(key, location));
                        Log.d("Key : ", driverFoundID);

                        Alerter.create(LokasiDerekSekarangActivity.this)
                                .setTitle("Supir Tersedia ")
                                .setText(" Sapa supir derek di dekatmu ")
                                .enableProgress(false)
                                .setBackgroundColorInt(getResources().getColor(R.color.orange_200))
                                .enableSwipeToDismiss()
                                .setIcon(R.drawable.truck_icon)
                                .setIconColorFilter(0)
                                .show();
                        getDriverLocation();
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
                    if (!driverFound) {
                        distance++;
                        getClosestDriver();
                        Log.d("Radius :", String.valueOf(distance));
                    }
                }

                @Override
                public void onGeoQueryError(DatabaseError error) {

                }
            });

        } catch (Exception e){

        }

    }

    private void getDriverLocation() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Common.DRIVERS_LOCATION_REFERENCES)
                .child(cityName);
        GeoFire geoFire = new GeoFire(ref);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lokasiLat, lokasiLon), distance);
        geoFire.getLocation(driverFoundID, new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location != null) {
                    lat = location.latitude;
                    lng = location.longitude;
                    LatLng driverLatLng = new LatLng(lat, lng);
                    if (marker != null) {
                        mapboxMap.removeMarker(marker);
                    }
                    marker = mapboxMap.addMarker(new MarkerOptions()
                            .position(driverLatLng)
                            .title("Your Driver").setIcon(IconFactory.getInstance(LokasiDerekSekarangActivity.this)
                                    .fromResource(R.drawable.derek_mobil)));

                    /*marker = mapboxMap.addMarker(markerOptions);*/

                } else {
                    mapboxMap.removeMarker(marker);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {
                double prelat = lat;
                double prelon = lng;

                LatLng pre_latlng = new LatLng(prelat,prelon);
                LatLng driverPostition = new LatLng(location.latitude, location.longitude);

                ValueAnimator markerAnimator = ValueAnimator.ofObject(new TypeEvaluator<LatLng>() {
                    @Override
                    public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                        return new LatLng(startValue.getLatitude() + (endValue.getLatitude() - startValue.getLatitude()) * fraction,
                                startValue.getLongitude() + (endValue.getLongitude() - startValue.getLongitude()) * fraction);
                    }

                }, new LatLng[]{pre_latlng, driverPostition});
                markerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if (marker!=null){
                            marker.setPosition((LatLng) animation.getAnimatedValue());

                        }
                    }
                });

                markerAnimator.setDuration(7500);
                markerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                markerAnimator.start();
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

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

            startActivity(new Intent(LokasiDerekSekarangActivity.this, RequestDriverActivity.class));
            EventBus.getDefault().postSticky(new SelectedPlaceEvent(originPoint,destinationPoint,"",selectedCarmenFeature.address()));
            Log.d("TAG","Origin : "+originPoint+", Desti : "+destinationPoint);

            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
            // Then retrieve and update the source designated for showing a selected location's symbol layer icon



        }

    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Create and customize the LocationComponent's options
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .elevation(5)
                    .accuracyAlpha(.6f)
                    .accuracyColor(getColor(R.color.orange_200))
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

            /*assert locationComponent.getLastKnownLocation() != null;*/
/*
            lokasiSaya = new LatLng(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude());
*/


        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressLint("MissingPermission")
    private void initLocationEngine(){
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }


    private class LokasiDerekSekarangActivityCallback implements LocationEngineCallback<LocationEngineResult>{

        private final WeakReference<LokasiDerekSekarangActivity> myLocationToMarkerWeakReference;

        private LokasiDerekSekarangActivityCallback(LokasiDerekSekarangActivity myLocationToMarkerWeakReference) {
            this.myLocationToMarkerWeakReference = new WeakReference<>(myLocationToMarkerWeakReference);
        }


        @Override
        public void onSuccess(LocationEngineResult result) {
            LokasiDerekSekarangActivity locationMarkerActivity = myLocationToMarkerWeakReference.get();

            if (locationMarkerActivity != null) {
                Location location = result.getLastLocation();

                Location previousLocation, currentLocation = null;

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                lokasiLat = latitude;
                lokasiLon = longitude;

                if (location == null) {

                    return;
                }

                /*Toast.makeText(LokasiDerekSekarangActivity.this, "Lokasi anda :"+". Latitude: "+
                                result.getLastLocation().getLatitude()+". Longitude: "+result.getLastLocation().getLongitude()+". Accuracy: "
                                +result.getLastLocation().getAccuracy(),
                        Toast.LENGTH_SHORT).show();*/

                getClosestDriver();

                if (locationMarkerActivity.mapboxMap != null && result.getLastLocation() != null) {
                    locationMarkerActivity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());

                }
            }


        }

        @Override
        public void onFailure(@NonNull @NotNull Exception exception) {
            LokasiDerekSekarangActivity locationToMarkerActivity = myLocationToMarkerWeakReference.get();
            if (locationToMarkerActivity != null){
                Toast.makeText(locationToMarkerActivity, exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Needed Permissions", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "Permission Required", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    protected void onStart() {
        super.onStart();
        mapView.onStart();
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
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}