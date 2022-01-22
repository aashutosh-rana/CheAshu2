package com.bcebhagalpur.cheashu.dashboard.chat.service;

import com.bcebhagalpur.cheashu.dashboard.chat.notifications.MyResponse;
import com.bcebhagalpur.cheashu.dashboard.chat.notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

        @Headers({
                "Content-Type:application/json",
                "Authorization:key=AAAAxsrbc_o:APA91bGATviOM39huuyK7amj-Llio5KnnZ1M-0ynKXly_1DstDHsF_mwcT_QhMOUy3Gw0V7GDEJA4HcWbJgwl7-8UEHOHc6h_R9GBZzbUfNkLPNQfveXztxvEoSuIQEpwT-kOVlM2tyM"
        })

        @POST("fcm/send")
        Call<MyResponse> sendNotification(@Body Sender body);
    }

