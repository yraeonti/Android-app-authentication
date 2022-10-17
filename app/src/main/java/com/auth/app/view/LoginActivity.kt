package com.auth.app.view

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.auth.app.R
import com.auth.app.data.LoginBody
import com.auth.app.databinding.ActivityLoginBinding
import com.auth.app.databinding.ActivityRegisterBinding
import com.auth.app.repository.AuthRepository
import com.auth.app.utils.APIService
import com.auth.app.utils.VibratorView
import com.auth.app.view_model.LoginActivityViewModel
import com.auth.app.view_model.LoginActivityViewModelFactory

class LoginActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener,
View.OnKeyListener {

    private lateinit var mBinding: ActivityLoginBinding
    private lateinit var mViewModel: LoginActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         mBinding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)

        mBinding.emailtext.onFocusChangeListener = this
        mBinding.passwordtext.onFocusChangeListener = this
        mBinding.loginbutton.setOnClickListener(this)

        mViewModel = ViewModelProvider(this, LoginActivityViewModelFactory(AuthRepository(APIService.getService()), application))[LoginActivityViewModel::class.java]


       setObservers()
    }

    private fun setObservers() {
        mViewModel.getIsLoading().observe(this){
            mBinding.progressBar.isVisible = it
        }

        mViewModel.getErrorMessage().observe(this){
            val formErrorKeys = arrayOf("email", "password")
            val message = StringBuilder()

            it.map { entry ->
                if (formErrorKeys.contains(entry.key)) {
                    when(entry.key) {
                        "email" -> {
                            mBinding.email.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                        "password" -> {
                           mBinding.password.apply {
                               isErrorEnabled = true
                               error = entry.value
                           }
                        }
                    }
                }else{
                    message.append(entry.value).append("/n")
                }
                if (message.isNotEmpty()) {
                    AlertDialog.Builder(this)
                        .setIcon(R.drawable.info_24)
                        .setTitle("Informacion")
                        .setMessage(message)
                        .setPositiveButton("OK") { dialog, _ -> dialog!!.dismiss() }
                        .show()
                }
            }
        }


        mViewModel.getUser().observe(this){
            if (it != null) {
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }

    }

    private fun validateEmail(shouldUpdateView : Boolean = true, shouldVibrateView: Boolean = true): Boolean {
        var errorMessage: String? = null
        var email = mBinding.emailtext.text.toString()
        if (email.isEmpty()) {
            errorMessage = "Email address is required"
        }

        if (errorMessage != null && shouldUpdateView) {
            mBinding.email.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibratorView.vibrate(this@LoginActivity, this)
            }
        }

        return errorMessage == null
    }

    private fun validatePassword(shouldUpdateView : Boolean = true, shouldVibrateView: Boolean = true): Boolean {
        var errorMessage: String? = null
        var password = mBinding.passwordtext.text.toString()
        if(password.isEmpty()) {
            errorMessage = "Password is required"
        }
        else if(password.length < 6) {
            errorMessage = "Password is invalid"
        }

        if (errorMessage != null && shouldUpdateView) {
            mBinding.password.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibratorView.vibrate(this@LoginActivity, this)
            }
        }


        return errorMessage == null
    }




    override fun onClick(v: View?) {
        if (v!!.id == R.id.loginbutton) {
            onSubmit()
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if(v != null) {
            when(v.id) {
                R.id.emailtext -> {
                    if (hasFocus) {
                        mBinding.email.apply {
                            if(isErrorEnabled) isErrorEnabled = false
                        }
                    }else {
                        validateEmail()
                    }
                }
                R.id.passwordtext -> {
                    if (hasFocus) {
                        mBinding.password.apply {
                            if(isErrorEnabled) isErrorEnabled = false
                        }
                    }else {
                        validatePassword()
                    }
                }
            }
        }
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (KeyEvent.KEYCODE_ENTER == keyCode && event!!.action == KeyEvent.ACTION_UP) {
            onSubmit()
        }
        return false
    }

    private fun onSubmit() {
      if (validate()) {
          mViewModel.loginUser(LoginBody(mBinding.emailtext.text!!.toString() , mBinding.passwordtext.text!!.toString()))
      }
    }

    private fun validate(): Boolean {
        var isValid : Boolean = true

        if(!validateEmail(shouldVibrateView = false)) isValid = false
        if (!validatePassword(shouldVibrateView = false)) isValid = false
        if(isValid && !validatePassword(shouldVibrateView = false)) isValid = false

        if (!isValid) VibratorView.vibrate(this, mBinding.cardView)

        return isValid
    }


}