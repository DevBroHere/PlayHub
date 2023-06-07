package com.example.application.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "")
public class HomeView extends VerticalLayout {

    @Autowired
    public HomeView() {
        setClassName("home-page");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSizeFull();

        // Apply CSS styles to the body
        getStyle().set("margin", "0").set("padding", "0");

        // Navigation Bar
        Nav navBar = new Nav();
        navBar.getStyle()
                .set("background-color", "#f2f2f2")
                .set("padding", "20px")
                .set("text-align", "center")
                .set("width", "100%");


        // Logo in the Navbar
        H2 logo = new H2("PlayHub");
        logo.getStyle().set("margin", "0");

        // Image next to the logo
        Image image = new Image("images/logo.png", "Logo");
        image.setWidth("60px");
        image.setHeight("40px");
        image.getStyle().set("margin-left", "10px");

        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.add(image, logo);

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        loginButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));
        registerButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(RegisterView.class)));

        loginButton.setClassName("login-button");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_CONTRAST);
        loginButton.getStyle().set("margin", "5px");
        registerButton.setClassName("register-button");
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_CONTRAST);
        registerButton.getStyle().set("margin", "5px");

        // Container for the buttons on the right side
        Div buttonContainer = new Div(loginButton, registerButton);
        buttonContainer.getStyle().set("margin-right", "10px").set("padding", "5px");

        // Horizontal layout for the navbar contents
        FlexLayout navContents = new FlexLayout(logoLayout, buttonContainer);
        navContents.setSizeFull();
        navContents.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        navContents.setAlignItems(FlexComponent.Alignment.CENTER);
//        navContents.add(buttonContainer, image, logo);

        navBar.add(navContents);

        // Space between Navbar and First Description
        Div space = new Div();
        space.getStyle().set("height", "50px"); // Adjust the height as needed

        // App Description Sections
        Div descriptionSection1 = createAppDescriptionSection("Try PlayHub! Play Together!",
                "Are you tired of missing out on your favorite games because you can't find someone to " +
                        "play with? Say goodbye to those lonely gaming sessions and welcome PlayHub, the " +
                        "ultimate app designed to connect gamers like you!\n" +
                "\n" +
                "With PlayHub, you can easily discover and connect with fellow gamers who share your passion " +
                        "for the games you love. No more waiting around for friends or struggling to find compatible " +
                        "teammates. Whether you're into multiplayer battles, cooperative missions, or casual gaming " +
                        "sessions, PlayHub has got you covered.");
        descriptionSection1.getStyle()
                .set("background-color", "rgba(128, 128, 128, 0.5)")
                .set("color", "white") // Set background color to semi-transparent grey and text color to white
                .set("margin-bottom", "150px") // Increase the space between the description divs
                .set("padding", "50px")
                .set("margin-top", "50px")
                .set("width", "100%");
//        Div descriptionSection2 = createAppDescriptionSection("App Description Section 2", "Text and content for section 2.");


        // Footer
        Div footer = new Div();
        footer.getStyle()
                .set("background-color", "#f2f2f2")
                .set("padding", "20px")
                .set("text-align", "center")
                .set("width", "100%")
                .set("min-height", "200px");

        // Authors paragraph
        Paragraph authorsParagraph1 = new Paragraph("Authors");
        Paragraph authorsParagraph2 = new Paragraph("Cezary Bujak: 244769");
        Paragraph authorsParagraph3 = new Paragraph("Adam Żerkowski: 244795");

        footer.add(authorsParagraph1, authorsParagraph2, authorsParagraph3);

        // Adding components to the layout
        add(navBar, space, descriptionSection1, footer);
        setSizeFull();
        setSpacing(false);
        setMargin(false);

//        Div backgroundImage = new Div();
//        backgroundImage.setClassName("background-image");
//        backgroundImage.getElement().getStyle().set("background-image", "url('background.jpg')");
//        backgroundImage.getElement().getStyle().set("background-size", "cover");
//        backgroundImage.getElement().getStyle().set("background-position", "center");

        this.getElement().getStyle()
                .set("background-image", "url('images/background.jpg')")
                .set("background-size", "cover");

//        Label title = new Label("PlayHub");
//        title.setClassName("title");
//
//        Label description = new Label("Join the community and play together.");
//        description.setClassName("description");
//
//
//        VerticalLayout contentLayout = new VerticalLayout(title, description, buttonsLayout);
//        contentLayout.setClassName("content-layout");
//        contentLayout.setAlignItems(Alignment.CENTER);
//
//        add(contentLayout);
    }

    private Div createAppDescriptionSection(String heading, String content) {
        Div section = new Div();
        section.getStyle()
                .set("background-color", "rgba(128, 128, 128, 0.5)")
                .set("color", "white")
                .set("padding", "50px")
                .set("text-align", "center")
                .set("width", "100%");

        H2 title = new H2(heading);
        title.getStyle().set("margin-top", "0");

        Paragraph text = new Paragraph(content);
        text.getStyle().set("margin-bottom", "0");

        section.add(title, text);
        return section;
    }

}
