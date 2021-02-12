package ru.bargaincave.warehouse

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.addTextChangedListener
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.options.AuthSignOutOptions
import com.amplifyframework.auth.result.step.AuthSignInStep
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import ru.bargaincave.warehouse.databinding.ActivityCognitoLoginBinding

class CognitoLoginActivity : AppCompatActivity() {

    private lateinit var b: ActivityCognitoLoginBinding
    private var signedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityCognitoLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        getPreferences(Context.MODE_PRIVATE).also {
            it.getString(getString(R.string.user_email_key), "").also { value ->
                b.email.setText(value ?: "")
            }
        }

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
            Amplify.addPlugin(AWSS3StoragePlugin())
            Amplify.configure(applicationContext)
            Log.i("Cave", "Initialized Amplify")

            Amplify.Auth.fetchAuthSession(
                { result ->
                    signedIn = result.isSignedIn
                    Log.i("Cave", "Signed in: $signedIn")
                    updateGUI()
                },
                { error ->
                    Log.i("Cave", "Unable to fetch authentication session")
                    runOnUiThread {
                        b.loginError.text = error.message
                    }
                }
            )

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

            // TODO: move to successful login
            getPreferences(Context.MODE_PRIVATE).also {
                with(it.edit()) {
                    val email = b.email.text.toString()
                    putString(getString(R.string.user_email_key), email)
                    apply()
                }
            }

            val email = b.email.text.toString()
            val password = b.password.text.toString()

            Log.i("Cave", "Authenticating with $email and ${password.length}")

            Amplify.Auth.signIn(
                email, password,
                { result ->
                    if (result.isSignInComplete) {
                        Log.i("Cave", "Sign in succeeded")
                        signedIn = true
                        updateGUI()
                    } else {
                        Log.i("Cave", "Sign in not complete - ${result.nextStep.signInStep}")
                        signedIn = false
                        updateGUI()

                        if (result.nextStep.signInStep == AuthSignInStep.CONFIRM_SIGN_IN_WITH_NEW_PASSWORD) {
                            val intent = Intent(this, NewPasswordActivity::class.java)
                            startActivity(intent)
                        } else {
                            runOnUiThread {
                                b.loginError.text = getString(R.string.cannot_login)
                            }
                        }
                    }
                },
                { error ->
                    Log.e("Cave", error.toString())
                    runOnUiThread {
                        b.loginError.text = error.message
                    }
                }
            )
        }

        b.logout.setOnClickListener {
            Log.i("Cave", "Logging out")
            Amplify.Auth.signOut(
                AuthSignOutOptions.builder().globalSignOut(true).build(),
                {
                    Log.i("Cave", "Signed out")
                    signedIn = false
                    updateGUI()
                },
                { error ->
                    Log.e("Cave", error.toString())
                    runOnUiThread {
                        b.loginError.text = error.message
                    }
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

    private fun updateGUI() {
        runOnUiThread {
            b.email.isEnabled = !signedIn
            b.password.isEnabled = !signedIn
            b.login.isEnabled = !signedIn
            b.logout.isEnabled = signedIn
            b.sorter.isEnabled = signedIn
            b.manager.isEnabled = false
        }
    }
}
