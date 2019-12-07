package mcc.group14.apiclientapp.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProjectCreateResponse {
    @SerializedName("success")
    public String success;

    @SerializedName("payload")
    public ArrayList<ProjectsResponse.Payload> payload = null;

    public class Payload {

        @SerializedName("project_id")
        public String project_id;

    }
}
