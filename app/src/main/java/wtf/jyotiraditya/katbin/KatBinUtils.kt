/*
 * Copyright (C) 2018 Potato Open Sauce Project
 * Copyright (C) 2021 WaveOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wtf.jyotiraditya.katbin

import android.util.JsonReader
import android.util.JsonWriter
import kotlinx.coroutines.*
import java.io.InputStreamReader
import java.io.StringWriter
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.net.ssl.HttpsURLConnection

/**
 * Helper functions for uploading to KatBin (https://katb.in).
 */
@DelicateCoroutinesApi
object KatBinUtils {

    /**
     * Uploads `content` to KatBin
     *
     * @param content  the content to upload to KatBin
     * @param callback the callback to call on success / failure
     */
    fun upload(content: String, callback: UploadResultCallback) {
        try {
            GlobalScope.launch(Dispatchers.IO) {
                val url = URL("https://api.katb.in/api/paste")
                val urlConnection = url.openConnection() as HttpsURLConnection
                urlConnection.setRequestProperty("Content-Type", "application/json")
                urlConnection.doOutput = true

                val stringWriter = StringWriter()
                val jsonWriter = JsonWriter(stringWriter)
                jsonWriter.beginObject().name("content").value(content).endObject()

                urlConnection.outputStream.use { output ->
                    output.write(stringWriter.toString()
                        .toByteArray(StandardCharsets.UTF_8))
                }

                var id = ""
                JsonReader(InputStreamReader(urlConnection.inputStream,
                    StandardCharsets.UTF_8)).use { reader ->
                    reader.beginObject()
                    while (reader.hasNext()) {
                        if (reader.nextName() == "paste_id") {
                            id = reader.nextString()
                            break
                        } else {
                            reader.skipValue()
                        }
                    }
                    reader.endObject()
                }

                withContext(Dispatchers.Main) {
                    if (id.isNotEmpty()) {
                        callback.onSuccess(String.format("https://katb.in/%s", id))
                    } else {
                        val msg = "Failed to upload to KatBin: No id retrieved"
                        callback.onFail(msg, java.lang.Exception(msg))
                    }
                }
                urlConnection.disconnect()
            }
        } catch (e: java.lang.Exception) {
            val msg = "Failed to upload to KatBin"
            callback.onFail(msg, e)
        }
    }
}