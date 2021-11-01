package com.example.mobilderekuser.Services;

import androidx.annotation.NonNull;

import com.example.mobilderekuser.Common.Common;
import com.example.mobilderekuser.Model.EventBus.DeclineRequestAndRemoveTripFromDriver;
import com.example.mobilderekuser.Model.EventBus.DeclineRequestFromDriver;
import com.example.mobilderekuser.Model.EventBus.DriverAcceptTripEvent;
import com.example.mobilderekuser.Model.EventBus.DriverCompleteTripEvent;
import com.example.mobilderekuser.Model.EventBus.DriverRequestRecevived;
import com.example.mobilderekuser.Utils.UserUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            UserUtils.updateToken(this, s);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> dataRecv = remoteMessage.getData();
        if (dataRecv != null) {

            if (dataRecv.get(Common.NOTI_TITLE) != null) {

                if (dataRecv.get(Common.NOTI_TITLE).equals(Common.REQUEST_DRIVER_DECLINE)) {

                    EventBus.getDefault().postSticky(new DeclineRequestFromDriver());

                } else if (dataRecv.get(Common.NOTI_TITLE).equals(Common.REQUEST_DRIVER_DECLINE_AND_REMOVE_TRIP)) {

                    EventBus.getDefault().postSticky(new DeclineRequestAndRemoveTripFromDriver());

                } else if (dataRecv.get(Common.NOTI_TITLE).equals(Common.REQUEST_DRIVER_ACCEPT)) {

                    String tripKey = dataRecv.get(Common.TRIP_KEY);
                    EventBus.getDefault().postSticky(new DriverAcceptTripEvent(tripKey));

                } else if (dataRecv.get(Common.NOTI_TITLE).equals(Common.REQUEST_COMPLETE_TRIP)) {

                    String tripKey = dataRecv.get(Common.TRIP_KEY);
                    EventBus.getDefault().postSticky(new DriverCompleteTripEvent(tripKey));

                } else if (dataRecv.get(Common.NOTI_TITLE).equals(Common.REQUEST_DRIVER_TITLE)) {
                    DriverRequestRecevived driverRequestRecevived = new DriverRequestRecevived();
                    driverRequestRecevived.setKey(dataRecv.get(Common.RIDER_KEY));
                    driverRequestRecevived.setPickupLocation(dataRecv.get(Common.RIDER_PICKUP_LOCATION));
                    driverRequestRecevived.setPickupLocationString(dataRecv.get(Common.RIDER_PICKUP_LOCATION_STRING));
                    driverRequestRecevived.setDestinationLocation(dataRecv.get(Common.RIDER_DESTIONATION));
                    driverRequestRecevived.setDestinationLocationString(dataRecv.get(Common.RIDER_DESTINATION_STRING));
                    driverRequestRecevived.setDistanceValue(dataRecv.get(Common.RIDER_DISTANCE_VALUE));
                    driverRequestRecevived.setDurationValue(dataRecv.get(Common.RIDER_TIME_VALUE));
                    driverRequestRecevived.setJarakValue(Double.valueOf(dataRecv.get(Common.RIDER_JARAK_VALUE)));


                /*driverRequestRecevived.setKey(dataRecv.get(Common.RIDER_KEY));
                driverRequestRecevived.setPickupLocation(dataRecv.get(Common.RIDER_PICKUP_LOCATION));
                driverRequestRecevived.setPickupLocationString(dataRecv.get(Common.RIDER_PICKUP_LOCATION_STRING));
                driverRequestRecevived.setDestinationLocation(dataRecv.get(Common.RIDER_DESTIONATION));
                driverRequestRecevived.setDestinationLocationString(dataRecv.get(Common.RIDER_DESTINATION_STRING));*/

                    EventBus.getDefault().postSticky(driverRequestRecevived);
                } else
                    Common.showNotification(this, new Random().nextInt(),
                            dataRecv.get(Common.NOTI_TITLE),
                            dataRecv.get(Common.NOTI_CONTENT),
                            null);
            }
        }
    }
}
