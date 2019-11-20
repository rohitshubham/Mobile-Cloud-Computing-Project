package mcc.group14.apiclientapp

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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


class UserModel : UserContract.Model {

    val client by lazy {
        UsersApiClient.create()
    }
    val TAG = "UserModel"

    override fun getUser(listenerGet: UserContract.Model.OnGetFinishedListener,
                         userId: Int) {

        var disposable = client.getUser(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.v(TAG, "" + result)
                    listenerGet.onGetFinished(result)
                },
                { error -> Log.e(TAG, error.message)
                    listenerGet.onGetFailure(error)
                }
            )
    }

    override fun postUser(user: User) {
        var disposable = client.addUser(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.v(TAG, "" + user ) },
                { error -> Log.e(TAG, error.message ) }
            )
    }


    override fun storeImage(onUploadFinishedListener: UserContract.Model.OnUploadFinishedListener,
                            userId: Int, dispName: String,
                            img: Bitmap, applicationContext: Context) {

        var userId = RequestBody.create(
            MediaType.
                parse("multipart/form-data"), userId.toString())

        var userDisplayName = RequestBody.create(MediaType.
            parse("multipart/form-data"), dispName)

        var profilePic: MultipartBody.Part? = null

        val file =
                storeImageLocally(applicationContext, dispName,
                    onUploadFinishedListener, img)

        uploadImage(file, profilePic, userId, userDisplayName, onUploadFinishedListener)
    }

    private fun uploadImage(
        file: File,
        profilePic: MultipartBody.Part?,
        userId: RequestBody,
        userDisplayName: RequestBody,
        onUploadFinishedListener: UserContract.Model.OnUploadFinishedListener
    ) {
        var profilePic1 = profilePic
        val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)

        profilePic1 = MultipartBody.Part.createFormData("profileImage", file.name, requestFile)

        val call: Call<ResponseBody> =
            client.uploadProfilePicture(userId, userDisplayName, profilePic1)
        call.enqueue(object : Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onUploadFinishedListener.OnUploadFailure(t)
                Log.d(TAG, t.message)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d(TAG, "Image successfully uploaded, response: ${response.body().toString()}")
            }
        })
    }

    private fun storeImageLocally(
        applicationContext: Context,
        dispName: String,
        onUploadFinishedListener: UserContract.Model.OnUploadFinishedListener,
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
        onUploadFinishedListener.OnUploadFinished(file.absolutePath)

        return file
    }

}