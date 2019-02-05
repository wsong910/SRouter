# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/Cellar/android-sdk/24.3.3/tools/proguard/proguard-android.txt
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
-optimizationpasses 5
-dontusemixedcaseclassnames # 混淆时不使用大小写混写的类名
-dontskipnonpubliclibraryclasses #不跳过 libraray 中的非 public 类
-dontskipnonpubliclibraryclassmembers
-dontpreverify # 关闭预校验功能，Android 平台上不需要，所以默认是关闭的
-verbose # 打印处理过程的信息
#混淆包名
-ignorewarnings
-repackageclasses ''
#混淆包名 end
-allowaccessmodification
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-adaptresourcefilenames    **.properties,**.gif,**.jpg
-adaptresourcefilecontents **.properties,META-INF/MANIFEST.MF
#-dontoptimize # 关闭优化功能，因为优化可能会造成一些潜在的风险，无法保证在所有版本的 Dalvik 都正常运行
# Disabling obfuscation is useful if you collect stack traces from production crashes
# (unless you are using a system that supports de-obfuscate the stack traces).
-dontobfuscate
###-----------基本配置-不能被混淆的------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
#support.v4/v7包不混淆
-keep class android.support.** { *; }
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.** { *; }
-keep public class * extends android.support.v7.**
-keep interface android.support.v7.app.** { *; }
-dontnote android.support.**    # 忽略警告
-dontwarn android.support.**    # 忽略警告
-keep class android.support.annotation.Keep     # 保留 Keep 注解
# 接下来的规则都是保留 Keep 注解标记的类型
-keep @android.support.annotation.Keep class * {*;}   # 标记类时，保留类及其所有成员
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}
# 标记方法时，保留标注的方法和包含它的类名
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}
# 标记字段时，保留标记的字段和包含它的类名
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}
#标记构造函数时，保留标记的构造函数和包含它的类名
#native and callbacks
-keepclasseswithmembernames class * {
    native <methods>;
}
#保持注解继承类不混淆
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keep class * extends java.lang.annotation.Annotation {*;}
#保持Serializable实现类不被混淆
-keepnames class * implements java.io.Serializable
#保持Serializable不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}
#保持枚举enum类不被混淆
-keepclassmembers enum * {
  public static **[] values();
 public static ** valueOf(java.lang.String);
}
#自定义组件不被混淆
-keep public class * extends android.view.View {
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}
#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}
-keepclassmembers class * {
    void *(**On*Event);
}

#webview
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}
# 保留 JavascriptInterface 注解标记的方法，不然 js 调用时就会找不到方法
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
# React Native

# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.proguard.annotations.DoNotStrip
-keep,allowobfuscation @interface com.facebook.proguard.annotations.KeepGettersAndSetters
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.proguard.annotations.DoNotStrip class *
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.proguard.annotations.DoNotStrip *;
    @com.facebook.common.internal.DoNotStrip *;
}

-keepclassmembers @com.facebook.proguard.annotations.KeepGettersAndSetters class * {
  void set*(***);
  *** get*();
}

-keep class * extends com.facebook.react.bridge.JavaScriptModule { *; }
-keep class * extends com.facebook.react.bridge.NativeModule { *; }
-keepclassmembers,includedescriptorclasses class * { native <methods>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.UIProp <fields>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactProp <methods>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactPropGroup <methods>; }

-dontwarn com.facebook.react.**
-keep class com.facebook.react.**{*;}

# gif image

-keep class com.facebook.imagepipeline.animated.factory.AnimatedFactoryImpl {
  public AnimatedFactoryImpl(com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory, com.facebook.imagepipeline.core.ExecutorSupplier);
}

#
-keepattributes JNINamespace
-keepattributes CalledByNative
-keepattributes EnclosingMethod

# okhttp

-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# okio

-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

#wechat

-keep class com.tencent.mm.opensdk.** {
   *;
}
-keep class com.tencent.wxop.** {
   *;
}
-keep class com.tencent.mm.sdk.** {
   *;
}

# dingding
-keep class com.android.dingtalk.share.ddsharemodule.** {
*;
}

#  xUtils3相关

-keepattributes Signature,*Annotation*
-keep public class org.xutils.** {
    public protected *;
}
-keep public interface org.xutils.** {
    public protected *;
}
-keepclassmembers class * extends org.xutils.** {
    public protected *;
}
-keepclassmembers @org.xutils.db.annotation.* class * {*;}
-keepclassmembers @org.xutils.http.annotation.* class * {*;}
-keepclassmembers class * {
    @org.xutils.view.annotation.Event <methods>;
}
-dontwarn org.xutils.**


-keepnames class * extends android.view.View

-keep class * extends android.app.Fragment {
 public void setUserVisibleHint(boolean);
 public void onHiddenChanged(boolean);
 public void onResume();
 public void onPause();
}
-keep class android.support.v4.app.Fragment {
 public void setUserVisibleHint(boolean);
 public void onHiddenChanged(boolean);
 public void onResume();
 public void onPause();
}
-keep class * extends android.support.v4.app.Fragment {
 public void setUserVisibleHint(boolean);
 public void onHiddenChanged(boolean);
 public void onResume();
 public void onPause();
}

# cloud push

-keepclasseswithmembernames class ** {
    native <methods>;
}
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.taobao.** {*;}
-keep class com.alibaba.** {*;}
-keep class com.alipay.** {*;}
-keep class com.ut.** {*;}
-keep class com.ta.** {*;}
-keep class anet.**{*;}
-keep class anetwork.**{*;}
-keep class org.android.spdy.**{*;}
-keep class org.android.agoo.**{*;}
-keep class android.os.**{*;}
-dontwarn com.taobao.**
-dontwarn com.alibaba.**
-dontwarn com.alipay.**
-dontwarn anet.**
-dontwarn org.android.spdy.**
-dontwarn org.android.agoo.**
-dontwarn anetwork.**
-dontwarn com.ut.**
-dontwarn com.ta.**

# Fabric
# see https://docs.fabric.io/android/crashlytics/dex-and-proguard.html

-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

# Intercom
-keep class intercom.** { *; }
-keep class io.intercom.android.** { *; }
-dontwarn intercom.**
-dontwarn io.intercom.**
-keep class org.slf4j.**
-dontwarn org.slf4j.**
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry
#-keep class org.apache.commons.codec.binary.**
-keep class org.apache.commons.** { *; }
-keep interface org.apache.commons.** { *; }

#Aliyun-log
-keep class com.taobao.securityjni.**{*;}
 -keep class com.taobao.wireless.security.**{*;}
 -keep class com.ut.secbody.**{*;}
 -keep class com.taobao.dp.**{*;}
 -keep class com.alibaba.wireless.security.**{*;}
 -keep class com.alibaba.security.rp.**{*;}
 -keep class com.alibaba.sdk.android.**{*;}
 -keep class com.alibaba.security.biometrics.**{*;}
 -keep class android.taobao.windvane.**{*;}

#xwalk crosswork
# Too many hard code reflections between xwalk wrapper and bridge,so
# keep all xwalk classes.
-keep class org.xwalk.**{ *; }
-keep interface org.xwalk.**{ *; }
-keep class com.example.extension.**{ *; }
-keep class org.crosswalkproject.**{ *; }

-keep @chuxin.shimo.shimowendang.utils.NotProguard class * {*;}
-keep class * {
        @chuxin.shimo.shimowendang.utils.NotProguard <fields>;
}
-keepclassmembers class * {
        @chuxin.shimo.shimowendang.utils.NotProguard <methods>;
}
# 保留 JavascriptInterface 注解标记的方法，不然 js 调用时就会找不到方法
-keepclassmembers class * {
    @org.xwalk.core.JavascriptInterface <methods>;
}

# Rules for org.chromium classes:
# Keep annotations used by chromium to keep members referenced by native code
-keep class org.chromium.base.*Native*
-keep class org.chromium.base.annotations.JNINamespace
-keepclasseswithmembers class org.chromium.** {
    @org.chromium.base.AccessedByNative <fields>;
}
-keepclasseswithmembers class org.chromium.** {
    @org.chromium.base.*Native* <methods>;
}
-keep public class chuxin.shimo.shimowendang.webview.CrosswalkWebView$CrosswalkWebViewBridge {
    public void closeWebView();
}
-keep public class chuxin.shimo.shimowendang.webview.ShimoAdvancedWebView$AdvanceWebViewBridge {
    public void closeWebView();
}

-keep class org.chromium.** {
    native <methods>;
}

# Keep methods used by reflection and native code
-keep class org.chromium.base.UsedBy*
-keep @org.chromium.base.UsedBy* class *
-keepclassmembers class * {
    @org.chromium.base.UsedBy* *;
}

-keep @org.chromium.base.annotations.JNINamespace* class *
-keepclassmembers class * {
    @org.chromium.base.annotations.CalledByNative* *;
}

# Suppress unnecessary warnings.
-dontnote org.chromium.net.AndroidKeyStore
# Objects of this type are passed around by native code, but the class
# is never used directly by native code. Since the class is not loaded, it does
# not need to be preserved as an entry point.
-dontnote org.chromium.net.UrlRequest$ResponseHeadersMap

# Generate by aapt. may only need for testing, just add them here.
-keep class org.chromium.ui.ColorPickerAdvanced { <init>(...); }
-keep class org.chromium.ui.ColorPickerMoreButton { <init>(...); }
-keep class org.chromium.ui.ColorPickerSimple { <init>(...); }

#神策
-dontwarn com.sensorsdata.analytics.android.**
-keep class com.sensorsdata.analytics.android.** {
*;
}
# 如果使用了 DataBinding
-dontwarn android.databinding.**
-keep class android.databinding.** { *; }

# Ping++ 混淆过滤
-dontwarn com.pingplusplus.**
-keep class com.pingplusplus.** {*;}

# 支付宝混淆过滤
-dontwarn com.alipay.**
-keep class com.alipay.** {*;}

# 微信或QQ钱包混淆过滤
-dontwarn  com.tencent.**
-keep class com.tencent.** {*;}

# 内部WebView混淆过滤
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
#百度asr
-keep class com.baidu.speech.**{*;}
-dontwarn org.joda.convert.FromString
-dontwarn org.joda.convert.ToString

#fast-image
-keep public class com.dylanvann.fastimage.* {*;}
-keep public class com.dylanvann.fastimage.** {*;}

-keep class chuxin.shimo.shimowendang.smrouter.** { *; }