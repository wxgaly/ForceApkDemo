package wxgaly.android.forceapkdemo;

import android.app.Application;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * wxgaly.android.forceapkdemo.
 *
 * @author Created by WXG on 2019-11-04 15:18.
 * @version V1.0
 */
public class SourceApplication extends Application {

    /**
     * {@link Application}
     */
    private static SourceApplication mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;
        Log.i("SourceApplication", "source apk onCreate:" + mAppContext.getPackageName());
    }

    @Nullable
    public static Application getAppContext() {
        return mAppContext;
    }
}
