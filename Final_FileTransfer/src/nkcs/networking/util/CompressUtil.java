package nkcs.networking.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 * ZIP压缩文件操作工具类 支持密码 依赖zip4j开源项目(http://www.lingala.net/zip4j/) 版本1.3.2
 */
public class CompressUtil {

	/**
	 * 使用给定密码解压指定的ZIP压缩文件到指定目录
	 * 如果指定目录不存在,可以自动创建,不合法的路径将导致异常被抛出
	 * 
	 * @param zip
	 *            指定的ZIP压缩文件
	 * @param dest
	 *            解压目录
	 * @param passwd
	 *            ZIP文件的密码
	 * @return 解压后文件数组
	 * @throws ZipException
	 *             压缩文件有损坏或者解压缩失败抛出
	 */
	public static File[] unzip(File zipFile, String dest, String passwd) throws ZipException {
		System.out.println(zipFile.getAbsolutePath());
		ZipFile zFile = new ZipFile(zipFile);
		System.out.println("zpath"+zFile.getFile().getAbsolutePath());
		zFile.setFileNameCharset("GBK");
		if (!zFile.isValidZipFile()) {
			throw new ZipException("压缩文件不合法,可能被损坏.");
		}
		File destDir = new File(dest);
		if (destDir.isDirectory() && !destDir.exists()) {
			destDir.mkdir();
		}
		if (zFile.isEncrypted()) {
			zFile.setPassword(passwd.toCharArray());
		}
		zFile.extractAll(dest);

		List<FileHeader> headerList = zFile.getFileHeaders();
		List<File> extractedFileList = new ArrayList<File>();
		for (FileHeader fileHeader : headerList) {
			if (!fileHeader.isDirectory()) {
				extractedFileList.add(new File(destDir, fileHeader.getFileName()));
			}
		}
		File[] extractedFiles = new File[extractedFileList.size()];
		extractedFileList.toArray(extractedFiles);
		return extractedFiles;
	}

	/**
	 * 使用给定密码压缩指定文件或文件夹到指定位置.
	 * dest可传最终压缩文件存放的绝对路径,也可以传存放目录,也可以传null或者"".
	 * 如果传null或者""则将压缩文件存放在当前目录,即跟源文件同目录,压缩文件名取源文件名,以.zip为后缀;
	 * 如果以路径分隔符(File.separator)结尾,则视为目录,压缩文件名取源文件名,以.zip为后缀,否则视为文件名.
	 * 
	 * @param src
	 *            要压缩的文件或文件夹路径
	 * @param dest
	 *            压缩文件存放路径
	 * @param isCreateDir
	 *            是否在压缩文件里创建目录,仅在压缩文件为目录时有效.<br />
	 *            如果为false,将直接压缩目录下文件到压缩文件.
	 * @param passwd
	 *            压缩使用的密码
	 * @return 最终的压缩文件存放的绝对路径,如果为null则说明压缩失败.
	 */
	public static String zip(String src, String dest, boolean isCreateDir, String passwd) {  
        File srcFile = new File(src);  
        ZipParameters parameters = new ZipParameters();  
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);           // 压缩方式  
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);    // 压缩级别  
        if (!StringUtils.isEmpty(passwd)) {  
            parameters.setEncryptFiles(true);  
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD); // 加密方式  
            parameters.setPassword(passwd.toCharArray());  
        }  
        try {  
            ZipFile zipFile = new ZipFile(dest);  
            zipFile.setFileNameCharset("GBK");
            if (srcFile.isDirectory()) {  
                // 如果不创建目录的话,将直接把给定目录下的文件压缩到压缩文件,即没有目录结构  
                if (!isCreateDir) {  
                    File [] subFiles = srcFile.listFiles();  
                    ArrayList<File> temp = new ArrayList<File>();  
                    Collections.addAll(temp, subFiles);  
                    zipFile.addFiles(temp, parameters);  
                    return dest;  
                }  
                zipFile.addFolder(srcFile, parameters);  
            } else {  
                zipFile.addFile(srcFile, parameters);  
            }  
            return dest;  
        } catch (ZipException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
	
//    public static void main(String[] args) {
//        String zipAbsoPath = zip("/Users/macdowell/Desktop/关键.png", "/Users/macdowell/Desktop/test.zip", false, "guesswhat");
//        System.out.println(zipAbsoPath);
//        // File zipFile = new File("/Users/macdowell/Desktop/UI/test.zip");
//        File zipFile = new File(zipAbsoPath);
//        try {
//			File[] unzipFileGroup = unzip(zipFile, "/Users/macdowell/Desktop/UI", "guesswhat");
//		} catch (ZipException e) {
//			e.printStackTrace();
//		}
//    }
}