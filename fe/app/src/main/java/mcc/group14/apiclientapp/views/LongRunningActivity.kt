package mcc.group14.apiclientapp.views

import okhttp3.ResponseBody
import retrofit2.Response

// this interface should be implemented by all the activities which require time to process specific requests
// (e.g.: uploading images or files, look at optionalProjectSettingsActivity)
interface LongRunningActivity {

    fun onLongProcessSuccess(result: Response<ResponseBody>)

    fun onLongProcessFailure(t: Throwable)
}