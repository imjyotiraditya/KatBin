package wtf.jyotiraditya.katbin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.DelicateCoroutinesApi
import wtf.jyotiraditya.katbin.databinding.ActivityMainBinding

@DelicateCoroutinesApi
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.button.setOnClickListener {
            postToKatBinAndCopyURL(
                binding.editText.text.toString()
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

                Snackbar.make(binding.mainContainer, "Copied to ClipBoard", Snackbar.LENGTH_LONG)
                    .show()
            }

            override fun onFail(message: String, e: Exception) {
                Log.e("Utils", message, e)
            }
        })
    }
}