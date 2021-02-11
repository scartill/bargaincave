package ru.bargaincave.warehouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.addTextChangedListener
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.AWSDataStorePlugin
import ru.bargaincave.warehouse.databinding.ActivityCognitoLoginBinding

class CognitoLoginActivity : AppCompatActivity() {

    private lateinit var b: ActivityCognitoLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityCognitoLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.login.isEnabled = false
        b.sorter.isEnabled = false
        b.manager.isEnabled = false

        b.email.addTextChangedListener {
            textChangeListener()
        }
        b.password.addTextChangedListener {
            textChangeListener()
        }

        try {
            Amplify.addPlugin(AWSDataStorePlugin())
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(applicationContext)
            Log.i("Cave", "Initialized Amplify")
    /*
            // TODO: move to after-login code
            Amplify.DataStore.observe(
                Lot::class.java,
                { Log.i("Cave", "Observation began.") },
                { Log.i("Cave", it.item().toString()) },
                { Log.e("Cave", "Observation failed.", it) },
                { Log.i("Cave", "Observation complete.") }
            )
    */
        } catch (failure: AmplifyException) {
            Log.e("Cave", "Could not initialize Amplify", failure)
        }

        b.login.setOnClickListener {
            b.loginError.text = getString(R.string.ok)

            val email = b.email.text.toString()
            val password = b.password.text.toString()

            Log.i("Cave", "Authenticating with $email and ${password.length}")

            Amplify.Auth.signIn(
                email, password,
                { result ->
                    if (result.isSignInComplete) {
                        Log.i("Cave", "Sign in succeeded")
                    } else {
                        Log.i("Cave","Sign in not complete")
                        b.loginError.text = getString(R.string.cannot_login)
                    }
                },
                { error ->
                    Log.e("Cave", error.toString())
                    b.loginError.text  = error.message
                }
            )
        }

        b.sorter.setOnClickListener {
            val intent = Intent(this, SorterMainActivity::class.java)
            startActivity(intent)
        }

        b.manager.setOnClickListener {
            val intent = Intent(this, ManagerMainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun textChangeListener() {
        val emailOk = b.email.text.contains("@")
        val passwordOk = b.password.text.isNotEmpty()
        b.login.isEnabled = emailOk && passwordOk
    }
}