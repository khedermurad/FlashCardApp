package com.example.flipcards.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flipcards.R
import com.example.flipcards.adapters.MessageAdapter
import com.example.flipcards.databinding.FragmentProfileBinding
import com.example.flipcards.model.Message
import kotlinx.coroutines.launch


class ChatbotFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var messageList: MutableList<Message> = arrayListOf()

    private lateinit var adapter: MessageAdapter

    private var alertDialog: AlertDialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        messageList = arrayListOf()

        adapter = MessageAdapter(messageList)
        binding.chatbotRv.adapter = adapter
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        binding.chatbotRv.layoutManager = layoutManager


        binding.sendBtn.setOnClickListener{
            val question = binding.messageEditText.text.toString().trim()
            addToChat(question, Message.SENT_BY_ME)
            binding.messageEditText.text.clear()
            callAPI(question)
        }

        binding.chatbotMenu.setOnClickListener{
            val dialogView = LayoutInflater.from(context).inflate(R.layout.popup_chatbot, null)
            alertDialog = AlertDialog.Builder(context).setView(dialogView).create()

            dialogView.findViewById<TextView>(R.id.generateSetTv).setOnClickListener{
                val intent = Intent(context, GenerateSetActivity::class.java)
                startActivity(intent)
            }

            alertDialog?.show()
        }

    }

    private fun addToChat(message: String, sentBy: String) {
        requireActivity().runOnUiThread {
            messageList.add(Message(message, sentBy))
            adapter.notifyDataSetChanged()
            binding.chatbotRv.smoothScrollToPosition(adapter.itemCount -1)
        }
    }

    private fun callAPI(question: String) {
        addToChat("...", Message.SENT_BY_BOT)

        lifecycleScope.launch {
            val response = MyApp.appModule.generativeModel.generateContent(question)

            messageList.removeAt(messageList.size - 1)
            adapter.notifyItemRemoved(adapter.itemCount - 1)

            addToChat(response.text.toString(), Message.SENT_BY_BOT)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        alertDialog?.dismiss()
    }

}