package co.gladminds.logistics.ui.auth.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebasechatapp.data.local.SharedPrefs
import com.example.firebasechatapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val sharedPrefs: SharedPrefs by inject()

    private val auth: FirebaseAuth by lazy { auth }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (sharedPrefs.isUserLoggedIn()) {
            navigateToUsers()
        }

        handleUiEvents()
    }

    private fun handleUiEvents() {
        binding.loginBtn.setOnClickListener {
            val email = binding.emailField.editText?.text?.trim().toString()
            val password = binding.passwordField.editText?.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Email & Password required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                navigateToUsers()
            }
            .addOnFailureListener {
                // If user not found, sign up then sign in
                createUser(email, password)
            }
    }

    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                navigateToUsers()
            }
            .addOnFailureListener { e ->
                showToast(e.message ?: "Error")
            }
    }

    private fun navigateToUsers() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToUsersFragment())
    }

    private fun showLoader(show: Boolean) {
        binding.loader.isVisible = show
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
