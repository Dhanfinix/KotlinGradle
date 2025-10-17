package com.dhandev.convention

enum class BuildType() {
    DEBUG(){
        override fun toString() = "debug"
    },
    RELEASE{
        override fun toString() = "release"
    }
}