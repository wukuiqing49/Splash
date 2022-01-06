![效果.gif](https://upload-images.jianshu.io/upload_images/5317456-d3242a8c9e5d3e34.gif?imageMogr2/auto-orient/strip)

由于Android启动的冷启动白屏问题 ,Google终于看不下去了.从 Android 12 开始，在所有应用的[冷启动](https://developer.android.com/topic/performance/vitals/launch-time#cold)和[温启动](https://developer.android.com/topic/performance/vitals/launch-time#warm)期间，系统一律会应用 [Android 系统的默认启动画面](https://developer.android.com/about/versions/12/features/splash-screen)(意味着Android12以后强制执行SplashScreen),

**重要提示**：如果您之前已经在 Android 11 或更低版本中实现自定义启动画面，则需要将您的应用迁移到 [`SplashScreen`](https://developer.android.com/reference/android/window/SplashScreen) API，以确保它会在 Android 12 及更高版本中正确显示。如需了解相关说明，请参阅[将现有的启动画面实现迁移到 Android 12](https://developer.android.com/guide/topics/ui/splash-screen/migrate)。

如果是Android12以前的版本就需要做兼容了 google 提供了core-splashscreen库兼容之前的版本.以下就是参考官方文档以及百度大法简单的使用

#### 引用:
     //splashscreen 启动页
    implementation "androidx.core:core-splashscreen:1.0.0-alpha02"
    //携程
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.2"

####Style配置:
```
<!--    启动页Style -->
    <style name="Theme.TestSplashScreen.Starting" parent="Theme.SplashScreen">
        <!--展示背景色-->
        <item name="windowSplashScreenBackground">@color/white</item>
        <!-- 启动页展示的图片-->
        <item name="windowSplashScreenAnimatedIcon">@drawable/ic_v</item>
        <!-- 展示时常(低版本不生效需要代码设置)-->
        <item name="windowSplashScreenAnimationDuration">3000</item>
        <!-- 启动画面退出后 Activity 的主题-->
        <item name="postSplashScreenTheme">@style/Launcher</item>
    </style>

    <!--    启动页下一页的Style-->
    <style name="Launcher">
        <item name="windowActionBar">false</item>
        <item name="windowNoTitle">true</item>
        <item name="android:windowTranslucentStatus">true</item>
        <item name="android:windowTranslucentNavigation">true</item>
    </style>

```

####AndroidManifest.xml 配置:
```
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true">
<!--        配置启动页的Style -->
        <activity
            android:name=".SplashActivity"
            android:theme="@style/Theme.TestSplashScreen.Starting">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <!--        配置首页的Style 必须 AppCompat 下的Style否则报错 -->
        <activity
            android:name=".MainActivity"
            android:theme="@style/Theme.MaterialComponents.Light.NoActionBar" />
    </application>

```
####页面实现:
```
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class SplashActivity : ComponentActivity() {
    private var keepOnScreen = AtomicBoolean(true)
    var splashScreen: SplashScreen?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //初始化操作(必须放在setContentView()之前)
         splashScreen= installSplashScreen()
        setContentView(R.layout.activity_splash)

        //设置欢迎页展示时间
        countDownTime ()
        //展示完毕的监听
        splashScreen!!.setOnExitAnimationListener { provider ->
            //移除监听
            provider.remove()
            //跳转下个页面
            // 进入主界面，顺便给个 FadeOut 退场动画
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            overridePendingTransition(0, R.anim.live_activity_out)
        }
    }
    //设置欢迎页展示时间
    private fun countDownTime() {
        GlobalScope.launch {
            delay(2000)
            //lanch展示完毕
            keepOnScreen.compareAndSet(true, false)
        }
        //绑定数据
        splashScreen!!.setKeepVisibleCondition { keepOnScreen.get() }


    }
}
```

####注意:Android12 和 splashscreen androidx库的对比

```
进场部分的功能对比	  Jetpack版	   Android 12版
ScreenBackground	    YES	             YES
ScreenIcon	            YES              YES
AnimatedVectorDrawable   NA	             YES
IconBackgroundColor	     NA	             YES
BrandingImage	         NA	             YES

```
备注：后面会讲到SplashScreen库的实现原理，面向低版本的进场效果本质上是一个 LayerDrawable 。说实话，对于支持 Vector Drawable 动画、 Adaptive Icon 背景是无能为力的。但笔者认为在代码层面加入些额外处理，是可以做到完美支持的，期待后期升级能完善一下！

    





##总结:
在官方没出稳定版本之前不建议使用,毕竟这是个测试版.可以当作一个兼容的点来了解一下,方便后期切换正式版本

####[1.官方介绍](https://developer.android.com/about/versions/12/features/splash-screen)
####[2.资源库引用](https://developer.android.com/jetpack/androidx/versions/all-channel)
####[3.源码地址](https://github.com/wukuiqing49/Splash)


