package wxgaly.android.forceapkdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_scrolling.*
import wxgaly.android.proxy_lib.NativeUtil

class ScrollingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        Log.d("ScrollingActivity", SourceApplication.getAppContext()!!.packageName)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action 1111: ${SourceApplication.getAppContext()?.packageName}  " +
                    "${NativeUtil.getString()} ||||  disableDex2oat = ${NativeUtil.disableDex2oat()}"  , Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
//            val intent = Intent(ScrollingActivity@this, EmptyActivity::class.java)
//            startActivity(intent)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
