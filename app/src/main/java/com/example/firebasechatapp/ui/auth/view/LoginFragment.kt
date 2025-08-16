package com.example.firebasechatapp.ui.auth.view

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebasechatapp.data.local.SharedPrefs
import com.example.firebasechatapp.databinding.FragmentLoginBinding
import com.google.firebase.Firebase
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
            val email = emailField.editText?.text?.trim().toString()
            val password = passwordField.editText?.text.toString()

            if (!isValidInput(email, password)) return@setOnClickListener

            loginUser(email, password)
        }
    }

    private fun isValidInput(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            showToast("Email & Password required"); return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailField.error = "Invalid email"
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
            }
            .addOnFailureListener {
                createUser(email, password)
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
                    }
                    .addOnFailureListener { e -> showToast("Failed to save user: ${e.message}") }
            }
            .addOnFailureListener { e -> showToast(e.message ?: "Error") }
    }

    private fun navigateToUsers() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToUsersFragment())
    }

    private fun showLoader(show: Boolean) {
        binding.loader.isVisible = show
    }

    private fun showToast(message: String) =
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    override fun onDestroyView() {
        _binding = null;
        super.onDestroyView()
    }
}
