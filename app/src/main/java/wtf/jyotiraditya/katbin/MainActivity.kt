package wtf.jyotiraditya.katbin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import wtf.jyotiraditya.katbin.KatBinUtils.UploadResultCallback

class MainActivity : AppCompatActivity() {

    private var editText: EditText? = null
    private var button: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editText = findViewById(R.id.editText)
        button = findViewById(R.id.button)
        button?.setOnClickListener {
            postToKatBinAndCopyURL(
                editText?.text.toString()
            )
        }
    }

    private fun postToKatBinAndCopyURL(content: String) {
        // Post to KatBin
        KatBinUtils.upload(content, object : UploadResultCallback {
            override fun onSuccess(url: String) {
                // Copy to clipboard
                val clipboard: ClipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Log URL", url))

                Toast.makeText(this@MainActivity, "Copied to ClipBoard", Toast.LENGTH_LONG).show()
            }

            override fun onFail(message: String, e: Exception) {
                Log.e("Utils", message, e)
            }
        })
    }
}