package com.example.flipcards.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.flipcards.R
import com.example.flipcards.databinding.ActivityCardsInputBinding
import com.example.flipcards.model.CardData
import com.example.flipcards.model.CardSet
import com.example.flipcards.viewmodel.CardsInputViewModel
import com.example.flipcards.viewmodel.CardsInputViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth



private const val MAX_CARDS = 150
private const val TAG = "CardsInputActivity"

class CardsInputActivity : AppCompatActivity() {

    private lateinit var categorySpinner: Spinner
    private lateinit var languageSpinner: Spinner


    private lateinit var cardsInputViewModel: CardsInputViewModel

    private lateinit var binding: ActivityCardsInputBinding
    private lateinit var auth: FirebaseAuth
    private var docId: String? = null

    private lateinit var categoryList: List<String>
    private lateinit var languageList: List<String>
    private lateinit var visibilityList: List<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCardsInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = MyApp.appModule.firebaseAuth
        categoryList = listOf()
        languageList = listOf()
        visibilityList = listOf("Öffentlich", "Privat")

        cardsInputViewModel = ViewModelProvider(this, CardsInputViewModelFactory(MyApp.appModule.cardsInputRepository))[CardsInputViewModel::class.java]

        categorySpinner = binding.categorySpinner
        languageSpinner = binding.languageSpinner


        val toolbar: Toolbar = findViewById(R.id.ic_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)


        docId = intent.getStringExtra("setId")

        if (docId == null) {
            addNewCard("", "")
        } else {
            cardsInputViewModel.loadCardSet(docId!!)
            cardsInputViewModel.cardSet.observe(this, Observer { cardSet ->
                if (cardSet != null) {
                    setCardset(cardSet)
                } else {
                    Log.e(TAG, "Error when loading cardsets")
                }
            })
            categorySpinner.isEnabled = false
            languageSpinner.isEnabled = false
        }

        setSpinnerValues(binding.visibilitySpinner, visibilityList)

        cardsInputViewModel.loadCategories()
        cardsInputViewModel.categories.observe(this, Observer { categories ->
            if (categories != null){
                setSpinnerValues(binding.categorySpinner, categories)
                categoryList = categories

            }
            else {
                Log.e(TAG, "Error when loading categories")
            }
        })

        cardsInputViewModel.loadLanguages()
        cardsInputViewModel.languages.observe(this, Observer { languages ->
            if(languages != null){
                setSpinnerValues(binding.languageSpinner, languages)
                languageList = languages
            }
            else {
                Log.e(TAG, "Error when loading languages")
            }
        })

        cardsInputViewModel.error.observe(this, Observer { error ->
            Log.e(TAG, error)
        })

        binding.addCardButton.setOnClickListener {
            if (binding.cardsContainer.childCount < MAX_CARDS)
                addNewCard("", "")
            else
                Toast.makeText(
                    this,
                    "Es können nur bis zu $MAX_CARDS Karten in einem Set erstellt werden.",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.ci_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            R.id.confirm_ci -> {
                if (binding.cardSetTitle.text.toString().isNotEmpty()) {
                    if (atLeastOneCard() && firstCardFilled()) {
                        saveData()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Mindestens eine Karte muss ausgefüllt werden.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this, "Das Feld Titel darf nicht leer sein.", Toast.LENGTH_SHORT)
                        .show()
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addNewCard(word: String, definition: String) {
        val inflater = LayoutInflater.from(this)
        val cardView =
            inflater.inflate(R.layout.card_item, binding.cardsContainer, false) as CardView

        val wordInputLayout = cardView.findViewById<TextInputLayout>(R.id.word_input_layout)
        val definitionInputLayout =
            cardView.findViewById<TextInputLayout>(R.id.definition_input_layout)
        val wordEditText = cardView.findViewById<TextInputEditText>(R.id.word_edit_text)
        val definitionEditText = cardView.findViewById<TextInputEditText>(R.id.definition_edit_text)

        if (word.isEmpty() && definition.isEmpty()) {
            wordInputLayout.hint = getString(R.string.word)
            definitionInputLayout.hint = getString(R.string.definition)
        } else {
            wordEditText.setText(word)
            definitionEditText.setText(definition)
        }


        binding.cardsContainer.addView(cardView)
        cardView.setOnClickListener { showOptionsDialog(cardView) }
    }

    private fun showOptionsDialog(cardView: CardView) {
        AlertDialog.Builder(this)
            .setTitle("Aktion auswählen")
            .setItems(arrayOf("Löschen")) { _, which ->
                when (which) {
                    0 -> binding.cardsContainer.removeView(cardView)
                }
            }
            .show()
    }

    private fun getAllCardData(): List<CardData> {
        val cardDataList = mutableListOf<CardData>()

        for (i in 0 until binding.cardsContainer.childCount) {
            val cardView = binding.cardsContainer.getChildAt(i) as CardView
            val wordEditText = cardView.findViewById<TextInputEditText>(R.id.word_edit_text)
            val definitionEditText =
                cardView.findViewById<TextInputEditText>(R.id.definition_edit_text)

            val word = wordEditText.text.toString()
            val definition = definitionEditText.text.toString()

            val cardData = CardData(word, definition)
            cardDataList.add(cardData)
        }

        return cardDataList
    }

    private fun firstCardFilled(): Boolean {
        if (binding.cardsContainer.childCount == 0) {
            return false
        }

        val cardView = binding.cardsContainer.getChildAt(0) as CardView
        val wordEditText = cardView.findViewById<TextInputEditText>(R.id.word_edit_text)
        val definitionEditText = cardView.findViewById<TextInputEditText>(R.id.definition_edit_text)
        val word = wordEditText.text.toString()
        val definition = definitionEditText.text.toString()

        return word.isNotEmpty() || definition.isNotEmpty()
    }

    private fun atLeastOneCard(): Boolean {
        return binding.cardsContainer.childCount > 0
    }

    private fun setSpinnerValues(spinner: Spinner, values: List<*>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, values)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun saveData(){

        val title = binding.cardSetTitle.text.toString()
        val description = binding.cardSetDescription.text.toString()
        val category = binding.categorySpinner.selectedItem.toString()
        val language = binding.languageSpinner.selectedItem.toString()
        val visibility = binding.visibilitySpinner.selectedItem.toString()
        val userId = auth.currentUser?.uid.toString()


        if (docId == null) {
            cardsInputViewModel.saveCardSet(true, null, title, description, category, language, visibility, userId, getAllCardData())
        } else {
            cardsInputViewModel.deleteAllCards(docId!!)
            cardsInputViewModel.saveCardSet(false, docId, title, description, category, language, visibility, userId, getAllCardData())
        }

    }

    private fun setCardset(cardSet: CardSet){
        binding.cardSetTitle.setText(cardSet.title)
        binding.cardSetDescription.setText(cardSet.description)
        binding.visibilitySpinner.setSelection(visibilityList.indexOf(cardSet.visibility))
        binding.categorySpinner.setSelection(categoryList.indexOf(cardSet.category))
        binding.languageSpinner.setSelection(languageList.indexOf(cardSet.language))


        for(card in cardSet.cards){
            val word = card.word ?: ""
            val definition = card.definition ?: ""
            addNewCard(word, definition)
        }
    }




}