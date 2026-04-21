1. Set server host in NetworkClient.kt:14:
```
private const val BASE_HOST = ""                                                      
```                                                                                                         
2. Provide Settings in your Koin setup (platform-specific, outside authModule)                                          
```                   
// Android (in your app module or startKoin block)
single<Settings> {                                                                                                      
    SharedPreferencesSettings(                                                                                           
        androidContext().getSharedPreferences("friends_auth", Context.MODE_PRIVATE)                                        
    )                    
}                                                                                                                        
// iOS (in your iOS Koin init)                                                                                           
single<Settings> {
    NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
}

Then include authModule in startKoin { modules(authModule) }. Use get(named("auth")) for any HttpClient that needs         
Authorization headers on protected routes.
```