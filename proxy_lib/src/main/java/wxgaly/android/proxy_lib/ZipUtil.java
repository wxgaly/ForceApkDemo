package wxgaly.android.proxy_lib;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * wxgaly.android.proxy_lib.test.
 *
 * @author Created by WXG on 2019-11-01 11:30.
 * @version V1.0
 */
public class ZipUtil {

    /**
     * DeCompress the ZIP to the path
     *
     * @param zipFileString name of ZIP
     * @param outPathString path to be unZIP
     * @throws Exception
     */
    public static void UnZipFolder(String zipFileString, String outPathString) throws Exception {

        ZipFile zipFile = new ZipFile(zipFileString);

        Enumeration<?> enm = zipFile.entries();

        while (enm.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) enm.nextElement();

            if (entry.isDirectory()) {
                new File(outPathString + File.separator + entry.getName()).mkdirs();
                continue;
            }

            BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
            File file = new File(outPathString + File.separator + entry.getName());
            File parent = file.getParentFile();
            if (parent != null && (!parent.exists())) {
                parent.mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos, 2048);
            int count;
            byte[] data = new byte[2048];

            while ((count = bis.read(data, 0, 2048)) != -1) {
                bos.write(data, 0, count);
                bos.flush();
                if (count < data.length) {
                    break;
                }
            }

            bos.flush();
            bos.close();
            bis.close();
        }

        zipFile.close();
    }

    /**
     * DeCompress the ZIP to the path
     *
     * @param zipFileString name of ZIP
     * @param outPathString path to be unZIP
     * @param fileName      解压后文件名称
     * @throws Exception
     */
    public static void UnZipFolder(String zipFileString, String outPathString, List<String> fileName) throws Exception {

        ZipFile zipFile = new ZipFile(zipFileString);

        for (String s : fileName) {
            ZipEntry entry = zipFile.getEntry(s);

            if (entry.isDirectory()) {
                new File(outPathString + File.separator + entry.getName()).mkdirs();
                continue;
            }
            BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));

            File file = new File(outPathString + File.separator + s);
            File parent = file.getParentFile();
            if (parent != null && (!parent.exists())) {
                parent.mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos, 2048);
            int count;
            byte[] data = new byte[2048];

            while ((count = bis.read(data, 0, 2048)) != -1) {
                bos.write(data, 0, count);
                bos.flush();
                if (count < data.length) {
                    break;
                }
            }

            bos.flush();
            bos.close();
            bis.close();
        }

        zipFile.close();
    }

    /**
     * return files list(file and folder name) in the ZIP
     *
     * @param zipFileString ZIP name
     * @return
     * @throws Exception
     */
    public static List<String> GetFileNameList(String zipFileString)
            throws Exception {
        List<String> fileNameList = new ArrayList<String>();

        ZipFile zipFile = new ZipFile(zipFileString);

        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        ZipEntry zipEntry;
        String szName = "";
        while (entries.hasMoreElements()) {
            zipEntry = entries.nextElement();
            szName = zipEntry.getName();
            fileNameList.add(szName);
        }
        zipFile.close();
        return fileNameList;
    }

}
