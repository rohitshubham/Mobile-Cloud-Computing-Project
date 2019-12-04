package mcc.group14.apiclientapp.data

import java.io.Serializable

data class ProjectDetail(
    var name: String,
    var description: String,
    var project_type: String,
    var requester_email: String,
    var is_project_administrator: String,

    var project_id: String? = null,
    var deadline: String? = null,
    var team_members: String? = null,
    // it is generated by be
    var creation_time: String? = null,
    var badge: String? = null,
    var keywords: String? = null



    // we just need the display name of the users so that we can GET their images
    // we do not need all the users' fields

    // in milliseconds
    ) : Serializable