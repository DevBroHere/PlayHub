package com.example.application.views;

import com.example.application.data.service.AuthService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("register")
public class RegisterView extends Composite {
    private final AuthService authService;

    public RegisterView(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected Component initContent() {
        TextField usernameField = new TextField("Username");
        EmailField emailField = new EmailField("Email");
        PasswordField password1Field = new PasswordField("Password");
        PasswordField password2Field = new PasswordField("Confirm password");
        return new VerticalLayout(
                new H2("Register"),
                usernameField,
                emailField,
                password1Field,
                password2Field,
                new Button("Send", event -> register(
                        usernameField.getValue(),
                        emailField.getValue(),
                        password1Field.getValue(),
                        password2Field.getValue()
                ))
        );
    }

    private void register(String username, String email, String password1, String password2) {
        if (username.trim().isEmpty()) {
            Notification.show("Enter a username");
        } else if (email.isEmpty()) {
            Notification.show("Enter an email");
        } else if (password1.isEmpty()) {
            Notification.show("Enter a password");
        } else if (!password1.equals(password2)) {
            Notification.show("Passwords don't match");
        } else {
            authService.register(username, email, password1);
            Notification.show("Check your email.");
        }
    }
}
