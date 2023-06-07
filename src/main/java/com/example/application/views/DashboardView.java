package com.example.application.views;

import com.example.application.data.entity.SessionUsers;
import com.example.application.data.entity.Sessions;
import com.example.application.data.entity.Users;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.avatar.Avatar;
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
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | PlayHub")
public class DashboardView extends VerticalLayout {
    @PersistenceContext
    private EntityManager entityManager;

    private TextField name;
    private Button sayHello;

    public DashboardView() {
        setPadding(true);
        setSpacing(true);
        // ...
    }

    @PostConstruct
    public void initialize() {
        // Fetching user information
        Users user = VaadinSession.getCurrent().getAttribute(Users.class);
        String username = user.getUserName();
        Long userId = user.getUserID();

        // Create an Html component to include CSS styling
        Html contentHtml = new Html(
                "<style>" +
                        ".info-tiles {" +
                        "    display: flex;" +
                        "    flex-wrap: wrap;" +
                        "}" +
                        ".info-tile {" +
                        "    flex-basis: 1 0 33.33%;" +
                        "    margin: 0.5rem;" +
                        "    padding: 1rem;" +
                        "    border: 1px solid #ddd;" +
                        "    border-radius: 5px;" +
                        "    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);" +
                        "    transition: background-color 0.3s ease-in-out;" +
                        "    background-color: #96adc8;" +
                        "}" +
                        ".info-tile:hover {" +
                        "    background-color: #67597a;" +
                        "@media (max-width: 768px) {" +
                        "    .info-tile {" +
                        "        flex-basis: 1 0 100%;" +
                        "    }" +
                        "}" +
                        "</style>"
        );

        Div tilesLayout = new Div();
        tilesLayout.setWidth("100%");
        tilesLayout.addClassName("info-tiles");

        // Creating the user info tile
        Div userInfoTile = createUserInfoTile(username, userId);

        // Creating the upcoming sessions tile
        Div upcomingSessionsTile = createUpcomingSessionsTile(userId);

        // Creating other tiles...

//        // Constructing Rows
//        HorizontalLayout firstRow = new HorizontalLayout(userInfoTile, upcomingSessionsTile);
//        firstRow.setWidthFull();
//        firstRow.setSpacing(true);

        tilesLayout.add(userInfoTile, upcomingSessionsTile);

        // Adding Rows to the Dashboard View
        add(contentHtml, tilesLayout);
        setWidthFull();
    }

    private Div createUserInfoTile(String username, Long userId) {
        Div userInfoTile = new Div();
        userInfoTile.addClassName("info-tile");
        userInfoTile.setWidth("100%");

        // Content of tile
        HorizontalLayout tileContent = new HorizontalLayout();

        Avatar avatar = new Avatar();
        avatar.setName(username);
        avatar.getStyle().set("width", "100px")
                .set("height", "100px")
                .set("border-radius", "50%");

        // Add user-related content to the 2nd column in the tile
        VerticalLayout userContent = new VerticalLayout();

        // Username Label
        H1 usernameLabel = new H1(username);
        usernameLabel.getStyle().set("font-weight", "bold");
        userContent.add(usernameLabel);

        // Fetching user statistics
        TypedQuery<Long> gamesPlayedQuery = entityManager.createQuery(
                "SELECT COUNT(su) FROM SessionUsers su WHERE su.user.userID = :userId",
                Long.class
        );
        gamesPlayedQuery.setParameter("userId", userId);
        Long gamesPlayed = gamesPlayedQuery.getSingleResult();

        TypedQuery<String> favoriteGameQuery = entityManager.createQuery(
                "SELECT g.gameTitle " +
                        "FROM Games g " +
                        "WHERE g.gameID IN (" +
                        "   SELECT s.game.gameID " +
                        "   FROM SessionUsers su " +
                        "   JOIN su.session s " +
                        "   WHERE su.user.userID = :userId " +
                        "   GROUP BY s.game " +
                        "   ORDER BY COUNT(s) DESC " +
                        ")",
                String.class
        );
        favoriteGameQuery.setParameter("userId", userId);
        favoriteGameQuery.setMaxResults(1); // Retrieve only the first row
        String favoriteGame;
        try {
            favoriteGame = favoriteGameQuery.getSingleResult();
        } catch (Exception noResultException) {
            favoriteGame = "None";
        }

        // Statistics
        Paragraph statisticsParagraph = new Paragraph();
        statisticsParagraph.setText("Statystyki:");
        statisticsParagraph.getStyle().set("font-weight", "bold");
        statisticsParagraph.getStyle().set("margin-top", "10px");

        // Games Played
        Paragraph gamesPlayedParagraph = new Paragraph();
        gamesPlayedParagraph.setText("Ilość zagranych gier: " + gamesPlayed);

        // Favorite Game
        Paragraph favoriteGameParagraph = new Paragraph();
        favoriteGameParagraph.setText("Ulubiona gra: " + favoriteGame);

        userContent.add(usernameLabel,
                statisticsParagraph,
                gamesPlayedParagraph,
                favoriteGameParagraph);

        tileContent.add(avatar, userContent);

        userInfoTile.add(tileContent);

        return userInfoTile;
    }

    private Div createUpcomingSessionsTile(Long userId) {
        Div upcomingSessionsTile = new Div();
        upcomingSessionsTile.addClassName("info-tile");
        upcomingSessionsTile.setWidth("100%");

        VerticalLayout userContent = new VerticalLayout();

        H1 upcomingSessionLabel = new H1("Upcoming Session");

        userContent.add(upcomingSessionLabel);

        // Fetching the next upcoming session for the user
        TypedQuery<Object[]> upcomingSessionQuery = entityManager.createQuery(
                "SELECT s.sessionName, s.sessionStart " +
                        "FROM SessionUsers su " +
                        "JOIN su.session s " +
                        "WHERE su.user.userID = :userId " +
                        "AND s.sessionStart >= :currentDate " +
                        "ORDER BY s.sessionStart ASC",
                Object[].class
        );
        upcomingSessionQuery.setParameter("userId", userId);
        upcomingSessionQuery.setParameter("currentDate", LocalDateTime.now());
        upcomingSessionQuery.setMaxResults(1);
        List<Object[]> upcomingSessions = upcomingSessionQuery.getResultList();

        // Displaying the upcoming session information
        if (!upcomingSessions.isEmpty()) {
            Object[] upcomingSession = upcomingSessions.get(0);
            String sessionName = (String) upcomingSession[0];
            LocalDateTime sessionStart = (LocalDateTime) upcomingSession[1];

            H2 sessionNameLabel = new H2("Nazwa Sesji: " + sessionName);
            sessionNameLabel.getStyle().set("font-size", "20px"); // Set the font size to 16px
            Paragraph sessionDateLabel = new Paragraph("Date: " + sessionStart.toLocalDate().toString());
            Paragraph sessionTimeLabel = new Paragraph("Time: " + sessionStart.toLocalTime().toString());
            userContent.add(sessionNameLabel, sessionDateLabel, sessionTimeLabel);
            upcomingSessionsTile.add(userContent);
        } else {
            upcomingSessionsTile.add(new Paragraph("No upcoming sessions found."));
        }

        return upcomingSessionsTile;
    }
}