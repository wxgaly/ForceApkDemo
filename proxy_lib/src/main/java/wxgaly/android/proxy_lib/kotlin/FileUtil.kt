package wxgaly.android.proxy_lib.kotlin

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 *  wxgaly.android.proxy_lib.
 *
 * @author Created by WXG on 2019-10-29 09:38.
 * @version V1.0
 */
object FileUtil {

    @Throws(IOException::class)
    fun readFileBytes(file: File): ByteArray? {
        val buffer = ByteArray(4096)

        var bos: ByteArrayOutputStream?
        var fis: FileInputStream?

        while (true) {
            bos = ByteArrayOutputStream()
            fis = FileInputStream(file)

            val len = fis.read(buffer)

            if (len != -1) {
                bos.write(buffer, 0, len)
            } else {
                fis.close()
                bos.close()
                return bos.toByteArray()
            }
        }

    }


}