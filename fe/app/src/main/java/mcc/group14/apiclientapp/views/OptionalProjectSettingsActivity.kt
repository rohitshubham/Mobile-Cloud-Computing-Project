package mcc.group14.apiclientapp.views

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import mcc.group14.apiclientapp.R
import mcc.group14.apiclientapp.data.ProjectDetail
import mcc.group14.apiclientapp.utils.ProjectImageHelper
import okhttp3.ResponseBody
import retrofit2.Response

class OptionalProjectSettingsActivity :
    AppCompatActivity(),
    LongRunningActivity{

    val PROFILE_PIC_SELECTION = 0

    lateinit var  pickProjectImageBtn : Button
    lateinit var pickProjectImageIV : ImageView

    lateinit var curProject: ProjectDetail
    lateinit var userEmail: String
    lateinit var userAuth: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_optional_project_settings)

        curProject = intent.getSerializableExtra("PROJECT")
                as ProjectDetail
        userAuth = intent.getStringExtra("USER_AUTH")
        userEmail = intent.getStringExtra("USER_EMAIL")

        initUI()

        setListeners()

    }

    private fun initUI() {
        pickProjectImageBtn = findViewById(R.id.project_img_btn)
        pickProjectImageIV = findViewById(R.id.project_img_iv)

    }

    override fun onLongProcessFailure(t: Throwable) {
        TODO("not implemented") // Donno if needed
    }

    override fun onLongProcessSuccess(result: Response<ResponseBody>) {
        // TODO: @Max get path to image, after agreement with be
        // this.curProject.badge = result.body() as
    }


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

        val imageHelper = ProjectImageHelper.instance
        val listener = LongProcessListener(this)

        imageHelper.storeImage(listener, userEmail, userAuth,
            img, this.applicationContext)

        // user stores, not yet implemented
        // storeImage(1, "usr1", img)
        // sets image in ImageView
        pickProjectImageIV.setImageBitmap(img)

        // TODO: cool way to scale an image
        // val scaledImage: Bitmap = Bitmap.createScaledBitmap(img, iv.width, iv.height, true)
    }

    private fun setListeners() {
        pickProjectImageBtn.setOnClickListener {
            val pickGalleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            pickGalleryIntent.type = "image/*"
            startActivityForResult(
                pickGalleryIntent,
                PROFILE_PIC_SELECTION
            )
        }
    }

}
