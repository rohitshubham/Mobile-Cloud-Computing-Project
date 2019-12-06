package mcc.group14.apiclientapp.data;

import com.google.gson.annotations.SerializedName;


public  class TaskCreateResponse{

    @SerializedName("success")
    public String success;

    @SerializedName("payload")
    public String payload = null;


}
