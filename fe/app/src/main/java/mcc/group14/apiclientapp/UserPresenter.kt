package mcc.group14.apiclientapp

class UserPresenter (val userView: UserContract.View):
    UserContract.Presenter, UserContract.Model.OnFinishedListener {

    val userModel = UserModel()


    override fun getUserData(userId: Int) {
        userModel.getUser(this, userId)
        userView.hideProgress()

    }

    override fun onFinished(user: User) {
        userView.displayUserData(user)
        userView.hideProgress()


    }

    override fun onFailure(t: Throwable) {
        userView.onResponseFailure(t)
        userView.hideProgress()

    }

    override fun onDestroy() {

    }



}