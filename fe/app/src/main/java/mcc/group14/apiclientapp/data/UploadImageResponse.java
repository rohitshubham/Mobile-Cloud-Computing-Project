package mcc.group14.apiclientapp.data;

import com.google.gson.annotations.SerializedName;


public class UploadImageResponse {
    @SerializedName("success")
    public String success;

    @SerializedName("url")
    public String url;

}
