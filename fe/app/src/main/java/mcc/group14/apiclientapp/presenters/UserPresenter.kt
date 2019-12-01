package mcc.group14.apiclientapp.presenters

import android.content.Context
import android.graphics.Bitmap
import mcc.group14.apiclientapp.data.User
import mcc.group14.apiclientapp.contracts.UserContract
import mcc.group14.apiclientapp.models.UserModel

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
        // TODO: implement
    }

}