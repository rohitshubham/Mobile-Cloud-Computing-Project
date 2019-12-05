package mcc.group14.apiclientapp.views.projects.create

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
import mcc.group14.apiclientapp.data.ProjectDetail
import mcc.group14.apiclientapp.utils.LongProcessListener
import mcc.group14.apiclientapp.utils.LongRunningActivity
import mcc.group14.apiclientapp.utils.ProjectImageHelper
import okhttp3.ResponseBody
import retrofit2.Response
import java.time.LocalDateTime
import android.widget.TimePicker
import android.widget.DatePicker
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import mcc.group14.apiclientapp.R
import java.time.format.DateTimeFormatter
import java.util.*
import java.time.ZoneId.systemDefault


class OptionalProjectSettingsActivity :
    AppCompatActivity(),
    LongRunningActivity // this is required for image uploading
{
    val TAG = "OptionalProjectSettingsActivity"

    val PROFILE_PIC_SELECTION = 0

    lateinit var  pickProjectImageBtn : Button
    lateinit var pickProjectImageIV : ImageView
    lateinit var createProjectBtn : Button
    lateinit var deadlineDateTV: TextView

    lateinit var curProject: ProjectDetail
    lateinit var userEmail: String
    lateinit var userAuth: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mcc.group14.apiclientapp.R.layout.activity_optional_project_settings)

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
        deadlineDateTV = findViewById(R.id.pick_deadline_iv)
    }

    public fun onDateTimePick(view: View){
        // TODO: -- @Sasha personalise the time-date picker
        // to personalise it https://github.com/florent37/SingleDateAndTimePicker
        SingleDateAndTimePickerDialog.Builder(view.context)
            .defaultDate(Date())
            .minDateRange(Date())
            //.bottomSheet()
            .curved()
            .minutesStep(15)
            //.displayHours(false)
            //.displayMinutes(false)
            //.todayText("aujourd'hui")
            .displayListener {
                //retrieve the SingleDateAndTimePicker
            }

            .title("Select a deadline")
            .listener(object : SingleDateAndTimePickerDialog.Listener {
                override fun onDateSelected(date: Date) {
                    Log.d(TAG, date.toString())

                    if (isFuture(date)){
                        setDate(date)
                    } else {
                        Toast.makeText(applicationContext, "Invalid deadline",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }).display()
    }

    private fun setDate(date: Date) {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss'.000000Z'")
        val localTime = convertToLocalDateViaInstant(date)
        var formattedBEDate = localTime.format(formatter)

        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm:ss")
        var formattedFEDate = localTime.format(formatter)

        deadlineDateTV.text = formattedFEDate
        curProject.deadline = formattedBEDate
    }

    private fun isFuture(date: Date) = date >= Date()

    fun convertToLocalDateViaInstant(dateToConvert: Date): LocalDateTime {
        return dateToConvert.toInstant()
            .atZone(systemDefault())
            .toLocalDateTime()
    }

    override fun onLongProcessFailure(t: Throwable) {
        TODO("not implemented") // Donno if needed
    }

    override fun onLongProcessSuccess(result: Response<ResponseBody>) {
        // TODO: ++ @Max get path to image, after agreement with be
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
        val iv = findViewById<ImageView>(mcc.group14.apiclientapp.R.id.userImage)
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
            // TODO: + @Max check both date and time inserted properly, it is possible only date in
            val returnIntent = Intent()
            setResult(Activity.RESULT_OK, returnIntent)
            returnIntent.putExtra("PROJECT", curProject)
            finish()
        }
    }

    fun addKeywords(v: View){
        val keywordsET = findViewById<EditText>(mcc.group14.apiclientapp.R.id.keywords_et)
        val capturedKeywordsString = keywordsET.text.toString()
        if ( capturedKeywordsString != "" ){
            val keywords = capturedKeywordsString.split(",")
            if (keywords.size > 3){
                Toast.makeText(this, "Too many keywords inserted",
                    Toast.LENGTH_LONG).show()
            }else{
                this.curProject.keywords = capturedKeywordsString
                Toast.makeText(this, "Keywords added to ${curProject.name}",
                    Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this,"Insert keywords",
                Toast.LENGTH_LONG).show()
        }
    }
}

