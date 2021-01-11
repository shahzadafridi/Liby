package com.brikmas.traduora

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject

class TraduoraTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val dataStore = TraduoraDataStore(context)

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        this.tag = "tradoura"
    }

    fun setTranslateText(layoutName: String){
        val tvIdName = context.resources.getResourceEntryName(id)
        ioScope.launch {
            var layoutJSONObject = dataStore.getTranslation(layoutName)
            layoutJSONObject?.let { layout ->
                if (layout.has(tvIdName)){
                    withContext(Dispatchers.Main){
                        try {
                            var textJSONObject = JSONObject(layout.getString(tvIdName))
                            var language = dataStore.getLanguage()!!
                            this@TraduoraTextView.text = textJSONObject.getString(language)
                        }catch (exception: JSONException){
                            Log.e(tvIdName,exception.message.toString())
                        }
                    }
                }else{
                    Log.e(tvIdName,"Translation not found in Traduora")
                }
            }
        }
    }

}