package com.example.flipcards.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flipcards.R
import com.example.flipcards.ui.SetsCollectionActivity
import com.example.flipcards.viewmodel.CollAdapterViewModel

private const val TAG = "CollectionsAdapter"

class CollectionsAdapter(
    val context: Context,
    private var collectionIds:
    List<String>, val uid: String,
    private val collAdapterViewModel: CollAdapterViewModel
) : RecyclerView.Adapter<CollectionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.collection_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = collectionIds.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(collectionIds[position])
    }

    fun updateCollections(newCollectionIds: List<String>?) {
        if (newCollectionIds != null) {
            collectionIds = newCollectionIds
        }
        notifyDataSetChanged()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.collectionTitle)
        private  val settingsButton: ImageButton = itemView.findViewById(R.id.collSettingsBtn)
        private var collectionTitle = ""

        fun bind(collectionId: String) {

            collAdapterViewModel.getCollectionTitle(uid, collectionId, { collTitle ->
                title.text = collTitle
                collectionTitle = collTitle
            },{ e ->
                Log.e(TAG, "Error when loading Collection title:", e)
            })

            itemView.setOnClickListener{
                val intent = Intent(context, SetsCollectionActivity::class.java)
                intent.putExtra("title", collectionTitle)
                intent.putExtra("collectionId", collectionId)
                context.startActivity(intent)
            }

            settingsButton.setOnClickListener{ view ->
                showPopupMenu(view, collectionId)
            }
        }

        private fun showPopupMenu(view: View, collectionId: String){
            val popupMenu = PopupMenu(context, view)
            popupMenu.inflate(R.menu.collection_item_menu)

            popupMenu.setOnMenuItemClickListener { item ->

                when(item.itemId){
                    R.id.action_edit_coll ->{
                        editCollectionName(uid, collectionId)
                        true
                    }
                    R.id.action_delete_coll ->{
                        AlertDialog.Builder(context)
                            .setTitle("Löschen bestätigen")
                            .setMessage("Sind Sie sicher, dass Sie diese Collection löschen möchten?")
                            .setPositiveButton("Ja") { dialog, which ->
                                collAdapterViewModel.deleteCollection(uid, collectionId,
                                    {
                                        val position = collectionIds.indexOf(collectionId)
                                        if (position != -1) {
                                            collectionIds = collectionIds.toMutableList().apply { removeAt(position) }
                                            notifyItemRemoved(position)
                                        }
                                        Log.i(TAG, "Document and subcollections have been successfully deleted.")
                                    },{ e ->
                                        Log.e(TAG, "Error when deleting the document or subcollections: $e")
                                    })
                            }
                            .setNegativeButton("Nein", null)
                            .show()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        private fun editCollectionName(uid: String, collectionId: String) {

            collAdapterViewModel.getCollectionTitle(uid, collectionId, { collTitle ->
                val editText = EditText(context)
                editText.setText(collTitle)

                AlertDialog.Builder(context)
                    .setTitle("Edit Collection Name")
                    .setView(editText)
                    .setPositiveButton("OK") { dialog, which ->
                        val newTitle = editText.text.toString().trim()
                        if (newTitle.isNotEmpty()) {
                            collAdapterViewModel.updateCollectionTitle(uid, collectionId, newTitle,
                                {
                                    title.text = newTitle
                                    Log.i(TAG, "Collection title successfully updated.")
                                },{ e ->
                                    Log.e(TAG, "Error updating collection title: ", e)
                                })
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }, {

            })

        }


    }

}