package mcc.group14.apiclientapp

import android.content.Context
import android.graphics.Bitmap


interface UserContract {
    interface Model{

        interface OnFinishedListener {
            fun onFinished(user: User)

            fun onFailure(t: Throwable)
        }

        fun uploadImage(userId: Int, dispName: String,
                        img: Bitmap?, applicationContext: Context)

        fun postUser(user: User)

        fun getUser(listener: OnFinishedListener, userId: Int)
    }

    interface View{

        fun displayUserData(fetchedUser: User)

        fun showProgress()

        fun hideProgress()

        fun onResponseFailure(t: Throwable)

    }

    interface Presenter{

        fun onDestroy()

        fun getUserData(userId: Int)

    }
}