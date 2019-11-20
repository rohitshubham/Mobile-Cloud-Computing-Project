package mcc.group14.apiclientapp

import android.content.Context
import android.graphics.Bitmap


interface UserContract {
    interface Model{

        interface OnGetFinishedListener {
            fun onGetFinished(user: User)

            fun onGetFailure(t: Throwable)
        }

        interface  OnUploadFinishedListener{
            fun OnUploadFailure(t: Throwable)

            fun OnUploadFinished(localImagePath: String)

        }

        fun storeImage(uploadListener: OnUploadFinishedListener, userId: Int,
                       dispName: String, img: Bitmap,
                       applicationContext: Context)

        fun postUser(user: User)

        fun getUser(listenerGet: OnGetFinishedListener, userId: Int)
    }

    interface View{

        fun setLocalProfileImagePath(localPath: String)

        fun refreshAndDisplayUserData(fetchedUser: User)

        fun showProgress()

        fun hideProgress()

        fun onResponseFailure(t: Throwable)

    }

    interface Presenter{

        fun onDestroy()

        fun getUserData(userId: Int)

        fun storeProfileImage(profileImage: Bitmap, userId: Int,
                              dispName: String, curContext: Context)
    }
}