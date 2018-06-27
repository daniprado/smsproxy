package com.argallar.smsproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Configuration class & Main method holder.
 *
 * @author <a href="dpradom@argallar.com">Daniel Prado</a>
 *
 */
@SpringBootApplication
public class SmsProxy {
    public static void main(final String[] args) {
        ApplicationContext ctx = SpringApplication.run(SmsProxy.class, args);
        DispatcherServlet dispatcherServlet = (DispatcherServlet)ctx.getBean("dispatcherServlet");
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
    }
}
