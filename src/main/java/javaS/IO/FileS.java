package javaS.IO;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * 基于磁盘IO操作的类 java.io.File 可以表示文件，也可以表示文件夹目录
 * 
 * @author Evsward
 *
 */
public class FileS extends IOBaseS {
    @Test
    public void testFileMethods() throws IOException {
        logger.info("Start testing file methods.");
        File file = new File(root);
        if (!file.exists())
            /**
             * 创建目录 mkdir();
             */
            file.mkdir();

        if (file.isDirectory()) {
            File file1 = new File(root + "UME.txt");
            File file2 = new File(root + "HongXing.txt");
            /**
             * 创建文件 createNewFile();
             */
            file1.createNewFile();
            file2.createNewFile();
            File file3 = new File(root + "Cinema");
            file3.mkdir();
            /**
             * 列出文件路径下的所有文件（包括文件和目录）
             */
            File[] files = file.listFiles();
            for (File f : files) {
                /**
                 * 判断该文件路径是否为目录
                 */
                if (f.isDirectory()) {
                    logger.info("The directory in 'Files' is: " + f.getName());
                } else {
                    logger.info("The file in 'Files' is: " + f.getName());
                }
                logger.info("Whose path is: " + f.getAbsolutePath());
            }
        } else {
            logger.info("FileS is not a directory!");
        }
        logger.info("Complete testing file methods.");
        /**
         * 输出： 15:12:56[testFileMethods]: Start testing file methods.
         * 15:12:56[testFileMethods]: The file in 'Files' is: HongXing.txt
         * 15:12:56[testFileMethods]: Whose path is:
         * /home/work/github/mainbase/resource/StudyFile/HongXing.txt
         * 15:12:56[testFileMethods]: The directory in 'Files' is: Cinema
         * 15:12:56[testFileMethods]: Whose path is:
         * /home/work/github/mainbase/resource/StudyFile/Cinema
         * 15:12:56[testFileMethods]: The file in 'Files' is: UME.txt
         * 15:12:56[testFileMethods]: Whose path is:
         * /home/work/github/mainbase/resource/StudyFile/UME.txt
         * 15:12:56[testFileMethods]: Complete testing file methods.
         */
    }

    /**
     * 清空一个文件，以便于我们测试使用
     * 
     * @param filePath
     * @throws IOException
     */
    public static void initEV(String filePath) throws IOException {
        File f = new File(filePath);
        if (f.exists())
            f.delete();
        f.createNewFile();
    }

    @Test
    /**
     * 测试文件目录过滤器，例如列出目录下所有"*.txt"
     */
    public void testFileFilter() {
        String filterStr = "(./*)+(txt)$";
        File file = new File(root);
        logger.info("start testing file filter: " + file.getAbsolutePath());
        String list[] = file.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return Pattern.matches(filterStr, name);
            }

        });
        for (String s : list)
            logger.info(s);
    }

    /**
     * 输出：
     * 
     * 12:57:17[testFileFilter]: start testing file filter.
     * 
     * 12:57:17[testFileFilter]: HuaYi.txt
     * 
     * 12:57:17[testFileFilter]: HongXing.txt
     * 
     * 12:57:17[testFileFilter]: UME.txt
     */

    @Test
    public void testFileTool() {
        File file = new File(root);
        PPrint.pprint(file.listFiles());
        // 转为List
        @SuppressWarnings("unused")
        List<File> fl = Arrays.asList(file.listFiles());
        logger.info("file.length(): " + file.length());
        logger.info("file.getName(): " + file.getName());
        logger.info("file.getParent(): " + file.getParent());
        // file.renameTo(new File("resource/S"));// 重命名文件
        logger.info("file.canRead(): " + file.canRead());
        logger.info("file.canWrite(): " + file.canWrite());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS:SSS");
        Date a = new Date(file.lastModified());
        logger.info("file.lastModified(): " + sdf.format(a));
    }
}
