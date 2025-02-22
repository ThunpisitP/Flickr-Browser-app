package thunpisit.example.flickrbrowers

import android.os.AsyncTask
import android.util.Log
import org.json.JSONException
import org.json.JSONObject

class GetFlickrJsonData(private val listener: OnDataAvailable):AsyncTask<String,Void,ArrayList<Photo>>() {
    private val TAG = "GetFlickrJsonData"

    interface OnDataAvailable{
        fun onDataAvailable(data:List<Photo>)
        fun onError(exception: Exception)
    }

    override fun doInBackground(vararg params: String?): ArrayList<Photo> {
        Log.d(TAG,"doInBackground starts")

        val photoList = ArrayList<Photo>()
        try{
            val jsonData = JSONObject(params[0])
            val itemArray = jsonData.getJSONArray("items")

            for(i in 0 until itemArray.length()){
                val jsonPhoto = itemArray.getJSONObject(i)
                val title = jsonPhoto.getString("title")
                val author = jsonPhoto.getString("author")
                val authorId = jsonPhoto.getString("author_id")
                val tags = jsonPhoto.getString("tags")

                val jsonMedia = jsonPhoto.getJSONObject("media")
                val photoUrl = jsonMedia.getString("m")
                val link = photoUrl.replaceFirst("_m.jpg","_b.jpg")

                val photoObject = Photo(title,author,authorId,link,tags,photoUrl)

                photoList.add(photoObject)
                Log.d(TAG,".doInBackground $photoObject")
            }
        }catch(e: JSONException){
            e.printStackTrace()
            Log.e(TAG,".doInBackground : Error processing Json data ${e.message}")
            cancel(true)
            listener.onError(e)
        }
        Log.d(TAG,".doInBackground ends")
        return photoList
    }

    override fun onPostExecute(result: ArrayList<Photo>) {
        Log.d(TAG,"onPostExecute starts")
        super.onPostExecute(result)
        listener.onDataAvailable(result)
        Log.d(TAG,".onPostExecute ends")

    }
}