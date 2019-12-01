package mcc.group14.apiclientapp.api
import io.reactivex.Observable
import mcc.group14.apiclientapp.data.ProjectDetail
import mcc.group14.apiclientapp.data.UserProject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ProjectApiClient {
    companion object {

        fun create(): ProjectApiClient {

            val getURL = "https://mcc-fall-2019-g14.appspot.com/mcc/"
            val postRQ = "https://enh6adcabkabd.x.pipedream.net/"
            val postURL = "https://end3tov89or2uks.m.pipedream.net/"
            val localURL = "http://10.0.2.2:5000/"
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getURL)
                .build()

            return retrofit.create(ProjectApiClient::class.java)

        }
    }

    @GET("projects/{email}")
    fun getProjectsList(@Path("email") email: String):
            Observable<Response<MutableList<UserProject>>>

    /* Get one article by it's id */
    @GET("project/{project_id}")
    fun getProjectDetail(@Path("project_id") project_id: String):
            Observable<Response<ProjectDetail>>

    @Headers("Content-Type: application/json;charset=utf-8")
    //@POST("project/")
    @POST("?pipedream_response=3")
    fun createProject(@Body project: ProjectDetail): Observable<ProjectDetail>

    @Headers("Content-Type: application/json;charset=utf-8")
    //@POST("project/")
    @PUT("?pipedream_response=3")
    fun modifyProject(@Body project: ProjectDetail): Observable<ProjectDetail>


    @Multipart
    @POST("/")
    fun uploadProfilePicture (@Part("id") userId: RequestBody,
                              @Part("name") name: RequestBody,
                              @Part photo: MultipartBody.Part?
    ): Call<ResponseBody>
}
