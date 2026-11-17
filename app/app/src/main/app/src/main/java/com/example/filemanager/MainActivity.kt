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
    private fun buildUi() {
        val bg = Color.rgb(12, 18, 32)
        val panel = Color.rgb(24, 35, 55)
        val accent = Color.rgb(65, 220, 190)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 28, 18, 12)
            setBackgroundColor(bg)
        }

        val header = TextView(this).apply {
            text = "QUẢN LÝ TỆP"
            textSize = 22f
            gravity = Gravity.CENTER
            setTextColor(accent)
        }
        root.addView(header, LinearLayout.LayoutParams(-1, 54))

        pathLabel = TextView(this).apply {
            text = "Chưa chọn thư mục"
            textSize = 13f
            setTextColor(Color.LTGRAY)
            setPadding(10, 10, 10, 10)
            setBackgroundColor(panel)
        }
        root.addView(pathLabel, LinearLayout.LayoutParams(-1, 44))

        val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val choose = Button(this).apply {
            text = "Chọn thư mục"
            setOnClickListener { chooseFolder() }
        }
        val back = Button(this).apply {
            text = "Quay lại"
            setOnClickListener { goBack() }
        }
        actions.addView(choose, LinearLayout.LayoutParams(0, 52, 1f))
        actions.addView(back, LinearLayout.LayoutParams(0, 52, 1f))
        root.addView(actions)

        val scroll = ScrollView(this)
        list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(list)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        val footer = TextView(this).apply {
            text = "Rust native • Android 12 • ARMv7"
            textSize = 11f
            gravity = Gravity.CENTER
            setTextColor(Color.GRAY)
        }
        root.addView(footer, LinearLayout.LayoutParams(-1, 30))
        setContentView(root)
    }

    private fun chooseFolder() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION or
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
            Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        )
        startActivityForResult(intent, PICK_FOLDER)
    }

    @Deprecated("Deprecated by Android but works on Android 12")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FOLDER && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            val flags = data.flags and
                (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            contentResolver.takePersistableUriPermission(uri, flags)
            getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(TREE_URI, uri.toString()).apply()
            currentFolder = DocumentFile.fromTreeUri(this, uri)
            history.clear()
            showFolder()
        }
    }

    private fun showWelcome() {
        pathLabel.text = "Bộ nhớ / Chưa cấp quyền"
        list.removeAllViews()
        addMessage("Hãy nhấn “Chọn thư mục”, sau đó chọn bộ nhớ hoặc thư mục muốn quản lý.")
        addMessage("Android sẽ hỏi quyền truy cập. Ứng dụng chỉ xem được thư mục bạn đã chọn.")
    }
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
