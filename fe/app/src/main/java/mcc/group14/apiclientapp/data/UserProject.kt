package mcc.group14.apiclientapp.data

import java.io.Serializable

data class UserProject (
    var name: String,
    var description: String,
    var project_type: String,
    var requester_email: String,
    // var is_project_administrator: String,

    var project_id: String? = null,
    var deadline: String? = null,
    var team_members: String? = null,
    // it is generated by be
    // var creation_time: String? = null,
    var badge: String? = null,
    var keywords: String? = null,
    var creation_time: String?,
    var last_modified: String?
    // TODO: add last_modified: String
): Serializable