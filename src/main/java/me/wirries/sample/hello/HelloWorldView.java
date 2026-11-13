package me.wirries.sample.hello;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * The single view of this demo: asks for a name and greets the user.
 */
@Route("")
@PageTitle("Hello World")
public class HelloWorldView extends VerticalLayout {

    /**
     * Creates the Hello World view and initializes its user interface.
     *
     * The view contains a heading, descriptive text, a name input field, and a
     * primary action button. Pressing the button or the Enter key displays a
     * greeting notification using the entered name, with the greeting logic
     * handling blank input separately.
     */
    public HelloWorldView() {
        var name = new TextField("Your name");
        name.setPlaceholder("World");
        name.setClearButtonVisible(true);

        var sayHello = new Button("Say hello", event -> Notification.show(greet(name.getValue())));
        sayHello.addThemeVariants(ButtonVariant.PRIMARY);
        sayHello.addClickShortcut(Key.ENTER);

        var form = new HorizontalLayout(name, sayHello);
        form.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

        add(new H1("Hello World"),
                new Paragraph("A minimal Vaadin Flow application. Enter your name and press the button."),
                form);
        setPadding(true);
    }

    /**
     * Builds the greeting for the given name, falling back to "World" for blank input.
     */
    static String greet(String name) {
        var trimmed = name == null ? "" : name.strip();
        return "Hello " + (trimmed.isEmpty() ? "World" : trimmed) + "!";
    }
}
