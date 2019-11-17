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

    override fun getUser(listener: UserContract.Model.OnFinishedListener,
                         userId: Int) {

        var disposable = client.getUser(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.v(TAG, "" + result)
                    listener.onFinished(result)
                },
                { error -> Log.e(TAG, error.message)
                    listener.onFailure(error)
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


    override fun uploadImage(userId: Int, dispName: String,
                             img: Bitmap?, applicationContext: Context) {

        var userId = RequestBody.create(
            MediaType.
                parse("multipart/form-data"), userId.toString())

        var userDisplayName = RequestBody.create(MediaType.
            parse("multipart/form-data"), dispName)

        var profilePic: MultipartBody.Part? = null

        if (img != null) {
            //val file: File = File()
            val filesDir = applicationContext.filesDir
            val file = File(filesDir, "profile_image-cache.png")

            val bos = ByteArrayOutputStream()


            img.compress(Bitmap.CompressFormat.PNG, 0, bos)
            val bitmapdata = bos.toByteArray()

            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()

            val requestFile : RequestBody = RequestBody.
                create(MediaType.parse("image/*"), file)

            profilePic = MultipartBody.Part.
                createFormData("profileImage", file.name, requestFile)
        }
        val call: Call<ResponseBody> = client.uploadProfilePicture(userId, userDisplayName, profilePic)
        call.enqueue(object : Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d(TAG,t.message)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d(TAG, response.body().toString())
            }
        })
    }

}