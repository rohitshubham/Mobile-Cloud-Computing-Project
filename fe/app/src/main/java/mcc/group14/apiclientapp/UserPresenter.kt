package mcc.group14.apiclientapp

import android.content.Context
import android.graphics.Bitmap

class UserPresenter (val userView: UserContract.View):
    UserContract.Presenter,
    UserContract.Model.OnGetFinishedListener,
    UserContract.Model.OnUploadFinishedListener {


    val userModel = UserModel()


    override fun storeProfileImage(
        profileImage: Bitmap,
        userId: Int,
        dispName: String,
        curContext: Context
    ) {
        userModel.storeImage(this, userId,
            dispName, profileImage, curContext)
    }


    override fun getUserData(userId: Int) {
        userModel.getUser(this, userId)
        userView.hideProgress()

    }

    override fun onGetFinished(user: User) {
        userView.refreshAndDisplayUserData(user)
        userView.hideProgress()

    }

    override fun onGetFailure(t: Throwable) {
        userView.onResponseFailure(t)
        userView.hideProgress()

    }

    override fun OnUploadFailure(t: Throwable) {
        userView.onResponseFailure(t)
    }

    override fun OnUploadFinished(localImagePath: String) {
        userView.setLocalProfileImagePath(localImagePath)
    }

    override fun onDestroy() {

    }

}