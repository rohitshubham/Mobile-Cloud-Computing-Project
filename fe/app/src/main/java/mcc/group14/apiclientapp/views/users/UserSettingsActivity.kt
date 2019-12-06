package mcc.group14.apiclientapp.views.users

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.projects.*
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

    private val TAG = "UserSettingsActivity"

    // UI variables, NB lateinit lets us initialise them in initGUI
    private lateinit var pbloading: ProgressBar
    private lateinit var imagePutBtn: Button
    private lateinit var newPswET: EditText
    private lateinit var newPswBtn: Button
    private lateinit var imageQualityRadioGroup: RadioGroup
    private lateinit var userImage: Bitmap
    private var imageSet: Boolean = false

    // NB: user might be changing (projects and created_projects might change), always get it from the
    //     BE before using his data
    private lateinit var userCredentials: UserCredentials

    private val userApi = UsersApiClient.create()

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

        val sharedprefs =  getSharedPreferences("USER_AUTH_DATA",
            Context.MODE_PRIVATE)
        userEmail = sharedprefs.
            getString("user_email","defaultName")!!
        uid = sharedprefs.
            getString("user_auth","defaultName")!!

        userCredentials = UserCredentials(userEmail)

        this.hideProgress()
        Log.d(TAG,"User email: $userEmail, userAuth: $uid")

    }

    private fun initGUI() {
        this.title = ("User settings")
        pbloading = findViewById(R.id.pb_loading)
        imagePutBtn = findViewById(R.id.imagePutter)
        newPswBtn = findViewById(R.id.new_psw_btn)
        newPswET = findViewById(R.id.new_psw_et)
        imageQualityRadioGroup = findViewById(R.id.myRadioGroup)
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
            } else if (capturedPsw.length < 8){
                Toast.makeText(applicationContext,
                    "Password must be at least 8 chars", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(applicationContext, "Insert a password",
                    Toast.LENGTH_LONG).show()
            }
        }

        imageQualityRadioGroup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener {group, checkedId ->
                val radio = findViewById<RadioButton>(checkedId)
                var choice = ""
                if (radio.id == R.id.low) {
                    choice = "LOW"
                } else if (radio.id == R.id.high) {
                    choice = "HIGH"
                } else if (radio.id == R.id.original) {
                    choice = "ORIGINAL"
                }

                resizeProfilePicture(choice)
                Toast.makeText(applicationContext,"Set resolution to : ${choice}", Toast.LENGTH_SHORT).show()
            })
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
            pickProfilePicture(data)
        } else {
            val t = Toast.makeText(applicationContext,
                "No photo selected", Toast.LENGTH_SHORT)
            t.show()
        }
    }

    private fun resizeProfilePicture(imageQualityChoice: String) {
        if (imageSet) {
            val img = userImage
            var width = img.width
            var height = img.height
            if (imageQualityChoice == "LOW") {
                width = 640
                height = 480
            } else if (imageQualityChoice == "HIGH") {
                width = 1280
                height = 960
            }

            val scaledImage: Bitmap = Bitmap.createScaledBitmap(img, width, height, true)
            //Toast.makeText(applicationContext,"Resized to: ${width} x ${height}",
            //    Toast.LENGTH_SHORT).show()
            val iv = findViewById<ImageView>(R.id.userImage)
            iv.setImageBitmap(scaledImage)
        }

    }

    private fun pickProfilePicture(data: Intent?) {
        val iv = findViewById<ImageView>(R.id.userImage)
        val imageUri = data?.data

        val img: Bitmap = BitmapFactory.decodeStream(
            contentResolver.openInputStream(imageUri!!)
        )

        imageSet = true
        userImage = img

        val imageHelper = UserImageHelper.instance
        val listener = LongProcessListener(this)

<<<<<<< HEAD
        val jsonParams = Gson().toJson(userCredentials)

        imageHelper.storeImageAndParams(listener,
            jsonParams, img, this.applicationContext)

        /*imageHelper.storeImage(listener, userEmail,
            userCredentials.password, img, this.applicationContext)
*/
=======
>>>>>>> origin/fe/user-settings
        iv.setImageBitmap(img)
    }

    private fun uploadProfilePicture() {

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

