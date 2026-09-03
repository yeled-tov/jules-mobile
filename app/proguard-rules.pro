# ProGuard rules for Jules Mobile
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep WebKit and Custom Tabs
-keep class androidx.webkit.** { *; }
-keep class androidx.browser.customtabs.** { *; }
