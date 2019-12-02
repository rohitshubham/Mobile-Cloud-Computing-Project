package mcc.group14.apiclientapp.utils

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call

class ProjectImageHelper: FileHelper() {

    companion object{
        val instance = ProjectImageHelper()
    }

    override fun upload(
        userEmail: RequestBody,
        userAuth: RequestBody,
        fileMP: MultipartBody.Part?
    ): Call<ResponseBody> {
        return this.apiClient.uploadProjectPicture(userEmail, userAuth, fileMP)
    }

}