package mcc.group14.apiclientapp

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface UsersApiClient{
    companion object {

        fun create(): UsersApiClient {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://my-json-server.typicode.com/MMirelli/mock-server/")
                .build()

            return retrofit.create(UsersApiClient::class.java)

        }
    }

    /* Get one article by it's id */
        @GET("user/{userId}")
        fun getUser(@Path("userId") userId: Int): Observable<User>


}
