package wxgaly.android.proxy_lib;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * wxgaly.android.proxy_lib.test.
 *
 * @author Created by WXG on 2019-11-01 11:26.
 * @version V1.0
 */
public class ReflectUtil {

    /**
     * 反射变量
     *
     * @param object
     * @param fieldName
     * @return
     */
    public static Field getDeclaredField(Object object, String fieldName) {
        Field field = null;
        Class<?> clazz = object.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                return field;
            } catch (Exception e) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }
        return null;
    }

    /**
     * 反射方法
     *
     * @param object
     * @param methodName
     * @param parameterTypes
     * @return
     */
    public static Method getMethod(Object object, String methodName, Class<?>... parameterTypes) {
        Method method = null;
        Class<?> clazz = object.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
//                Method[] methods = clazz.getMethods();
//                for (Method method1 : methods) {
//                    Class<?>[] parameterTypes1 = method1.getParameterTypes();
//                    for (Class<?> aClass : parameterTypes1) {
//                        Log.d("ProxyApplication", clazz.getName() + " : "
//                                + method1.toGenericString() + " : " + aClass.getName());
//                    }
//                }

                method = clazz.getDeclaredMethod(methodName, parameterTypes);
                return method;
            } catch (Exception e) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }
        return null;
    }

    /**
     * 反射静态方法
     *
     * @param clazz
     * @param methodName
     * @param parameterTypes
     * @return
     */
    public static Method getStaticMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Method method = null;
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
//                Method[] methods = clazz.getMethods();
//                for (Method method1 : methods) {
//                    Class<?>[] parameterTypes1 = method1.getParameterTypes();
//                    for (Class<?> aClass : parameterTypes1) {
//                        Log.d("ProxyApplication", clazz.getName() + " : "
//                                + method1.toGenericString() + " : " + aClass.getName());
//                    }
//                }

                method = clazz.getDeclaredMethod(methodName, parameterTypes);
                return method;
            } catch (Exception e) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }
        return null;
    }

}
