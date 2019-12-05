package mcc.group14.apiclientapp.api

import com.google.firebase.auth.FirebaseAuth
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

            val mAuth = FirebaseAuth.getInstance()

            val REAL_API = "https://mcc-fall-2019-g14.appspot.com/mcc/"
            val MOCK_API = "https://virtserver.swaggerhub.com/mcc-fall-group14/mcc-proj/1.0.0/"
            val requestBinApi = "https://enh6adcabkabd.x.pipedream.net/"
            val postURL = "https://end3tov89or2uks.m.pipedream.net/"
            val localURL = "http://10.0.2.2:5000/"
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(REAL_API)
                .build()

            return retrofit.create(UsersApiClient::class.java)

        }

        // TODO: @Max
        // this should be used to integrate the authorization look at:
        // https://stackoverflow.com/questions/53504324/retrieve-sharedpreferences-in-retrofit2-interface
        /*class ServiceInterceptor(String email, String user): Interceptor {

            override fun intercept(chain: Interceptor.Chain): okhttp3.ResponseWrapper {
                var request = chain.request()
                if (request.header("Authorization") == null){

                }
            }*/
    }

    @Headers("Content-Type: application/json;charset=utf-8")
    //@POST("user/")
    @PUT("user/")
    fun editPassword(@Body userCredentials: UserCredentials):
            Observable<ResponseWrapper<UserCredentials>>

    // get users from regex
    @GET("project/user/{search_path}")
    fun searchForUsers(@Path("search_path") search_path: String):
            Observable<ResponseWrapper<MutableList<UserSearch>>>
}
