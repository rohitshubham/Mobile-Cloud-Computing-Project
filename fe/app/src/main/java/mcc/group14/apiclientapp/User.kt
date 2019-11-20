package mcc.group14.apiclientapp

data class User(
    val userId: Int,
    val displayName: String,
    val email: String,
    var password: String?,
    // NB: profileImagePath is the path to the profile image in the BE
    val profileImagePath: String,
    var projects: MutableList<String>,
    var createdProjects: MutableList<String>,
    var localProfileImagePath: String?
)