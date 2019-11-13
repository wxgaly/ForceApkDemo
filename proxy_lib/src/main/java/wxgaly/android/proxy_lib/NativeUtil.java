package wxgaly.android.proxy_lib;

/**
 * wxgaly.android.proxy_lib.
 *
 * @author Created by WXG on 2019-11-12 11:52.
 * @version V1.0
 */
public class NativeUtil {

    static {
        System.loadLibrary("native_force");
    }

    public static native String getString();

    /**
     * dalvik做 dex2oat，生成的文件是在 /data/dalvik-cache/arm/data@app@包名-数字@base.apk@classes.dex
     * 因为是普通app，安装的位置在/data/app目录下，系统app则在system下
     *
     * 首次安装为了提升速度，需要禁用dex2oat，然后后台去做dex2oat的操作，下次启动的时候有就用dex2oat，没有则继续禁用
     *
     * @return
     */
    public static native boolean disableDex2oat();

}
