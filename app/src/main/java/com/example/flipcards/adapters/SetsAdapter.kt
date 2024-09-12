package com.example.flipcards.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.InputType
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.RecyclerView
import com.example.flipcards.R
import com.example.flipcards.model.Review
import com.example.flipcards.model.Set
import com.example.flipcards.ui.CardTestActivity
import com.example.flipcards.ui.CardsInputActivity
import com.example.flipcards.ui.CardsViewActivity
import com.example.flipcards.ui.QuizActivity
import com.example.flipcards.viewmodel.SetsViewModel

private const val TAG = "SetsAdapter"

class SetsAdapter(
    val context: Context,
    private var sets: List<Set>,
    private var docIds: List<String>,
    val uid: String,
    val className: String,
    val collectionId: String?,
    private val setsViewModel: SetsViewModel
) : RecyclerView.Adapter<SetsAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = sets.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < sets.size && position < docIds.size) {
            holder.bind(sets[position], docIds[position])
        }
    }

    fun updateSets(newSets: List<Set>, newDocIds: List<String>) {
        sets = newSets
        docIds = newDocIds
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.titleRvItem)
        private val description: TextView = itemView.findViewById(R.id.descrRvItem)
        private val date: TextView = itemView.findViewById(R.id.dateRvItem)
        private val category: TextView = itemView.findViewById(R.id.categoryRvItem)
        private val ratingBar: AppCompatRatingBar = itemView.findViewById(R.id.ratingRvItem)
        private val imageButton: ImageButton = itemView.findViewById(R.id.menuRvItem)

        fun bind(set: Set, docId: String) {
            title.text = set.title
            description.text = set.description
            date.text = DateUtils.getRelativeTimeSpanString(set.creationTimeMs)
            category.text = set.category

            setsViewModel.getReviews(docId, { reviews ->
                val avgRating = reviews.map { it.rating }.average().toFloat()
                ratingBar.rating = avgRating
            }, { exception ->
                Log.e(TAG, "Error when retrieving the reviews", exception)
                ratingBar.rating = 0f
            })

            imageButton.setOnClickListener { view ->
                showPopupMenu(view, set, docId)
            }

            itemView.setOnClickListener { view ->
                showPopupWindow(view, docId)
            }
        }


        private fun showPopupMenu(view: View, set: Set, docId: String) {
            val popupMenu = PopupMenu(context, view)
            popupMenu.inflate(R.menu.set_item_menu)


            if (set.userId == uid) {
                popupMenu.menu.findItem(R.id.action_edit).isVisible = true
                popupMenu.menu.findItem(R.id.action_delete).isVisible = true
                if (className == "SetsCollectionActivity") {
                    popupMenu.menu.findItem(R.id.action_save).isVisible = false
                    popupMenu.menu.findItem(R.id.action_remove_from_collection).isVisible = true
                } else {
                    popupMenu.menu.findItem(R.id.action_save).isVisible = true
                    popupMenu.menu.findItem(R.id.action_remove_from_collection).isVisible = false
                }
                popupMenu.menu.findItem(R.id.action_rate).isVisible = false
            } else {
                popupMenu.menu.findItem(R.id.action_edit).isVisible = false
                popupMenu.menu.findItem(R.id.action_delete).isVisible = false
                if (className == "SetsCollectionActivity") {
                    popupMenu.menu.findItem(R.id.action_save).isVisible = false
                    popupMenu.menu.findItem(R.id.action_remove_from_collection).isVisible = true
                } else {
                    popupMenu.menu.findItem(R.id.action_save).isVisible = true
                    popupMenu.menu.findItem(R.id.action_remove_from_collection).isVisible = false
                }
                popupMenu.menu.findItem(R.id.action_rate).isVisible = true
            }

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        val intent = Intent(context, CardsInputActivity::class.java)
                        intent.putExtra("setId", docId)
                        context.startActivity(intent)
                        true
                    }
                    R.id.action_delete -> {
                        AlertDialog.Builder(context)
                            .setTitle("Löschen bestätigen")
                            .setMessage("Sind Sie sicher, dass Sie dieses Set löschen möchten?")
                            .setPositiveButton("Ja") { dialog, which ->
                                setsViewModel.deleteDocumentWithSubcollections(docId){
                                    val position = sets.indexOf(set)
                                    if (position != -1) {
                                        sets = sets.toMutableList().apply { removeAt(position) }
                                        docIds = docIds.toMutableList().apply { removeAt(position) }
                                        notifyItemRemoved(position)
                                    }
                                }
                            }
                            .setNegativeButton("Nein", null)
                            .show()
                        true
                    }
                    R.id.action_save -> {
                        showCollections(uid, docId)
                        true
                    }
                    R.id.action_remove_from_collection ->{
                        if(!collectionId.isNullOrEmpty()){
                            setsViewModel.deleteDocIdFromCollection(uid, collectionId, docId,
                                {
                                    val position = sets.indexOf(set)
                                    if(position != -1){
                                        sets = sets.toMutableList().apply { removeAt(position) }
                                        docIds = docIds.toMutableList().apply { removeAt(position) }
                                        notifyItemRemoved(position)
                                    }
                                },
                                { exception ->
                                    Log.e(TAG, "Error when removing set from collection: ",exception)
                                }
                            )
                        }
                        true
                    }
                    R.id.action_rate -> {
                        setsViewModel.alreadyRated(docId, uid) { result ->
                            if (result) {
                                Toast.makeText(context, "Sie haben dieses Set bereits bewertet.", Toast.LENGTH_SHORT).show()
                            } else {
                                showRatingDialog(docId)
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        private fun showPopupWindow(view: View, docId: String) {
            val layoutInflater = LayoutInflater.from(context)
            val popupView = layoutInflater.inflate(R.layout.popup_window_item, null)

            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            popupWindow.width = 800
            popupWindow.height = 400

            val actionLearn: TextView = popupView.findViewById(R.id.learn_cards)
            val actionQuiz: TextView = popupView.findViewById(R.id.quiz_cards)
            val actionTest: TextView = popupView.findViewById(R.id.test_cards)

            actionLearn.setOnClickListener{
                val intent = Intent(context, CardsViewActivity::class.java)
                intent.putExtra("setId", docId)
                context.startActivity(intent)

                popupWindow.dismiss()
            }

            actionQuiz.setOnClickListener{
                val intent = Intent(context, QuizActivity::class.java)
                intent.putExtra("setId", docId)
                context.startActivity(intent)
                popupWindow.dismiss()
            }

            actionTest.setOnClickListener{
                val intent = Intent(context, CardTestActivity::class.java)
                intent.putExtra("setId", docId)
                context.startActivity(intent)

                popupWindow.dismiss()
            }

            popupWindow.showAtLocation(view, android.view.Gravity.CENTER, 0, 0)
        }


        private fun showRatingDialog(docId: String) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rate, null)
            val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)

            AlertDialog.Builder(context)
                .setTitle("Set bewerten")
                .setView(dialogView)
                .setPositiveButton("Speichern") { dialog, which ->
                    val rating = ratingBar.rating
                    val review = Review(uid, rating)
                    setsViewModel.saveReview(docId, review, {
                        Toast.makeText(context, "Bewertung gespeichert", Toast.LENGTH_SHORT).show()
                    }, { e ->
                        Log.e(TAG, "Error when saving the reviews", e)
                    })
                }
                .setNegativeButton("Abbrechen", null)
                .show()
        }

        private fun showCollections(userId: String, docId: String) {
            setsViewModel.getUserCollections(userId,{ collections ->
                if (collections.isEmpty()) {
                    displayDialog(emptyArray(), emptyArray(), docId, userId)
                } else {
                    collections.forEach { (titles, collectionIds) ->
                        displayDialog(titles, collectionIds, docId, userId)
                    }
                }
            }, { exception ->
                Log.e(TAG, "Failed to load collections", exception)
            })
        }

        private fun displayDialog(titles: Array<String>, collectionIds: Array<String>, docId: String, userId: String) {
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Collection auswählen")
            builder.setItems(titles) { dialog, which ->
                val selectedCollectionId = collectionIds[which]
                setsViewModel.saveDocumentToCollection(userId, selectedCollectionId, docId, {
                    Toast.makeText(context, "Dokument erfolgreich gespeichert", Toast.LENGTH_SHORT).show()
                },{
                    Toast.makeText(context, "Dokument wurde bereits gespeichert", Toast.LENGTH_SHORT).show()
                }, {exception ->
                    Toast.makeText(context, "Fehler beim Speichern des Dokuments: ${exception.message}", Toast.LENGTH_SHORT).show()
                })
            }
            builder.setNeutralButton("NEUE COLLECTION") { dialog, which ->
                showNewCollectionDialog(userId, docId)
            }

            builder.show()
        }

        private fun showNewCollectionDialog(userId: String, docId: String) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Neue Collection")

            val input = EditText(context)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)

            builder.setPositiveButton("OK") { dialog, which ->
                val title = input.text.toString()
                if (title.isNotEmpty()) {
                    setsViewModel.createNewCollection(userId, docId, title, {
                        Toast.makeText(context, "Collection und Dokument erfolgreich erstellt", Toast.LENGTH_SHORT).show()
                    },{ e ->
                        Toast.makeText(context, "Fehler beim Speichern des Dokuments: ${e.message}", Toast.LENGTH_SHORT).show()
                    })
                } else {
                    Toast.makeText(context, "Titel darf nicht leer sein", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Abbrechen") { dialog, which ->
                dialog.cancel()
            }

            builder.show()
        }

    }
}

