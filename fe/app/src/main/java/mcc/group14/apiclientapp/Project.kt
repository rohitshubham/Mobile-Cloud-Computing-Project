package mcc.group14.apiclientapp

data class Project(
    var id: String,
    // we just need the display name of the users so that we can GET their images
    // we do not need all the users' fields
    var teamMembers: MutableList<String>,
    // in milliseconds
    var projectDeadline: Long,
    var projectDescription: String,
    val requesterDisplayName: String,
    var isPersonal: Boolean,
    var projectImage: String,
    var projectKeywords: MutableList<String>
    )