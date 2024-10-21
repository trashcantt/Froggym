package com.sabdev.froggym.utils

object BMICalculator {
    fun calculateBMI(height: Float, weight: Float): Float {
        val heightInMeters = height / 100
        return weight / (heightInMeters * heightInMeters)
    }
}