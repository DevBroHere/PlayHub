package com.example.application.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("goback")
public class ErrorView extends VerticalLayout {
    public ErrorView() {
        setId("error-view");
        setSizeFull();
        setSpacing(false);
        setMargin(false);
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

        H2 errorText = new H2("Oops... go back!");
        errorText.getStyle()
                .set("z-index", "1")
                .set("color", "black");

        RouterLink homeRouterLink = new RouterLink("Back", HomeView.class);
        homeRouterLink.getStyle()
                .set("z-index", "1")
                .set("color", "black");

        // Add the rectangle to the layout
        add(rectangle, errorText, homeRouterLink);
    }
}
