package com.example.lab1

class NBackHelper {

    // Native function declaration (calls the C function)
    private external fun createNBackString(size: Int, combinations: Int, percentMatch: Int, nBack: Int, seed: Int): IntArray

    fun generateNBackString(size: Int, combinations: Int, percentMatch: Int, nBack: Int, seed:Int): IntArray {
        return createNBackString(size, combinations, percentMatch, nBack, seed)
    }

    companion object {
        init {
            System.loadLibrary("JniBridge") // Load the native C library
        }
    }
}