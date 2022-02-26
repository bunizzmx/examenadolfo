package com.example.android.examenadolfo.data.network.model.response

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tv_table")
data class Tv(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    @Expose(serialize = false,deserialize = false)
    @SerializedName("id")
    var id:Int,


    @ColumnInfo(name = "backdrop_path")
    @Expose
    @SerializedName("backdrop_path")
    var backdrop_path:String?,


    @ColumnInfo(name = "name")
    @Expose
    @SerializedName("name")
    var name:String,

    @Ignore
    @Expose
    @SerializedName("origin_country")
    var origin_country:ArrayList<String>,

    @ColumnInfo(name = "original_language")
    @Expose
    @SerializedName("original_language")
    var original_language:String,

    @ColumnInfo(name = "original_name")
    @Expose
    @SerializedName("original_name")
    var original_name:String,

    @ColumnInfo(name = "popularity")
    @Expose
    @SerializedName("popularity")
    var popularity:Float,

    @ColumnInfo(name = "poster_path")
    @Expose
    @SerializedName("poster_path")
    var poster_path:String,

    @ColumnInfo(name = "overview")
    @Expose
    @SerializedName("overview")
    var overview:String,

    @ColumnInfo(name = "vote_average")
    @Expose
    @SerializedName("vote_average")
    var vote_average:Float,

    @ColumnInfo(name = "vote_count")
    @Expose
    @SerializedName("vote_count")
    var vote_count:Int,

    @ColumnInfo(name = "first_air_date")
    @Expose
    @SerializedName("first_air_date")
    var first_air_date:String,

    @Ignore
    @Expose
    @SerializedName("genre_ids")
    var genre_ids:ArrayList<Int>
) {
    constructor() : this(0, "",
        "", arrayListOf<String>(), "",
        "", 0f, "",
        "", 0f,0,"", arrayListOf<Int>()
    )
}
