package codedepot.webutils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Properties;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory; 

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

/**
 * Servlet implementation class RedirectServlet
 */
@WebServlet(name = "RedirectScxml", urlPatterns = { "/RedirectScxml" }, loadOnStartup = 1)
public class RedirectServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	Logger logger = LoggerFactory.getLogger(RedirectServlet.class);
	Properties appProperties;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RedirectServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String type = (String) request.getParameter("Type");
		if (type.equalsIgnoreCase("ping")) {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String tz = " at  " + timestamp;
			response.getWriter().append("Responded to ping for ").append(request.getContextPath()).append(tz);
		} else {
			String mb = (String) request.getParameter("mailbox");

			logger.info("mailbox from request : " + mb);
			logger.info("Type of Script from request : " + type);
			String ENT_Unit = appProperties.getProperty(mb + "-ENT_Unit");
			String ENT_Segment = appProperties.getProperty(mb + "-ENT_Segment");
			String ENT_Function = appProperties.getProperty(mb + "-ENT_Function");
			String lob = "Dgt" + ENT_Unit + ENT_Segment + ENT_Function;
			String hostUrl = appProperties.getProperty("hostUrl");
			String url = "";

			String emailBox = appProperties.getProperty(mb + "-Secure");
			
			if (emailBox!= null &&  emailBox.equalsIgnoreCase("true")) {
				url = hostUrl + lob + "/src-gen/IPD_Email_" + type + ".scxml?ENT_Unit=" + ENT_Unit + "&ENT_Segment="
						+ ENT_Segment + "&ENT_Function=" + ENT_Function + "&ENT_MediaClassNm=secureemail";		
			} else {			
				url = hostUrl + lob + "/src-gen/IPD_Email_" + type + ".scxml?ENT_Unit=" + ENT_Unit + "&ENT_Segment="
						+ ENT_Segment + "&ENT_Function=" + ENT_Function;
			}

			logger.info("Redirecting the request to url :  " + url);
			response.sendRedirect(url);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext context = config.getServletContext();
		String tomcat_env = "PROD";
		try {
			tomcat_env = System.getProperty("env").split("-")[0];
			logger.info("tomcat env from system property is : " + System.getProperty("env"));
		} catch (Exception e1) {
			tomcat_env = "PROD";
			logger.info("Tomcat env defaulting to PROD.");
		}
		String cfgFile = tomcat_env.trim() + "-redirectWebApp.properties";
		logger.info("Reading config file: " + cfgFile);
		// inputStream = getClass().getClassLoader().getResourceAsStream(cfgFile);

		try (InputStream inputStream = context.getResourceAsStream(cfgFile)) {
			appProperties = new Properties();
			appProperties.load(inputStream);
			logger.info("app properties are set successfully : " + appProperties);
		} catch (Exception e) {
			logger.info("Exception in Reading Config File : " + e.toString());
		}
		setLogOptions();
		logger.trace("Current LogLevel allows TRACE messages.");
		logger.debug("Current LogLevel allows DEBUG messages.");
		logger.info("Current LogLevel allows INFO messages.");
		logger.warn("Current LogLevel allows WARN messages.");
		logger.error("Current LogLevel allows ERROR messages.");
	}

	public void setLogOptions() {

		ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
//		ch.qos.logback.classic.Logger rollingFileLogger 
//		  = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("rollingFileLogger");
		String logLevel = "";
		try {
			logLevel = appProperties.getProperty("logLevel");
			logger.info("leg level from properties file : " + logLevel);
			// The possible levels are, in order of precedence: TRACE, DEBUG, INFO, WARN and
			// ERROR. -->
			switch (logLevel) {
			case "TRACE":
			case "trace":
				rootLogger.setLevel(Level.TRACE);
				break;
			case "DEBUG":
			case "debug":
				rootLogger.setLevel(Level.DEBUG);
				break;
			case "INFO":
			case "info":
				rootLogger.setLevel(Level.INFO);
				break;
			case "WARN":
			case "warn":
				rootLogger.setLevel(Level.WARN);
				break;
			case "ERROR":
			case "error":
				rootLogger.setLevel(Level.ERROR);
				break;
			default:
				rootLogger.setLevel(Level.INFO);
				break;
			}
		} catch (Exception e) {
			rootLogger.setLevel(Level.INFO);
		}
		logger.info("leg level set as : " + logLevel);
	}

	/**
	 * @see Servlet#destroy()
	 */

	public void destroy() {
		try {
//			SINCE 1.1.10 Logback-classic will automatically ask the web-server to install a 
//			LogbackServletContainerInitializer implementing the ServletContainerInitializer 
//			interface (available in servlet-api 3.x and later). This initializer will in turn
//			install and instance of LogbackServletContextListener. This listener will stop the 
//			current logback-classic context when the web-app is stopped or reloaded.
			// assume SLF4J is bound to logback-classic in the current environment
			// LoggerContext loggerContext = (LoggerContext)
			// LoggerFactory.getILoggerFactory();
			// loggerContext.stop();
		} catch (Exception e1) {
			System.out.println("Error in closing logger context : " + e1.toString());
		}
	}

}
