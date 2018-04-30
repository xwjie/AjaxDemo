package cn.xiaowenjie;

import static org.hamcrest.CoreMatchers.instanceOf;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.yaml.snakeyaml.emitter.Emitable;

@SpringBootApplication
public class AjaxdemoApplication implements EmbeddedServletContainerCustomizer {

	public static void main(String[] args) {
		SpringApplication.run(AjaxdemoApplication.class, args);
	}

	//@Bean
	public FilterRegistrationBean crosFilterBean() {

		FilterRegistrationBean bean = new FilterRegistrationBean();

		bean.setFilter(new CrosFilter());
		bean.addUrlPatterns("/*");

		return bean;
	}

	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {

		if (container instanceof TomcatEmbeddedServletContainerFactory) {
			TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;

			Connector connector = new Connector("HTTP/1.1");
			connector.setPort(8081);

			tomcat.addAdditionalTomcatConnectors(connector);
		}
	}

}
