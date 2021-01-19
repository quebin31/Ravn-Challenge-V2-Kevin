package co.ravn.kevin.peopleofstarwars.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.ravn.kevin.AllPeoplePaginatedQuery
import co.ravn.kevin.peopleofstarwars.R

class PeopleAdapter(context: Context, peopleList: MutableList<AllPeoplePaginatedQuery.Person>): RecyclerView.Adapter<PeopleAdapter.PersonViewHolder>() {
    class PersonViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val personName: TextView = itemView.findViewById(R.id.personName)
        val personFooter: TextView = itemView.findViewById(R.id.personFooter)
    }

    private val mContext = context
    private val mPeopleList = peopleList
    private val mInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val mItemView = mInflater.inflate(R.layout.item_person, parent, false)
        return PersonViewHolder(mItemView)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = mPeopleList[position]
        holder.personName.text = person.name

        val species = person.species?.name ?: "Human"
        val homeWorld = person.homeworld?.name ?: "Earth"
        holder.personFooter.text = mContext.getString(R.string.person_footer, species, homeWorld)
    }

    override fun getItemCount(): Int {
        return mPeopleList.size
    }
}