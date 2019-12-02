package mcc.group14.apiclientapp.views

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import mcc.group14.apiclientapp.R
import mcc.group14.apiclientapp.data.ProjectDetail
import mcc.group14.apiclientapp.utils.ProjectImageHelper
import okhttp3.ResponseBody
import retrofit2.Response

class OptionalProjectSettingsActivity :
    AppCompatActivity(),
    LongRunningActivity,
TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener
{

    val TAG = "OptionalProjectSettingsActivity"

    val PROFILE_PIC_SELECTION = 0

    lateinit var  pickProjectImageBtn : Button
    lateinit var pickProjectImageIV : ImageView
    lateinit var createProjectBtn : Button

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
        createProjectBtn = findViewById(R.id.create_project_optional_set_btn)
    }

    override fun onLongProcessFailure(t: Throwable) {
        TODO("not implemented") // Donno if needed
    }

    override fun onLongProcessSuccess(result: Response<ResponseBody>) {
        // TODO: @Max get path to image, after agreement with be
        // this.curProject.badge = result.body() as
        this.curProject.badge = "/img/my_project.png"
        Log.d(TAG, "$curProject")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == PROFILE_PIC_SELECTION){
            pickAndUploadProjectPicture(data)
        } else {
            val t = Toast.makeText(applicationContext,
                "No photo selected", Toast.LENGTH_SHORT)
            t.show()
        }
    }

    private fun pickAndUploadProjectPicture(data: Intent?) {
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
        // returns to NewProjectActivity
        createProjectBtn.setOnClickListener{
            val returnIntent = Intent()
            setResult(Activity.RESULT_OK, returnIntent)
            returnIntent.putExtra("PROJECT", curProject)
            finish()
        }
    }

    fun showDatePickerDialog(v: View) {
        val newFragment = DatePickerFragment(this, this)
        newFragment.show(supportFragmentManager, "datePicker")
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        curProject.deadline = "$year-$month-$day"
    }


    fun showTimePickerDialog(v: View) {
        TimePickerFragment(this).show(supportFragmentManager, "timePicker")
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user
        // TODO: -- remove second part of the first condition, just for testing
        if (this.curProject.deadline == "" ||
            this.curProject.deadline == "DEFAULT_DEADLINE"){
            Toast.makeText(this, "Please first set a deadline date", Toast.LENGTH_LONG)
        } else if (this.curProject.deadline.orEmpty().contains("\'T\'")){
            Toast.makeText(this, "Time deadline already selected", Toast.LENGTH_LONG)
        } else {
            this.curProject.deadline += "\'T\'$hourOfDay:$minute"
        }
    }

}

