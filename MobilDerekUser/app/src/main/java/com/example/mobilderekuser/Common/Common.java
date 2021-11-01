package com.example.mobilderekuser.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.mobilderekuser.Model.AnimationModel;
import com.example.mobilderekuser.Model.DriverGeoModel;
import com.example.mobilderekuser.Model.DriverInfoModel;
import com.example.mobilderekuser.Model.UsahaModel;
import com.example.mobilderekuser.Model.UserModel;
import com.example.mobilderekuser.R;
import com.example.mobilderekuser.Ui.RequestDriverActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.maps.android.ui.IconGenerator;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class Common {
    public static final String RIDER_INFO_REFERENCES = "Riders";
    public static final String RIDER_LOCATION_REFERENCES = "RidersLocation";
    public static final String DRIVERS_LOCATION_REFERENCES = "DriverLocation";
    public static final String DRIVER_INFO_REFERENCE = "UsersInfo";
    public static final String USERS_INFO_REFERENCE = "UsersInfo";
    public static final String REQUEST_DRIVER_TITLE = "RequestDriver";

    public static final String RIDER_KEY = "RiderKey";
    public static final String REQUEST_DRIVER_DECLINE = "Decline";
    public static final String RIDER_PICKUP_LOCATION = "PickupLocation";
    public static final String RIDER_PICKUP_LOCATION_STRING = "PickupLocationString";
    public static final String RIDER_DESTINATION_STRING = "DestinationLocationString";
    public static final String RIDER_DESTIONATION = "DestinationLocation";
    public static final String REQUEST_DRIVER_DECLINE_AND_REMOVE_TRIP = "DeclineAndRemoveTrip";
    public static final String REQUEST_COMPLETE_TRIP = "DriverCompleteTrip";

    public static final String REQUEST_DRIVER_ACCEPT = "Accept";
    public static final String TRIP_KEY = "TripKey";
    public static final String TRIP = "Trips"; // Same name in Firebase
    public static final String RATING = "Ratings"; // Same name in Firebase

    public static final String RIDER_DISTANCE_VALUE = "DistanceValue";
    public static final String RIDER_TIME_VALUE = "TimeValue";
    public static final String RIDER_JARAK_VALUE = "JarakValue";
    public static final String PEMILIK_INFO_REFERENCES = "Pemilik";

    public static final String DRIVER_KEY = "DriverKey";
    public static final String Trip = "Trips";
    public static final String TRIP_DESTINATION_LOCATION_REF = "TripDestinationLocation";
    public static final String TRIP_PICKUP_REF = "TripPickupLocation";
    public static final double MIN_RANGE_PICKUP_IN_KM = 0.05;
    public static final int WAIT_TIME_IN_MIN = 1;

    public static UserModel currentUser;
    public static UsahaModel currentUserUsaha;
    public static DriverInfoModel currentDriver;

    public static final String TOKEN_REFERENCE = "Token";
    public static final String NOTI_TITLE = "title";
    public static final String NOTI_CONTENT = "body";
    public static HashMap<String, Marker> markerList = new HashMap<String, Marker>();
    public static HashMap<String, MarkerView> markerViewList = new HashMap<String, MarkerView>();
    public static Map<String,DriverGeoModel> driversFound = new HashMap<>();
    public static HashMap<String, AnimationModel> driverLocationSubscribe = new HashMap<String, AnimationModel>();

    public static String buildWelcomeMessage(){
        if (Common.currentUser != null){
            return new StringBuilder("")
                    .append(Common.currentUser.getNamaUser()).toString();
        } else {
            return "";
        }
    }

    public static String buildEmailMessage(){
        if (Common.currentUser != null){
            return new StringBuilder("")
                    .append(Common.currentUser.getEmailUser()).toString();
        } else {
            return "";
        }
    }

    public static void setRiderPhoto(CircleImageView iv_profileUser){
        if (Common.currentUser != null) {
            try{
                Picasso.get().load(Common.currentUser.getProfileImage()).placeholder(R.drawable.ic_baseline_account_circle_24)
                        .into(iv_profileUser);
            }catch (Exception e){
                iv_profileUser.setImageResource(R.drawable.ic_baseline_account_circle_24);
            }
        }
    }
    public static void setRiderInfo(TextInputEditText et_namaUser, TextInputEditText et_noHpUser, TextInputEditText et_emailUser) {
        if (Common.currentUser != null){
            et_namaUser.setText(Common.currentUser.getNamaUser());
            et_noHpUser.setText(Common.currentUser.getNoHpUser());
            et_emailUser.setText(Common.currentUser.getEmailUser());
        }
    }

    public static void setWelcomeMessage(TextView txt_hai){
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 1 && hour <= 10) {
            txt_hai.setText(new StringBuilder("Selamat Pagi"));
        } else if (hour >= 11 && hour <= 15) {
            txt_hai.setText(new StringBuilder("Selamat Siang"));
        } else if (hour >= 16 && hour <= 18) {
            txt_hai.setText(new StringBuilder("Selamat Sore"));
        } else {
            txt_hai.setText(new StringBuilder("Selamat Malam"));
        }

    }

    public static void showNotification(Context context, int id, String title, String body, Intent intent) {
        PendingIntent pendingIntent = null;
        if (intent != null)
            pendingIntent = PendingIntent.getActivity(context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID = "aplikasi_mobil_derek";
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Mobil Derek", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Mobil Derek");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                .setSound(alarmSound)
                .setVibrate(new long[]{0,1000,500,1000})
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.logo_tarikin)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.logo_tarikin));
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }
        Notification notification = builder.build();
        notificationManager.notify(id,notification);

    }

    public static String buildName(String namaSeller) {
        return new StringBuilder(namaSeller).toString();
    }

    public static List<LatLng> decodePoly(String encoded) {
        List poly = new ArrayList();
        int index=0,len=encoded.length();
        int lat=0,lng=0;
        while(index < len)
        {
            int b,shift=0,result=0;
            do{
                b=encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift+=5;

            }while(b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1):(result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do{
                b = encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift +=5;
            }while(b >= 0x20);
            int dlng = ((result & 1)!=0 ? ~(result >> 1): (result >> 1));
            lng +=dlng;

            LatLng p = new LatLng((((double)lat / 1E5)),
                    (((double)lng/1E5)));
            poly.add(p);
        }
        return poly;
    }


    public static Bitmap createIconWithDuration(Context context, String wkt) {
        View view = LayoutInflater.from(context).inflate(R.layout.pickup_info_with_duration_windows,null);
        //TextView txt_time = view.findViewById(R.id.txt_duration);
        //txt_time.setText(Common.getNumberFromText(wkt));

        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setContentView(view);
        iconGenerator.setBackground(new ColorDrawable(Color.TRANSPARENT));
        return iconGenerator.makeIcon();

    }


    public static String createUniqueTripIdNumber(long timeOffset) {
        Random random = new Random();
        Long current = System.currentTimeMillis()+timeOffset;
        Long unique = current + random.nextLong();
        if (unique < 0) unique*=(-1);
        return String.valueOf(unique);
    }


}
