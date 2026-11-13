package me.wirries.sample.hello;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Browserless UI test: drives the view on the server side, without a real browser.
 */
@SpringBootTest
class HelloWorldViewTest {

    @Autowired
    private BrowserlessApplicationContext app;

    private BrowserlessUIContext window;

    @BeforeEach
    void openWindow() {
        window = app.newUser().newWindow();
    }

    @AfterEach
    void closeUser() {
        window.getUser().close();
    }

    @Test
    void enterName_clickButton_showsGreeting() {
        window.navigate(HelloWorldView.class);

        window.findTextField().withLabel("Your name").setValue("Vaadin");
        window.findButton().withText("Say hello").click();

        assertEquals("Hello Vaadin!", window.findNotification().getText());
    }

    @Test
    void blankName_greetsWorld() {
        assertEquals("Hello World!", HelloWorldView.greet("  "));
        assertEquals("Hello World!", HelloWorldView.greet(null));
    }
}
