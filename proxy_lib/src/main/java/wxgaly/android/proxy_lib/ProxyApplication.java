package wxgaly.android.proxy_lib;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * wxgaly.android.proxy_lib.
 * <p>
 * 加固的代理Application，目前由于打包出的jar中只有工程的类，如果使用kotlin的话就缺少kotlin的依赖jar，
 * 所以使用java实现，也是为了减少jar包的大小，加快安装速度
 *
 * @author Created by WXG on 2019-11-01 11:09.
 * @version V1.0
 */
public class ProxyApplication extends Application {

    private static final String TAG = "ProxyApplication";

    /**
     * 定义好的加密后的文件的存放路径
     */
    private String appName;
    private String appVersion;
    private boolean isBindReal;
    private Application delegate;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d(TAG, "attachBaseContext");

        //获取用户填入的metaData
        getMetaData();

        //准备dex文件
        if (Contants.BUILD_TYPE.equalsIgnoreCase(BuildConfig.BUILD_TYPE)) {
            prepareDexFiles();
        }

    }

    /**
     * 开始替换application
     */
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            bindRealApplication();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 让代码走入if的第三段中
     *
     * @return
     */
    @Override
    public String getPackageName() {
        if (!TextUtils.isEmpty(appName)) {
            return "";
        }
        return super.getPackageName();
    }

    @Override
    public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        if (TextUtils.isEmpty(appName)) {
            return super.createPackageContext(packageName, flags);
        }
        try {
            bindRealApplication();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return delegate;

    }

    private void getMetaData() {
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = applicationInfo.metaData;
            if (null != metaData) {
                if (metaData.containsKey(Contants.APP_NAME)) {
                    appName = metaData.getString(Contants.APP_NAME);
                }
                if (metaData.containsKey(Contants.APP_VERSION)) {
                    appVersion = metaData.getString(Contants.APP_VERSION);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 准备dex文件，如果解压过并解密过则直接加载。
     */
    private void prepareDexFiles() {
        Log.d(TAG, "prepare dex files");
        //得到当前apk文件
        File apkFile = new File(getApplicationInfo().sourceDir);

        //把apk解压  这个目录中的内容需要root权限才能使用
        File appDexDir = getDir(appName + "_" + this.appVersion, MODE_PRIVATE);

//        File appDir = new File(appDexDir, "app");
        File dexDir = new File(appDexDir, "dexDir");

        //得到我们需要加载的dex文件
        List<File> dexFiles = new ArrayList<>();
        //进行解密 （最好做md5文件校验）
        if (!dexDir.exists() || dexDir.list().length == 0) {
            if (!dexDir.exists()) {
                dexDir.mkdirs();
            }
            //把apk解压到appDir
            try {
                List<String> strings = ZipUtil.GetFileNameList(apkFile.getAbsolutePath());
                List<String> dexsFileNames = new ArrayList<>();
                for (String string : strings) {
                    if (string.endsWith(Contants.DEXS_SUFFIX)) {
                        Log.d(TAG, "zip: " + string);
                        dexsFileNames.add(string);
                    }
                }
                ZipUtil.UnZipFolder(apkFile.getAbsolutePath(), dexDir.getAbsolutePath(), dexsFileNames);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //获取目录下所有的文件
            File[] files = dexDir.listFiles();
            for (File file : files) {
                String name = file.getName();
                if (name.endsWith(Contants.DEXS_SUFFIX) && !TextUtils.equals(name, "classes.dex")) {
                    try {
                        DesCryptUtil desCryptUtil = new DesCryptUtil(Contants.DEFAULT_KEY);
                        //读取文件内容
                        byte[] bytes = FileUtil.readFileBytes(file);
                        //解密
                        byte[] decrypt = desCryptUtil.decrypt(bytes);
                        //写到指定的目录
                        File decryptDex = new File(dexDir, name);
                        FileOutputStream fos = new FileOutputStream(decryptDex);
                        fos.write(decrypt);
                        fos.flush();
                        fos.close();
                        Log.d(TAG, "decryptDex: " + decryptDex.getAbsolutePath());
                        dexFiles.add(decryptDex);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            Collections.addAll(dexFiles, dexDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(Contants.DEXS_SUFFIX);
                }
            }));
        }

        try {
            loadDex(dexFiles, dexDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 该方法的兼容性要注意，
     * 5.1以下的时候，方法名为makePathElements
     * 5.1以上包括5.1的时候，方法名为makeDexElements
     * 8.1该方法报错，目前没有查看8.1源码，不知道是否有其他变动
     *
     * @param dexFiles
     * @param dexDir
     * @throws Exception
     */
    private void loadDex(List<File> dexFiles, File dexDir) throws Exception {
        //1、获取pathList
        Field pathListField = ReflectUtil.getDeclaredField(getClassLoader(), "pathList");
        pathListField.setAccessible(true);
        Object pathList = pathListField.get(getClassLoader());
        //2、获取数组dexElements
        Field dexElementsField = ReflectUtil.getDeclaredField(pathList, "dexElements");
        dexElementsField.setAccessible(true);
        Object[] dexElements = (Object[]) dexElementsField.get(pathList);
        //3、反射到初始化makeDexElements的方法（注：Android5.1以后，Google对方法名进行了修改，原方法名为makePathElements，参数类型是(List.class, File.class, List.class)）
        Method makeDexElements = ReflectUtil.getMethod(pathList, "makeDexElements", ArrayList.class, File.class, ArrayList.class);
        makeDexElements.setAccessible(true);
        ArrayList<IOException> suppressedException = new ArrayList<>();
        Object[] addElements = (Object[]) makeDexElements.invoke(pathList, dexFiles, dexDir, suppressedException);

        Object[] newElements = (Object[]) Array.newInstance(dexElements.getClass().getComponentType(), dexElements.length + addElements.length);
        System.arraycopy(dexElements, 0, newElements, 0, dexElements.length);
        System.arraycopy(addElements, 0, newElements, dexElements.length, addElements.length);

        //替换classloader中的element数组
        dexElementsField.set(pathList, newElements);
    }

    /**
     * 下面主要是通过反射系统源码的内容，然后进行处理，把我们的内容加进去处理
     */
    private void bindRealApplication() throws Exception {
        if (isBindReal) {
            return;
        }

        if (TextUtils.isEmpty(appName)) {
            return;
        }

        //得到attchBaseContext(context) 传入的上下文 ContextImpl
        Context baseContext = getBaseContext();
        //创建用户真实的application  （MyApplication）
        Class<?> delegateClass = null;
        delegateClass = Class.forName(appName);

        delegate = (Application) delegateClass.newInstance();

        //得到attch()方法
        Method attach = Application.class.getDeclaredMethod("attach", Context.class);
        attach.setAccessible(true);
        attach.invoke(delegate, baseContext);

        //获取ContextImpl ----> ,mOuterContext(app);  通过Application的attachBaseContext回调参数获取
        Class<?> contextImplClass = Class.forName("android.app.ContextImpl");
        //获取mOuterContext属性
        Field mOuterContextField = contextImplClass.getDeclaredField("mOuterContext");
        mOuterContextField.setAccessible(true);
        mOuterContextField.set(baseContext, delegate);

        //ActivityThread  ----> mAllApplication(ArrayList)  ContextImpl的mMainThread属性
        Field mMainThreadField = contextImplClass.getDeclaredField("mMainThread");
        mMainThreadField.setAccessible(true);
        Object mMainThread = mMainThreadField.get(baseContext);

        //ActivityThread  ----->  mInitialApplication       ContextImpl的mMainThread属性
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Field mInitialApplicationField = activityThreadClass.getDeclaredField("mInitialApplication");
        mInitialApplicationField.setAccessible(true);
        mInitialApplicationField.set(mMainThread, delegate);

        //ActivityThread ------>  mAllApplications(ArrayList)   ContextImpl的mMainThread属性
        Field mAllApplicationsField = activityThreadClass.getDeclaredField("mAllApplications");
        mAllApplicationsField.setAccessible(true);
        ArrayList<Application> mApplications = (ArrayList<Application>) mAllApplicationsField.get(mMainThread);
        mApplications.remove(this);
        mApplications.add(delegate);

        //LoadedApk ----->  mApplicaion             ContextImpl的mPackageInfo属性
        Field mPackageInfoField = contextImplClass.getDeclaredField("mPackageInfo");
        mPackageInfoField.setAccessible(true);
        Object mPackageInfo = mPackageInfoField.get(baseContext);


        Class<?> loadedApkClass = Class.forName("android.app.LoadedApk");
        Field mApplicationField = loadedApkClass.getDeclaredField("mApplication");
        mApplicationField.setAccessible(true);
        mApplicationField.set(mPackageInfo, delegate);

        //修改ApplicationInfo  className  LoadedApk
        Field mApplicationInfoField = loadedApkClass.getDeclaredField("mApplicationInfo");
        mApplicationInfoField.setAccessible(true);
        ApplicationInfo mApplicationInfo = (ApplicationInfo) mApplicationInfoField.get(mPackageInfo);
        mApplicationInfo.className = appName;

        delegate.onCreate();
        isBindReal = true;
    }

}
