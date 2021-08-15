package wtf.jyotiraditya.katbin

interface UploadResultCallback {
    fun onSuccess(url: String)

    fun onFail(
        message: String,
        e: Exception,
    )
}