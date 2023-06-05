package com.example.application.views;

import com.example.application.data.service.AuthService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("register")
public class RegisterView extends VerticalLayout {

    private final AuthService authService;

    public RegisterView(AuthService authService) {
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

        this.authService = authService;

        TextField usernameField = new TextField("Username");
        usernameField.getStyle().set("z-index", "1");

        EmailField emailField = new EmailField("Email");
        emailField.getStyle().set("z-index", "1");

        PasswordField password1Field = new PasswordField("Password");
        password1Field.getStyle().set("z-index", "1");

        PasswordField password2Field = new PasswordField("Confirm password");
        password2Field.getStyle().set("z-index", "1");

        H2 loginTitle = new H2("Register");
        loginTitle.getStyle().set("z-index", "1");

        Button registerButton = new Button("Send", event -> register(
                usernameField.getValue(),
                emailField.getValue(),
                password1Field.getValue(),
                password2Field.getValue()
        ));
        registerButton.getStyle().set("z-index", "1");
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_CONTRAST);

        RouterLink loginRouterLink = new RouterLink("Back to Login", LoginView.class);
        loginRouterLink.getStyle()
                .set("z-index", "1")
                .set("color", "black");

        RouterLink homeRouterLink = new RouterLink("Back to Home", HomeView.class);
        homeRouterLink.getStyle()
                .set("z-index", "1")
                .set("color", "black")
                .set("margin-top", "100px");

        add(
                loginTitle,
                usernameField,
                emailField,
                password1Field,
                password2Field,
                registerButton,
                loginRouterLink,
                homeRouterLink);
    }

//    @Override
//    protected Component initContent() {
//        TextField usernameField = new TextField("Username");
//        EmailField emailField = new EmailField("Email");
//        PasswordField password1Field = new PasswordField("Password");
//        PasswordField password2Field = new PasswordField("Confirm password");
//        return new VerticalLayout(
//                new H2("Register"),
//                usernameField,
//                emailField,
//                password1Field,
//                password2Field,
//                new Button("Send", event -> register(
//                        usernameField.getValue(),
//                        emailField.getValue(),
//                        password1Field.getValue(),
//                        password2Field.getValue()
//                ))
//        );
//    }
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
