package me.wirries.sample;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.ColorScheme;
import com.vaadin.flow.theme.aura.Aura;

/**
 * Application-wide configuration of the HTML page shell: theme, color scheme and global stylesheets.
 * <p>
 * The default color scheme follows the operating system (light or dark). Users can override it in the view.
 */
@StyleSheet(Aura.STYLESHEET)
@StyleSheet("styles.css")
@ColorScheme(ColorScheme.Value.LIGHT_DARK)
public class AppShell implements AppShellConfigurator {
}
