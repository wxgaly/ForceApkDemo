package wxgaly.android.proxy_lib.kotlin

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.text.TextUtils
import java.io.*
import kotlin.properties.Delegates


/**
 *  wxgaly.android.proxy_lib.
 *
 * @author Created by WXG on 2019-10-28 15:28.
 * @version V1.0
 */
class ProxyApplication : Application() {

    //定义好的加密后的文件的存放路径
    private var app_name by Delegates.notNull<String>()
    private var app_dex_dir by Delegates.notNull<String>()
    private val desCryptUtil =
        DesCryptUtil(DEFAULT_KEY)
    var isBindReal: Boolean = false
    var delegate by Delegates.notNull<Application>()

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        //获取用户填入的metaData
        getMetaData()

        //得到当前apk文件
        val apkFile = File(applicationInfo.sourceDir)

        //把apk解压  这个目录中的内容需要root权限才能使用
        val appDexDir = getDir(app_name + "_" + app_dex_dir, Context.MODE_PRIVATE)

        val appDir = File(appDexDir, "app")
        val dexDir = File(appDir, "dexDir")

        //得到我们需要加载的dex文件
        val dexFiles = mutableListOf<File>()

        //进行解密 （最好做md5文件校验）
        if (!dexDir.exists() || dexDir.list().isEmpty()) {
            //把apk解压到appDir
            ZipUtil.UnZipFolder(apkFile.absolutePath, appDir.absolutePath)
//            Zip.unZip(apkFile,appDir);
            //获取目录下所有的文件
            val files = appDir.listFiles()
            files.forEach { file ->
                val name = file.name
                if (name.endsWith(".dexs") && !TextUtils.equals(name, "classes.dex")) {
                    var fin: FileInputStream? = null
                    var fos: FileOutputStream? = null
                    var bos: ByteArrayOutputStream? = null

                    try {
                        //读取文件内容
                        bos = ByteArrayOutputStream()

                        fin = FileInputStream(file)

                        //解密
                        val decypt = desCryptUtil.decrypt(
                            FileUtil.readFileBytes(
                                file
                            )!!)
                        //写到指定的目录
                        fos = FileOutputStream(file)
                        fos.write(decypt)
                        fos.flush()
                        fos.close()

                        dexFiles.add(file)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        bos?.close()
                        fin?.close()
                        fos?.close()
                    }
                }
            }

        } else {
            dexDir.listFiles().forEach {
                dexFiles.add(it)
            }
        }

        try {
            loadDex(dexFiles, appDexDir)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCreate() {
        super.onCreate()
        try {
            bindRealApplication()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getPackageName(): String {
        if (!TextUtils.isEmpty(app_name)) {
            return ""
        }
        return super.getPackageName()
    }

    override fun createPackageContext(packageName: String?, flags: Int): Context {
        if (TextUtils.isEmpty(app_name)) {
            return super.createPackageContext(packageName, flags)
        }
        try {
            bindRealApplication()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return delegate
    }

    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    private fun bindRealApplication() {

        if (isBindReal) {
            return
        }

        if (TextUtils.isEmpty(app_name)) {
            return
        }

        //得到attchBaseContext(context) 传入的上下文 ContextImpl
        val baseContext = baseContext

        //创建用户真实的application  （MyApplication）
        var delegateClass: Class<*>? = Class.forName(app_name)

        delegate = delegateClass!!.newInstance() as Application

        //得到attch()方法
        val attach = Application::class.java.getDeclaredMethod("attach", Context::class.java)
        attach.isAccessible = true
        attach.invoke(delegate, baseContext)

        //获取ContextImpl ----> ,mOuterContext(app);  通过Application的attachBaseContext回调参数获取
        val contextImplClass = Class.forName("android.app.ContextImpl")

        //获取mOuterContext属性
        val mOuterContextField = contextImplClass.getDeclaredField("mOuterContext")
        mOuterContextField.isAccessible = true
        mOuterContextField.set(baseContext, delegate)

        //ActivityThread  ----> mAllApplication(ArrayList)  ContextImpl的mMainThread属性
        val mMainThreadField = contextImplClass.getDeclaredField("mMainThread")
        mMainThreadField.isAccessible = true
        val mMainThread = mMainThreadField.get(baseContext)

        //ActivityThread  ----->  mInitialApplication       ContextImpl的mMainThread属性
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val mInitialApplicationField = activityThreadClass.getDeclaredField("mInitialApplication")
        mInitialApplicationField.isAccessible = true
        mInitialApplicationField.set(mMainThread, delegate)

        //ActivityThread ------>  mAllApplications(ArrayList)   ContextImpl的mMainThread属性
        val mAllApplicationsField = activityThreadClass.getDeclaredField("mAllApplications")
        mAllApplicationsField.isAccessible = true
        val mApplications = mAllApplicationsField.get(mMainThread) as ArrayList<Application>
        mApplications.remove(this)
        mApplications.add(delegate)

        //LoadedApk ----->  mApplicaion             ContextImpl的mPackageInfo属性
        val mPackageInfoField = contextImplClass.getDeclaredField("mPackageInfo")
        mPackageInfoField.isAccessible = true
        val mPackageInfo = mPackageInfoField.get(baseContext)

        val loadedApkClass = Class.forName("android.app.LoadedApk")
        val mApplicationField = loadedApkClass.getDeclaredField("mApplication")
        mApplicationField.isAccessible = true
        mApplicationField.set(mPackageInfo, delegate)

        //修改ApplicationInfo  className  LoadedApk
        val mApplicationInfoField = loadedApkClass.getDeclaredField("mApplicationInfo")
        mApplicationInfoField.isAccessible = true
        val mApplicationInfo = mApplicationInfoField.get(mPackageInfo) as ApplicationInfo
        mApplicationInfo.className = app_name

        delegate.onCreate()
        isBindReal = true
    }

    @Throws(Exception::class)
    private fun loadDex(dexFiles: List<File>, versionDir: File) {
        //1、获取pathList
        val pathListField = ReflectUtils.getDeclaredField(
            getClassLoader(),
            "pathList"
        )
        val pathList = pathListField?.get(getClassLoader())
        //2、获取数组dexElements
        val dexElementsField =
            ReflectUtils.getDeclaredField(pathList!!, "dexElements")
        val dexElements = dexElementsField?.get(pathList) as Array<Any>
        //3、反射到初始化makePathElements的方法
        val makeDexElements = ReflectUtils.getMethod(
            pathList,
            "makePathElements",
            List::class.java,
            File::class.java,
            List::class.java
        )

        val suppressedException = mutableListOf<IOException>()
        val addElements = makeDexElements!!.invoke(
            pathList,
            dexFiles,
            versionDir,
            suppressedException
        ) as Array<Any>

        val newElements = java.lang.reflect.Array.newInstance(
            dexElements.javaClass.componentType,
            dexElements.size + addElements.size
        ) as Array<Any>

        System.arraycopy(dexElements, 0, newElements, 0, dexElements.size)
        System.arraycopy(addElements, 0, newElements, dexElements.size, addElements.size)

        //替换classloader中的element数组
        dexElementsField.set(pathList, newElements)
    }

    private fun getMetaData() {

        try {
            val applicationInfo = packageManager.getApplicationInfo(
                packageName, PackageManager.GET_META_DATA
            )
            val metaData = applicationInfo.metaData
            if (null != metaData) {
                if (metaData.containsKey(APP_NAME)) {
                    app_name = metaData.getString(APP_NAME)!!
                }
                if (metaData.containsKey(APP_DEX_DIR)) {
                    app_dex_dir = metaData.getString(APP_DEX_DIR)!!
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}