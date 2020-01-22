package thunpisit.example.flickrbrowers

import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

enum class DownloadStatus{
    OK,IDLE,NOT_INITIALIZED,FAILED_OR_EMPTY,PERMISSION_ERROR,ERROR
}
class GetRawData(private val listener: OnDownloadComplete) : AsyncTask<String,Void,String>() {
    private val TAG = "GetRawData"
    private var downloadStatus = DownloadStatus.IDLE

    //Callbacks
    interface OnDownloadComplete{
        fun onDownloadComplete(data:String,status: DownloadStatus)
    }
    //C-Callbacks

    override fun onPostExecute(result: String) {
        Log.d(TAG,"onPostExecute called")
        listener.onDownloadComplete(result,downloadStatus)
    }

    override fun doInBackground(vararg params: String?): String {
        if(params[0]==null){
            downloadStatus = DownloadStatus.NOT_INITIALIZED
            return "No URL specified"
        }

        try{
            downloadStatus = DownloadStatus.OK
            return URL(params[0]).readText()
        }catch (e: Exception){
            val errorMessage = when (e){
                is MalformedURLException -> {
                    downloadStatus = DownloadStatus.NOT_INITIALIZED
                    "doInBackground: Invalid URL ${e.message}"
                }
                is IOException -> {
                    downloadStatus = DownloadStatus.FAILED_OR_EMPTY
                    "doInBackground: IO Exception reading data ${e.message}"
                }
                is SecurityException -> {
                    downloadStatus = DownloadStatus.PERMISSION_ERROR
                    "doInBackground: Security exception: Need permission? ${e.message}"
                }
                else -> {
                    downloadStatus = DownloadStatus.ERROR
                    "Unknown error: ${e.message}"
                }
            }
            Log.e(TAG, errorMessage)
            return errorMessage
        }
    }
}