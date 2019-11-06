package wxgaly.android.proxy_lib.kotlin

import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.properties.Delegates

/**
 *  wxgaly.android.proxy_lib.
 *
 * @author Created by WXG on 2019-10-28 16:33.
 * @version V1.0
 */
class DesCryptUtil(strKey: String) {

    /** 加密工具      */
    private var encryptCipher by Delegates.notNull<Cipher>()

    /** 解密工具      */
    private var decryptCipher by Delegates.notNull<Cipher>()

    init {
        val key = getKey(strKey.toByteArray())
        encryptCipher = Cipher.getInstance("DES")
        encryptCipher.init(Cipher.ENCRYPT_MODE, key)
        decryptCipher = Cipher.getInstance("DES")
        decryptCipher.init(Cipher.DECRYPT_MODE, key)
    }

    @Throws(Exception::class)
    private fun getKey(arr: ByteArray): Key {
        /* 创建一个空的8位字节数组（默认值为0）*/
        val newArr = ByteArray(8)
        /* 将原始字节数组转换为8位*/
        var i = 0
        while (i < arr.size && i < newArr.size) {
            newArr[i] = arr[i]
            i++
        }
        /* 生成密钥*/
        return SecretKeySpec(newArr, "DES")
    }

    /**
     * 加密字节数组
     *
     * @param arr
     * 需加密的字节数组
     * @return 加密后的字节数组
     * @throws
     */
    @Throws(Exception::class)
    fun encrypt(arr: ByteArray): ByteArray {
        return encryptCipher.doFinal(arr)
    }

    /**
     * 解密字节数组
     *
     * @param arr
     * 需解密的字节数组
     * @return 解密后的字节数组
     * @throws
     */
    @Throws(Exception::class)
    fun decrypt(arr: ByteArray): ByteArray {
        return decryptCipher.doFinal(arr)
    }

}