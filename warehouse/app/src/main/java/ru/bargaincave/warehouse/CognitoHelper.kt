package ru.bargaincave.warehouse

import android.util.Log
import com.amazonaws.mobile.client.AWSMobileClient
import com.amplifyframework.core.Amplify
import com.beust.klaxon.Klaxon

class CognitoHelper {
    companion object {
        fun getUserGroups() : List<String>? {
            val mobileClient = Amplify.Auth.getPlugin("awsCognitoAuthPlugin").escapeHatch as AWSMobileClient?

            mobileClient?.run {
                val groupsString = tokens.accessToken.getClaim("cognito:groups")
                return Klaxon().parseArray(groupsString)
            } ?. run {
                Log.w("Cave", "Group token not found")
            }

            return null
        }
    }
}
