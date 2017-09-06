# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\AndroidSDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-ignorewarnings
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*


-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.preference.Preference
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.support.annotation.**
-keep public class * extends android.support.v7.**


-keep public class * extends android.view.view{
	public <init>(android.content.Context);
	public <init>(android.content.Context,android.util.AttributeSet);
	public <init>(android.content.Context,android.util.AttributeSet,int);
	public void set*(***);
	*** get* ();
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * implements java.io.Serializable{
	static final long serialVersionUID;
	private static final java.io.ObjectStreamField[] srrialPersistentFields;
	!static !transient <fields>;
	private void writeObject(java.io.ObjectOutputStream);
	private void readObject(java.io.ObjectInputStream);
	java.lang.Object writeReplace();
	java.lang.Object readResolve();
}
-keepclasseswithmembernames class * {
    native <methods>;
}

#glide 混淆start
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
##end


#okhttp3
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**

#gson
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }


#rxjava和rxandroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# 使用注解
-keepattributes *Annotation*,Signature
#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}


#retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

#picasso
-dontwarn com.squareup.okhttp.**

#retrolambda
-dontwarn java.lang.invoke.*

#picasso 混淆
-dontwarn com.squareup.okhttp.**


#
-keep class com.pax.ipp.tools.ui.view.*{ *; }
-keep class com.pax.ipp.tools.mvp.impl.*{*;}
-keep class com.pax.ipp.tools.mvp.presenter.*{*;}
-keep class com.pax.ipp.tools.model.*{*;}
-keep class com.pax.ipp.tools.adapter.*{*;}
-keep class com.pax.ipp.tools.injector.*{*;}
-keep class com.pax.ipp.tools.service.*{*;}


-keep class android.content.pm.IPackageDataObserver { *; }
-keep class android.content.pm.IPackageStatsObserver { *; }
-keep class PackageStats{ *; }