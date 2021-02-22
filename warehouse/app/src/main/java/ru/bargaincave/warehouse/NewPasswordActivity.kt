package ru.bargaincave.warehouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.amplifyframework.core.Amplify
import ru.bargaincave.warehouse.databinding.ActivityNewPasswordBinding

class NewPasswordActivity : AppCompatActivity() {

    private lateinit var b: ActivityNewPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityNewPasswordBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.newPassword.addTextChangedListener {
            textChangeListener()
        }

        b.newPasswordConfirm.addTextChangedListener {
            textChangeListener()
        }

        b.submitNewPassword.setOnClickListener {
            val password = b.newPassword.text.toString()
            Amplify.Auth.confirmSignIn(password,
                { result ->
                    if (result.isSignInComplete) {
                        Log.i("Cave", "Password changed")
                        val text = getString(R.string.pass_change_ok)
                        val duration = Toast.LENGTH_SHORT
                        val toast = Toast.makeText(applicationContext, text, duration)
                        toast.show()
                        finish()
                    }
                    else {
                        Log.i("Cave", "Unable to set a new password (${result.nextStep})")
                        b.newPassError.text = getString(R.string.new_password_failed)
                    }
                },
                { error ->
                    Log.e("Cave", error.toString())
                    b.newPassError.text = error.message
                }
            )
        }
    }

    private fun textChangeListener() {
        val password = b.newPassword.text.toString()
        val confirm = b.newPasswordConfirm.text.toString()
        b.submitNewPassword.isEnabled = (password == confirm) && password.isNotEmpty()
    }
}
