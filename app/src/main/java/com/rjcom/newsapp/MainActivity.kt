package com.rjcom.newsapp

import android.app.DownloadManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.textclassifier.TextLinks
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest

class MainActivity : AppCompatActivity(), NewsItemClicked {
private lateinit var mAdapter: NewsListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_main)

        val recyclerView: RecyclerView=findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.visibility=View.INVISIBLE
        val search: EditText=findViewById(R.id.search)
        val button: Button=findViewById<Button>(R.id.button)
          button.setOnClickListener(
              {
                  button.visibility=View.INVISIBLE
                  search.visibility=View.INVISIBLE
                  recyclerView.visibility=View.VISIBLE
                  fetch(search.text.toString())
              }

          )
         recyclerView.layoutManager=LinearLayoutManager(this)
          mAdapter= NewsListAdapter(this)
        recyclerView.adapter=mAdapter
    }
    private fun fetch (searchQues:String)
    {

        val url = "https://newsapi.org/v2/everything?q="+searchQues+"&apiKey=926550d7247c4012972b94a716a75a3e"

        val jsonObjectRequest = object :JsonObjectRequest(Request.Method.GET, url, null,

            { response ->
                val newsJsonArray = response.getJSONArray("articles")
                val newsArray = ArrayList<News>()
                for (i in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = News(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("author"),
                        newsJsonObject.getString("url"),
                        newsJsonObject.getString("urlToImage")
                    )
                    newsArray.add(news)
                }

                mAdapter.updateNews(newsArray)

            },
            { _ ->

            })

        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String>? {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }
        }
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)


    }

    override fun onItemClicked(item: News) {
        val url:String=item.url
        val builder : CustomTabsIntent.Builder= CustomTabsIntent.Builder()// CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        val customTabsIntent=builder.build()//CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }
}

