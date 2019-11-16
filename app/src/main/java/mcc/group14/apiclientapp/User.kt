package mcc.group14.apiclientapp

data class User(
    val displayName: String,
    val userId: Int,
    var profileImagePath: String,
    val email: String,
    var projects: MutableList<String>,
    var createdProjects: MutableList<String>
)