package mcc.group14.apiclientapp.api

import io.reactivex.Observable
import mcc.group14.apiclientapp.data.UserCredentials
import mcc.group14.apiclientapp.data.UserSearch
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface UsersApiClient{
    companion object {

        fun create(): UsersApiClient {

            val ourApi = "https://mcc-fall-2019-g14.appspot.com/mcc/"
            val fakeApi = "https://virtserver.swaggerhub.com/mcc-fall-group14/mcc-proj/1.0.0/"
            val requestBinApi = "https://enh6adcabkabd.x.pipedream.net/"
            val postURL = "https://end3tov89or2uks.m.pipedream.net/"
            val localURL = "http://10.0.2.2:5000/"
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(fakeApi)
                .build()

            return retrofit.create(UsersApiClient::class.java)

        }
    }

/* Get one article by it's id *//*

    @GET("user/{uid}")
    fun getUser(@Path("uid") userId: Int): Observable<User>

    @Headers("Content-Type: application/json;charset=utf-8")
    //@POST("user/")
    @POST("?pipedream_response=3")
    fun addUser(@Body user: User): Observable<User>
*/

    @Headers("Content-Type: application/json;charset=utf-8")
    //@POST("user/")
    @PUT("user/")
    fun editPassword(@Body userCredentials: UserCredentials):
            Observable<Response<UserCredentials>>

    // get users from regex
    @GET("project/user/{search_path}")
    fun searchForUsers(@Path("search_path") search_path: String):
            Observable<Response<MutableList<UserSearch>>>


    /*@Multipart
    @POST("/")
    fun uploadPicture (@Part("uid") userId: RequestBody,
                       @Part("name") name: RequestBody,
                       @Part photo: MultipartBody.Part?
    ):Call<ResponseBody>*/
}
