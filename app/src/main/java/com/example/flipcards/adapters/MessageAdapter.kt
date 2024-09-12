package com.example.flipcards.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flipcards.R
import com.example.flipcards.model.Message

class MessageAdapter(private val messageList: List<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = messageList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]
        if(message.sentBy == Message.SENT_BY_ME){
            holder.leftChatView.visibility = View.GONE
            holder.rightChatView.visibility = View.VISIBLE
            holder.rightTextView.text = message.message
        }
        else{
            holder.rightChatView.visibility = View.GONE
            holder.leftChatView.visibility = View.VISIBLE
            holder.leftTextView.text = message.message
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        internal val leftChatView: LinearLayout = itemView.findViewById(R.id.left_chat_view)
        internal val rightChatView: LinearLayout = itemView.findViewById(R.id.right_chat_view)
        internal val leftTextView: TextView = itemView.findViewById(R.id.left_chat_text_view)
        internal val rightTextView: TextView = itemView.findViewById(R.id.right_chat_text_view)



    }

}