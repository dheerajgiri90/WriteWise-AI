
package com.dheeraj.writewiseai.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface WriteWiseApi {

    @POST("rewrite")
    suspend fun rewriteText(
        @Body request: RewriteRequest
    ): Response<RewriteResponse>
}
