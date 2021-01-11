package com.brikmas.traduora

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONException
import org.json.JSONObject

class TraduoraDataStore(context: Context) {

    val TAG = "TraduoraDataStore"
    var mContext: Context
    var dataStore: DataStore<Preferences>
    val TRANSLATION_JSON_KEY = "translation_json"
    val TRANSLATION_LANGUAGE_KEY = "app_language"
    val TRANSLATION_SCREENS_KEY = "screens"
    val TRADUORA_SYNC_KEY = "traduora_sync"
    var LANGUAGE: String? = "english"

    init {
        mContext = context
        dataStore = mContext.createDataStore(name = "app", migrations = listOf(
            SharedPreferencesMigration(mContext,"App-prefs")
        ))
    }

    /*
    * saveInDataStore(value: String) : Store api response in datastore.
    * value : response comes from api.
    */
    suspend fun saveInDataStore(value: String){
        dataStore.edit {app ->
            val dataStoreKey = preferencesKey<String>(TRANSLATION_JSON_KEY)
            app[dataStoreKey] = value
        }
        splitJSONbyScreen(value)
    }

    /*
    * splitJSONbyScreen(translationJsonStr: String) : Split traduora translation json file into screens. Can access fast instead of whole json response.
    * translationJsonStr : Traduora translation json string comes from Tradoura api response.
    * TRADUORA_SYNC_KEY : Flag to check either json response splited and stored in datastore or not.
    * TRANSLATION_LANGUAGE_KEY : Default language translation. e-g If api has french then get frech language values.
    */
    suspend fun splitJSONbyScreen(translationJsonStr: String){
        var jsonObject: JSONObject? = null
        try {
            jsonObject = JSONObject(translationJsonStr)
            var jsonArray = jsonObject.getJSONArray(TRANSLATION_SCREENS_KEY)
            var screens = jsonArray.length() - 1
            for (i in 0..screens){
                var screenJson = jsonArray.getJSONObject(i)
                var screenName = screenJson.keys().next()
                saveScreenJsonInDataStore(screenName,screenJson.toString())
                Log.e(TAG,screenName + " -> " + screenJson)
            }
            dataStore.edit {app ->
                val dataStoreKey = preferencesKey<Boolean>(TRADUORA_SYNC_KEY)
                val dataStoreLanguageKey = preferencesKey<String>(TRANSLATION_LANGUAGE_KEY)
                app[dataStoreKey] = true
                app[dataStoreLanguageKey] = jsonObject.getString(TRANSLATION_LANGUAGE_KEY)
                Log.e(TAG,"Traduora translation synced.")
            }
        }catch (exception: JSONException) {
            Log.e(TAG,exception.message.toString())
        }
    }

    /*
    * saveScreenJsonInDataStore(key: String, value: String) : Store screen json object in datastore. e-g home_activity json.
    * key : screen name e-g home_activity.
    * value : json object
    */
    suspend fun saveScreenJsonInDataStore(key: String, value: String){
        dataStore.edit {app ->
            val dataStoreKey = preferencesKey<String>(key)
            app[dataStoreKey] = value
        }
    }

    /*
    * readFromDataStore(screenKey: String) : get Screen json object from datasotre. e-g home_activity
    * screenKey : screen name e-g home_activity.
    * @return : string json object.
    */
    suspend fun readFromDataStore(screenKey: String): String?{
        val dataStoreKey = preferencesKey<String>(screenKey)
        val preferences = dataStore.data.first()
        val translation_json = preferences[dataStoreKey]
        return translation_json
    }

    suspend fun getTranslation(key: String): JSONObject?{
        val translationJsonStr = readFromDataStore(key)
        if (translationJsonStr != null){
            var jsonObject: JSONObject? = null
            try {
                jsonObject = JSONObject(translationJsonStr)
            }catch (exception: JSONException) {
                Log.e(TAG,exception.message.toString())
            }finally {
                if (jsonObject != null){
                    if (jsonObject.has(key)){
                        return jsonObject.getJSONObject(key)
                    }else{
                        return null
                    }
                }else{
                    return null
                }
            }
        }
        return null
    }

    suspend fun getLanguage(): String? {
        val dataStoreKey = preferencesKey<String>(TRANSLATION_LANGUAGE_KEY)
        val preferences = dataStore.data.first()
        LANGUAGE = preferences[dataStoreKey]
        return LANGUAGE
    }

    fun traduoraSync(): Flow<Boolean>{
        val dataStoreKey = preferencesKey<Boolean>(TRADUORA_SYNC_KEY)
        return dataStore.data
            .map { preferences ->
                preferences[dataStoreKey] ?: false
            }
    }

    /*
    * setTraduoraSync(isSync: Boolean) : To refresh response in cache If it set to false..
    * isSync : true/false value.
    */
    suspend fun setTraduoraSync(isSync: Boolean){
        dataStore.edit {app ->
            val dataStoreKey = preferencesKey<Boolean>(TRADUORA_SYNC_KEY)
            app[dataStoreKey] = isSync

        }
    }
}