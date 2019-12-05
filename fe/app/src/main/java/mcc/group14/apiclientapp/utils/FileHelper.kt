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
    private val TMP_FILENAME = "tmp-file.png"

    fun storeImage(onUploadListener: LongProcessListener,
                   userEmail: String?, userPass: String?,
                   img: Bitmap, applicationContext: Context) {

        var userEmailReq: RequestBody? = null

        if (userEmail != null){
            userEmailReq = RequestBody.create(
            MediaType.
                parse("multipart/form-data"), userEmail.orEmpty())
        }

        var userPassReq: RequestBody? = null
        if(userPass != null) {

            userPassReq = RequestBody.create(MediaType.
                parse("multipart/form-data"), userPass)
        }

        //var profilePic: MultipartBody.Part? = null

        val file =
            storeImageLocallyAndGetFile(applicationContext, img)
                //onUploadListener, )

        uploadImage(file, userEmailReq, userPassReq, onUploadListener)
    }

    protected abstract fun upload(
        userEmail: RequestBody?, userPassword: RequestBody?,
        fileMP: MultipartBody.Part?)
            : Call<ResponseBody>

    private fun uploadImage(
        file: File,
        userEmailReq: RequestBody?,
        userPassReq: RequestBody?,
        onUploadListener: LongProcessListener
    ) {
        // NOTE heavy calls solution: this is to make heavy calls with support of mainLooper.
        // For more here https://stackoverflow.com/questions/37856571/retrofit-2-callback-issues-with-ui-thread

        val requestFile: RequestBody = RequestBody.
            create(MediaType.parse("image/*"), file)

        var profilePicMP = MultipartBody.Part.
            createFormData("file", file.name, requestFile)

        val call: Call<ResponseBody> =
            this.upload(userEmailReq, userPassReq, profilePicMP)
        call.enqueue(object : Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                onUploadListener.onLongProcessFailure(t)
                Log.d(TAG, t.message)
                deleteTmpFile(file)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d(TAG, "Image successfully uploaded, response: " +
                        "${response.body()}")
                // get image path from response back the image path
                onUploadListener.onLongProcessSuccess(response)
                deleteTmpFile(file)
            }
        })
    }

    private fun deleteTmpFile(file: File) {
        var deletionSuccessful = file.delete()

        if (!deletionSuccessful) {
            Log.e(
                TAG, "Deletion of file ${file.absolutePath} " +
                        "not successful."
            )
        }
    }

    // Saves images as png
    private fun storeImageLocallyAndGetFile(
        applicationContext: Context,
         // onUploadFinishedListener: UserContract.Model.OnUploadFinishedListener,
        img: Bitmap
    ): File {
        //val file: File = File()
        val filesDir = applicationContext.filesDir

        val file = File(filesDir, TMP_FILENAME)

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