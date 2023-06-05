package com.example.application.views;

import com.example.application.data.entity.Users;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
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
public class DashboardView extends VerticalLayout {
    private TextField name;
    private Button sayHello;

    public DashboardView() {
        setPadding(true);
        setSpacing(true);

        // User Info Tile
        Div userInfoTile = createUserInfoTile();

        // Upcoming Sessions Tile
        Div upcomingSessionsTile = createTile("Upcoming Game Sessions", "upcoming-sessions-tile");

        // Calendar Tile
        Div calendarTile = createTile("Calendar with Booked Sessions", "calendar-tile");

        // Friends Tile
        Div friendsTile = createTile("Last Added Friends", "friends-tile");

        // Settings Tile
        Div settingsTile = createTile("Settings", "settings-tile");

        // Constructing Rows
        HorizontalLayout firstRow = new HorizontalLayout(userInfoTile, upcomingSessionsTile);
        firstRow.setWidthFull();
        firstRow.setSpacing(true);

        HorizontalLayout secondRow = new HorizontalLayout(calendarTile, friendsTile, settingsTile);
        secondRow.setWidthFull();
        secondRow.setSpacing(true);

        // Adding Rows to the Dashboard View
        add(firstRow, secondRow);
        setWidthFull();
    }

    private Div createUserInfoTile() {
        Div userInfoTile = new Div();
        userInfoTile.addClassName("user-info-tile");
        userInfoTile.getStyle()
                .set("border", "1px solid black")
                .set("padding", "10px")
                .set("background-color", "#ccd4df")
                .set("color", "#333333");

        // Fetching user information (sample data)
        String username = VaadinSession.getCurrent().getAttribute(Users.class).getUserName();
        int gamesPlayed = 50;
        String favoriteGame = "Chess";

        // Username Label
        H1 usernameLabel = new H1(username);
        usernameLabel.getStyle().set("font-weight", "bold");

        // Statistics
        Div statisticsDiv = new Div();
        statisticsDiv.setText("Statystyki:");
        statisticsDiv.getStyle().set("font-weight", "bold");
        statisticsDiv.getStyle().set("margin-top", "10px");

        // Games Played
        Div gamesPlayedDiv = new Div();
        gamesPlayedDiv.setText("Ilość zagranych gier: " + gamesPlayed);

        // Favorite Game
        Div favoriteGameDiv = new Div();
        favoriteGameDiv.setText("Ulubiona gra: " + favoriteGame);

        // Adding components to the user info tile
        userInfoTile.add(usernameLabel, statisticsDiv, gamesPlayedDiv, favoriteGameDiv);

        return userInfoTile;
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
