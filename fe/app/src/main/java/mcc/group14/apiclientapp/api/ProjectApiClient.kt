package mcc.group14.apiclientapp.api
import io.reactivex.Observable
import mcc.group14.apiclientapp.data.ProjectDetail
import mcc.group14.apiclientapp.data.UserProject
import mcc.group14.apiclientapp.data.UserRegistration
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ProjectApiClient {
    companion object {

        @JvmStatic fun create(): ProjectApiClient {

            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(AuthInterceptor())

            val REAL_API = "https://mcc-fall-2019-g14.appspot.com/mcc/"
            val MOCK_API = "https://virtserver.swaggerhub.com/mcc-fall-group14/mcc-proj/1.0.0/"
            val requestBinApi = "https://enh6adcabkabd.x.pipedream.net/"

            val postURL = "https://end3tov89or2uks.m.pipedream.net/"
            val localURL = "http://10.0.2.2:5000/"

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .baseUrl(REAL_API)
                .build()

            return retrofit.create(ProjectApiClient::class.java)

        }
    }

    @POST("user/")
    fun createUser(@Body user: UserRegistration): Observable<UserRegistration>

    @GET("projects/{email}")
    fun getProjectsList(@Path("email") email: String):
            Call<Response<MutableList<ProjectDetail>>>
    //Observable<Response<MutableList<ProjectDetail>>>
            //Observable<Response<MutableList<UserProject>>>

    /* Get one article by it's id */
    @GET("project/{project_id}")
    fun getProjectDetail(@Path("project_id") project_id: String):
            Observable<Response<ProjectDetail>>

    @Headers("Content-Type: application/json;charset=utf-8")
    //@POST("project/")
    @POST("project/")
    fun createProject(@Body project: ProjectDetail): Observable<Response<ProjectDetail>>

    @Headers("Content-Type: application/json;charset=utf-8")
    //@POST("project/")
    @PUT("project/")
    fun modifyProject(@Body project: UserProject): Observable<Response<ProjectDetail>>
/*
    @Multipart
    @POST("/")
    fun uploadProfilePicture (@Part("id") uid: RequestBody,
                              @Part("name") name: RequestBody,
                              @Part photo: MultipartBody.Part?
    ): Call<ResponseBody>
*/
}
