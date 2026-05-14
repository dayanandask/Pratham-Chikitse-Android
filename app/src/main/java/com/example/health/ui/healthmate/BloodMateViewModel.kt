package com.example.health.ui.healthmate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.health.data.model.EmergencyContact
import com.example.health.data.repository.UserPreferencesRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BloodMateViewModel @Inject constructor(
    private val userPrefsRepo: UserPreferencesRepository
) : ViewModel() {

    private val gson = Gson()

    val contacts: StateFlow<List<EmergencyContact>> = userPrefsRepo.emergencyContacts
        .map { json ->
            if (json.isBlank() || json == "[]") return@map emptyList<EmergencyContact>()
            try {
                val type = object : TypeToken<List<EmergencyContact>>() {}.type
                gson.fromJson<List<EmergencyContact>>(json, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addContact(name: String, phone: String, relationship: String, bloodGroup: String) {
        viewModelScope.launch {
            val current = contacts.value.toMutableList()
            current.add(EmergencyContact(name = name, phone = phone, relationship = relationship, bloodGroup = bloodGroup))
            userPrefsRepo.setEmergencyContacts(gson.toJson(current))
        }
    }

    fun deleteContact(id: String) {
        viewModelScope.launch {
            val current = contacts.value.filter { it.id != id }
            userPrefsRepo.setEmergencyContacts(gson.toJson(current))
        }
    }

    fun updateContact(contact: EmergencyContact) {
        viewModelScope.launch {
            val current = contacts.value.map { if (it.id == contact.id) contact else it }
            userPrefsRepo.setEmergencyContacts(gson.toJson(current))
        }
    }
}
