package com.example.filemanager

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.documentfile.provider.DocumentFile

class MainActivity : Activity() {
    companion object {
        init { System.loadLibrary("filecore") }
        private const val PICK_FOLDER = 701
        private const val PREFS = "file_manager"
        private const val TREE_URI = "tree_uri"
    }

    private external fun formatSize(bytes: Long): String

    private lateinit var list: LinearLayout
    private lateinit var pathLabel: TextView
    private var currentFolder: DocumentFile? = null
    private val history = ArrayList<DocumentFile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()

        val saved = getSharedPreferences(PREFS, MODE_PRIVATE).getString(TREE_URI, null)
        if (saved != null) {
            try {
                val uri = Uri.parse(saved)
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                currentFolder = DocumentFile.fromTreeUri(this, uri)
                showFolder()
            } catch (e: Exception) {
                showWelcome()
            }
        } else {
            showWelcome()
        }
    }
