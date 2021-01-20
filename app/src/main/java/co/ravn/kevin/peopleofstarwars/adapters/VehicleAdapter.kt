package co.ravn.kevin.peopleofstarwars.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.ravn.kevin.PersonInformationQuery
import co.ravn.kevin.peopleofstarwars.R

class VehicleAdapter(context: Context, vehicleList: MutableList<PersonInformationQuery.Vehicle>): RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {
    class VehicleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val key: TextView = itemView.findViewById(R.id.dataKey)

        init {
            // Remove `value` TextView from layout
            val value: TextView = itemView.findViewById(R.id.dataValue)
            value.visibility = View.GONE
        }
    }

    private val mVehicleList = vehicleList
    private val mInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val mItemView = mInflater.inflate(R.layout.item_person, parent, false)
        return VehicleViewHolder(mItemView)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        holder.key.text = mVehicleList[position].name ?: "Unknown"
    }

    override fun getItemCount(): Int {
        return mVehicleList.size
    }

}