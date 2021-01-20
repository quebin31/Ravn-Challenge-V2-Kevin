package co.ravn.kevin.peopleofstarwars

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.ravn.kevin.PersonInformationQuery
import co.ravn.kevin.peopleofstarwars.adapters.VehiclesAdapter
import co.ravn.kevin.peopleofstarwars.components.LoadingComponent
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*

class PersonActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "PersonActivity"
    }

    private lateinit var mLoadingComponent: LoadingComponent
    private lateinit var mKeyValueLayout: LinearLayout
    private lateinit var mVehiclesRecyclerView: RecyclerView
    private lateinit var mVehiclesListAdapter: VehiclesAdapter

    private var mVehiclesList = mutableListOf<PersonInformationQuery.Vehicle>()
    private val mApolloClient = ApolloClient.builder()
        .serverUrl("https://swapi-graphql.netlify.app/.netlify/functions/index")
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person)

        mLoadingComponent = findViewById(R.id.loadingComponent)
        mKeyValueLayout = findViewById(R.id.keyValueLayout)
        mVehiclesRecyclerView = findViewById(R.id.vehiclesRecyclerView)
        mVehiclesListAdapter = VehiclesAdapter(this, mVehiclesList)
        mVehiclesRecyclerView.adapter = mVehiclesListAdapter
        mVehiclesRecyclerView.layoutManager = LinearLayoutManager(this)

        val id = intent.getStringExtra("id")
        if (id != null) {
            mLoadingComponent.show()
            lifecycleScope.launch {
                getPersonInformation(id)
            }
        } else {
            finish()
        }
    }

    private fun addItemDataView(key: String, value: String) {
        val view = layoutInflater.inflate(R.layout.item_data, mKeyValueLayout, false)
        view.findViewById<TextView>(R.id.dataKey).text = key
        view.findViewById<TextView>(R.id.dataValue).text = value.customCapitalize(Locale.getDefault())
        mKeyValueLayout.addView(view)
    }

    private fun showLoadError() {
        mLoadingComponent.error(resources.getString(R.string.failed_data))
    }

    private suspend fun getPersonInformation(id: String): Unit = coroutineScope {
        val response = try {
            mApolloClient.query(PersonInformationQuery(id = id)).await()
        } catch (e: ApolloException) {
            runOnUiThread { showLoadError() }
            return@coroutineScope
        }

        val person = response.data?.person
        if (person == null || response.hasErrors()) {
            runOnUiThread { showLoadError() }
            return@coroutineScope
        }

        Log.d(TAG, "getPersonInformation: $person")

        mVehiclesList
            .addAll(person.vehicleConnection?.vehicles?.filterNotNull()?: emptyList())

        runOnUiThread {
            addItemDataView(getString(R.string.eye_color), person.eyeColor ?: "Unknown")
            addItemDataView(getString(R.string.hair_color), person.hairColor ?: "Unknown")
            addItemDataView(getString(R.string.skin_color), person.skinColor ?: "Unknown")
            addItemDataView(getString(R.string.birth_year), person.birthYear ?: "Unknown")
            mVehiclesListAdapter.notifyDataSetChanged()
            mLoadingComponent.hide()
        }
    }
}