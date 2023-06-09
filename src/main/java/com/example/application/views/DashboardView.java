package com.example.application.views;

import com.example.application.data.entity.SessionUsers;
import com.example.application.data.entity.Sessions;
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

        // Creating the user info tile
        Div userInfoTile = createUserInfoTile(username, userId);

        // Creating the upcoming sessions tile
        Div upcomingSessionsTile = createUpcomingSessionsTile(userId);

        // Creating other tiles...

        // Constructing Rows
        HorizontalLayout firstRow = new HorizontalLayout(userInfoTile, upcomingSessionsTile);
        firstRow.setWidthFull();
        firstRow.setSpacing(true);

        // Adding Rows to the Dashboard View
        add(firstRow);
        setWidthFull();

        checkAndUpdateSessionStatus(userId);
    }

    private Div createUserInfoTile(String username, Long userId) {
        Div userInfoTile = new Div();
        userInfoTile.addClassName("user-info-tile");
        userInfoTile.getStyle()
                .set("border", "1px solid black")
                .set("padding", "10px")
                .set("background-color", "#ccd4df")
                .set("color", "#333333");

        // Username Label
        H1 usernameLabel = new H1(username);
        usernameLabel.getStyle().set("font-weight", "bold");

        // Fetching user statistics
        TypedQuery<Long> gamesPlayedQuery = entityManager.createQuery(
                "SELECT COUNT(DISTINCT su.session) " +
                        "FROM SessionUsers su " +
                        "JOIN su.session s " +
                        "WHERE su.user.userID = :userId " +
                        "AND s.sessionStatus = :finishedStatus",
                Long.class
        );
        gamesPlayedQuery.setParameter("userId", userId);
        gamesPlayedQuery.setParameter("finishedStatus", "FINISHED");
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
        String favoriteGame = favoriteGameQuery.getSingleResult();
        // Statistics
        Div statisticsDiv = new Div();
        statisticsDiv.setText("Stats:");
        statisticsDiv.getStyle().set("font-weight", "bold");
        statisticsDiv.getStyle().set("margin-top", "10px");

        // Games Played
        Div gamesPlayedDiv = new Div();
        gamesPlayedDiv.setText("Games Played: " + gamesPlayed);

        // Favorite Game
        Div favoriteGameDiv = new Div();
        favoriteGameDiv.setText("Favorite Game: " + favoriteGame);

        // Adding components to the user info tile
        userInfoTile.add(usernameLabel, statisticsDiv, gamesPlayedDiv, favoriteGameDiv);

        return userInfoTile;
    }

    private Div createUpcomingSessionsTile(Long userId) {
        Div upcomingSessionsTile = createTile("Upcoming Session", "upcoming-sessions-tile");

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

            H2 sessionNameLabel = new H2("Session Name: " + sessionName);
            sessionNameLabel.getStyle().set("font-size", "20px"); // Set the font size to 16px
            Paragraph sessionDateLabel = new Paragraph("Date: " + sessionStart.toLocalDate().toString());
            Paragraph sessionTimeLabel = new Paragraph("Time: " + sessionStart.toLocalTime().toString());
            upcomingSessionsTile.add(sessionNameLabel, sessionDateLabel, sessionTimeLabel);
        } else {
            upcomingSessionsTile.add(new Paragraph("No Upcoming Sessions Found."));
        }

        return upcomingSessionsTile;
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

    private void checkAndUpdateSessionStatus(Long userId) {
        // Fetching the sessions that are still open
        TypedQuery<Sessions> openSessionQuery = entityManager.createQuery(
                "SELECT s FROM Sessions s " +
                        "WHERE s.sessionStatus = :openStatus " +
                        "AND s.sessionStart < :currentDate",
                Sessions.class
        );
        openSessionQuery.setParameter("openStatus", "OPEN");
        openSessionQuery.setParameter("currentDate", LocalDateTime.now());
        List<Sessions> openSessions = openSessionQuery.getResultList();

        // Update the session status to "FINISHED" for the sessions that have passed
        for (Sessions session : openSessions) {
            session.setSessionStatus("FINISHED");
            entityManager.persist(session);
        }
    }
}