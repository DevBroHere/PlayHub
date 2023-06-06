package com.example.application.views;

import com.example.application.data.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route(value = "login")
public class LoginView extends VerticalLayout {
    public LoginView(AuthService authService) {
        setId("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        this.getElement().getStyle()
                .set("background-image", "url('images/background.jpg')")
                .set("background-size", "cover");

        // Create the semi-transparent grey rectangle
        Div rectangle = new Div();
        rectangle.getStyle()
                .set("position", "fixed")
                .set("top", "50%")
                .set("left", "50%")
                .set("transform", "translate(-50%, -50%)")
                .set("width", "300px")
                .set("height", "600px")
                .set("background-color", "rgba(255, 255, 255, 0.8)")
                .set("text-align", "center")
                .set("line-height", "200px");

        // Add the rectangle to the layout
        add(rectangle);
        setSizeFull();
        setSpacing(false);
        setMargin(false);

        var username = new TextField("Username");
        username.getStyle().set("z-index", "1");
        var password = new PasswordField("Password");
        password.getStyle().set("z-index", "1");

        Image image = new Image("images/logo.png", "Logo");
        image.setWidth("60px");
        image.setHeight("40px");
        image.getStyle().set("z-index", "1");

        Button loginButton = new Button("Login", event -> {
            try {
                authService.authenticate(username.getValue(), password.getValue());
                UI.getCurrent().navigate("dashboard");
            } catch (AuthService.AuthException e) {
                Notification.show("Wrong credentials.");
            }
        });
        loginButton.getStyle().set("z-index", "1");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_CONTRAST);

        Paragraph text1 = new Paragraph("Don't have an account yet?");
        text1.getStyle()
                .set("color", "black")
                .set("z-index", "1");

        RouterLink registerRouterLink = new RouterLink("Register", RegisterView.class);
        registerRouterLink.getStyle()
                .set("z-index", "1")
                .set("color", "black");

        RouterLink homeRouterLink = new RouterLink("Back to Home", HomeView.class);
        homeRouterLink.getStyle()
                .set("z-index", "1")
                .set("color", "black")
                .set("margin-top", "100px");

        add(
                image,
                username,
                password,
                loginButton,
                text1,
                registerRouterLink,
                homeRouterLink
        );
    }
}
