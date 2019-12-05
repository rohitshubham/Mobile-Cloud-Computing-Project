package mcc.group14.apiclientapp.views.users

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mcc.group14.apiclientapp.R
import mcc.group14.apiclientapp.api.UsersApiClient
import mcc.group14.apiclientapp.data.UserCredentials
import mcc.group14.apiclientapp.utils.LongProcessListener
import mcc.group14.apiclientapp.utils.LongRunningActivity
import mcc.group14.apiclientapp.utils.UserImageHelper
import okhttp3.ResponseBody
import retrofit2.Response


// User Settings view
class UserSettingsActivity : AppCompatActivity(), LongRunningActivity {

    // TODO: demolish MVP, put everything here and maybe utils class (image upload)
    private val TAG = "UserSettingsActivity"

    // UI variables, NB lateinit lets us initialise them in initGUI
    private lateinit var pbloading: ProgressBar
    private lateinit var imagePutBtn: Button
    private lateinit var newPswET: EditText
    private lateinit var newPswBtn: Button

    // NB: user might be changing (projects and created_projects might change), always get it from the
    //     BE before using his data
    private lateinit var userCredentials: UserCredentials

    private val userApi = UsersApiClient.create()

    // @TODO: @Max @Kirthi understand how to get user data from mAuth
    lateinit var uid: String
    lateinit var userEmail: String

    // just a constant
    private companion object{
        const val PROFILE_PIC_SELECTION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_settings)

        // initialises GUI elements
        initGUI()

        setListeners()

        userEmail = intent.getStringExtra("USER_EMAIL")
        // @TODO: ++ userAuth should be in sharedPreferences
        uid = intent.getStringExtra("USER_AUTH")
        val userPsw = "DEFAULT_PSW"

        userCredentials = UserCredentials(userEmail, userPsw)

        this.hideProgress()
        Log.d(TAG,"User email: $userEmail, userAuth: $uid")

/*
        val user = User(4, "usr4", "usr4@mail.fi", null,
            "/img1.png", mutableListOf("pr5"), mutableListOf("pr1"))
        postUser(user)*/
    }

    private fun initGUI() {
        this.title = ("User settings")
        pbloading = findViewById(R.id.pb_loading)
        imagePutBtn = findViewById(R.id.imagePutter)
        newPswBtn = findViewById(R.id.new_psw_btn)
        newPswET = findViewById(R.id.new_psw_et)

    }

    private fun setListeners() {
        imagePutBtn.setOnClickListener {
            val pickGalleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            pickGalleryIntent.type = "image/*"
            startActivityForResult(pickGalleryIntent,
                PROFILE_PIC_SELECTION
            )
        }
        newPswBtn.setOnClickListener{
            val capturedPsw = newPswET.text.toString()
            if (capturedPsw != "") {
                userCredentials.password = capturedPsw
                postCredentials()
            } else if (capturedPsw.length < 6){
                Toast.makeText(applicationContext,
                    "Password must be at least 6 chars", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(applicationContext, "Insert a password",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun postCredentials() {
        val returnIntent = Intent()
        var disposable = userApi.editPassword(userCredentials)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    Log.v(TAG, "POSTED: $result")

                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                },
                { error ->
                    Log.e(TAG, "ERROR POSTING CREDENTIALS ${error.message}")
                    setResult(Activity.RESULT_CANCELED, returnIntent)
                    finish()
                }
            )
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

        val imageHelper = UserImageHelper.instance
        val listener = LongProcessListener(this)

        imageHelper.storeImage(listener, userEmail, uid,
            img, this.applicationContext)

        iv.setImageBitmap(img)

        // TODO: cool way to scale an image
        // val scaledImage: Bitmap = Bitmap.createScaledBitmap(img, iv.width, iv.height, true)
    }

    override fun onLongProcessSuccess(result: Response<ResponseBody>) {
        // TODO: ++ @Max get the path from BE
        //user.photo_url = result.body()...
    }

    override fun onLongProcessFailure(t: Throwable) {
        Toast.makeText(applicationContext,
            "Could not upload the image", Toast.LENGTH_LONG).show()
    }

    fun setLocalProfileImagePath(localPath: String) {
        // user.localProfileImagePath = localPath
        Log.d(TAG, "Image saved successfully in: $localPath")
    }

    fun showProgress() {
        pbloading.visibility = View.VISIBLE
    }

    fun hideProgress() {
        pbloading.visibility = View.GONE
    }

}
