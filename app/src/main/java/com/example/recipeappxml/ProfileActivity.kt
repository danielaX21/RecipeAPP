package com.example.recipeappxml

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileImage: ImageView

    // 1) Launcher pentru CAMERA (intent result)
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                if (imageBitmap != null) {
                    processAndSaveImage(imageBitmap)
                } else {
                    Toast.makeText(this, "Couldn't capture image (no data).", Toast.LENGTH_SHORT).show()
                }
            }
        }

    // 2) Launcher pentru GALERIE
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri ?: return@registerForActivityResult

            // IMPORTANT: use{} => închide stream-ul (repară poza albă)
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    processAndSaveImage(bitmap)
                } else {
                    Toast.makeText(this, "Couldn't load image from gallery.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    // 3) Launcher pentru PERMISIUNEA de CAMERA
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                launchCamera()
            } else {
                Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profileImage = findViewById(R.id.profileImage)
        val tvName = findViewById<TextView>(R.id.tvProfileName)
        val tvCount = findViewById<TextView>(R.id.tvFavCount)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnHome = findViewById<ImageView>(R.id.btnHome)
        val btnFavorites = findViewById<ImageView>(R.id.btnFavorites)

        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        tvName.text = "Chef ${sharedPref.getString("userName", "Chef")}"
        tvCount.text = "Favorite Recipes: ${RecipeFavoritesManager.getFavorites().size}"

        loadSavedImage()

        profileImage.setOnClickListener { showImagePickerDialog() }

        btnHome.setOnClickListener { finish() }
        btnFavorites.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }
        btnLogout.setOnClickListener {
            val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

            // NU mai folosim clear(). Ștergem doar sesiunea numelui dacă vrei,
            // dar cel mai sigur este doar să navigăm spre Login.
            // sharedPref.edit().remove("userName").apply() // Opțional

            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Selfie", "Choose from Gallery", "Cancel")

        AlertDialog.Builder(this)
            .setTitle("Update Profile Picture")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> ensureCameraPermissionAndLaunch()
                    1 -> pickImageLauncher.launch("image/*")
                    else -> dialog.dismiss()
                }
            }
            .show()
    }

    private fun ensureCameraPermissionAndLaunch() {
        val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED

        if (granted) {
            launchCamera()
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Evită crash dacă nu există aplicație de cameră
        if (intent.resolveActivity(packageManager) != null) {
            takePictureLauncher.launch(intent)
        } else {
            Toast.makeText(this, "No camera app found on this device.", Toast.LENGTH_SHORT).show()
        }
    }

    // 2. Salvarea pozei folosind numele utilizatorului în cheie
    private fun processAndSaveImage(bitmap: Bitmap) {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, true)
        profileImage.setImageBitmap(scaledBitmap)
        profileImage.setPadding(0, 0, 0, 0)
        profileImage.scaleType = ImageView.ScaleType.CENTER_CROP

        val baos = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos)
        val encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)

        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        // Preluăm numele utilizatorului curent logat
        val currentUserName = sharedPref.getString("userName", "Chef")

        sharedPref.edit()
            .putString("profileImage_$currentUserName", encodedImage) // Cheie unică: profileImage_NumeUtilizator
            .apply()
    }

    private fun loadSavedImage() {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val currentUserName = sharedPref.getString("userName", "Chef")

        // Încărcăm poza salvată specific pentru acest utilizator
        val encodedImage = sharedPref.getString("profileImage_$currentUserName", null) ?: return

        val imageBytes = Base64.decode(encodedImage, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        if (bitmap != null) {
            profileImage.setImageBitmap(bitmap)
            profileImage.setPadding(0, 0, 0, 0)
            profileImage.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }
}