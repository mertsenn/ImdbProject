package com.mertsen.imdbproject.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mertsen.imdbproject.R
import com.mertsen.imdbproject.model.Cast

class CastsRecyclerAdapter(val listOfCast : List<Cast>): RecyclerView.Adapter<CastsRecyclerAdapter.CastHolder>() {



    inner class CastHolder(view : View): RecyclerView.ViewHolder(view){
        val myCastImage = itemView.findViewById<ImageView>(R.id.castImage)
        val myCastNamee = itemView.findViewById<TextView>(R.id.myCastName)
        val myCastCharacterNamee = itemView.findViewById<TextView>(R.id.myCastCharName)


        fun bind(movieCast : Cast){
            val moviecastID = movieCast.castId
            val myCastNameVar = movieCast.name
            val myCastCharacterNameVar = movieCast.character
            Log.d("CastNameVar", "${myCastNameVar}")
            Log.d("CastNameVar", "${myCastCharacterNameVar}")
            val myCastImageUrl = movieCast.profile_path
            if(myCastImageUrl != null && myCastNameVar != null && myCastCharacterNameVar != null){
                Glide.with(itemView).load("https://image.tmdb.org/t/p/w780${myCastImageUrl}").into(myCastImage)
                myCastNamee.text = myCastNameVar
                myCastCharacterNamee.text = myCastCharacterNameVar
            }
            Log.d("CastsRecyclerAdapter", "Name: ${movieCast.name}, Character: ${movieCast.character}")


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.single_cast_description,parent,false)
        return CastHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listOfCast.size
    }

    override fun onBindViewHolder(holder: CastHolder, position: Int) {
        val cast = listOfCast[position]
        holder.bind(cast)
    }

}