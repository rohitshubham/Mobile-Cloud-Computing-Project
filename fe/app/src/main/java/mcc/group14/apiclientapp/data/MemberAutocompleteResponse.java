package mcc.group14.apiclientapp.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MemberAutocompleteResponse {
    @SerializedName("success")
    public String success;

    @SerializedName("payload")
    public ArrayList<MemberAutocompleteResponse.Payload> payload = null;

    public class Payload {

        @SerializedName("display_name")
        public String display_name;

        @SerializedName("email_id")
        public String email_id;


    }
}
