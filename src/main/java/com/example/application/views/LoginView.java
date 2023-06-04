package com.example.application.views;

import com.example.application.data.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route(value = "login")
public class LoginView extends Div {
    public LoginView(AuthService authService) {
        setId("login-view");
        var username = new TextField("Username");
        var password = new PasswordField("Password");
        add(
                new H1("Welcome"),
                username,
                password,
                new Button("Login", event -> {
                    try {
                        authService.authenticate(username.getValue(), password.getValue());
                        UI.getCurrent().navigate("dashboard");
                    } catch (AuthService.AuthException e) {
                        Notification.show("Wrong credentials.");
                    }
                }),
                new RouterLink("Register", RegisterView.class)
        );
    }
}
