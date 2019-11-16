package mcc.group14.apiclientapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    val client by lazy {
        UsersApiClient.create()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showUser(1)

    }

    // GET Article by userId
    private fun showUser(userId: Int) {
        var disposable = client.getUser(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.v("USER ID ${userId}: ", "" + result) },
                { error -> Log.e("ERROR", error.message) }
            )
    }
}
