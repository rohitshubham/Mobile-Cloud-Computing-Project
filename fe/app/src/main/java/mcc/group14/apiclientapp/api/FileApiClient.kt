package mcc.group14.apiclientapp.api

import okhttp3.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import okhttp3.OkHttpClient

interface FileApiClient {

        companion object {

            fun create(): FileApiClient {

                val ourAPI = "https://mcc-fall-2019-g14.appspot.com/mcc/"
                val requestBinAPI = "https://enh6adcabkabd.x.pipedream.net/"

                val postURL = "https://end3tov89or2uks.m.pipedream.net/"
                val localURL = "http://10.0.2.2:5000/"

                val httpClient = OkHttpClient.Builder()
                httpClient.addInterceptor(AuthInterceptor())

                val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .baseUrl(requestBinAPI)
                    .build()

                return retrofit.create(FileApiClient::class.java)

            }
        }

    @Multipart
    @POST("/")
    fun uploadProjectPicture (@Part("user_email") user_email: RequestBody,
                       @Part("user_auth") user_auth: RequestBody,
                       @Part image: MultipartBody.Part?): Call<ResponseBody>

}