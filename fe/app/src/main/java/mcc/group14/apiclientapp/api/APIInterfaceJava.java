package mcc.group14.apiclientapp.api;


import java.util.LinkedList;

import mcc.group14.apiclientapp.data.ProjectDetail;
import mcc.group14.apiclientapp.data.ProjectsDeleteResponse;
import mcc.group14.apiclientapp.data.ProjectsResponse;
import mcc.group14.apiclientapp.data.Task;
import mcc.group14.apiclientapp.data.TaskCreateResponse;
import mcc.group14.apiclientapp.data.TaskMembers;
import mcc.group14.apiclientapp.data.TaskResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterfaceJava {

    @GET("projects/{email}")
    Call<ProjectsResponse> doGetListProjects(@Path("email") String email);

    @GET("project/report/{project_id}")
    Call<ResponseBody> doGetProjectsReportPDF(@Path("project_id") String project_id);

    @DELETE("project/{project_id}")
    Call<ProjectsDeleteResponse> doDeleteProject(@Path("project_id") String project_id);



  
    @GET("tasks/{project_id}")
    Call<TaskResponse> doGetListTasks(@Path("project_id") String project_id);

    @POST("task/")
    Call<TaskCreateResponse> createTask(@Body Task task);


    @POST("task/member/")
    Call<ResponseBody> createTaskMember(@Body TaskMembers task);


}