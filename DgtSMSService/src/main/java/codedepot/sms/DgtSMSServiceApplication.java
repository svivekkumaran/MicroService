package codedepot.sms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

//
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.AbstractEnvironment;

@SpringBootApplication
public class DgtSMSServiceApplication extends SpringBootServletInitializer {


	public static void main(String[] args) {
		SpringApplication.run(DgtSMSServiceApplication.class, args);
	}



}
