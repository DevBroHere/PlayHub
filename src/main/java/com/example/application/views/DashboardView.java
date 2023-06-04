package com.example.application.views;

import com.example.application.data.entity.Users;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.material.Material;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | PlayHub")
public class DashboardView extends HorizontalLayout {
    private TextField name;
    private Button sayHello;

    public DashboardView() {
        setPadding(true);
        setSpacing(true);
        // First Row
        Div welcomeTile = createTile("Welcome " + VaadinSession.getCurrent()
                .getAttribute(Users.class).getUserName() + "!", "welcome-tile");
        Div upcomingSessionTile = createTile("Upcoming Game Session", "upcoming-session-tile");

        HorizontalLayout firstRow = new HorizontalLayout(welcomeTile, upcomingSessionTile);
        firstRow.setWidthFull();
        firstRow.setSpacing(true);

        // Second Row
        Div calendarTile = createTile("Calendar with Booked Sessions", "calendar-tile");
        Div friendsTile = createTile("Last Added Friends", "friends-tile");
        Div settingsTile = createTile("Settings", "settings-tile");

        HorizontalLayout secondRow = new HorizontalLayout(calendarTile, friendsTile, settingsTile);
        secondRow.setWidthFull();
        secondRow.setSpacing(true);

        VerticalLayout rows = new VerticalLayout(firstRow, secondRow);

        // Add all tiles to the main dashboard view
        add(rows);
        setWidthFull();
    }

    private Div createTile(String title, String styleClass) {
        Div tile = new Div(new H2(title));
        tile.addClassName(styleClass);
        tile.getStyle().set("border", "1px solid black").set("padding", "10px");
        tile.setWidth("100%");
        tile.getStyle().set("font-size", "18px");
        tile.getStyle().set("background-color", "#ccd4df");
        tile.getStyle().set("color", "#333333");
        return tile;
    }
}
