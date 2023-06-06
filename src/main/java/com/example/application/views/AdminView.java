package com.example.application.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@Route("admin")
@RolesAllowed("ADMIN") // <- Should match one of the user's roles (case-sensitive)
public class AdminView extends VerticalLayout {

    @Autowired
    public AdminView() {
        setSizeFull();
        // Apply CSS styles to the body
        getStyle().set("margin", "0").set("padding", "0");

        // Navigation Bar
        Nav navBar = new Nav();
        navBar.getStyle()
                .set("background-color", "#f2f2f2")
                .set("padding-top", "20px")
                .set("padding-bottom", "20px")
                .set("text-align", "center")
                .set("width", "100%");

        // Logo in the Navbar
        H2 logo = new H2("PlayHub | Admin Tools");
        logo.getStyle().set("margin", "0");

        // Image next to the logo
        Image image = new Image("images/logo.png", "Logo");
        image.setWidth("60px");
        image.setHeight("40px");
        image.getStyle().set("margin-left", "10px");

        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.add(image, logo);

        // Return to the main user panel (dashboard)
        Button returnButton = new Button("Return");
        returnButton.setClassName("login-button");
        returnButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_CONTRAST);
        returnButton.getStyle()
                .set("margin-right", "20px");
        returnButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(DashboardView.class)));

        // Horizontal layout for the navbar contents
        FlexLayout navContents = new FlexLayout(logoLayout, returnButton);
        navContents.setSizeFull();
        navContents.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        navContents.setAlignItems(FlexComponent.Alignment.CENTER);

        navBar.add(navContents);

        add(navBar);
    }
}
