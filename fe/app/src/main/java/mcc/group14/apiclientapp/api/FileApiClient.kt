package mcc.group14.apiclientapp.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

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

    // TODO: ++ @Max change endpoints
    @Multipart
    //@POST("project/")
    @PUT("project/")
    fun uploadBadge (@Part("bodyParams") user_email: RequestBody?,
                       @Part image: MultipartBody.Part?): Call<ResponseBody>

    @Multipart
    @PUT("user/")
    //@POST("/user")
    fun uploadUserPictureAndParams (@Part("bodyParams") json: RequestBody?,
                           @Part image: MultipartBody.Part?): Call<ResponseBody>

}