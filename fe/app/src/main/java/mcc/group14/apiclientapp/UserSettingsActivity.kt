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


// User Settings view
class UserSettingsActivity : AppCompatActivity(), UserContract.View {

    private val TAG = "UserSettingsActivity"

    // UI variables, NB lateinit lets us initialise them in initGUI
    private lateinit var pbloading: ProgressBar
    private lateinit var imagePutBtn: Button

    // Data variables
    private lateinit var userPresenter: UserPresenter
    // NB: user might be changing (projects and created_projects might change), always get it from the
    //     BE before using his data
    private lateinit var user: User

    // just a constant
    private companion object{
        const val PROFILE_PIC_SELECTION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_settings)



        // for getting/posting data
        userPresenter = UserPresenter(this)

        // initialises GUI elements
        initGUI()

        setListeners()

        val userEmail = intent.getStringExtra("USER_EMAIL")
        val userAuth = intent.getStringExtra("USER_AUTH")

        this.hideProgress()
        Log.d(TAG,"User email: $userEmail, userAuth: $userAuth")
        // from login or sign-up
//        var id : Int = 2
//        userPresenter.getUserData(id)


/*
        val user = User(4, "usr4", "usr4@mail.fi", null,
            "/img1.png", mutableListOf("pr5"), mutableListOf("pr1"))
        postUser(user)*/
    }

    private fun initGUI() {
        pbloading = findViewById(R.id.pb_loading)
        imagePutBtn = findViewById<Button>(R.id.imagePutter)

    }

    private fun setListeners() {
        imagePutBtn.setOnClickListener {
            val pickGalleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            pickGalleryIntent.type = "image/*"
            startActivityForResult(pickGalleryIntent, PROFILE_PIC_SELECTION)
        }
    }

    // to implement the profile image selection from the gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == PROFILE_PIC_SELECTION){
            pickAndUploadProfilePicture(data)
        } else {
            val t = Toast.makeText(applicationContext,
                "No photo selected", Toast.LENGTH_SHORT)
            t.show()
        }
    }

    private fun pickAndUploadProfilePicture(data: Intent?) {
        val iv = findViewById<ImageView>(R.id.userImage)
        val imageUri = data?.data

        val img: Bitmap = BitmapFactory.decodeStream(
            contentResolver.openInputStream(imageUri!!)
        )

        // this function will store the image both locally and on the BE.
        // local path -> user.localProfileImagePath
        // remote path -> user.profileImagePath
        userPresenter.storeProfileImage(img, user.userId,
            user.displayName, this.applicationContext)

        // user stores, not yet implemented
        // storeImage(1, "usr1", img)
        // sets image in ImageView
        iv.setImageBitmap(img)

        // TODO: cool way to scale an image
        // val scaledImage: Bitmap = Bitmap.createScaledBitmap(img, iv.width, iv.height, true)
    }

    override fun setLocalProfileImagePath(localPath: String) {
        user.localProfileImagePath = localPath
        Log.d(TAG, "Image saved successfully in: $localPath")
    }

    override fun showProgress() {
        pbloading.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        pbloading.visibility = View.GONE
    }

    override fun refreshAndDisplayUserData(fetchedUser: User) {
        this.user = fetchedUser
        if (this.user != null) {
            Log.d(TAG, fetchedUser.toString())
            Toast.makeText(this,
                fetchedUser.displayName, Toast.LENGTH_LONG).show()

            Log.d(TAG, this.user.toString() )
        }
    }

    override fun onResponseFailure(t: Throwable) {
        Log.e(TAG, t.message)
        Toast.makeText(this,
            getString(R.string.communication_error),
            Toast.LENGTH_LONG).show()
    }

}
