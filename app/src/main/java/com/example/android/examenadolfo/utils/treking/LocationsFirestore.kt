package com.example.android.examenadolfo.utils.treking

data class LocationsFirestore(
    var lat:String,
    var lon:String,
    var date:String
){
    constructor() : this("", "",""
    )
}
