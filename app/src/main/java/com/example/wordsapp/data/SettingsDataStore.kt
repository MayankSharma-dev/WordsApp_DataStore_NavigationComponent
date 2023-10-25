package com.example.wordsapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class SettingsDataStore(context:Context) {
    /**In order to implement the SettingsDataStore class, the first step is to create a key that stores a Boolean value that specifies whether the user setting is a linear layout. Create a private class property called IS_LINEAR_LAYOUT_MANAGER, and initialize it using booleanPreferencesKey() passing in the is_linear_layout_manager key name as the function parameter.*/
    private val IS_LINEAR_LAYOUT_MANAGER = booleanPreferencesKey("is_linear_layout_manager")


    /** Now it's time to use your key and store the Boolean layout setting in the DataStore. Preferences DataStore provides an edit() suspend function that transactionally updates the data in DataStore. The function's transform parameter accepts a block of code where you can update the values as needed. All of the code in the transform block is treated as a single transaction. Under the hood the transaction work is moved to Dispacter.IO, so don't forget to make your function suspend when calling the edit() function.

    Create a suspend function called saveLayoutToPreferencesStore() that takes two parameters: the layout setting Boolean, and the Context. */
    suspend fun saveLayoutToPreferencesStore(isLinearLayoutManager: Boolean , context: Context){
        context.dataStore.edit { preferences ->
            preferences[IS_LINEAR_LAYOUT_MANAGER] = isLinearLayoutManager
        }
    }

    /** Read from the Preferences DataStore
    Preferences DataStore exposes the data stored in a Flow<Preferences> that emits every time a preference has changed. You don't want to expose the entire Preferences object, just the Boolean value. To do this, we map the Flow<Preferences>, and get the Boolean value you're interested in.

    Expose a preferenceFlow: Flow<UserPreferences>, constructed based on dataStore.data: Flow<Preferences>, map it to retrieve the Boolean preference. Since the Datastore is empty on the first run, return true by default. */
    val preferencesFlow: Flow<Boolean> = context.dataStore.data
        /** SharedPreference DataStore throws an IOException when an error is encountered while reading data.
         * In preferenceFlow declaration, before map(), use the catch() operator to catch the IOException and emit emptyPreferences().
         * To keep things simple, since we don't expect any other types of exceptions here, if a different type of exception is thrown, re-throw it. */
        .catch {
            if( it is IOException){
                it.stackTrace
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map {
                preferences ->
            // On the first run of the app, we will use LinearLayoutManager by default
            preferences[IS_LINEAR_LAYOUT_MANAGER] ?: true
        }

}


private const val LAYOUT_PREFERENCES_NAME = "layout_preferences"

// Create a DataStore instance using the preferencesDataStore delegate, with the Context as
// receiver.

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = LAYOUT_PREFERENCES_NAME)