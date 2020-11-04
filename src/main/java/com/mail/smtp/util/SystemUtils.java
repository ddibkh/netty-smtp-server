package com.mail.smtp.util;

public class SystemUtils {
	public static boolean isWindow() {
		return (System.getProperty("os.name").startsWith("Windows"));
	}

	public static boolean isLinux() {
		return (System.getProperty("os.name").startsWith("Linux"));
	}

	public static boolean isSolaris() {
		return (System.getProperty("os.name").startsWith("SunOS"));
	}

	public static boolean isAIX() {
		return (System.getProperty("os.name").startsWith("AIX"));
	}

	public static String getOSArch() {
		return System.getProperty("os.arch");
	}

	public static String getOSRoot() {
		return System.getenv("SystemRoot");
	}

	public static String getSystemPath() {
		String systemPath = "/usr/local/etc/";

		if (isWindow()) {
			String osArch = getOSArch();
			systemPath = getOSRoot();

			if ((osArch != null) && (osArch.length() != 0)) {
				if ((osArch.equals("x64")) || (osArch.equals("amd64"))) {
					systemPath = systemPath + "\\SysWOW64\\";
				} else {
					systemPath = systemPath + "\\System32\\";
				}

			} else {
				systemPath = systemPath + "\\System32\\";
			}
		}

		return systemPath;
	}
}