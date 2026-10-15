
package com.dheeraj.writewiseai.data.remote

data class RewriteRequest(
    val text: String,
    val style: String
)

data class RewriteResponse(
    val result: String
)
