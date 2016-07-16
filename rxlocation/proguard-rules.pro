# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/julionb/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# Avoid changing of any file names (useles since this is an open source library) but keep shrinking
# the class files to reduce the size of the library package.

-dontobfuscate

# Prevent removing the entry points of the library while performing shrinking.
-keep public class net.kjulio.rxlocation.RxLocation {
    public *;
}
