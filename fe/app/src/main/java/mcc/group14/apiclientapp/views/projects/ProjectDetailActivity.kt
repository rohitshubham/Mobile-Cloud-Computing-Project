package mcc.group14.apiclientapp.views.projects

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mcc.group14.apiclientapp.R
import mcc.group14.apiclientapp.api.ProjectApiClient

class ProjectDetailActivity : AppCompatActivity() {
    private val TAG = "ProjectDetailActivity"
    private lateinit var projectId : String
    private  lateinit var projectName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_detail)

        projectId = intent.getStringExtra("PROJECT_ID")
        projectName = intent.getStringExtra("PROJECT_NAME")

        val projectApi = ProjectApiClient.create()

        var disposable = projectApi.getProjectDetail(projectId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).map {
                if (it.success == "true") Log.v(TAG,it.payload.toString())
                else throw Throwable("APIError")
            }
            .subscribe(
                { result -> Log.v(TAG, "" + result)
                },
                { error -> Log.e(TAG, error.message)
                }
            )
    }
}
