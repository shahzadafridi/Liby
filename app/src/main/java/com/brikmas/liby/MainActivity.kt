package com.brikmas.liby

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.brikmas.traduora.TraduoraDataStore
import com.brikmas.traduora.TraduoraTextView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val jsonStr = "{\n" +
            "  \"app_language\": \"french\",\n" +
            "  \"screens\": [\n" +
            "    {\n" +
            "      \"home_activity\": {\n" +
            "        \"home_title\": {\n" +
            "          \"english\": \"That is flex.\",\n" +
            "          \"french\": \"Datisflex.\"\n" +
            "        },\n" +
            "        \"home_email_hint\": {\n" +
            "          \"english\": \"Enter email\",\n" +
            "          \"french\": \"Ehte ehmail\"\n" +
            "        },\n" +
            "        \"home_password_hint\": {\n" +
            "          \"english\": \"Enter password\",\n" +
            "          \"french\": \"Ehte pashhword\"\n" +
            "        },\n" +
            "        \"home_login_btn_text\": {\n" +
            "          \"english\": \"Login\",\n" +
            "          \"french\": \"loghing\"\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"Onboarding_activity\": {\n" +
            "        \"onboard_title\": {\n" +
            "          \"english\": \"That is flex.\",\n" +
            "          \"french\": \"Datisflex.\"\n" +
            "        },\n" +
            "        \"onboard_email_hint\": {\n" +
            "          \"english\": \"Enter email\",\n" +
            "          \"french\": \"Ehte ehmail\"\n" +
            "        },\n" +
            "        \"onboard_password_hint\": {\n" +
            "          \"english\": \"Enter password\",\n" +
            "          \"french\": \"Ehte pashhword\"\n" +
            "        },\n" +
            "        \"onboard_login_btn_text\": {\n" +
            "          \"english\": \"Login\",\n" +
            "          \"french\": \"loghing\"\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            TraduoraDataStore(this@MainActivity).setTraduoraSync(false)
        }

        Handler(Looper.myLooper()!!).postDelayed({
            TraduoraDataStore(this@MainActivity).traduoraSync().asLiveData()
                .observe(this@MainActivity, { isDataStoreSync ->
                    if (!isDataStoreSync) {
                        lifecycleScope.launch {
                            TraduoraDataStore(this@MainActivity).saveInDataStore(jsonStr)
                            findViewById<TraduoraTextView>(R.id.home_title).setTranslateText("home_activity")
                        }
                    } else {
                        Log.e("Traduora", "Tradoura is synced")
                    }
                })
        }, 3000)
    }

}