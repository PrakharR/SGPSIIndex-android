package com.example.sgpsiindex.utility

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.example.sgpsiindex.R

class Utility {

    companion object {
        const val PSI_TWENTY_FOUR_HOURLY = "psi_twenty_four_hourly"
        const val PM25_TWENTY_FOUR_HOURLY = "pm25_twenty_four_hourly"

        const val PSI_NATIONAL = "national"

        const val DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

        fun descriptorInfo(context: Context, psi: Double): Pair<String, Int> {
            return when {
                psi in 0.0..50.0 -> Pair(context.getString(R.string.good), ContextCompat.getColor(context, R.color.psiDescriptorGood))
                psi in 51.0..100.0 -> Pair(context.getString(R.string.moderate), ContextCompat.getColor(context, R.color.psiDescriptorModerate))
                psi in 101.0..200.0 -> Pair(context.getString(R.string.unhealthy), ContextCompat.getColor(context, R.color.psiDescriptorUnhealthy))
                psi in 201.0..300.0 -> Pair(context.getString(R.string.very_unhealthy), ContextCompat.getColor(context, R.color.psiDescriptorVeryUnhealthy))
                psi > 301.0 -> Pair(context.getString(R.string.hazardous), ContextCompat.getColor(context, R.color.psiDescriptorHazardous))
                else -> Pair(context.getString(R.string.unknown), Color.WHITE)
            }
        }
    }

}