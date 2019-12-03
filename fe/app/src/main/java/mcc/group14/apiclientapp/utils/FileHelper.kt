package mcc.group14.apiclientapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import mcc.group14.apiclientapp.api.FileApiClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

// different kinda of upload per different file, NB: its implementations must be singleton
abstract class FileHelper {

    val TAG = "FileHelper"
    var apiClient = FileApiClient.create()

    fun storeImage(onUploadListener: LongProcessListener,
                   userEmail: String, userAuth: String,
                   img: Bitmap, applicationContext: Context) {

        var userEmailReq = RequestBody.create(
            MediaType.
                parse("multipart/form-data"), userEmail.toString())

        var userAuthReq = RequestBody.create(MediaType.
            parse("multipart/form-data"), userAuth)

        var profilePic: MultipartBody.Part? = null

        val file =
            storeImageLocally(applicationContext, userAuth, img)
                //onUploadListener, )

        uploadImage(file, profilePic, userEmailReq, userAuthReq, onUploadListener)
    }

    protected abstract fun upload(
        userEmail: RequestBody, userAuth: RequestBody,
        fileMP: MultipartBody.Part?)
            : Call<ResponseBody>

    private fun uploadImage(
        file: File,
        profilePic: MultipartBody.Part?,
        userEmailReq: RequestBody,
        userAuthReq: RequestBody,
        onUploadListener: LongProcessListener
    ) {
        var profilePicMP = profilePic
        val requestFile: RequestBody = RequestBody.
            create(MediaType.parse("image/*"), file)

        profilePicMP = MultipartBody.Part.
            createFormData("profileImage", file.name, requestFile)

        val call: Call<ResponseBody> =
            this.upload(userEmailReq, userAuthReq, profilePicMP)
        call.enqueue(object : Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                onUploadListener.onLongProcessFailure(t)
                Log.d(TAG, t.message)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d(TAG, "Image successfully uploaded, response: ${response.body().toString()}")
                // get image path from response back the image path
                onUploadListener.onLongProcessSuccess(response)
            }
        })
    }

    private fun storeImageLocally(
        applicationContext: Context,
        dispName: String,
        // onUploadFinishedListener: UserContract.Model.OnUploadFinishedListener,
        img: Bitmap
    ): File {
        //val file: File = File()
        val filesDir = applicationContext.filesDir
        // saves the file in the filesDir, TODO: check it
        val file = File(filesDir, "$dispName.png")

        val bos = ByteArrayOutputStream()

        img.compress(Bitmap.CompressFormat.PNG, 0, bos)
        val bitmapdata = bos.toByteArray()

        // bitmap -> stream -> file
        val fos = FileOutputStream(file)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()

        // saves the localImagePath inside the user
        // onUploadFinishedListener.OnUploadFinished(file.absolutePath)

        return file
    }
}