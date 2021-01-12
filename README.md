# Traduora Custom Android Library

## DEPENDENCY REQUIRED

```
implementation 'com.github.shahzadafridi:Liby:1.0'
```
Coroutines dependencies are required becuase in library datastore is used.
```
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7"
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9"
```
## KOTLIN CODE

### Set traduora Sync
If the response is changed or modified locally then make false to fetch new response from api.
```
CoroutineScope(IO).launch {
	TraduoraDataStore(this@MainActivity).setTraduoraSync(false) 
}
```

### Traduora Sync
Check if the traduora response is save locally or not. If the application open for the first time the get the response from api and pass it to tradoura to save it locally then for rest the method will check either response exists or not.
```
CoroutineScope(IO).launch {
		TraduoraDataStore(this@MainActivity).traduoraSync().collect { isDataStoreSync ->
		  if (!isDataStoreSync) {
		      Log.e("Traduora", "Tradoura is not synced")
		      TraduoraDataStore(this@MainActivity).saveInDataStore(jsonStr)
		  } else {
		      Log.e("Traduora", "Tradoura is synced")
		  }
	}
}
```

Set Translation e-g home_title is defined key value pair in JSON.
```
findViewById<TraduoraTextView>(R.id.home_title).setTranslateText("home_activity")
```

## JSON RESPONSE TEMPLATE
```
{
  "app_language": "french",
  "screens": [
    {
      "home_activity": {
        "home_title": {
          "english": "That is flex.",
          "french": "Datisflex."
        },
        "home_email_hint": {
          "english": "Enter email",
          "french": "Ehte ehmail"
        },
        "home_password_hint": {
          "english": "Enter password",
          "french": "Ehte pashhword"
        },
        "home_login_btn_text": {
          "english": "Login",
          "french": "loghing"
        }
      }
    },
    {
      "Onboarding_activity": {
        "onboard_title": {
          "english": "That is flex.",
          "french": "Datisflex."
        },
        "onboard_email_hint": {
          "english": "Enter email",
          "french": "Ehte ehmail"
        },
        "onboard_password_hint": {
          "english": "Enter password",
          "french": "Ehte pashhword"
        },
        "onboard_login_btn_text": {
          "english": "Login",
          "french": "loghing"
        }
      }
    }
  ]
}
```
