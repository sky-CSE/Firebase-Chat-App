package com.example.firebasechatapp.ui.auth.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebasechatapp.data.local.SharedPrefs
import com.example.firebasechatapp.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.android.inject

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val sharedPrefs: SharedPrefs by inject()
    private val auth by lazy { Firebase.auth }
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentLoginBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Skip login if already logged in
        if (sharedPrefs.getUser() != null) navigateToUsers()

        handleUiEvents()
    }

    private fun handleUiEvents() = with(binding) {
        loginBtn.setOnClickListener {
            hideKeyboard()

            val email = emailField.editText?.text?.trim().toString()
            val password = passwordField.editText?.text.toString()
            if (!isValidInput(email, password)) return@setOnClickListener

            loginUser(email, password)
        }

        emailField.editText!!.doAfterTextChanged {
            if (emailField.isErrorEnabled) emailField.isErrorEnabled = false
        }

        passwordField.editText!!.doAfterTextChanged {
            if (passwordField.isErrorEnabled) passwordField.isErrorEnabled = false
        }
    }

    private fun isValidInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.emailField.error = "Can't be empty"
            return false
        }

        if (password.isEmpty()) {
            binding.passwordField.error = "Can't be empty"
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailField.error = "Invalid email"
            return false
        }

        if(password.length < 6){
            binding.passwordField.error = "Password must be at least 6 characters long"
            return false
        }

        return true
    }

    private fun loginUser(email: String, password: String) {
        showLoader(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                sharedPrefs.saveUser(uid)
                navigateToUsers()
                showSuccess("Logged In Successfully")
            }
            .addOnFailureListener { e ->
                if (e is FirebaseAuthException) {
                    when (e.errorCode) {
                        "ERROR_INVALID_CREDENTIAL" -> {
                            createUser(email, password)
                        }

                        else -> {
                            showError("Login failed: ${e.message}")
                        }
                    }
                } else {
                    showError("Something went wrong: ${e.message}")
                }
            }
            .addOnCompleteListener { showLoader(false) }
    }

    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                val userMap = mapOf("uid" to uid, "email" to email, "password" to password)

                firestore.collection("users").document(uid)
                    .set(userMap)
                    .addOnSuccessListener {
                        sharedPrefs.saveUser(uid)
                        navigateToUsers()
                        showSuccess("Signed-up Successfully")
                    }
                    .addOnFailureListener { e ->
                        showError("Failed to save user: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                showError(e.message ?: "Error")
            }
    }

    private fun navigateToUsers() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToUsersFragment())
    }

    private fun showLoader(show: Boolean) {
        binding.loader.isVisible = show
    }

    private fun showError(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))
            .setTextColor(Color.WHITE)
            .show()
    }

    private fun showSuccess(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), android.R.color.holo_green_light))
            .setTextColor(Color.WHITE)
            .show()
    }

    private fun showToast(message: String) =
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    override fun onDestroyView() {
        _binding = null;
        super.onDestroyView()
    }

    fun Fragment.hideKeyboard() {
        view?.let { v ->
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }
}
