package com.joyintech.tams.system;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class SpringUtil {
	
	private static ConfigurableApplicationContext context;
	private static ConfigurableEnvironment environment;

    @SuppressWarnings("unchecked")
	public static <T> T getBean(String name){
    	if(null==context) {
    		return null;
    	}
        return (T) context.getBean(name);
    }
 
    @SuppressWarnings("unchecked")
	public static <T> T getBean(Class<?> requiredType){
    	if(null==context) {
    		return null;
    	}
        return (T) context.getBean(requiredType);
    }

	public static ConfigurableApplicationContext getContext() {
		return context;
	}

	public static void setContext(ConfigurableApplicationContext context) {
		SpringUtil.context = context;
	}

	public static ConfigurableEnvironment getEnvironment() {
		return environment;
	}

	public static void setEnvironment(ConfigurableEnvironment environment) {
		SpringUtil.environment = environment;
	}
	
	public static <T> Map<String,T> getBeansOfType(Class<T> clazz) {
		return context.getBeansOfType(clazz);
	}
	
	public static void registerSingleton(String beanName, Object singletonObject) {
		context.getBeanFactory().registerSingleton(beanName, singletonObject);
	}
	
	public static String getHome(Class<?> sourceClass) {
		Home home = new Home(sourceClass);
		return null==home.getSource() ? home.toString() : home.getSource().getParent();
	}
	
	private static class Home {
		private final File source;

		private final File dir;

		/**
		 * Create a new {@link ApplicationHome} instance for the specified source class.
		 * @param sourceClass the source class or {@code null}
		 */
		public Home(Class<?> sourceClass) {
			this.source = findSource(sourceClass == null ? getStartClass() : sourceClass);
			this.dir = findHomeDir(this.source);
		}

		private Class<?> getStartClass() {
			try {
				ClassLoader classLoader = getClass().getClassLoader();
				return getStartClass(classLoader.getResources("META-INF/MANIFEST.MF"));
			}
			catch (Exception ex) {
				return null;
			}
		}

		private Class<?> getStartClass(Enumeration<URL> manifestResources) {
			while (manifestResources.hasMoreElements()) {
				try {
					InputStream inputStream = manifestResources.nextElement().openStream();
					try {
						Manifest manifest = new Manifest(inputStream);
						String startClass = manifest.getMainAttributes()
								.getValue("Start-Class");
						if (startClass != null) {
							return ClassUtils.forName(startClass,
									getClass().getClassLoader());
						}
					}
					finally {
						inputStream.close();
					}
				}
				catch (Exception ex) {
				}
			}
			return null;
		}

		private File findSource(Class<?> sourceClass) {
			try {
				ProtectionDomain domain = (sourceClass == null ? null
						: sourceClass.getProtectionDomain());
				CodeSource codeSource = (domain == null ? null : domain.getCodeSource());
				URL location = (codeSource == null ? null : codeSource.getLocation());
				File source = (location == null ? null : findSource(location));
				if (source != null && source.exists() && !isUnitTest()) {
					return source.getAbsoluteFile();
				}
				return null;
			}
			catch (Exception ex) {
				return null;
			}
		}

		private boolean isUnitTest() {
			try {
				for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
					if (element.getClassName().startsWith("org.junit.")) {
						return true;
					}
				}
			}
			catch (Exception ex) {
			}
			return false;
		}

		private File findSource(URL location) throws IOException {
			URLConnection connection = location.openConnection();
			if (connection instanceof JarURLConnection) {
				return getRootJarFile(((JarURLConnection) connection).getJarFile());
			}
			return new File(location.getPath());
		}

		private File getRootJarFile(JarFile jarFile) {
			String name = jarFile.getName();
			int separator = name.indexOf("!/");
			if (separator > 0) {
				name = name.substring(0, separator);
			}
			return new File(name);
		}

		private File findHomeDir(File source) {
			File homeDir = source;
			homeDir = (homeDir == null ? findDefaultHomeDir() : homeDir);
			if (homeDir.isFile()) {
				homeDir = homeDir.getParentFile();
			}
			homeDir = (homeDir.exists() ? homeDir : new File("."));
			return homeDir.getAbsoluteFile();
		}

		private File findDefaultHomeDir() {
			String userDir = System.getProperty("user.dir");
			return new File(StringUtils.hasLength(userDir) ? userDir : ".");
		}

		/**
		 * Returns the underlying source used to find the home directory. This is usually the
		 * jar file or a directory. Can return {@code null} if the source cannot be
		 * determined.
		 * @return the underlying source or {@code null}
		 */
		public File getSource() {
			return this.source;
		}

		/**
		 * Returns the application home directory.
		 * @return the home directory (never {@code null})
		 */
		public File getDir() {
			return this.dir;
		}

		@Override
		public String toString() {
			return getDir().toString();
		}
	}
}
