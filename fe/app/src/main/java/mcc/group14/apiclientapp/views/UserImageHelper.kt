package mcc.group14.apiclientapp.views

import mcc.group14.apiclientapp.utils.FileHelper
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call

class UserImageHelper : FileHelper() {
    companion object{
        val instance = UserImageHelper()
    }

    override fun upload(
        userEmail: RequestBody,
        userAuth: RequestBody,
        fileMP: MultipartBody.Part?
    ): Call<ResponseBody> {
        return this.apiClient.uploadUserPicture(userEmail, userAuth, fileMP)
    }

}