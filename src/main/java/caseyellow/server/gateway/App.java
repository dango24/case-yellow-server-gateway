package caseyellow.server.gateway;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class App extends SpringBootServletInitializer {

	private static Logger logger = Logger.getLogger(App.class);

	/**
	 *	Running as a jar
	 */
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
		logger.error("dangooooooooooooo");
		logger.info("esfir");
		logger.debug("orennn");
	}

	/**
	 *	Running as a war
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(App.class);
	}
}
