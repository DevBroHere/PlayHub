package com.example.application.views;

import com.example.application.data.entity.*;
import com.example.application.data.repository.GameRepository;
import com.example.application.data.repository.SessionRepository;
import com.example.application.data.repository.SessionUsersRepository;
import com.example.application.data.repository.UserRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Route(value = "sessions", layout = MainLayout.class)
@PageTitle("Sessions | PlayHub")
public class SessionsView extends VerticalLayout {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final SessionUsersRepository sessionUsersRepository;
    private final Grid<Sessions> sessionsGrid;

    private final TextField searchField;

    public SessionsView(GameRepository gameRepository, UserRepository userRepository,
                        SessionRepository sessionRepository, SessionUsersRepository sessionUsersRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.sessionUsersRepository = sessionUsersRepository;

        checkSessionStatus();

        Button createGameButton = new Button("New Session", new Icon(VaadinIcon.PLUS));
        createGameButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        createGameButton.getElement().setAttribute("aria-label", "Add item");
        createGameButton.addClickListener(e -> showCreateGameDialog());

        Button publicGamesButton = new Button("Public Sessions");
        Button privateGamesButton = new Button("Private Sessions");

        searchField = new TextField();
        searchField.setPlaceholder("Name/Game/Platform");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> filterSessions(e.getValue()));
        add(searchField);

        HorizontalLayout sectionLayout = new HorizontalLayout(createGameButton, publicGamesButton, privateGamesButton, searchField);
        add(sectionLayout);


        sessionsGrid = new Grid<>();
        sessionsGrid.addColumn(Sessions::getSessionName).setHeader("Session Name");
        sessionsGrid.addColumn(session -> session.getGame().getGameTitle()).setHeader("Game");
        sessionsGrid.addColumn(session -> session.getGame().getPlatformType()).setHeader("Platform");
        sessionsGrid.addColumn(session -> session.getUser().getUserName()).setHeader("Owner");
        sessionsGrid.setVisible(false);
        add(sessionsGrid);


        publicGamesButton.addClickListener(e -> {
            List<Sessions> publicSessions = sessionRepository.findBySessionTypeAndSessionStatus("PUBLIC", "OPEN");
            showSessionsGrid(publicSessions);
        });

        privateGamesButton.addClickListener(e -> {
            List<Sessions> privateSessions = sessionRepository.findBySessionTypeAndSessionStatus("PRIVATE", "OPEN");
            showSessionsGrid(privateSessions);
        });
    }

    private void showCreateGameDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);

        TextField sessionNameField = new TextField("Sessin Name");

        Select<String> gameTypeSelect = new Select<>();
        gameTypeSelect.setLabel("Game");
        List<String> gameTypes = gameRepository.findDistinctGameTitle();
        gameTypeSelect.setItems(gameTypes);

        Select<String> platformSelect = new Select<>();
        platformSelect.setLabel("Platform");
        platformSelect.setEnabled(false); // początkowo pole jest nieaktywne

        gameTypeSelect.addValueChangeListener(event -> {
            String selectedGame = event.getValue();
            List<String> platformList = gameRepository.findDistinctPlatformTypesByGameTitle(selectedGame);
            platformSelect.setItems(platformList);
            platformSelect.setEnabled(true); // aktywuj pole po wybraniu gry
        });

        Checkbox privateSessionCheckbox = new Checkbox("Private Session");
        PasswordField passwordField = new PasswordField("Password");
        passwordField.setVisible(false);

        privateSessionCheckbox.addValueChangeListener(event -> {
            passwordField.setVisible(event.getValue());
        });

        DateTimePicker dateTimePicker = new DateTimePicker();
        dateTimePicker.setLabel("Date and Time of Session Start");
        dateTimePicker.setStep(Duration.ofMinutes(15));
        dateTimePicker.setValue(LocalDateTime.now());

        FormLayout formLayout = new FormLayout();
        formLayout.add(
                sessionNameField,
                gameTypeSelect,
                platformSelect,
                privateSessionCheckbox,
                passwordField,
                dateTimePicker
        );

        Button saveButton = new Button("Save");
        saveButton.addClickListener(e -> {
            String sessionName = sessionNameField.getValue();
            String gameType = gameTypeSelect.getValue();
            String platformType = platformSelect.getValue();
            boolean isPrivateSession = privateSessionCheckbox.getValue();
            String sessionPassword = isPrivateSession ? passwordField.getValue() : null;
            LocalDateTime startDate = dateTimePicker.getValue();

            if (sessionName.isEmpty() || gameType == null || platformType == null || startDate == null) {
                Notification.show("Fill in all the fields!", 3000, Notification.Position.MIDDLE);
                return;
            }

            if (isPrivateSession && sessionPassword.isEmpty()) {
                Notification.show("Enter the password for the private session!", 3000, Notification.Position.MIDDLE);
                return;
            }

            Games game = gameRepository.findFirstByGameTitleAndPlatformType(gameType, platformType);
            Notification.show(game.getGameTitle());

            if (game == null) {
                Notification.show("No game with the selected parameters found!", 3000, Notification.Position.MIDDLE);
                return;
            }

            Users user = getCurrentUser();
            Sessions session = new Sessions(game, user, sessionName, isPrivateSession ? "PRIVATE" : "PUBLIC", startDate, sessionPassword);
            session.setSessionPassword(sessionPassword);
            sessionRepository.save(session);

            SessionUsers sessionUser = new SessionUsers();
            sessionUser.setSession(session);
            sessionUser.setUser(user);
            sessionUsersRepository.save(sessionUser);

            Notification.show("The session was created!", 3000, Notification.Position.MIDDLE);
            dialog.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e -> dialog.close());

        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, cancelButton);

        VerticalLayout dialogLayout = new VerticalLayout(formLayout, buttonsLayout);
        dialog.add(dialogLayout);

        dialog.open();
    }

    private void showSessionsGrid(List<Sessions> sessionsList) {
        List<Sessions> openSessions = sessionsList.stream()
                .filter(session -> session.getSessionStatus().equals("OPEN"))
                .collect(Collectors.toList());

        sessionsGrid.setItems(sessionsList);
        sessionsGrid.removeAllColumns();

        sessionsGrid.addColumn(Sessions::getSessionName).setHeader("Session Name");
        sessionsGrid.addColumn(session -> session.getGame().getGameTitle()).setHeader("Game");
        sessionsGrid.addColumn(session -> session.getGame().getPlatformType()).setHeader("Platform");
        sessionsGrid.addColumn(session -> session.getUser().getUserName()).setHeader("Owner");

        Users currentUser = getCurrentUser();

        sessionsGrid.addComponentColumn(session -> {
            HorizontalLayout buttonLayout = new HorizontalLayout();
            Button joinButton = new Button("Join", new Icon(VaadinIcon.PLUS));
            joinButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);

            SessionUsers sessionUser = sessionUsersRepository.findBySessionAndUser(session, currentUser);
            if (sessionUser != null) {
                joinButton.setEnabled(false);
                joinButton.setIcon(new Icon(VaadinIcon.CHECK));
                buttonLayout.addClassName("joined-session");
            }

            joinButton.addClickListener(e -> {
                if (session.getSessionType().equals("PRIVATE")) {
                    showJoinPrivateSessionDialog(session, currentUser, joinButton, buttonLayout);
                } else {
                    joinSession(session, currentUser, joinButton, buttonLayout, null);
                }
            });
            buttonLayout.add(joinButton);
            return buttonLayout;
        }).setHeader(createButtonHeader());

        sessionsGrid.setVisible(true);
    }

    private void showJoinPrivateSessionDialog(Sessions session, Users user, Button joinButton, HorizontalLayout buttonLayout) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);

        PasswordField passwordField = new PasswordField("Password");

        Button joinPrivateButton = new Button("Join");
        joinPrivateButton.addClickListener(e -> {
            String sessionPassword = passwordField.getValue();
            if (sessionPassword.equals(session.getSessionPassword())) {
                joinSession(session, user, joinButton, buttonLayout, dialog);
            } else {
                Notification.show("Incorrect session password!", 3000, Notification.Position.MIDDLE);
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e -> dialog.close());

        dialog.add(passwordField, new HorizontalLayout(joinPrivateButton, cancelButton));
        dialog.open();
    }

    private Users getCurrentUser() {
        return VaadinSession.getCurrent().getAttribute(Users.class);
    }

    private void joinSession(Sessions session, Users user, Button joinButton, HorizontalLayout buttonLayout, Dialog dialog) {
        SessionUsers sessionUser = new SessionUsers();
        sessionUser.setSession(session);
        sessionUser.setUser(user);
        sessionUsersRepository.save(sessionUser);

        joinButton.setEnabled(false);
        joinButton.setIcon(new Icon(VaadinIcon.CHECK));
        buttonLayout.addClassName("joined-session");

        Notification.show("Joined the session!", 3000, Notification.Position.MIDDLE);

        if (dialog != null) {
            dialog.close();
        }
    }

    private Component createButtonHeader() {
        Span header = new Span();
        header.addClassName("button-header");
        return header;
    }

    private void filterSessions(String searchTerm) {
        List<Sessions> sessionsList = sessionRepository.findAll();

        if (!searchTerm.isEmpty()) {
            sessionsList = sessionsList.stream()
                    .filter(session -> {
                        String sessionName = session.getSessionName().toLowerCase(Locale.ENGLISH);
                        String gameTitle = session.getGame().getGameTitle().toLowerCase(Locale.ENGLISH);
                        String platformType = session.getGame().getPlatformType().toLowerCase(Locale.ENGLISH);
                        return sessionName.contains(searchTerm.toLowerCase(Locale.ENGLISH))
                                || gameTitle.contains(searchTerm.toLowerCase(Locale.ENGLISH))
                                || platformType.contains(searchTerm.toLowerCase(Locale.ENGLISH));
                    })
                    .filter(session -> session.getSessionStatus().equals("OPEN"))
                    .collect(Collectors.toList());
        } else {
            sessionsList = sessionsList.stream()
                    .filter(session -> session.getSessionStatus().equals("OPEN"))
                    .collect(Collectors.toList());
        }

        sessionsGrid.setItems(sessionsList);
    }

    private void checkSessionStatus() {
        List<Sessions> sessionsList = sessionRepository.findAll();
        LocalDateTime currentDate = LocalDateTime.now();

        for (Sessions session : sessionsList) {
            LocalDateTime sessionStart = session.getSessionStart();

            if (currentDate.isAfter(sessionStart) && session.getSessionStatus().equals("OPEN")) {
                session.setSessionStatus("FINISHED");
                sessionRepository.save(session);
            }
        }
    }
}
