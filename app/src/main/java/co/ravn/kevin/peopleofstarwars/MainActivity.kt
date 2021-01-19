package co.ravn.kevin.peopleofstarwars

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

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
    }
}