package mcc.group14.apiclientapp.data

data class ProjectDetail(

    var project_id: String,
    var deadline: String,
    var description: String,
    var team_members: String,
    val name: String,
    val requester_email: String,
    var badge: String,
    var keywords: String,
    val creationTime: String,
    val project_type: String
    // we just need the display name of the users so that we can GET their images
    // we do not need all the users' fields

    // in milliseconds
    )