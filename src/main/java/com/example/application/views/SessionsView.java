package com.example.application.views;

import com.example.application.data.entity.Sessions;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route(value = "sessions", layout = MainLayout.class)
@PageTitle("Sessions | PlayHub")
public class SessionsView extends VerticalLayout {
    private List<Sessions> sessions;

    public SessionsView() {
        Button plusButton = new Button(new Icon(VaadinIcon.PLUS));
        plusButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        plusButton.getElement().setAttribute("aria-label", "Add item");

        HorizontalLayout horizontalLayout = new HorizontalLayout(plusButton);
        add(horizontalLayout);
    }
}
