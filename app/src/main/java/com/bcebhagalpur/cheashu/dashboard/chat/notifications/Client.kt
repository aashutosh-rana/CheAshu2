package com.bcebhagalpur.cheashu.dashboard.chat.notifications

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Client {
    object Client
    {
        private var retrofit: Retrofit?=null
        fun getClint(url:String?):Retrofit?
        {
            if (retrofit==null){
                retrofit=Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build()
            }
            return  retrofit

        }
    }
}
