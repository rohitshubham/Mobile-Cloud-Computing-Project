package mcc.group14.apiclientapp.data;
import com.google.gson.annotations.SerializedName;


import java.util.ArrayList;


public class ProjectsResponse {

    @SerializedName("success")
    public String success;

    @SerializedName("payload")
    public ArrayList<Payload> payload = null;

    public class Payload {

        @SerializedName("creation_time")
        public String creation_time;

        @SerializedName("project_type")
        public String project_type;

        @SerializedName("deadline")
        public String deadline;

        @SerializedName("description")
        public String description;

        @SerializedName("name")
        public String name;

        @SerializedName("team_members")
        public String team_members;

        @SerializedName("requester_email")
        public String requester_email;

        @SerializedName("badge")
        public String badge;

        @SerializedName("keywords")
        public String keywords;

        @SerializedName("project_id")
        public String project_id;

        @SerializedName("is_project_administrator")
        public String is_project_administrator;

        @SerializedName("last_modified")
        public String last_modified = "NA";

    }
}