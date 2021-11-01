package com.example.mobilderekuser.Remote;

import com.example.mobilderekuser.Model.FCMResponse;
import com.example.mobilderekuser.Model.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAGdlnSps:APA91bEtZKFBffpimHpxnJYg61k_D_HAKNUQHGhaC_6xGuFFdbax3MYYjXlI3m6UM81i0XBG5knUYRnLZ_8q82dGnqq-BX8WHrPn7OOYJSxnDn5ixRS-2h_nPwqIfPk5l2CJF2vfljQc"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);
}
