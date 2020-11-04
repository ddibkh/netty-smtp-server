package com.mail.smtp.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class PathUtils {

	private static ConfigMap fileConfig;
	private static String		hostId;
	private static String		installPath;
	private static String		sharedPath;
	private static String		configPath;
	private static String		companyPath;
	private static String		binPath;
	
	private static String 		processId;

	static {
		final Logger logger = LoggerFactory.getLogger(PathUtils.class);
		String filePath = SystemUtils.getSystemPath() + "postian6.cfg";

		try {
			IConfigLoader configLoader =  new NormalConfigLoader();
			fileConfig = configLoader.load(filePath);

			hostId = fileConfig.getConfigStr("host_id", "");
			installPath = fileConfig.getConfigStr("install_path", "");
			sharedPath = fileConfig.getConfigStr("shared_path", "");

			logger.info("host_id : {}", hostId);
			logger.info("install_path : {}", installPath);
			logger.info("shared_path : {}", sharedPath);

			if( hostId.equals("") || installPath.equals("") || sharedPath.equals("") )
				throw new Exception("postian6.cfg config insufficient");

			configPath = sharedPath + "config" + File.separator;
			companyPath = sharedPath + "company" + File.separator;
			binPath = installPath + "bin" + File.separator;

			ManagementFactory.getRuntimeMXBean();
			RuntimeMXBean rt = ManagementFactory.getRuntimeMXBean();
			processId = rt.getName().substring(0, rt.getName().indexOf("@"));
			
		} catch (Exception e) {
			logger.error("[Postian6 DEBUG] Don't load pathinfo, {}", ExceptionUtils.getFullStackTrace(e));
		}
	}

	public static String getProcessId() {
		return processId;
	}
	
	public static String getInstallPath() {
		return installPath;
	}
	
	public static String getSamplePath() {
		return installPath + "sample" + File.separator;
	}	

	public static String getSharedPath() {

		return sharedPath;
	}

	public static String getConfigPath() {

		return configPath;
	}

	public static String getCompanyPath() {

		return companyPath;
	}

	public static String getCompanyUserPath(int cidx) {

		return sharedPath + "user" + File.separator + cidx + File.separator;
	}

	public static String getBinPath() {

		return binPath;
	}

	public static String getUserPath(int didx, int aidx, String id) {

		String temp = Integer.toString(aidx);
		int length = temp.length();

		temp = (length >= 4) ? temp.substring(length - 4) : String.format("%04d", aidx);

		return didx + File.separator + temp.substring(0, 2) + File.separator + temp.substring(2) + File.separator + id + File.separator;
	}

	public static String makeLinkPath(int idx) {

		String temp = Integer.toString(idx);
		int length = temp.length();

		temp = (length >= 4) ? temp.substring(length - 4) : String.format("%04d", idx);

		return temp.substring(0, 2) + File.separator + temp.substring(2) + File.separator;
	}
	
	public static String getHostId() {
		return hostId;
	}

	public static void setHostId(String hostId) {
		PathUtils.hostId = hostId;
	}
	
	public static void reloadPath() throws Exception
	{
		final Logger logger = LoggerFactory.getLogger(PathUtils.class);
		String filePath = SystemUtils.getSystemPath() + "postian6.cfg";

		try {
			IConfigLoader configLoader = new NormalConfigLoader();
			fileConfig = configLoader.load(filePath);

			hostId = fileConfig.getConfigStr("host_id", "");
			installPath = fileConfig.getConfigStr("install_path", "");
			sharedPath = fileConfig.getConfigStr("shared_path", "");

			logger.info("host_id : {}", hostId);
			logger.info("install_path : {}", installPath);
			logger.info("shared_path : {}", sharedPath);

			if( hostId.equals("") || installPath.equals("") || sharedPath.equals("") )
				throw new Exception("postian6.cfg config insufficient");

			configPath = sharedPath + "config" + File.separator;
			companyPath = sharedPath + "company" + File.separator;
			binPath = installPath + "bin" + File.separator;
			
			ManagementFactory.getRuntimeMXBean();
			RuntimeMXBean rt = ManagementFactory.getRuntimeMXBean();
			processId = rt.getName().substring(0, rt.getName().indexOf("@"));
			
		} catch (Exception e) {
			logger.error("[Postian6 DEBUG] Don't load pathinfo, {}", ExceptionUtils.getFullStackTrace(e));
			throw e;
		}
	}
	
	/**
	 * 파일 시스템 경로를 리턴 <br>
	 */
	public static String getFileSystemPath(String path) {
			return FilenameUtils.separatorsToSystem(path);
	}
}
