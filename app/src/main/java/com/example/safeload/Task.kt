package com.example.safeload

data class Task(
    var name: String,
    var startingLocation: String = "",
    var endingLocation: String = ""
)