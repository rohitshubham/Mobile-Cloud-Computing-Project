package mcc.group14.apiclientapp.data

data class UserProject (
    val project_id: String,
    val project_name: String,
    // might be a boolean
    val is_user_administrator: String
)