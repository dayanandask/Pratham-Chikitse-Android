package com.example.health.data.repository

import com.example.health.data.local.JsonDataSource
import com.example.health.data.model.Hospital
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HospitalRepository @Inject constructor(
    private val jsonDataSource: JsonDataSource
) {
    private var cachedHospitals: List<Hospital>? = null

    fun getHospitals(): Result<List<Hospital>> {
        cachedHospitals?.let { return Result.success(it) }
        return jsonDataSource.loadHospitals().map { data ->
            data.hospitals.also { cachedHospitals = it }
        }
    }

    fun clearCache() {
        cachedHospitals = null
    }

    /**
     * Map Matching Priority Algorithm: Prioritizes hospitals based on critical distance thresholds.
     * Priority 1: < 1.0 km (Immediate reach)
     * Priority 2: < 2.5 km (High priority)
     * Priority 3: < 10.0 km (Standard priority)
     * Priority 4: > 10.0 km (Low priority / Others)
     */
    fun getHospitalsForLocation(lat: Double, lng: Double): List<Hospital> {
        val hospitals = getHospitals().getOrNull() ?: return emptyList()
        
        return hospitals.sortedWith(compareBy<Hospital> { 
            val dist = it.distanceTo(lat, lng)
            when {
                dist < 1.0 -> 1
                dist < 2.5 -> 2
                dist < 10.0 -> 3
                else -> 4
            }
        }.thenBy { it.distanceTo(lat, lng) })
    }

    fun getEmergencyHospitals(): List<Hospital> {
        return getHospitals().getOrNull()?.filter { it.emergencyAvailable } ?: emptyList()
    }

    fun searchHospitals(query: String): List<Hospital> {
        val hospitals = getHospitals().getOrNull() ?: return emptyList()
        if (query.isBlank()) return hospitals
        val lq = query.lowercase().trim()
        return hospitals.filter {
            it.name.lowercase().contains(lq) ||
            it.address.lowercase().contains(lq) ||
            it.specialties.any { s -> s.lowercase().contains(lq) } ||
            it.type.lowercase().contains(lq)
        }
    }
}
