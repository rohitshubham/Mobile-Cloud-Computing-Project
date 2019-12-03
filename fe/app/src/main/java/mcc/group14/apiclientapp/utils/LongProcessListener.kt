package mcc.group14.apiclientapp.utils

import okhttp3.ResponseBody
import retrofit2.Response

// use this class whenever you need a custom listener on long activities
// down(/up)load of files and so on
class LongProcessListener(val activity: LongRunningActivity) {

    /*fun onLongProcessFailure(t: Throwable){

    }*/

    fun onLongProcessSuccess(result: Response<ResponseBody>){
        activity.onLongProcessSuccess(result)
    }

}