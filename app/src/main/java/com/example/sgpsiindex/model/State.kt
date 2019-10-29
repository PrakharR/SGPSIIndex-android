package com.example.sgpsiindex.model

data class State(
    val status: Status,
    val message: String? = null) {

    companion object {
        val LOADED = State(Status.SUCCESS)
        val LOADING = State(Status.RUNNING)
        fun error(message: String?) = State(Status.FAILED, message)
    }

}