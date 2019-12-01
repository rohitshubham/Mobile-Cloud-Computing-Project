package mcc.group14.apiclientapp.api

data class Response<T> (
    var success: String?,
    // this will be payload
    var payload : T?,
    var error: String?
)