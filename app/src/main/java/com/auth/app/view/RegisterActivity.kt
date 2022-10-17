package com.auth.app.view

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.auth.app.R
import com.auth.app.data.RegisterBody
import com.auth.app.data.ValidateEmailBody
import com.auth.app.databinding.ActivityRegisterBinding
import com.auth.app.repository.AuthRepository
import com.auth.app.utils.APIService
import com.auth.app.utils.VibratorView
import com.auth.app.view_model.RegisterActivityViewModel
import com.auth.app.view_model.RegisterActivityViewModelFactory

class RegisterActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener,
    View.OnKeyListener, TextWatcher {

    private lateinit var mBinding: ActivityRegisterBinding
    private lateinit var mViewModel: RegisterActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)

        mBinding.fullnametext.onFocusChangeListener = this
        mBinding.emailtext.onFocusChangeListener = this
        mBinding.passwordtext.onFocusChangeListener = this
        mBinding.confirmpasswordtext.onFocusChangeListener = this
        mBinding.confirmpasswordtext.setOnKeyListener(this)
        mBinding.registerbutton.setOnClickListener(this)
        mBinding.confirmpasswordtext.addTextChangedListener(this)

        mViewModel = ViewModelProvider(
            this,
            RegisterActivityViewModelFactory(AuthRepository(APIService.getService()), application)
        )[RegisterActivityViewModel::class.java]
        setupObservers()
    }

    private fun setupObservers() {
        mViewModel.getIsLoading().observe(this) {
            mBinding.progressBar.isVisible = it
        }

        mViewModel.getIsUnique().observe(this) {
            if (validateEmail(false)) {
                if (it) {
                    mBinding.email.apply {
                        if (isErrorEnabled) isErrorEnabled = false
                        setStartIconDrawable(R.drawable.check_circle_24)
                        setStartIconTintList(ColorStateList.valueOf(Color.GREEN))

                    }

                } else {
                    mBinding.email.apply {
                        if (startIconDrawable != null) startIconDrawable = null
                        isErrorEnabled = true
                        error = "Email is already taken"
                    }
                }
            }


        }

        mViewModel.getErrorMessage().observe(this) {
            val formErrorKeys = arrayOf("fullName", "email", "password")
            val message = StringBuilder()

            it.map { entry ->
                if (formErrorKeys.contains(entry.key)) {
                    when (entry.key) {
                        "fullName" -> {
                            mBinding.fullname.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }
                        }
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
                } else {
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

        mViewModel.getUser().observe(this) {
            Log.d("user", "$it")
            if (it != null) {
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }
    }

    private fun validateFullName(shouldVibrateView: Boolean = true): Boolean {
        var errorMessage: String? = null
        val fullName = mBinding.fullnametext.text.toString()

        if (fullName.isEmpty()) {
            errorMessage = "Please provide full name"
        }

        if (errorMessage != null) {
            mBinding.fullname.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibratorView.vibrate(this@RegisterActivity, this)
            }
        }

        return errorMessage == null
    }


    private fun validateEmail(
        shouldUpdateView: Boolean,
        shouldVibrateView: Boolean = true
    ): Boolean {
        var errorMessage: String? = null
        val email = mBinding.emailtext.text.toString()
        if (email.isEmpty()) {
            errorMessage = "Email address is required"
        }
//        else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            errorMessage = "Email address is invalid"
//        }

        if (errorMessage != null && shouldUpdateView) {
            mBinding.email.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibratorView.vibrate(this@RegisterActivity, this)
            }
        }
        return errorMessage == null
    }


    private fun validatePassword(
        shouldUpdateView: Boolean = true,
        shouldVibrateView: Boolean = true
    ): Boolean {
        var errorMessage: String? = null
        val password = mBinding.passwordtext.text.toString()
        if (password.isEmpty()) {
            errorMessage = "Password is required"
        } else if (password.length < 6) {
            errorMessage = "Password is invalid"
        }

        if (errorMessage != null && shouldUpdateView) {
            mBinding.password.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibratorView.vibrate(this@RegisterActivity, this)
            }
        }
        return errorMessage == null
    }


    private fun validateConfirmPassword(
        shouldUpdateView: Boolean = true,
        shouldVibrateView: Boolean = true
    ): Boolean {
        var errorMessage: String? = null
        val password = mBinding.passwordtext.text.toString()
        val confirmPassword = mBinding.confirmpasswordtext.text.toString()
        if (confirmPassword.isEmpty()) {
            errorMessage = "Confirm Password is required"
        } else if (confirmPassword.length < 6) {
            errorMessage = "Confirm password must be more than 6 characters long"
        } else if (confirmPassword != password) {
            errorMessage = "Password does not match"
        }


        if (errorMessage != null && shouldUpdateView) {
            mBinding.confirmpassword.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibratorView.vibrate(this@RegisterActivity, this)
            }
        }
        return errorMessage == null
    }


//    private fun validateConfirmPasswordAndPassword (shouldUpdateView: Boolean = true): Boolean {
//        var errorMessage: String? = null
//        val password = mBinding.passwordtext.text.toString()
//        val confirmPassword = mBinding.confirmpasswordtext.text.toString()
//        if (confirmPassword != password) {
//            errorMessage = "Password does not match"
//        }
//        if (errorMessage != null) {
//            mBinding.confirmpassword.apply {
//                isErrorEnabled = true
//                error = errorMessage
//            }
//        }
//        return errorMessage == null
//    }

    override fun onClick(v: View?) {
        if (v!!.id == R.id.registerbutton) {
            onSubmit()
        }
    }


    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v != null) {
            when (v.id) {
                R.id.fullnametext -> {
                    if (hasFocus) {
                        if (mBinding.fullname.isErrorEnabled) {
                            mBinding.fullname.isErrorEnabled = false
                        }
                    } else {
                        validateFullName()
                    }
                }
                R.id.emailtext -> {
                    if (hasFocus) {

                        if (mBinding.email.isErrorEnabled) {
                            mBinding.email.isErrorEnabled = false
                        }
                    } else {
                        if (validateEmail(true)) {
                            mViewModel.validateEmailAddress(ValidateEmailBody(mBinding.emailtext.text!!.toString()))
                        }


                    }
                }
                R.id.passwordtext -> {
                    if (hasFocus) {
                        if (mBinding.password.isErrorEnabled) {
                            mBinding.password.isErrorEnabled = false
                        }
                    } else {
                        if (validatePassword() && mBinding.confirmpasswordtext.text!!.isNotEmpty()
                            && validateConfirmPassword()
                        ) {
                            if (mBinding.confirmpassword.isErrorEnabled) {
                                mBinding.confirmpassword.isErrorEnabled = false
                            }
                        }
                    }
                }
                R.id.confirmpasswordtext -> {
                    if (hasFocus) {
                        if (mBinding.confirmpassword.isErrorEnabled) {
                            mBinding.confirmpassword.isErrorEnabled = false
                        }
                    } else {
                        if (validateConfirmPassword() && validatePassword()) {
                            if (mBinding.password.isErrorEnabled) {
                                mBinding.password.isErrorEnabled = false
                            }
                        }
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

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (validatePassword(shouldUpdateView = false) && validateConfirmPassword(shouldUpdateView = false)) {
            mBinding.confirmpassword.apply {
                if (isErrorEnabled) isErrorEnabled = false
                setStartIconDrawable(R.drawable.check_circle_24)
                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
            }
        } else {
            mBinding.confirmpassword.apply {
                if (startIconDrawable != null) startIconDrawable = null
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {}

    private fun onSubmit() {
        if (validateForm()) {
            mViewModel.registerUser(
                RegisterBody(
                    mBinding.fullnametext.text!!.toString(), mBinding.emailtext.text!!.toString(),
                    mBinding.passwordtext.text!!.toString()
                )
            )
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (!validateFullName(shouldVibrateView = false)) isValid = false
        if (!validateEmail(true, shouldVibrateView = false)) isValid = false
        if (!validatePassword(shouldVibrateView = false)) isValid = false
        if (!validateConfirmPassword(shouldVibrateView = false)) isValid = false
        if (isValid && !validateConfirmPassword(shouldVibrateView = false)) isValid = false

        if (!isValid) VibratorView.vibrate(this, mBinding.cardView)


        return isValid
    }
}