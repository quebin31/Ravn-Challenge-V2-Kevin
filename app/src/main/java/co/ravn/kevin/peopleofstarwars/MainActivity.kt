package co.ravn.kevin.peopleofstarwars

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.ravn.kevin.AllPeoplePaginatedQuery
import co.ravn.kevin.peopleofstarwars.adapters.PeopleAdapter
import co.ravn.kevin.peopleofstarwars.components.LoadingComponent
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import kotlinx.coroutines.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var mLoadingComponent: LoadingComponent
    private lateinit var mPeopleRecyclerView: RecyclerView
    private lateinit var mPeopleListAdapter: PeopleAdapter

    private var mPeopleList = mutableListOf<AllPeoplePaginatedQuery.Person>()
    private var mCurrEndCursor: String? = null
    private val mApolloClient = ApolloClient.builder()
            .serverUrl("https://swapi-graphql.netlify.app/.netlify/functions/index")
            .build()

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mLoadingComponent = findViewById(R.id.loadingComponent)
        mPeopleRecyclerView = findViewById(R.id.peopleRecyclerView)
        mPeopleListAdapter = PeopleAdapter(this, mPeopleList)
        mPeopleListAdapter.onItemClickListener { person ->
            launchPersonInfoActivity(person.id)
        }

        mPeopleRecyclerView.adapter = mPeopleListAdapter
        mPeopleRecyclerView.layoutManager = LinearLayoutManager(this)

        mLoadingComponent.show()
        lifecycleScope.launch {
            Log.d(TAG, "onCreate: Getting data")

            while (true) {
                val result = try {
                    getAllPeople()
                } catch (e: Exception) {
                    Log.d(TAG, "onCreate: Getting data failure $e")
                    break
                }

                when (result) {
                    LoadResult.Empty -> break
                    LoadResult.Successful -> delay(5.seconds)
                }
            }
        }
    }

    private fun showLoadError() {
        mLoadingComponent.error(resources.getString(R.string.failed_data))
    }

    private enum class LoadResult {
        Empty,
        Successful
    }

    private suspend fun getAllPeople(): LoadResult = coroutineScope {
        val response = try {
            mApolloClient
                    .query(AllPeoplePaginatedQuery(first = 5, Input.optional(mCurrEndCursor)))
                    .await()
        } catch (e: ApolloException) {
            runOnUiThread { showLoadError() }
            throw e
        }

        val allPeople = response.data?.allPeople
        // Check data and errors
        if (allPeople == null || response.hasErrors()) {
            runOnUiThread { showLoadError() }
            throw Exception("Failed to get data from endpoint")
        }

        // Is `people` empty? Stop getting data
        if (allPeople.people?.isEmpty() != false) {
            runOnUiThread { mLoadingComponent.hide() }
            return@coroutineScope LoadResult.Empty
        }

        val lastIndex = mPeopleList.size
        mPeopleList.addAll(allPeople.people.filterNotNull())

        runOnUiThread {
            mPeopleListAdapter.notifyItemRangeInserted(lastIndex, 5)
        }

        // This could happen and given that `mCurrEndCursor` can be null
        // setting this to `null` again would populate the list with repeated items.
        if (allPeople.pageInfo.endCursor == null)
            throw Exception("Get a null cursor")

        mCurrEndCursor = allPeople.pageInfo.endCursor
        return@coroutineScope LoadResult.Successful
    }

    private fun launchPersonInfoActivity(id: String) {
        val intent = Intent(this,  PersonActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }
}