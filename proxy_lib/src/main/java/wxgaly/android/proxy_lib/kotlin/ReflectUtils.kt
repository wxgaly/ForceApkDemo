package wxgaly.android.proxy_lib.kotlin

import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 *  wxgaly.android.proxy_lib.
 *
 * @author Created by WXG on 2019-10-29 11:12.
 * @version V1.0
 */
object ReflectUtils {

    fun getDeclaredField(`object`: Any, fieldName: String): Field? {
        var field: Field? = null
        var clazz: Class<*> = `object`.javaClass
        while (clazz != Any::class.java) {
            try {
                field = clazz.getDeclaredField(fieldName)
                return field
            } catch (e: Exception) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
            }

            clazz = clazz.superclass
        }
        return null
    }

    fun getMethod(`object`: Any, methodName: String, vararg elements: Class<*>): Method? {
        var method: Method? = null
        var clazz: Class<*> = `object`.javaClass
        while (clazz != Any::class.java) {
            try {
                method = clazz.getDeclaredMethod(methodName, *elements)
                return method
            } catch (e: Exception) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
            }

            clazz = clazz.superclass
        }
        return null
    }

}