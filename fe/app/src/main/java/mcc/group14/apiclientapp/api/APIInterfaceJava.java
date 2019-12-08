package mcc.group14.apiclientapp.api;


import java.util.LinkedList;
import java.util.Map;

import mcc.group14.apiclientapp.data.MemberAutocompleteResponse;
import mcc.group14.apiclientapp.data.Project;
import mcc.group14.apiclientapp.data.ProjectAttachmentsResponse;
import mcc.group14.apiclientapp.data.ProjectCreateResponse;
import mcc.group14.apiclientapp.data.ProjectDetail;
import mcc.group14.apiclientapp.data.ProjectImagesResponse;
import mcc.group14.apiclientapp.data.ProjectsDeleteResponse;
import mcc.group14.apiclientapp.data.ProjectsResponse;
import mcc.group14.apiclientapp.data.RegistrationToken;
import mcc.group14.apiclientapp.data.Task;
import mcc.group14.apiclientapp.data.TaskComplete;
import mcc.group14.apiclientapp.data.TaskCreateResponse;
import mcc.group14.apiclientapp.data.TaskMembers;
import mcc.group14.apiclientapp.data.TaskResponse;
import mcc.group14.apiclientapp.data.UploadAttachmentResponse;
import mcc.group14.apiclientapp.data.UploadImageResponse;
import mcc.group14.apiclientapp.data.UserUpdateResponse;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APIInterfaceJava {

    @GET("projects/{email}")
    Call<ProjectsResponse> doGetListProjects(@Path("email") String email);

    @GET("project/report/{project_id}")
    Call<ResponseBody> doGetProjectsReportPDF(@Path("project_id") String project_id);

    @DELETE("project/{project_id}")
    Call<ProjectsDeleteResponse> doDeleteProject(@Path("project_id") String project_id);


    @GET("project/attachment/{project_id}")
    Call<ProjectAttachmentsResponse> getAllAttachments(@Path("project_id") String project_id);

    @GET("project/image/{project_id}")
    Call<ProjectImagesResponse> getAllImages(@Path("project_id") String project_id);


    @GET("project/user/{search_path}")
    Call<MemberAutocompleteResponse> getMemberSuggestions(@Path("search_path") String search_path);


    @GET
    Call<ResponseBody> downloadAttachMent(@Url String fileUrl);

    @Multipart
    @PUT("user/")
    Call<UserUpdateResponse> updateUserInfo(@PartMap Map<String,RequestBody> params);

    @Multipart
    @POST("project/")
    Call<ProjectCreateResponse> createProjectWithBadge(@PartMap Map<String,RequestBody> params);

    @Multipart
    @PUT("project/")
    Call<ProjectCreateResponse> updateProjectWithBadge(@PartMap Map<String,RequestBody> params);

    @Multipart
    @POST("project/attachment/")
    Call<UploadAttachmentResponse> uploadAttachment(@PartMap Map<String,RequestBody> params);

    @Multipart
    @POST("project/image/")
    Call<UploadImageResponse> uploadImage(@PartMap Map<String,RequestBody> params);

  

    @GET("tasks/{project_id}/{email_id}")
    Call<TaskResponse> doGetListTasks(@Path("project_id") String project_id, @Path("email_id") String email_id);

    @POST("task/")
    Call<TaskCreateResponse> createTask(@Body Task task);


    @POST("task/member/")
    Call<ResponseBody> createTaskMember(@Body TaskMembers task);

    @POST("task/complete/")
    Call<ResponseBody> completeTask(@Body TaskComplete task);

    @POST("token/")
    Call<ResponseBody> sendResgistrationTokenToServer(@Body RegistrationToken token);


}