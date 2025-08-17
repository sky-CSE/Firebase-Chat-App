package com.example.firebasechatapp.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasechatapp.data.local.SharedPrefs
import com.example.firebasechatapp.data.model.Message
import com.example.firebasechatapp.databinding.FragmentChatBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.koin.android.ext.android.inject

class ChatFragment : Fragment() {

    private lateinit var receiverId: String
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val args: ChatFragmentArgs by navArgs()
    private val sharedPrefs: SharedPrefs by inject()
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatId: String
    private lateinit var currentUid: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentChatBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUid = sharedPrefs.getUser() ?: return
        receiverId = args.user.uid
        chatId = getChatId(currentUid, receiverId)

        setupUi()
        listenForMessages()
        handleUiEvents()
    }

    private fun setupUi() {
        binding.toolbar.title = args.user.email

        setupRecycler()
    }

    private fun setupRecycler() {
        chatAdapter = ChatAdapter(currentUid)
        binding.chats.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true // start list from bottom
            }
            adapter = chatAdapter
        }
    }

    private fun handleUiEvents() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.sendBtn.setOnClickListener {
            val text = binding.messageField.editText!!.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener

            val msg = Message(
                senderId = currentUid,
                receiverId = receiverId,
                text = text,
                timeStamp = System.currentTimeMillis()
            )

            firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(msg)
                .addOnSuccessListener {
                    binding.messageField.editText!!.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun listenForMessages() {
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timeStamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val messages = snapshot?.toObjects(Message::class.java) ?: emptyList()
                chatAdapter.submitList(messages)
                binding.chats.scrollToPosition(messages.size - 1)
            }
    }

    private fun getChatId(uid1: String, uid2: String): String {
        return listOf(uid1, uid2).sorted().joinToString("_")
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
