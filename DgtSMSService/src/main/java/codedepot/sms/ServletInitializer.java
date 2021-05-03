package codedepot.sms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.AbstractEnvironment;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(DgtSMSServiceApplication.class);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		String tomcat_env = "PROD";
		try {
			tomcat_env = System.getProperty("env").split("-")[0];
			System.out.println("tomcat env from system property is : "+System.getProperty("env"));
		} catch (Exception e1) {
			tomcat_env = "PROD";
			System.out.println("Tomcat env defaulting to PROD.");
		}		
		System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, tomcat_env);
		super.onStartup(servletContext);
	}
}
