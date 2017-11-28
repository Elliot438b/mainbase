package javaS.IO;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 * 基于磁盘IO操作的类 java.io.File
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
            File file1 = new File(root+"/UME.txt");
            File file2 = new File(root+"/HongXing.txt");
            /**
             * 创建文件 createNewFile();
             */
            file1.createNewFile();
            file2.createNewFile();
            File file3 = new File(root+"/Cinema");
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
         * 输出：
         * 15:12:56[testFileMethods]: Start testing file methods.
         * 15:12:56[testFileMethods]: The file in 'Files' is: HongXing.txt
         * 15:12:56[testFileMethods]: Whose path is: /home/work/github/mainbase/resource/StudyFile/HongXing.txt
         * 15:12:56[testFileMethods]: The directory in 'Files' is: Cinema
         * 15:12:56[testFileMethods]: Whose path is: /home/work/github/mainbase/resource/StudyFile/Cinema
         * 15:12:56[testFileMethods]: The file in 'Files' is: UME.txt
         * 15:12:56[testFileMethods]: Whose path is: /home/work/github/mainbase/resource/StudyFile/UME.txt
         * 15:12:56[testFileMethods]: Complete testing file methods.
         */
    }
    
    public static void initEV(String filePath) throws IOException {
        File f = new File(filePath);
        if (f.exists())
            f.delete();
        f.createNewFile();
    }
}
