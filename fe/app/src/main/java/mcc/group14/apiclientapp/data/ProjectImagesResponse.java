
package mcc.group14.apiclientapp.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class ProjectImagesResponse {

    @SerializedName("success")
    public String success;

    @SerializedName("payload")
    public ArrayList<Payload> payload = null;

    public class Payload {

        @SerializedName("filename")
        public String filename;

        @SerializedName("attachment_url")
        public String attachment_url;

        @SerializedName("creation_time")
        public String creation_time;




    }
}