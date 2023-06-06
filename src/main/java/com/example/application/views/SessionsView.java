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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Route(value = "sessions", layout = MainLayout.class)
@PageTitle("Sessions | PlayHub")
public class SessionsView extends VerticalLayout {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final SessionUsersRepository sessionUsersRepository;
    private final Grid<Sessions> sessionsGrid;

    public SessionsView(GameRepository gameRepository, UserRepository userRepository,
                        SessionRepository sessionRepository, SessionUsersRepository sessionUsersRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.sessionUsersRepository = sessionUsersRepository;

        Button createGameButton = new Button("Stwórz własną grę", new Icon(VaadinIcon.PLUS));
        createGameButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        createGameButton.getElement().setAttribute("aria-label", "Add item");
        createGameButton.addClickListener(e -> showCreateGameDialog());

        Button publicGamesButton = new Button("Gry publiczne");
        Button privateGamesButton = new Button("Gry prywatne");

        HorizontalLayout sectionLayout = new HorizontalLayout(createGameButton, publicGamesButton, privateGamesButton);
        add(sectionLayout);

        sessionsGrid = new Grid<>();
        sessionsGrid.addColumn(Sessions::getSessionName).setHeader("Nazwa sesji");
        sessionsGrid.addColumn(session -> session.getGame().getGameTitle()).setHeader("Rodzaj gry");
        sessionsGrid.addColumn(session -> session.getGame().getPlatformType()).setHeader("Typ platformy");
        sessionsGrid.addColumn(session -> session.getUser().getUserName()).setHeader("Założyciel");
        sessionsGrid.setVisible(false);
        add(sessionsGrid);

        publicGamesButton.addClickListener(e -> {
            List<Sessions> publicSessions = sessionRepository.findBySessionType("PUBLIC");
            showSessionsGrid(publicSessions);
        });

        privateGamesButton.addClickListener(e -> {
            List<Sessions> privateSessions = sessionRepository.findBySessionType("PRIVATE");
            showSessionsGrid(privateSessions);
        });
    }

    private void showCreateGameDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);

        TextField sessionNameField = new TextField("Nazwa sesji");

        Select<String> gameTypeSelect = new Select<>();
        gameTypeSelect.setLabel("Rodzaj gry");
        List<String> gameTypes = gameRepository.findDistinctGameTitle();
        gameTypeSelect.setItems(gameTypes);

        Select<String> platformSelect = new Select<>();
        platformSelect.setLabel("Typ platformy");
        platformSelect.setEnabled(false); // początkowo pole jest nieaktywne

        gameTypeSelect.addValueChangeListener(event -> {
            String selectedGame = event.getValue();
            List<String> platformList = gameRepository.findDistinctPlatformTypesByGameTitle(selectedGame);
            platformSelect.setItems(platformList);
            platformSelect.setEnabled(true); // aktywuj pole po wybraniu gry
        });

        Checkbox privateSessionCheckbox = new Checkbox("Prywatna sesja");
        PasswordField passwordField = new PasswordField("Hasło do sesji");
        passwordField.setVisible(false);

        privateSessionCheckbox.addValueChangeListener(event -> {
            passwordField.setVisible(event.getValue());
        });

        DateTimePicker dateTimePicker = new DateTimePicker();
        dateTimePicker.setLabel("Data i godzina rozpoczęcia sesji");
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

        Button saveButton = new Button("Zapisz");
        saveButton.addClickListener(e -> {
            String sessionName = sessionNameField.getValue();
            String gameType = gameTypeSelect.getValue();
            String platformType = platformSelect.getValue();
            boolean isPrivateSession = privateSessionCheckbox.getValue();
            String sessionPassword = isPrivateSession ? passwordField.getValue() : null;
            LocalDateTime startDate = dateTimePicker.getValue();

            if (sessionName.isEmpty() || gameType == null || platformType == null || startDate == null) {
                Notification.show("Wypełnij wszystkie pola!", 3000, Notification.Position.MIDDLE);
                return;
            }

            if (isPrivateSession && sessionPassword.isEmpty()) {
                Notification.show("Podaj hasło do sesji prywatnej!", 3000, Notification.Position.MIDDLE);
                return;
            }

            Games game = gameRepository.findFirstByGameTitleAndPlatformType(gameType, platformType);

            if (game == null) {
                Notification.show("Nie znaleziono gry o wybranych parametrach!", 3000, Notification.Position.MIDDLE);
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

            Notification.show("Sesja została utworzona!", 3000, Notification.Position.MIDDLE);
            dialog.close();
        });

        Button cancelButton = new Button("Anuluj");
        cancelButton.addClickListener(e -> dialog.close());

        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, cancelButton);

        VerticalLayout dialogLayout = new VerticalLayout(formLayout, buttonsLayout);
        dialog.add(dialogLayout);

        dialog.open();
    }

    private void showSessionsGrid(List<Sessions> sessionsList) {
        sessionsGrid.setItems(sessionsList);
        sessionsGrid.removeAllColumns();

        sessionsGrid.addColumn(Sessions::getSessionName).setHeader("Nazwa sesji");
        sessionsGrid.addColumn(session -> session.getGame().getGameTitle()).setHeader("Rodzaj gry");
        sessionsGrid.addColumn(session -> session.getGame().getPlatformType()).setHeader("Typ platformy");
        sessionsGrid.addColumn(session -> session.getUser().getUserName()).setHeader("Założyciel");

        Users currentUser = getCurrentUser();

        sessionsGrid.addComponentColumn(session -> {
            HorizontalLayout buttonLayout = new HorizontalLayout();
            Button joinButton = new Button("Dołącz", new Icon(VaadinIcon.PLUS));
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

        PasswordField passwordField = new PasswordField("Hasło do sesji");

        Button joinPrivateButton = new Button("Dołącz");
        joinPrivateButton.addClickListener(e -> {
            String sessionPassword = passwordField.getValue();
            if (sessionPassword.equals(session.getSessionPassword())) {
                joinSession(session, user, joinButton, buttonLayout, dialog);
            } else {
                Notification.show("Nieprawidłowe hasło do sesji!", 3000, Notification.Position.MIDDLE);
            }
        });

        Button cancelButton = new Button("Anuluj");
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

        Notification.show("Dołączono do sesji!", 3000, Notification.Position.MIDDLE);

        if (dialog != null) {
            dialog.close();
        }
    }

    private Component createButtonHeader() {
        Span header = new Span();
        header.addClassName("button-header");
        return header;
    }
}
