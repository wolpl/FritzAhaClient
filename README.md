# FritzAhaClient
[![](https://jitpack.io/v/wolpl/FritzAhaClient.svg)](https://jitpack.io/#wolpl/FritzAhaClient)


A Kotlin/JVM client for accessing the AVM Fritzbox AHA-HTTP Interface.
## Usage in your gradle project
### 1. Include dependency
``` gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
} 
```
``` gradle
dependencies {
    implementation 'com.github.wolpl:FritzAhaClient:Tag'
}
```
### 2. Use a FritzSession
````kotlin
fun main(){
    val session = FritzSession("username", "password")
    val switches = session.getSwitchList()
    println("Available switches: $switches")
}
````
