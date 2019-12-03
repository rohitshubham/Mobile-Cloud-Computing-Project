package mcc.group14.apiclientapp.views.projects.create

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
import mcc.group14.apiclientapp.utils.LongProcessListener
import mcc.group14.apiclientapp.utils.LongRunningActivity
import mcc.group14.apiclientapp.utils.ProjectImageHelper
import okhttp3.ResponseBody
import retrofit2.Response
import java.time.LocalDateTime

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
    lateinit var deadlineDateTV: TextView

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
        deadlineDateTV = findViewById(R.id.deadline_date_tv)
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
            // TODO: + @Max check both date and time inserted properly, it is possible only date in
            val returnIntent = Intent()
            setResult(Activity.RESULT_OK, returnIntent)
            returnIntent.putExtra("PROJECT", curProject)
            finish()
        }
    }

    fun showDatePickerDialog(v: View) {
        val newFragment =
            DatePickerFragment(this, this)
        newFragment.show(supportFragmentManager, "datePicker")
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        var dayString = addPadding(day)
        val actualMonth = month + 1
        var monthString = addPadding(actualMonth)
        deadlineDateTV.text = "$dayString/$monthString/$year"
        curProject.deadline = "$year-$monthString-$dayString"
    }


    fun showTimePickerDialog(v: View) {
        TimePickerFragment(this)
            .show(supportFragmentManager, "timePicker")
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user
        // TODO: -- remove second part of the first condition, just for testing
        if (this.curProject.deadline == "" ||
            this.curProject.deadline == "DEFAULT_DEADLINE"){
            Toast.makeText(this, "Please first set a deadline date",
                Toast.LENGTH_LONG).show()

        } else if (this.curProject.deadline.orEmpty().contains("\'T\'")){
            Toast.makeText(this, "Time deadline already selected",
                Toast.LENGTH_LONG).show()

        } else {
            updateOnlyFutureDeadline(hourOfDay, minute)
        }
    }

    private fun updateOnlyFutureDeadline(hourOfDay: Int, minute: Int) {
        val curDT = LocalDateTime.now()

        if (isDeadlineToday(curDT)) {
            // updates if and only if deadline is future
            if (isPastTime(minute, hourOfDay, curDT)) {
                Toast.makeText(
                    this,
                    "Past time selected", Toast.LENGTH_LONG
                ).show()
            } else {
                updateDeadlineTime(hourOfDay, minute)
            }
        } else {
            // the date keeper prevent the user from inserting previous date than today's
            updateDeadlineTime(hourOfDay, minute)
        }
    }

    private fun isDeadlineToday(
        curDT: LocalDateTime
    ): Boolean {

        val insertedDeadlineDate = deadlineDateTV.text.toString().split('/')
        val curDay = curDT.dayOfMonth.toString()
        val curMonth = curDT.month.value.toString()
        val curYear = curDT.year.toString()

        return insertedDeadlineDate[0] == curDay &&
                insertedDeadlineDate[1] == curMonth &&
                insertedDeadlineDate[2] == curYear
    }

    private fun isPastTime(
        minute: Int,
        hourOfDay: Int,
        curDT: LocalDateTime
    ) = (hourOfDay < curDT.hour) ||
            (hourOfDay == curDT.hour && minute > curDT.minute)

    private fun updateDeadlineTime(hourOfDay: Int, minute: Int) {
        var minString = addPadding(minute)
        var hourString = addPadding(hourOfDay)
        val deadlineTimeTV = findViewById<TextView>(R.id.deadline_time_tv)
        deadlineTimeTV.text = "$hourString:$minString"
        this.curProject.deadline += "\'T\'$hourString:$minString"
    }

    private fun addPadding(toBePadded: Int) = if (toBePadded < 10) "0$toBePadded" else "$toBePadded"

    fun addKeywords(v: View){
        val keywordsET = findViewById<EditText>(R.id.keywords_et)
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

