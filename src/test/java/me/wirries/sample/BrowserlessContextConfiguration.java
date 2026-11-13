package me.wirries.sample;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.SpringBrowserlessApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides one shared browserless Vaadin context for all {@code @SpringBootTest} classes.
 */
@Configuration
public class BrowserlessContextConfiguration {

    @Bean
    BrowserlessApplicationContext browserlessApplicationContext(ApplicationContext applicationContext) {
        return SpringBrowserlessApplicationContext.create(applicationContext, Application.class);
    }
}
