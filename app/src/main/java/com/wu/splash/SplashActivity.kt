package com.wu.splash
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