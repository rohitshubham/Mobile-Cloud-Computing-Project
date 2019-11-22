package mcc.group14.apiclientapp

import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.POST
import retrofit2.http.Multipart

interface UsersApiClient{
    companion object {

        fun create(): UsersApiClient {

            val getURL = "https://my-json-server.typicode.com/MMirelli/mock-server/"
            val postRQ = "https://enh6adcabkabd.x.pipedream.net/"
            val postURL = "https://end3tov89or2uks.m.pipedream.net/"
            val localURL = "http://10.0.2.2:5000/"
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getURL)
                .build()

            return retrofit.create(UsersApiClient::class.java)

        }
    }

    /* Get one article by it's id */
    @GET("user/{userId}")
    fun getUser(@Path("userId") userId: Int): Observable<User>

    @Headers("Content-Type: application/json;charset=utf-8")
    //@POST("user/")
    @POST("?pipedream_response=3")
    fun addUser(@Body user: User): Observable<User>

    @Multipart
    @POST("/")
    fun uploadProfilePicture (@Part("userId") userId: RequestBody,
                              @Part("name") name: RequestBody,
                              @Part photo: MultipartBody.Part?
    ):Call<ResponseBody>



}
