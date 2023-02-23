package com.example.alldocumentsreaderimagescanner.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "AdsRemoteData")
object PreDataStoreUtils {
    suspend fun Context.write(key: String, value: String) {
        dataStore.edit { settings ->
            settings[stringPreferencesKey(key)] = value
        }
    }

    suspend fun Context.read(key: String, defaultValue: String): String {
        return dataStore.data.map { settings ->
            settings[stringPreferencesKey(key)] ?: defaultValue
        }.first().toString()
    }

    suspend fun Context.write(key: String, value: Int) {
        dataStore.edit { settings ->
            settings[intPreferencesKey(key)] = value
        }
    }

    suspend fun Context.read(key: String, defaultValue: Int): Int {
        return dataStore.data.map { settings ->
            settings[intPreferencesKey(key)] ?: defaultValue
        }.first().toInt()
    }

    suspend fun Context.write(key: String, value: Double) {
        dataStore.edit { settings ->
            settings[doublePreferencesKey(key)] = value
        }
    }

    suspend fun Context.read(key: String, defaultValue: Double): Double {
        return dataStore.data.map { settings ->
            settings[doublePreferencesKey(key)] ?: defaultValue
        }.first().toDouble()

    }
    suspend fun Context.write(key: String, value: Boolean) {
        dataStore.edit { settings ->
            settings[booleanPreferencesKey(key)] = value
        }
    }

    suspend fun Context.read(key: String, defaultValue: Boolean): Boolean {
        return dataStore.data.map { settings ->
            settings[booleanPreferencesKey(key)] ?: defaultValue
        }.first()

    }
}
