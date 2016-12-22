# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Android_SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
# -keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
# }
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

##android-oauth-client rules - START
# Needed to keep generic types and @Key annotations accessed via reflection
-keepattributes Signature,Exceptions,RuntimeVisibleAnnotations,AnnotationDefault,EnclosingMethod,InnerClasses

-keepclasseswithmembers class * {
  @com.google.api.client.util.Key <fields>;
}

-keepclasseswithmembers class * {
  @com.google.api.client.util.Value <fields>;
}

-keepnames class com.google.api.client.http.HttpTransport

# Needed by google-http-client-android when linking against an older platform version
-dontwarn com.google.api.client.extensions.android.**

# Needed by google-api-client-android when linking against an older platform version
-dontwarn com.google.api.client.googleapis.extensions.android.**

# Do not obfuscate but allow shrinking of android-oauth-client
-keepnames,includedescriptorclasses class com.wuman.android.auth.** { *; }
-keepnames,includedescriptorclasses class com.google.api.client.auth.oauth2.AuthorizationCodeFlow.** {*;}
-keepnames,includedescriptorclasses class com.google.api.client.util.** {*;}

-dontwarn com.wuman.android.auth.**
##android-oauth-client rules - END

## GSON 2.2.4 specific rules ##
-dontnote com.google.gson.internal.UnsafeAllocator
-dontwarn sun.misc.Unsafe
-keep,includedescriptorclasses class com.google.gson.stream.** { *; }

-keep public class com.pl.azurestorageexplorer.models.** {*;}
-keep public class com.pl.azurestorageexplorer.storage.** {*;}
-keep public class com.pl.azurestorageexplorer.storage.models.** {*;}

-keep,includedescriptorclasses class android.support.v4.** {*;}
-keepnames,includedescriptorclasses class android.support.v4.** {*;}

-keep,includedescriptorclasses public class android.support.v7.widget.** { *; }
-keep,includedescriptorclasses public class android.support.v7.internal.widget.** { *; }
-keep,includedescriptorclasses public class android.support.v7.internal.view.menu.** { *; }

-keep,includedescriptorclasses public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

-dontwarn android.support.design.**
-keep,includedescriptorclasses class android.support.design.** { *; }
-keep,includedescriptorclasses interface android.support.design.** { *; }
-keep,includedescriptorclasses public class android.support.design.R$* { *; }

-keep,includedescriptorclasses class okhttp3.**
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

-keep,includedescriptorclasses class okio.** { *; }
-dontwarn okio.**
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

-dontwarn com.google.common.cache.**
-dontwarn com.google.common.primitives.**
-dontwarn android.support.design.**
-dontwarn android.support.v4.app.NotificationCompatBase
-dontwarn android.support.v4.app.NotificationCompatGingerbread
-dontwarn java.awt.**
-dontwarn com.google.vending.licensing.**
-dontwarn com.android.vending.licensing.**
-dontwarn com.google.android.gms.**
-dontwarn android.support.v4.media.**
-dontwarn android.support.v4.**
-dontwarn javax.annotation**

-keep,includedescriptorclasses class com.fasterxml.** { *; }
-dontwarn com.fasterxml.**
-keep,includedescriptorclasses public class org.codehaus.**

-keep,includedescriptorclasses class com.microsoft.azure.** {*;}