package mcc.group14.apiclientapp.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain) : Response {
        val request = chain.request().newBuilder()
            .header("Authorization", "12345")
            .build()
        return chain.proceed(request)
    }
}