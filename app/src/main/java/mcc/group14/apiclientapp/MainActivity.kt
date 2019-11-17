package mcc.group14.apiclientapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast

class MainActivity : AppCompatActivity(), UserContract.View {

    private val TAG = "UserActivity"
    private var pbloading: ProgressBar? = null
    private var user: User? = null
    private var userPresenter: UserPresenter? = null

    // just a constant
    private companion object{
        const val GALLERY_PHOTO_REQ = 1

    }

    override fun showProgress() {
        pbloading?.visibility = View.VISIBLE


    }

    override fun hideProgress() {
        pbloading?.visibility = View.GONE
    }

    override fun displayUserData(fetchedUser: User) {
        user = fetchedUser
        if (user != null) {
            Toast.makeText(this,
                fetchedUser.displayName, Toast.LENGTH_LONG).show()
        }
    }

    override fun onResponseFailure(t: Throwable) {
        Log.e(TAG, t.message)
        Toast.makeText(this,
            getString(R.string.communication_error),
            Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pbloading = ProgressBar(this)
        userPresenter = UserPresenter(this)
        // from login or sign-up
        var id : Int = 1
        userPresenter?.getUserData(id)

        val imagePutBtn = findViewById<Button>(R.id.imagePutter)


        imagePutBtn.setOnClickListener {
            val pickGalleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            pickGalleryIntent.type = "image/*"
            startActivityForResult(pickGalleryIntent, GALLERY_PHOTO_REQ)
        }


        //getUser(1)
/*
        val user = User(4, "usr4", "usr4@mail.fi", null,
            "/img1.png", mutableListOf("pr5"), mutableListOf("pr1"))
        postUser(user)*/

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
            // uploadImage(1, "usr1", img)
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
