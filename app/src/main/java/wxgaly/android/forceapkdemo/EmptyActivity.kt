package wxgaly.android.forceapkdemo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 *  wxgaly.android.forceapkdemo.
 *
 * @author Created by WXG on 2019-10-25 15:30.
 * @version V1.0
 */
class EmptyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textView = TextView(this)
        textView.text = "这是一个空页面"
        setContentView(textView)
    }

}