package wxgaly.android.proxy_lib;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * wxgaly.android.proxy_lib.test.
 *
 * @author Created by WXG on 2019-11-01 11:18.
 * @version V1.0
 */
public class FileUtil {

    /**
     *
     * @param file
     * @return
     */
    public static byte[] readFileBytes(File file) {
        byte[] buffer = new byte[4096];

        ByteArrayOutputStream bos = null;
        FileInputStream fileInputStream = null;

        try {
            bos = new ByteArrayOutputStream();
            fileInputStream = new FileInputStream(file);

            int count = 0;
            while ((count = fileInputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, count);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }

                if (fileInputStream != null) {
                    fileInputStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}
