package wxgaly.android.forceapkdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle

/**
 *  layout.
 *
 * @author Created by WXG on 2019-11-05 11:45.
 * @version V1.0
 */
class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_layout)
        Thread {
            Thread.sleep(1000)
            startActivity(Intent(this, ScrollingActivity::class.java))
            finish()
        }.start()

    }

}