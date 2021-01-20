package co.ravn.kevin.peopleofstarwars

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.ravn.kevin.AllPeoplePaginatedQuery
import co.ravn.kevin.peopleofstarwars.adapters.PeopleAdapter
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import kotlinx.coroutines.*
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var mLoadingComp: LinearLayout
    private lateinit var mPeopleRecyclerView: RecyclerView
    private lateinit var mPeopleListAdapter: PeopleAdapter

    private var mIsLoadingData = true
    private var mPeopleList = mutableListOf<AllPeoplePaginatedQuery.Person>()
    private var mCurrEndCursor: String? = null
    private val mApolloClient = ApolloClient.builder()
            .serverUrl("https://swapi-graphql.netlify.app/.netlify/functions/index")
            .build()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mLoadingComp = findViewById(R.id.loadingComponent)
        mPeopleRecyclerView = findViewById(R.id.peopleRecyclerView)
        mPeopleListAdapter = PeopleAdapter(this, mPeopleList)
        mPeopleRecyclerView.adapter = mPeopleListAdapter
        mPeopleRecyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            getAllPeople()
        }
    }

    private fun showLoadError() {
        mLoadingComp[0].visibility = View.GONE

        val loadingTextView = mLoadingComp[1] as TextView
        loadingTextView.visibility = View.VISIBLE
        loadingTextView.text = resources.getText(R.string.failed_data)
        loadingTextView.setTextColor(resources.getColor(R.color.text_emphasis))

        mIsLoadingData = false
    }

    private fun showLoadingAnimation() {
        mLoadingComp[0].visibility = View.VISIBLE

        val loadingTextView = mLoadingComp[1] as TextView
        loadingTextView.visibility = View.VISIBLE
        loadingTextView.text = resources.getText(R.string.failed_data)
        loadingTextView.setTextColor(resources.getColor(R.color.black_50))

        mIsLoadingData = true

    }

    private fun hideLoadingAnimation() {
        mLoadingComp[0].visibility = View.GONE
        mLoadingComp[1].visibility = View.GONE

        mIsLoadingData = false
    }

    suspend fun getAllPeople(): Unit = coroutineScope {
        runOnUiThread {
            if (!mIsLoadingData) showLoadingAnimation()
        }

        val response = try {
            mApolloClient
                    .query(AllPeoplePaginatedQuery(first = 5, Input.optional(mCurrEndCursor)))
                    .await()
        } catch (e: ApolloException) {
            runOnUiThread { showLoadError() }
            return@coroutineScope
        }

        val allPeople = response.data?.allPeople
        // Check data and errors
        if (allPeople == null || response.hasErrors()) {
            runOnUiThread { showLoadError() }
            return@coroutineScope
        }

        // Is `people` empty? Stop getting data
        if (allPeople.people?.isNotEmpty() != false) {
            runOnUiThread { hideLoadingAnimation() }
            return@coroutineScope
        }

        val lastIndex = mPeopleList.size
        for (person in allPeople.people) {
            if (person != null)
                mPeopleList.add(person)
        }

        Log.d(TAG, "getAllPeople: ${mPeopleList}")

        runOnUiThread {
            mPeopleListAdapter.notifyItemRangeInserted(lastIndex, 5)
        }

        getAllPeople()
    }
}