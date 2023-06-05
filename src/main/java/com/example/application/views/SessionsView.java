package com.example.application.views;

import com.example.application.data.entity.*;
import com.example.application.data.repository.GameRepository;
import com.example.application.data.repository.SessionRepository;
import com.example.application.data.repository.SessionUsersRepository;
import com.example.application.data.repository.UserRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

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
        Select<SessionType> sessionTypeSelect = new Select<>();
        sessionTypeSelect.setLabel("Rodzaj sesji");
        sessionTypeSelect.setItems(SessionType.values());

        Select<Games> gameTypeSelect = new Select<>();
        gameTypeSelect.setLabel("Rodzaj gry");
        List<Games> gamesList = gameRepository.findAll();
        gameTypeSelect.setItems(gamesList);
        gameTypeSelect.setItemLabelGenerator(Games::getGameTitle);

        Button saveButton = new Button("Zapisz");
        saveButton.addClickListener(e -> {
            String sessionName = sessionNameField.getValue();
            SessionType sessionType = sessionTypeSelect.getValue();
            Games game = gameTypeSelect.getValue();

            if (sessionName.isEmpty() || sessionType == null || game == null) {
                Notification.show("Wypełnij wszystkie pola!", 3000, Notification.Position.MIDDLE);
                return;
            }

            Users user = getCurrentUser();
            Sessions session = new Sessions(game, user, sessionName, sessionType.name());
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

        dialog.add(sessionNameField, sessionTypeSelect, gameTypeSelect, new HorizontalLayout(saveButton, cancelButton));
        dialog.open();
    }

    private void showSessionsGrid(List<Sessions> sessionsList) {
        sessionsGrid.setItems(sessionsList);
        sessionsGrid.removeAllColumns();

        sessionsGrid.addColumn(Sessions::getSessionName).setHeader("Nazwa sesji");
        sessionsGrid.addColumn(session -> session.getGame().getGameTitle()).setHeader("Rodzaj gry");
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

            joinButton.addClickListener(e -> joinSession(session, currentUser, joinButton, buttonLayout));
            buttonLayout.add(joinButton);
            return buttonLayout;
        }).setHeader(createButtonHeader());

        sessionsGrid.setVisible(true);
    }

    private Users getCurrentUser() {
        return VaadinSession.getCurrent().getAttribute(Users.class);
    }

    private void joinSession(Sessions session, Users user, Button joinButton, HorizontalLayout buttonLayout) {
        SessionUsers sessionUser = new SessionUsers();
        sessionUser.setSession(session);
        sessionUser.setUser(user);
        sessionUsersRepository.save(sessionUser);

        joinButton.setEnabled(false);
        joinButton.setIcon(new Icon(VaadinIcon.CHECK));
        buttonLayout.addClassName("joined-session");

        Notification.show("Dołączono do sesji!", 3000, Notification.Position.MIDDLE);
    }

    private Component createButtonHeader() {
        Span header = new Span();
        header.addClassName("button-header");
        return header;
    }
}
