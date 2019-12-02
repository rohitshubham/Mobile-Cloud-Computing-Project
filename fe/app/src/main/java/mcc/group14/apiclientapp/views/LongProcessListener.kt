package mcc.group14.apiclientapp.views

import okhttp3.ResponseBody
import retrofit2.Response

// implement this class we y
class LongProcessListener(val activity: LongRunningActivity) {

    /*fun onLongProcessFailure(t: Throwable){

    }*/

    fun onLongProcessSuccess(result: Response<ResponseBody>){
        activity.onLongProcessSuccess(result)
    }

}