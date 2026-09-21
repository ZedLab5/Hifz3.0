# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Preserve Room database classes, entities, and DAOs
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep class * extends androidx.room.RoomDatabase$Callback { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>();
}
-dontwarn androidx.room.paging.**

# Keep data models
-keep class com.example.data.local.** { *; }
-keep class com.example.data.model.** { *; }
-keep class com.example.data.prayer.** { *; }
-keep class com.example.data.quran.** { *; }
-keep class com.example.data.repository.** { *; }

# Keep Android Broadcast Receivers
-keep class com.example.data.prayer.PrayerAlarmReceiver { *; }
-keep class com.example.data.prayer.BootReceiver { *; }

# Kotlin coroutines and reflection
-keepattributes *Annotation*,InnerClasses,EnclosingMethod,Signature,SourceFile,LineNumberTable

