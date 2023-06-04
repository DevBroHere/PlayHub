package com.example.application.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
public class HomeView extends VerticalLayout {

    private final Button loginButton;
    private final Button registerButton;

    @Autowired
    public HomeView() {
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSizeFull();

        H1 title = new H1("PlayHub");
        loginButton = new Button("Login");
        registerButton = new Button("Register");

        Paragraph description = new Paragraph("An app for arranging to play together.");

        loginButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));
        registerButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(RegisterView.class)));

        add(title, description, loginButton, registerButton);
    }

}
