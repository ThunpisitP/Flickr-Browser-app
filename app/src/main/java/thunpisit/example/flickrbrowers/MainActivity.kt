package thunpisit.example.flickrbrowers

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), GetRawData.OnDownloadComplete, GetFlickrJsonData.OnDataAvailable,
RecyclerItemClickListener.OnRecyclerClickListener{
    private val TAG = "MainActivity"

    private val flickrRecyclerViewAdapter = FlickrRecyclerViewAdapter(ArrayList())

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"onCreated called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.addOnItemTouchListener(RecyclerItemClickListener(this,recycler_view,this))
        recycler_view.adapter = flickrRecyclerViewAdapter

        val url = createUri("https://www.flickr.com/services/feeds/photos_public.gne","android,oreo","en-us",true)
        val getRawData = GetRawData(this)
        getRawData.execute(url)

        Log.d(TAG,"onCreate ends")
    }

    private fun createUri(baseUrl:String,searchCriteria:String,lang:String,matchAll:Boolean):String{
        Log.d(TAG,".createUri starts")

        return Uri.parse(baseUrl).buildUpon()
            .appendQueryParameter("tags",searchCriteria)
            .appendQueryParameter("tagmode",if(matchAll) "ALL" else "ANY")
            .appendQueryParameter("lang",lang)
            .appendQueryParameter("format","json")
            .appendQueryParameter("nojsoncallback","1")
            .build().toString()
    }

    override fun onDownloadComplete(data: String,status: DownloadStatus){
        if(status==DownloadStatus.OK){
            Log.d(TAG,"onDownloadComplete called")

            val getFlickrJsonData = GetFlickrJsonData(this)
            getFlickrJsonData.execute(data)
        }else{
            //download failed
            Log.d(TAG,"onDownloadComplete failed with status $status, Error message is: $data")
        }
    }

    override fun onDataAvailable(data: List<Photo>) {
        Log.d(TAG,".onDataAvailable called")
        flickrRecyclerViewAdapter.loadNewData(data)
        Log.d(TAG,".onDataAvailable ends")
    }

    override fun onError(exception: Exception) {
        Log.d(TAG,".onError called with ${exception.message}")

        Log.d(TAG,".onError ends")
    }

    override fun onItemClick(view: View, position: Int) {
        Log.d(TAG,".onItemClick: starts")
        Toast.makeText(this,"Normal tap at position $position",Toast.LENGTH_SHORT).show()
    }

    override fun onItemLongClick(view: View, position: Int) {
        Log.d(TAG,".onItemLongClick: starts")
        Toast.makeText(this,"Long tap at position $position",Toast.LENGTH_SHORT).show()
    }
}
