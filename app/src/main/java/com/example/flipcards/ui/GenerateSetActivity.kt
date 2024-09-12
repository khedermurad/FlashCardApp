package com.example.flipcards.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.flipcards.R
import com.example.flipcards.databinding.ActivityGenerateSetBinding
import com.example.flipcards.model.GeneratedData
import com.example.flipcards.viewmodel.CardsInputViewModel
import com.example.flipcards.viewmodel.CardsInputViewModelFactory
import com.google.gson.Gson
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch

class GenerateSetActivity : AppCompatActivity() {

    private lateinit var recognizer: TextRecognizer
    private lateinit var binding: ActivityGenerateSetBinding
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var cardsInputViewModel: CardsInputViewModel
    private var extractedText: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGenerateSetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cardsInputViewModel = ViewModelProvider(
            this,
            CardsInputViewModelFactory(MyApp.appModule.cardsInputRepository)
        )[CardsInputViewModel::class.java]



        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        var image: InputImage


        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val selectedImageUri: Uri? = result.data?.data

                    image = selectedImageUri?.let { InputImage.fromFilePath(this, it) }!!
                    binding.imageView.setImageURI(selectedImageUri)

                    recognizer.process(image)
                        .addOnSuccessListener { visionText ->
                            binding.recognizedTextTv.text = visionText.text
                            extractedText = visionText.text
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Fehler beim Erkennen des Textes: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }

        binding.folderBtn.setOnClickListener {
            openGallery()
        }

        binding.generateCardsBtn.setOnClickListener {
            if (!extractedText.isNullOrEmpty()) {
                generateAndSaveCards(extractedText!!)
            } else {
                Toast.makeText(this, "Kein Text vorhanden", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }


    private fun generateAndSaveCards(text: String) {

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE


        val prompt = """Bitte generiere Karteikarten basierend auf folgendem extrahierten Text:

        "$text"

        Gib die Antwort im JSON-Format zurück. Das JSON-Objekt sollte die folgenden Felder enthalten:
        - "title": Der Titel der Karteikarten-Sammlung
        - "description": Eine kurze Beschreibung der Sammlung
        - "category": Eine der folgenden Kategorien: "Informatik", "Biologie", "Physik", "Chemie", "Medizin", "BWL", "Mathematik", "Sonstiges"
        - "language": Die Sprache des Textes
        - "cards": Eine Liste von Karteikarten, wobei jede Karteikarte ein Objekt mit den Feldern "word" (der Begriff) und "definition" (die Definition) ist.

        Hier ist ein Beispiel, wie das JSON aussehen sollte:

        {
          "title": "Beispiel-Titel",
          "description": "Beispiel-Beschreibung",
          "category": "Informatik",
          "language": "Deutsch",
          "cards": [
           {
            "word": "Begriff 1",
              "definition": "Definition 1"
            },
          {
           "word": "Begriff 2",
             "definition": "Definition 2"
           }
         ]
        }

        Die Karteikarten sollten relevant, prägnant und die Hauptkonzepte des Textes abdecken.
        """.trimIndent()

        lifecycleScope.launch {
            try {
                val response = MyApp.appModule.generativeModel.generateContent(prompt)
                val responseData = response.text

                Log.i("TAG", responseData.toString())

                val generatedData = parseGeneratedData(responseData!!)

                if (generatedData != null) {
                    val userID = MyApp.appModule.firebaseAuth.currentUser?.uid.toString()

                    cardsInputViewModel.saveCardSet(
                        create = true,
                        docId = null,
                        title = generatedData.title,
                        description = generatedData.description,
                        category = generatedData.category,
                        language = generatedData.language,
                        visibility = "Privat",
                        userId = userID,
                        cards = generatedData.cards
                    )

                    Toast.makeText(
                        this@GenerateSetActivity,
                        "Karteikarten-Set generiert und gespeichert.",
                        Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(this@GenerateSetActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@GenerateSetActivity,
                        "Fehler beim Verarbeiten der API-Antwort.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@GenerateSetActivity,
                    "Fehler bei der Kommunikation mit der API: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }finally {
                progressBar.visibility = View.GONE
            }
        }
    }


    private fun parseGeneratedData(responseData: String): GeneratedData? {
        return try {
            val cleanedData = responseData.trim().removeSurrounding("```json", "```").trim()

            Gson().fromJson(cleanedData, GeneratedData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}




