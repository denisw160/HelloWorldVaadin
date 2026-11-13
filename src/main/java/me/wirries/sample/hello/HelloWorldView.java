package me.wirries.sample.hello;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.ColorScheme;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

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
     * handling blank input separately. Below the form, a selector switches
     * between the system, light and dark color schemes.
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
                form,
                createColorSchemeSelector());
        setPadding(true);
    }

    /**
     * Creates the light/dark switch. The choice is applied to the page and remembered in the
     * Vaadin session, so it survives a page reload. "System" follows the operating system setting.
     */
    private RadioButtonGroup<ColorScheme.Value> createColorSchemeSelector() {
        var selector = new RadioButtonGroup<ColorScheme.Value>("Color scheme");
        selector.setItems(ColorScheme.Value.LIGHT_DARK, ColorScheme.Value.LIGHT, ColorScheme.Value.DARK);
        selector.setItemLabelGenerator(HelloWorldView::colorSchemeLabel);
        selector.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrentOrThrow().getPage().setColorScheme(event.getValue());
                VaadinSession.getCurrent().setAttribute(ColorScheme.Value.class, event.getValue());
            }
        });

        var stored = VaadinSession.getCurrent().getAttribute(ColorScheme.Value.class);
        selector.setValue(stored != null ? stored : ColorScheme.Value.LIGHT_DARK);
        return selector;
    }

    private static String colorSchemeLabel(ColorScheme.Value value) {
        return switch (value) {
            case LIGHT -> "Light";
            case DARK -> "Dark";
            default -> "System";
        };
    }

    /**
     * Builds the greeting for the given name, falling back to "World" for blank input.
     */
    static String greet(String name) {
        var trimmed = name == null ? "" : name.strip();
        return "Hello " + (trimmed.isEmpty() ? "World" : trimmed) + "!";
    }
}
