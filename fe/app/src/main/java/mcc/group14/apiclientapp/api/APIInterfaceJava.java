package mcc.group14.apiclientapp.api;


import java.util.LinkedList;

import mcc.group14.apiclientapp.data.ProjectDetail;
import mcc.group14.apiclientapp.data.ProjectsResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterfaceJava {

    @GET("projects/{email}")
    Call<ProjectsResponse> doGetListProjects(@Path("email") String email);

  /*  @POST("/api/users")
    Call<User> createUser(@Body User user);

    @GET("/api/users?")
    Call<UserList> doGetUserList(@Query("page") String page);

    @FormUrlEncoded
    @POST("/api/users?")
    Call<UserList> doCreateUserWithField(@Field("name") String name, @Field("job") String job);*/
}