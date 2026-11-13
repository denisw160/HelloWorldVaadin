package me.wirries.sample.hello;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.page.ColorScheme;
import com.vaadin.flow.component.select.Select;
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
    void selectDark_appliesAndRemembersColorScheme() {
        window.navigate(HelloWorldView.class);
        var page = window.getUI().getPage();
        assertEquals(ColorScheme.Value.LIGHT_DARK, page.getColorScheme());

        window.test(colorSchemeSelector(window)).selectItem("Dark");
        assertEquals(ColorScheme.Value.DARK, page.getColorScheme());

        // A second window of the same user (like a page reload) shares the session and keeps the choice
        var secondWindow = window.getUser().newWindow();
        secondWindow.navigate(HelloWorldView.class);
        assertEquals(ColorScheme.Value.DARK, colorSchemeSelector(secondWindow).getValue());
        assertEquals(ColorScheme.Value.DARK, secondWindow.getUI().getPage().getColorScheme());
    }

    @SuppressWarnings("unchecked")
    private static Select<ColorScheme.Value> colorSchemeSelector(BrowserlessUIContext window) {
        return window.find(Select.class).single();
    }

    @Test
    void blankName_greetsWorld() {
        assertEquals("Hello World!", HelloWorldView.greet("  "));
        assertEquals("Hello World!", HelloWorldView.greet(null));
    }
}
