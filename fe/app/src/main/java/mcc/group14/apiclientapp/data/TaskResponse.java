package mcc.group14.apiclientapp.data;
import com.google.gson.annotations.SerializedName;

import java.security.PublicKey;
import java.util.ArrayList;


public class TaskResponse {

    @SerializedName("success")
    public String success;

    @SerializedName("payload")
    public ArrayList<Payload> payload = null;

    public class Payload {

        @SerializedName("creation_time")
        public String creation_time = "NA";

        @SerializedName("email_id")
        public String email_id ;

        @SerializedName("deadline")
        public String deadline;

        @SerializedName("description")
        public String description;

        @SerializedName("name")
        public String name;

        @SerializedName("status")
        public String status;

        @SerializedName("project_id")
        public String project_id;

        @SerializedName("task_id")
        public String task_id;

        @SerializedName("last_modified")
        public String last_modified = "NA";

    }
}
