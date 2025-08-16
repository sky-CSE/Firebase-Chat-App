package com.example.firebasechatapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebasechatapp.MainActivity
import com.example.firebasechatapp.data.local.SharedPrefs
import com.example.firebasechatapp.data.model.User
import com.example.firebasechatapp.databinding.FragmentUsersBinding
import com.example.firebasechatapp.databinding.ListItemUserBinding
import com.example.firebasechatapp.utils.CommonAdapter
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.android.inject

class UsersFragment : Fragment() {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private val sharedPrefs: SharedPrefs by inject()
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    private lateinit var usersAdapter: CommonAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentUsersBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRv()
        fetchUsers()
        handleUiEvents()
    }

    private fun handleUiEvents() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

        binding.logoutBtn.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        sharedPrefs.clearAll()
        (requireActivity() as MainActivity).finish()
    }

    private fun initRv() {
        usersAdapter = CommonAdapter(ListItemUserBinding::inflate) { itemBinding, item, position ->
            if (itemBinding is ListItemUserBinding && item is User) {
                with(itemBinding) {
                    userEmail.text = item.email

                    root.setOnClickListener {
                        findNavController().navigate(UsersFragmentDirections.actionUsersFragmentToChatFragment())
                    }
                }
            }
        }

        binding.list.apply {
            adapter = usersAdapter
            setHasFixedSize(true)
            addItemDecoration(
                MaterialDividerItemDecoration(
                    context,
                    MaterialDividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun fetchUsers() {
        val currentUid = sharedPrefs.getUser()

        firestore.collection("users")
            .get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.toObjects(User::class.java)
                    .filter { it.uid != currentUid } // exclude myself

                usersAdapter.submitList(users)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
    }
}