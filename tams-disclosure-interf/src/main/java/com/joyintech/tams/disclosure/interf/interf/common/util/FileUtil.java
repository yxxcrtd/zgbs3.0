/*
 * 文件名：FileUtil.java 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved. 描述：中信银行资管系统
 * 修改人：lihuadong 修改时间：2017年2月3日 修改内容：新建 系统名称：中信银行资管系统
 */

package com.joyintech.tams.disclosure.interf.interf.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;

import com.joyintech.framework.file.SafelyFile;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.FileNameUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.disclosure.interf.interf.common.constant.InterfConstant;
import com.joyintech.tams.disclosure.interf.interf.common.dto.FileDisposeResultEntity;
import com.joyintech.tams.disclosure.interf.interf.common.dto.InterfEntity;

/**
 * 文件处理共通
 * 
 * @author lihuadong
 * @version 1.0 2017年2月3日
 * @see FileUtil
 * @since 1.0
 */
public class FileUtil {

	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory.getLogger(SocketClient.class);

	/**
	 * 拆分后文件标识
	 */
	public static final String MARK_SPLITFILE = "_chaifen_";

	/**
	 * 将Entity转换成[X03]分隔符的字符串
	 * 
	 * @param entitys
	 *            要转换的字符串
	 * @param ags
	 *            分隔符
	 * @param flag
	 *            文件是否以分隔符结尾
	 * @return
	 * @throws SecurityException
	 *             SecurityException
	 * @throws NoSuchMethodException
	 *             NoSuchMethodException
	 * @throws InvocationTargetException
	 *             InvocationTargetException
	 * @throws IllegalArgumentException
	 *             IllegalArgumentException
	 * @throws IllegalAccessException
	 *             IllegalAccessException
	 * @return String [X03]分隔符的字符串
	 * @see
	 * @since 1.0
	 */
	public static List<String> toStringTxt(List<? extends InterfEntity> entitys, Boolean flag, String... ags)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		String splitString = InterfConstant.SPLIT_SIGN_X03;
		if (ags != null && ags.length > 0) {
			splitString = ags[0];
		}
		// 返回结果
		List<String> results = new ArrayList<String>();
		if (CollectionUtils.isEmpty(entitys)) {
			return results;
		}
		for (InterfEntity entity : entitys) {
			StringBuffer str = new StringBuffer();
			// 获取有顺序的字段
			String[] array = entity.getArrayFortxt();
			// 循环字段集
			for (int i = 0; i < array.length; i++) {

				if (i > 0) {
					str.append(splitString);
				}
				// Java反射调用方法
				Method m = entity.getClass().getMethod(InterfConstant.GET + captureName(array[i]));
				String value = (String) m.invoke(entity);
				if (StringUtils.isNotEmpty(value)) {
					str.append(value);
				}
				if (i == array.length - 1 && flag) {
					str.append(splitString);
				}
			}
			results.add(str.toString());
		}
		return results;
	}

	/**
	 * 将[X03]分隔符的字符串转换成Entity
	 * 
	 * @param entity
	 *            要转换成的Entity
	 * @param str
	 *            用来转换的字符串
	 * @param ags
	 *            分隔符
	 * @throws SecurityException
	 *             SecurityException
	 * @throws NoSuchMethodException
	 *             NoSuchMethodException
	 * @throws InvocationTargetException
	 *             InvocationTargetException
	 * @throws IllegalArgumentException
	 *             IllegalArgumentException
	 * @throws IllegalAccessException
	 *             IllegalAccessException
	 * @return Boolean 转换结果
	 * @see
	 * @since 1.0
	 */
	public static Boolean stringTxtToEntity(String str, InterfEntity entity, String... ags)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		String splitString = InterfConstant.SPLIT_SIGN_X03;
		if (ags != null && ags.length > 0) {
			splitString = ags[0];
		}
		// 检查字符串和entity是否匹配
		if (checkStr(str, entity, splitString)) {
			return false;
		}

		// 分隔字符串
		String[] strs = str.split(splitString, -1);
		// 获取有顺序的字段
		String[] array = entity.getArrayFortxt();
		// 循环字段集
		for (int i = 0; i < array.length; i++) {
			// Java反射调用方法
			Method m = entity.getClass().getMethod(InterfConstant.SET + captureName(array[i]), String.class);
			m.invoke(entity, strs[i]);
		}
		return true;
	}

	/**
	 * 检查字符串和entity是否匹配
	 * 
	 * @param str
	 *            字符串
	 * @param entity
	 *            entity
	 * @param splitString
	 *            分隔符
	 * @return Boolean 匹配结果
	 * @see
	 * @since 1.0
	 */
	private static Boolean checkStr(String str, InterfEntity entity, String splitString) {
		if (StringUtils.isEmpty(str)) {
			return true;
		}
		// 分隔字符串
		String[] strs = str.split(splitString, -1);
		// 获取有顺序的字段
		String[] array = entity.getArrayFortxt();
		// 对比字符串长度
		if (strs.length < array.length) {
			return true;
		}
		return false;
	}

	/**
	 * 将Entity转换成xml标签的字符串
	 * 
	 * @param entitys
	 *            要转换的字符串
	 * @return
	 * @throws SecurityException
	 *             SecurityException
	 * @throws NoSuchMethodException
	 *             NoSuchMethodException
	 * @throws InvocationTargetException
	 *             InvocationTargetException
	 * @throws IllegalArgumentException
	 *             IllegalArgumentException
	 * @throws IllegalAccessException
	 *             IllegalAccessException
	 * @return String [X03]分隔符的字符串
	 * @see
	 * @since 1.0
	 */
	public static List<String> toStringXml(List<? extends InterfEntity> entitys) throws NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// 返回结果
		List<String> results = new ArrayList<String>();
		if (CollectionUtils.isEmpty(entitys)) {
			return results;
		}
		for (InterfEntity entity : entitys) {
			StringBuffer str = new StringBuffer();
			// 获取有顺序的字段
			String[] array = entity.getArrayForXml();
			// 循环字段集
			for (int i = 0; i < array.length; i++) {
				// 左标签
				str.append(getLeftSign(array[i]));
				// Java反射调用方法
				Method m = entity.getClass().getMethod(InterfConstant.GET + captureName(array[i]));
				String value = (String) m.invoke(entity);
				str.append(value);
				// 右标签
				str.append(getRightSign(array[i]));
			}
			results.add(str.toString());
		}

		return results;
	}

	/**
	 * 将Entity转换成可以插入DB的sql文
	 * 
	 * @param entitys
	 *            要转换的字符串
	 * @param tableName
	 *            要登录的表名
	 * @return
	 * @throws SecurityException
	 *             SecurityException
	 * @throws NoSuchMethodException
	 *             NoSuchMethodException
	 * @throws InvocationTargetException
	 *             InvocationTargetException
	 * @throws IllegalArgumentException
	 *             IllegalArgumentException
	 * @throws IllegalAccessException
	 *             IllegalAccessException
	 * @return String [X03]分隔符的字符串
	 * @see
	 * @since 1.0
	 */
	public static List<String> toStringDB(List<? extends InterfEntity> entitys, String tableName)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		// 返回结果
		List<String> listsql = new ArrayList<String>();
		// 一条sql文
		StringBuffer str = new StringBuffer();
		if (CollectionUtils.isEmpty(entitys)) {
			return listsql;
		}
		// 字段列表
		String[] cloumns = entitys.get(0).getCloumns();
		// DB字段对应的属性列表
		String[] properties = entitys.get(0).getArrayForDB();
		if (cloumns.length != properties.length) {
			return listsql;
		}
		str.append(InterfConstant.INSERT_INTO).append(tableName).append(GlobalConstant.OPEN_PARENTHESIS) // 列 名 左括号
				.append(StringUtils.join(cloumns, GlobalConstant.COMMA)).append(GlobalConstant.CLOSE_PARENTHESIS)
				.append(InterfConstant.VALUES);

		// 外部循环次数
		int cnt = 0;
		for (InterfEntity entity : entitys) {
			if (str.length() > InterfConstant.SQL_MAX_LENGTH && cnt > 0) {
				listsql.add(str.toString());
				str = new StringBuffer();
				str.append(InterfConstant.INSERT_INTO).append(tableName).append(GlobalConstant.OPEN_PARENTHESIS) // 列 名
																													// 左括号
						.append(StringUtils.join(cloumns, GlobalConstant.COMMA))
						.append(GlobalConstant.CLOSE_PARENTHESIS).append(InterfConstant.VALUES);
			} else if (cnt > 0) {
				str.append(GlobalConstant.COMMA);
			}
			StringBuffer insertVal = new StringBuffer();
			// 循环字段集
			for (int i = 0; i < cloumns.length; i++) {
				if (i > 0) {
					insertVal.append(GlobalConstant.COMMA);
				}
				// 创建时间
				if (InterfConstant.CREATEDATE.equalsIgnoreCase(properties[i])
						|| InterfConstant.LSTMNTDATE.equalsIgnoreCase(properties[i])) {
					insertVal.append(InterfConstant.CURRENT_DATE);
				} else {
					// Java反射调用方法
					Method m = entity.getClass().getMethod(InterfConstant.GET + captureName(properties[i]));
					String value = (String) m.invoke(entity);
					if (StringUtils.isEmpty(value)) {
						insertVal.append(InterfConstant.SINGLE_QUOTES).append(InterfConstant.SINGLE_QUOTES);

					} else {
						insertVal.append(InterfConstant.SINGLE_QUOTES).append(value.replaceAll("'", ""))
								.append(InterfConstant.SINGLE_QUOTES);

					}
				}

			}
			str.append(GlobalConstant.OPEN_PARENTHESIS).append(insertVal).append(GlobalConstant.CLOSE_PARENTHESIS);
			cnt++;
		}
		listsql.add(str.toString());
		return listsql;
	}

	/**
	 * 获取xml 左标签
	 *
	 * @param name
	 *            标签名称
	 * @return 左标签
	 * @see
	 * @since 1.0
	 */
	private static String getLeftSign(String name) {
		return InterfConstant.LEFT_BRACKET + name + InterfConstant.RIGHT_BRACKET;

	}

	/**
	 * 获取xml 右标签
	 * 
	 * @param name
	 *            标签名称
	 * @return 右标签
	 * @see
	 * @since 1.0
	 */
	private static String getRightSign(String name) {
		return InterfConstant.LEFT_BRACKET + name + InterfConstant.VIRGULE + InterfConstant.RIGHT_BRACKET;

	}

	/**
	 * 将首字母大写,如果第二字符是大写的时候转小写
	 * 
	 * @param name
	 *            转换字符串
	 * @return 转换后的字符串
	 * @see
	 * @since 1.0
	 */
	public static String captureName(String name) {
		if (StringUtils.isEmpty(name)) {
			return name;
		}
		char[] cs = name.toCharArray();
		if (cs.length == 1) {
			cs[0] -= 32;
		}
		if (cs.length >= 2 && cs[1] >= 97 && cs[1] <= 122) {
			cs[0] -= 32;
		}
		return String.valueOf(cs);
	}

	/**
	 * 读取文件
	 * 
	 * @param fullPath
	 *            文件全路径
	 * @param encode
	 *            编码方式
	 * @return List<String> 文件内容
	 * @throws IOException
	 *             IOException
	 * @see
	 * @since 1.0
	 */
	public static List<String> getFile(String fullPath, String encode) throws IOException {
		File file = new SafelyFile(fullPath);
		if (!file.exists()) {
			return null;
		}
		List<String> result = new ArrayList<String>();
		FileInputStream fis=null;
		InputStreamReader isr=null;
		BufferedReader reader=null;
		try{
		// 以行为单位读取文件内容，一次读一整行
		fis=new FileInputStream(file);
		isr=new InputStreamReader(fis, encode);
		reader = new BufferedReader(isr);
		String tempString = null;
		// 一次读入一行，直到读入null为文件结束
		while ((tempString = reader.readLine()) != null) {
			// 显示行号
			result.add(tempString);
		}
		fis.close();
		isr.close();
		reader.close();
		}
		catch(IOException e){
		    throw new IOException(); 
		}
		finally {
          if(fis!=null){
              try {
                fis.close();
            }
            catch (IOException e) {
            	logger.error("error:",e);
            }
          }  
          if(isr!=null){
              try {
                  isr.close();
              }
              catch (IOException e) {
            	  logger.error("error:",e);
              }
          }  
          if(reader!=null){ 
              try {
                  reader.close();
              }
              catch (IOException e) {
            	  logger.error("error:",e);
              }
          }  
        }
		return result;
	}

	/**
	 * 轮询文件夹
	 * 
	 * @param filePath
	 *            文件夹路径
	 * @return List<String> 文件夹下文件内容
	 * @see
	 * @since 1.0
	 */
	public static List<String> getFileList(String filePath) {
		if (StringUtil.isEmpty(filePath)) {
			return null;
		}
		File folder = new SafelyFile(filePath);
		// 判断文件夹是否存在
		if (!folder.exists()) {
			return null;
		}
		// 判断是否是文件夹
		if (!folder.isDirectory()) {
			return null;
		}
		// 文件夹下文件list
		List<String> result = new ArrayList<String>();
		// 获取文件列表
		File[] files = folder.listFiles();
		// 判断文件夹下面有没有文件
		if (files == null || files.length == 0) {
			return result;
		}
		// 循环读取文件属性
		for (File file : files) {
			result.add(file.getName());
		}
		return result;
	}

	/**
	 * 移动文件到指定文件夹
	 * 
	 * @param fullPath
	 *            文件全路径
	 * @param directoryPath
	 *            移动目标目录
	 * @return Boolean 移动结果: 移动失败 false，移动成功 true
	 * @see
	 * @since 1.0
	 */
	public static FileDisposeResultEntity removeFileToDirectory(String fullPath, String directoryPath) {
		// 处理结果
		FileDisposeResultEntity result = new FileDisposeResultEntity();
		File directory = new SafelyFile(directoryPath);
		// 判断文件夹不存在并且创建失败
		if (!directory.exists() && !directory.mkdirs()) {
			result.setMessage(InterfConstant.FOLDER_CREATE_FAILURE_MSG);
			result.setStatus(false);
			return result;
		}
		// 判断是否是文件夹
		if (!directory.isDirectory()) {
			result.setMessage(InterfConstant.FOLDER_NOT_FOLDER_MSG);
			result.setStatus(false);
			return result;
		}
		File file = new SafelyFile(fullPath);
		// 判断文件夹是否存在
		if (!file.exists()) {
			result.setMessage(InterfConstant.FILE_NOT_EXISTED_MSG);
			result.setStatus(false);
			return result;
		}
		try {
			// 将文件copy到指定目录
			FileUtils.copyFileToDirectory(file, directory);
			// 删除源文件
			file.delete();
		} catch (IOException e) {
			result.setMessage(InterfConstant.IOEXCEPTION_MSG);
			result.setStatus(false);
			return result;
		}
		result.setStatus(true);
		return result;
	}

	/**
	 * 删除文件
	 * 
	 * @param fullPath
	 *            文件全路径
	 * @return FileDisposeResultEntity 文件处理结果
	 * @see
	 * @since 1.0
	 */
	public static FileDisposeResultEntity deleteFile(String fullPath) {
		File file = new SafelyFile(fullPath);
		// 处理结果
		FileDisposeResultEntity result = new FileDisposeResultEntity();
		try {
			// 删除源文件
			file.delete();
		} catch (Exception e) {
			result.setMessage(InterfConstant.DELETE_FILE_FAILURE_MSG);
			result.setStatus(false);
			return result;
		}
		result.setStatus(true);
		return result;
	}

	/**
	 * 写文件
	 * 
	 * @param path
	 *            写文件路径
	 * @param datas
	 *            数据
	 * @param name
	 *            文件名称
	 * @param encode
	 *            写入的编码方式
	 * @return Boolean 写入的状态:写入成功 true,写入失败 false
	 * @see
	 * @since 1.0
	 */
	public static FileDisposeResultEntity writeFile(String path, String name, List<String> datas, String encode) {
		// 处理结果
		FileDisposeResultEntity result = new FileDisposeResultEntity();
		// 检查文件名和路径
		result = checkFile(path, name);
		// 检查失败
		if (!result.getStatus()) {
			return result;
		}
		FileOutputStream fos=null;
		OutputStreamWriter osw=null;
		// 输出字符流
		BufferedWriter br = null;
		try {
		    //String fileStrName=path + System.getProperty(InterfConstant.FILE_SEPARATOR) + name;
		    String fileStrName=path + File.separator + name;
		    fos=new FileOutputStream(fileStrName);
		    osw=new OutputStreamWriter(fos, encode);
		    // 创建输出流
			br = new BufferedWriter(osw);
			// 轮询数据结果集一行一行的写入
			for (String data : datas) {
				// 写入一条
				br.write(data);
				// 换行
				br.write("\n");
			}
			br.flush();
			fos.close();
			osw.close();
			br.close();

		} catch (UnsupportedEncodingException e) {
			result.setMessage(InterfConstant.UNSUPPORTEDENCODINGE_MSG);
			result.setStatus(false);
		} catch (FileNotFoundException e) {
			result.setMessage(InterfConstant.FILENOTFOUND_MSG);
			result.setStatus(false);
		} catch (IOException e) {
			result.setMessage(InterfConstant.IOEXCEPTION_MSG);
			result.setStatus(false);
		} finally {
		    if (fos != null) {
		        try {
		            fos.close();
		        } catch (IOException e) {
		            logger.info(e.getMessage());
		        }
		    }
		    if (osw != null) {
		        try {
		            osw.close();
		        } catch (IOException e) {
		            logger.info(e.getMessage());
		        }
		    }
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.info(e.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * 检查要写的文件名和路径
	 * 
	 * @param path
	 *            文件路径
	 * @param name
	 *            文件名
	 * @return FileDisposeResultEntity 检查结果
	 * @see
	 * @since 1.0
	 */
	private static FileDisposeResultEntity checkFile(String path, String name) {
		// 处理结果
		FileDisposeResultEntity result = new FileDisposeResultEntity();
		// 文件夹
		File folder = new SafelyFile(path);
		// 判断文件夹不存在并且创建失败
		if (!folder.exists() && !folder.mkdirs()) {
			result.setMessage(InterfConstant.FOLDER_CREATE_FAILURE_MSG);
			result.setStatus(false);
			return result;
		}
		// 判断是否是文件夹
		if (!folder.isDirectory()) {
			result.setMessage(InterfConstant.FOLDER_NOT_FOLDER_MSG);
			result.setStatus(false);
			return result;
		}
        //String fullFileDir=path + System.getProperty(InterfConstant.FILE_SEPARATOR) + name;
        String fullFileDir=path + File.separator + name;
		File file = new SafelyFile(fullFileDir);
		// 判断相同路径下有没有一个同名文件夹
		if (file.exists() && file.isDirectory()) {
			result.setMessage(InterfConstant.FILE_NOT_FILE_MSG);
			result.setStatus(false);
			return result;
		}
		result.setStatus(true);
		return result;
	}

	/**
	 * 数字左边补充0
	 * 
	 * @param data
	 *            数字
	 * @param length
	 *            位数
	 * @return 字符串
	 * @see
	 * @since 1.0
	 */
	public static String leftZero(int data, int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = String.valueOf(data).length(); i < length; i++) {
			sb.append("0");
		}
		sb.append(data);
		return sb.toString();
	}

	
	/**
     * 瑙ｅ帇zip鏍煎紡鏂囦欢
     *
     * @param originFile zip鏂囦欢銆�
     * @param targetDir 瑕佽В鍘嬪埌鐨勭洰鏍囪矾寰勩��
     * @return 濡傛灉鐩爣鏂囦欢涓嶆槸zip鏂囦欢鍒欒繑鍥瀎alse銆�
     * @throws IOException 濡傛灉鍙戠敓I/O閿欒銆�
     */
    @SuppressWarnings("unchecked")
    public static boolean decompressZip(File originFile, String targetDir)
        throws IOException {

        if (!StringUtil.equals("zip", FileNameUtil.getFileExtName(originFile))) {
            return false;
        }
        if (!targetDir.endsWith(File.separator)) {
            targetDir += File.separator;
        }
        ZipFile zipFile = new ZipFile(originFile,"GBK");
        ZipEntry zipEntry;
        Enumeration<ZipEntry> entry = zipFile.getEntries();
        while (entry.hasMoreElements()) {
            zipEntry = entry.nextElement();
            String fileName = zipEntry.getName();
            File outputFile = new SafelyFile(targetDir + fileName);
            if (zipEntry.isDirectory()) {
                FileUtils.forceMkdir(outputFile);
                continue;
            }
            else if (!outputFile.getParentFile().exists()) {
                FileUtils.forceMkdir(outputFile.getParentFile());
            }

			OutputStream outputStream = null;
			InputStream inputStream = null;
            try {
				outputStream = new FileOutputStream(outputFile);
				inputStream = zipFile.getInputStream((org.apache.tools.zip.ZipEntry) zipEntry);
				int len;
				byte[] buffer = new byte[8192];
				while (-1 != (len = inputStream.read(buffer))) {
					outputStream.write(buffer, 0, len);
				}
			}
			catch (Exception e)
			{
				logger.error(e.getMessage(),e);
			}
			finally {
            	if(outputStream!=null)
				{
					outputStream.close();
				}
				if(inputStream!=null)
				{
					inputStream.close();

				}

			}

        }
        zipFile.close();
        return true;
    }
    
    
    /**
     * 閫掑綊鍒犻櫎鐩綍涓嬬殑鎵�鏈夋枃浠跺強瀛愮洰褰曚笅鎵�鏈夋枃浠�
     * @param dir 灏嗚鍒犻櫎鐨勬枃浠剁洰褰�
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new SafelyFile(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 鐩綍姝ゆ椂涓虹┖锛屽彲浠ュ垹闄�
        return dir.delete();
    }
}
