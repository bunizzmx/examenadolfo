package com.example.android.examenadolfo.presentation.ui.tvs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.examenadolfo.R
import com.example.android.examenadolfo.data.network.model.response.Tv

class TvAdapter (context :Context,listener: OpenTvListener): RecyclerView.Adapter<TvAdapter.CajerosHolder>() {
    val TYPE_TV :Int =0
    lateinit var context:Context
    var  listener: OpenTvListener
    lateinit var items:ArrayList<Tv>
    init {
        this.items= arrayListOf()
        this.context = context
        this.listener=listener
    }

    fun setTvsItems(items:ArrayList<Tv>)
    {
        this.items = items
        notifyDataSetChanged()
    }

    private fun getInflatedView(parent: ViewGroup, resourceLayout: Int): View? {
        return LayoutInflater
            .from(parent.context)
            .inflate(resourceLayout, parent, false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CajerosHolder {
        var  view: View?
        view = getInflatedView(parent, R.layout.item_tv)
        return   CajerosHolder(view!!)


    }

    override fun getItemViewType(position: Int): Int {
        return TYPE_TV
    }

    override fun onBindViewHolder(holder: CajerosHolder, position: Int) {
        holder.bind(items[position], listener)

    }

    override fun getItemCount(): Int {
        if(items!=null)
            return  items.size
        else return  0
    }

    class CajerosHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name_tv: TextView
        val image_tv:ImageView
        val vote_average:TextView
        init {
            name_tv = itemView.findViewById(R.id.name_tv)
            image_tv = itemView.findViewById(R.id.image_tv)
            vote_average = itemView.findViewById(R.id.vote_average)
        }

        fun bind(item: Tv, listener: OpenTvListener) = with(itemView) {
            name_tv.text = item.original_name
            vote_average.text = item.vote_average.toString()
            Glide.with(context)
                .load("https://image.tmdb.org/t/p/original"+item.backdrop_path)
                .into(image_tv)
           //setOnClickListener { listener(item) }
        }
    }
}