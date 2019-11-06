package wxgaly.android.proxy_lib.kotlin

import java.io.*
import java.util.ArrayList
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 *  wxgaly.android.proxy_lib.
 *
 * @author Created by WXG on 2019-10-28 16:05.
 * @version V1.0
 */
object ZipUtil {

    /**
     * DeCompress the ZIP to the path
     *
     * @param zipFileString
     * name of ZIP
     * @param outPathString
     * path to be unZIP
     * @throws Exception
     */
    @Throws(Exception::class)
    fun UnZipFolder(zipFileString: String, outPathString: String) {

        val zipFile = ZipFile(zipFileString)

        val enm = zipFile.entries()

        while (enm.hasMoreElements()) {
            val entry = enm.nextElement() as ZipEntry

            if (entry.isDirectory) {
                File(outPathString + File.separator + entry.name).mkdirs()
                continue
            }

            val bis = BufferedInputStream(zipFile.getInputStream(entry))
            val file = File(outPathString + File.separator + entry.name)
            val parent = file.parentFile
            if (parent != null && !parent.exists()) {
                parent.mkdirs()
            }

            val fos = FileOutputStream(file)
            val bos = BufferedOutputStream(fos, 2048)
            var count: Int
            val data = ByteArray(2048)

            while (true) {
                count = bis.read(data, 0, 2048)
                if (count == -1) {
                    break
                }
                bos.write(data, 0, count)
                bos.flush()
                if (count < data.size) {
                    break
                }
            }

            bos.flush()
            bos.close()
            bis.close()
        }

        zipFile.close()

    }

    /**
     * DeCompress the ZIP to the path
     *
     * @param zipFileString
     * name of ZIP
     * @param outPathString
     * path to be unZIP
     * @param fileName
     * 解压后文件名称
     * @throws Exception
     */
    @Throws(Exception::class)
    fun UnZipFolder(zipFileString: String, outPathString: String, fileName: List<String>?) {

        val zipFile = ZipFile(zipFileString)

        val enm = zipFile.entries()

        var index = 0

        while (enm.hasMoreElements()) {
            val entry = enm.nextElement() as ZipEntry

            if (entry.isDirectory) {
                File(outPathString + File.separator + entry.name).mkdirs()
                continue
            }

            val bis = BufferedInputStream(zipFile.getInputStream(entry))

            var name = ""
            if (fileName != null && fileName.size >= index) {
                name = fileName[index]
            } else {
                name = entry.name
            }
            val file = File(outPathString + File.separator + name)
            val parent = file.parentFile
            if (parent != null && !parent.exists()) {
                parent.mkdirs()
            }

            val fos = FileOutputStream(file)
            val bos = BufferedOutputStream(fos, 2048)
            var count: Int
            val data = ByteArray(2048)

            while (true) {
                count = bis.read(data, 0, 2048)
                if (count == -1) {
                    break
                }
                bos.write(data, 0, count)
                bos.flush()
                if (count < data.size) {
                    break
                }
            }

            bos.flush()
            bos.close()
            bis.close()
            index++
        }

        zipFile.close()

    }

    /**
     * Compress file and folder
     *
     * @param srcFileString
     * file or folder to be Compress
     * @param zipFileString
     * the path name of result ZIP
     * @throws Exception
     */
    @Throws(Exception::class)
    fun ZipFolder(srcFileString: String, zipFileString: String) {
        // create ZIP
        val outZip = ZipOutputStream(FileOutputStream(zipFileString))
        // create the file
        val file = File(srcFileString)
        // compress
        ZipFiles(
            file.parent + File.separator,
            file.name,
            outZip
        )
        // finish and close
        outZip.finish()
        outZip.close()
    }

    /**
     * compress files
     *
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun ZipFiles(
        folderString: String,
        fileString: String,
        zipOutputSteam: ZipOutputStream?
    ) {
        if (zipOutputSteam == null) {
            return
        }
        val file = File(folderString + fileString)
        if (file.isFile) {
            val zipEntry = ZipEntry(fileString)
            val inputStream = FileInputStream(file)
            zipOutputSteam.putNextEntry(zipEntry)
            var len: Int
            val buffer = ByteArray(4096)
            while (true) {
                len = inputStream.read(buffer)
                if (len == -1) {
                    break
                }
                zipOutputSteam.write(buffer, 0, len)
            }
            zipOutputSteam.closeEntry()
        } else {
            // folder
            val fileList = file.list()
            // no child file and compress
            if (fileList!!.isEmpty()) {
                val zipEntry = ZipEntry(fileString + File.separator)
                zipOutputSteam.putNextEntry(zipEntry)
                zipOutputSteam.closeEntry()
            }
            // child files and recursion
            for (i in fileList.indices) {
                ZipFiles(
                    folderString,
                    fileString + File.separator + fileList[i],
                    zipOutputSteam
                )
            } // end of for
        }
    }

    /**
     * return the InputStream of file in the ZIP
     *
     * @param zipFileString
     * name of ZIP
     * @param fileString
     * name of file in the ZIP
     * @return InputStream
     * @throws Exception
     */
    @Throws(Exception::class)
    fun UpZip(zipFileString: String, fileString: String): InputStream {
        val zipFile = ZipFile(zipFileString)
        val zipEntry = zipFile.getEntry(fileString)
        return zipFile.getInputStream(zipEntry)
    }

    /**
     * return files list(file and folder) in the ZIP
     *
     * @param zipFileString
     * ZIP name
     * @param bContainFolder
     * contain folder or not
     * @param bContainFile
     * contain file or not
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun GetFileList(
        zipFileString: String,
        bContainFolder: Boolean,
        bContainFile: Boolean
    ): List<File> {

        val fileList = ArrayList<File>()

        val zipFile = ZipFile(zipFileString)

        val entries = zipFile.entries()

        var zipEntry: ZipEntry
        var szName = ""
        while (entries.hasMoreElements()) {
            zipEntry = entries.nextElement()
            szName = zipEntry.name
            if (zipEntry.isDirectory) {
                // get the folder name of the widget
                szName = szName.substring(0, szName.length - 1)
                val folder = File(szName)
                if (bContainFolder) {
                    fileList.add(folder)
                }

            } else {
                val file = File(szName)
                if (bContainFile) {
                    fileList.add(file)
                }
            }
        }
        zipFile.close()
        return fileList
    }


    /**
     * return files list(file and folder name) in the ZIP
     *
     * @param zipFileString
     * ZIP name
     * @param bContainFolder
     * contain folder or not
     * @param bContainFile
     * contain file or not
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun GetFileNameList(
        zipFileString: String,
        bContainFolder: Boolean,
        bContainFile: Boolean
    ): List<String> {
        val fileNameList = ArrayList<String>()

        val zipFile = ZipFile(zipFileString)

        val entries = zipFile.entries()

        var zipEntry: ZipEntry
        var szName = ""
        while (entries.hasMoreElements()) {
            zipEntry = entries.nextElement()
            szName = zipEntry.name
            fileNameList.add(szName)
        }
        zipFile.close()
        return fileNameList
    }

    /**
     *
     * @param file zip文件路径
     * @param fileName 要读取的文件名（zip中文件的文件名）
     * @return 文件内容
     * @throws Exception
     */
    @Throws(Exception::class)
    fun readZipFile(file: String, fileName: String): String {
        val zf = ZipFile(file)
        val ze = zf.getEntry(fileName)
        val `in` = zf.getInputStream(ze)
        val bis = BufferedInputStream(`in`)

        var count: Int
        val data = ByteArray(1024)
        //将“+=”改为StringBuilder.append方法 --bcc
        val content = StringBuilder()

        while (true) {
            count = bis.read(data, 0, 1024)
            if (count == -1) {
                break
            }
            val temp = ByteArray(count)
            System.arraycopy(data, 0, temp, 0, count)
            content.append(String(temp))

            if (count < data.size) {
                break
            }
        }
        bis.close()
        `in`.close()
        zf.close()
        return content.toString()
    }

}