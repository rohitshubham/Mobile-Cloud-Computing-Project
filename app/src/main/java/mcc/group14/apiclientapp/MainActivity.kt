package mcc.group14.apiclientapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class MainActivity : AppCompatActivity() {

    // just a constant
    private companion object{
        const val GALLERY_PHOTO_REQ = 1

    }

    val client by lazy {
        UsersApiClient.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imagePutBtn = findViewById<Button>(R.id.imagePutter)

        imagePutBtn.setOnClickListener {
            val pickGalleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            pickGalleryIntent.type = "image/*"
            startActivityForResult(pickGalleryIntent, GALLERY_PHOTO_REQ)
        }


        //showUser(1)

        val user = User(4, "usr4", "usr4@mail.fi", null,
            "/img1.png", mutableListOf("pr5"), mutableListOf("pr1"))
        postUser(user)
    }


    // POST new User
    private fun postUser(user: User) {

        var disposable = client.addUser(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.v("POSTED USER", "" + user ) },
                { error -> Log.e("ERROR", error.message ) }
            )
    }

    // GET User by userId
    private fun showUser(userId: Int) {

        var disposable = client.getUser(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.v("USER ID ${userId}: ", "" + result) },
                { error -> Log.e("ERROR", error.message) }
            )
    }

    private fun uploadImage(userId: Int, dispName: String, img: Bitmap?){
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
        call.enqueue(object : Callback<ResponseBody>{

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("test-onFailure",t.message)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("test-onResponse", response.body().toString())
            }


        })
    }

    // to implement the profile image selection from the gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_PHOTO_REQ){

            val iv = findViewById<ImageView>(R.id.userImage)
            val imageUri = data?.data

            val img : Bitmap = BitmapFactory.decodeStream(contentResolver.
                openInputStream(imageUri!!))

            // user stores, not yet implemented
            uploadImage(1, "usr1", img)
            // sets image in ImageView
            iv.setImageBitmap(img)

            // TODO: cool way to scale an image
            // val scaledImage: Bitmap = Bitmap.createScaledBitmap(img, iv.width, iv.height, true)
        } else {
            val t = Toast.makeText(applicationContext,
                "No photo selected", Toast.LENGTH_SHORT)
            t.show()
        }
    }



}
