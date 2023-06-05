package com.example.application.views;

import com.example.application.data.service.AuthService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import java.util.List;
import java.util.Map;

@Route("activate")
public class ActivationView extends VerticalLayout implements BeforeEnterObserver {

    private VerticalLayout layout;

    private final AuthService authService;

    public ActivationView(AuthService authService) {
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

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            Map<String, List<String>> params = event.getLocation().getQueryParameters().getParameters();
            String code = params.get("code").get(0);
            authService.activate(code);

            H2 activationText = new H2("Account activated");
            activationText.getStyle()
                    .set("z-index", "1")
                    .set("color", "black");

            RouterLink loginRouterLink = new RouterLink("Login", LoginView.class);
            loginRouterLink.getStyle()
                    .set("z-index", "1")
                    .set("color", "black");

            add(
                    activationText,
                    loginRouterLink
            );
        } catch (AuthService.AuthException e) {
            Text invalidText = new Text("Invalid link.");
            invalidText.getStyle()
                    .set("z-index", "1")
                    .set("color", "black");
            add(invalidText);
        }
    }
}
