package mcc.group14.apiclientapp.data

data class User(
    val uid: String,
    val display_name: String,
    val email: String,
    var password: String,
    // NB: photo_url is the path to the profile image in the BE
    val photo_url: String? = null,

    var projects: MutableList<ProjectDetail>? = null,
    var createdProjects: MutableList<ProjectDetail>? = null,
    var localProfileImagePath: String? = null
)