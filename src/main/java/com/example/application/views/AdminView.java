package com.example.application.views;

import com.example.application.data.entity.*;
import com.example.application.data.repository.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("admin")
public class AdminView extends VerticalLayout {

    private SessionRepository sessionRepository;
    private UserRepository userRepository;
    private FriendshipRepository friendshipRepository;
    private LogRepository logRepository;
    private SessionUsersRepository sessionUsersRepository;
    @Autowired
    public AdminView(SessionRepository sessionRepository,
                     UserRepository userRepository,
                     SessionUsersRepository sessionUsersRepository,
                     FriendshipRepository friendshipRepository,
                     LogRepository logRepository) {
        try {
            VaadinSession.getCurrent()
                    .getAttribute(Users.class).getUserName();
        } catch (Exception NullPointerException) {
            UI.getCurrent().getPage().setLocation("goback");
        }
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.sessionUsersRepository = sessionUsersRepository;
        this.friendshipRepository = friendshipRepository;
        this.logRepository = logRepository;

        setSizeFull();
        // Apply CSS styles to the body
        getStyle().set("margin", "0").set("padding", "0");

        // Navigation Bar
        Nav navBar = new Nav();
        navBar.getStyle()
                .set("background-color", "#f2f2f2")
                .set("padding-top", "20px")
                .set("padding-bottom", "20px")
                .set("text-align", "center")
                .set("width", "100%");

        // Logo in the Navbar
        H2 logo = new H2("Admin Tools");
        logo.getStyle().set("margin", "0");

        // Image next to the logo
        Image image = new Image("images/logo.png", "Logo");
        image.setWidth("60px");
        image.setHeight("40px");
        image.getStyle().set("margin-left", "10px");

        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.add(image, logo);

        // Return to the main user panel (dashboard)
        Button returnButton = new Button("Return");
        returnButton.setClassName("login-button");
        returnButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_CONTRAST);
        returnButton.getStyle()
                .set("margin-right", "20px");
        returnButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(DashboardView.class)));

        // Horizontal layout for the navbar contents
        FlexLayout navContents = new FlexLayout(logoLayout, returnButton);
        navContents.setSizeFull();
        navContents.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        navContents.setAlignItems(FlexComponent.Alignment.CENTER);

        navBar.add(navContents);

        Grid<Sessions> sessionsGrid = new Grid<>();
        sessionsGrid.addColumn(Sessions::getSessionName).setHeader("Session Name");
        sessionsGrid.addColumn(session -> session.getGame().getGameTitle()).setHeader("Game Title");
        sessionsGrid.addColumn(session -> session.getUser().getUserName()).setHeader("Session Owner");
        sessionsGrid.addColumn(Sessions::getSessionDate).setHeader("Creation Date");
        sessionsGrid.addColumn(Sessions::getSessionStart).setHeader("Due Date");

        List<Sessions> sessionsList = getSessionsList();
        GridListDataView<Sessions> dataSessionsView = sessionsGrid.setItems(sessionsList);

        TextField searchFieldSessions = new TextField();
        searchFieldSessions.setWidth("50%");
        searchFieldSessions.setPlaceholder("Search");
        searchFieldSessions.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchFieldSessions.setValueChangeMode(ValueChangeMode.EAGER);
        searchFieldSessions.addValueChangeListener(textFieldStringComponentValueChangeEvent -> dataSessionsView.refreshAll());

        dataSessionsView.addFilter(session -> {
            String searchTerm = searchFieldSessions.getValue().trim();

            if (searchTerm.isEmpty()){
                return true;
            }

            boolean matchesSessionName = matchesTerm(session.getSessionName(), searchTerm);
            boolean matchesUserName = matchesTerm(session.getUser().getUserName(), searchTerm);

            return matchesSessionName || matchesUserName;
        });

        SessionsContextMenu contextMenu = new SessionsContextMenu(sessionsGrid,
                sessionUsersRepository, sessionRepository);

        Grid<Users> usersGrid = new Grid<>();
        usersGrid.addColumn(Users::getUserName).setHeader("Username");
        usersGrid.addColumn(Users::getEmail).setHeader("Email");
        usersGrid.addColumn(Users::getUserRole).setHeader("Role");

        List<Users> usersList = getUsersList();
        GridListDataView<Users> dataUsersView = usersGrid.setItems(usersList);

        TextField searchFieldUsers = new TextField();
        searchFieldUsers.setWidth("50%");
        searchFieldUsers.setPlaceholder("Search");
        searchFieldUsers.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchFieldUsers.setValueChangeMode(ValueChangeMode.EAGER);
        searchFieldUsers.addValueChangeListener(textFieldStringComponentValueChangeEvent -> dataUsersView.refreshAll());

        dataUsersView.addFilter(user -> {
            String searchTerm = searchFieldUsers.getValue().trim();

            if (searchTerm.isEmpty()){
                return true;
            }

            boolean matchesUserName = matchesTerm(user.getUserName(), searchTerm);
            boolean matchesEmail = matchesTerm(user.getEmail(), searchTerm);
            boolean matchesRole = matchesTerm(user.getUserRole().name(), searchTerm);

            return matchesUserName || matchesEmail || matchesRole;
        });

        UsersContextMenu usersContextMenu = new UsersContextMenu(usersGrid,
                userRepository, friendshipRepository, logRepository);

        add(navBar, searchFieldSessions, sessionsGrid, searchFieldUsers, usersGrid);
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }


    private static class SessionsContextMenu extends GridContextMenu<Sessions> {
        private SessionUsersRepository sessionUsersRepository;
        private SessionRepository sessionRepository;
        public SessionsContextMenu(Grid<Sessions> target,
                                   SessionUsersRepository sessionUsersRepository,
                                   SessionRepository sessionRepository) {
            super(target);
            this.sessionUsersRepository = sessionUsersRepository;
            this.sessionRepository = sessionRepository;

            addItem("Details", e -> e.getItem().ifPresent(session -> {
                sessionsDetailsPopUp(session);
                // System.out.printf("Edit: %s%n", person.getFullName());
            }));

            add(new Hr());

            addItem("Delete", e -> e.getItem().ifPresent(session -> {
                Notification.show("Delete: " + session.getSessionID());
                sessionRepository.delete(session);
                Page page = getUI().get().getPage();
                page.reload();
                // System.out.printf("Delete: %s%n", person.getFullName());
            }));
        }

        private void sessionsDetailsPopUp(Sessions session) {
            Dialog dialog = new Dialog();

            dialog.setDraggable(true);
            dialog.setResizable(true);
            dialog.setSizeFull();

            dialog.setHeaderTitle("Session Details");

            // Create the Grid
            Grid<SessionUsers> grid = new Grid<>();

            grid.addColumn(sessionUsers -> sessionUsers.getSession().getSessionName()).setHeader("Session Name");
            grid.addColumn(sessionUsers -> sessionUsers.getUser().getUserName()).setHeader("Username");
            grid.addColumn(sessionUsers -> sessionUsers.getUser().getEmail()).setHeader("Email");

            List<SessionUsers> sessionsUsersList = getSessionUsers(session);
            grid.setItems(sessionsUsersList);

            // Add Close button to the Dialog
            Button closeButton = new Button("Close");
            closeButton.addClickListener(e -> dialog.close());

            dialog.add(grid, closeButton);

            dialog.open();
        }

        private List<SessionUsers> getSessionUsers(Sessions session){
            return sessionUsersRepository.findAllBySession(session);
        }
    }

    private static class UsersContextMenu extends GridContextMenu<Users> {
        UserRepository userRepository;
        FriendshipRepository friendshipRepository;
        LogRepository logRepository;
        public UsersContextMenu(Grid<Users> target,
                                UserRepository userRepository,
                                FriendshipRepository friendshipRepository,
                                LogRepository logRepository) {
            super(target);

            this.userRepository = userRepository;
            this.friendshipRepository = friendshipRepository;

            addItem("Change to ADMIN", e -> e.getItem().ifPresent(user -> {
                user.setUserRole(Role.ADMIN);
                userRepository.save(user);
                Page page = getUI().get().getPage();
                page.reload();
                // System.out.printf("Edit: %s%n", person.getFullName());
            }));

            addItem("Change to USER", e -> e.getItem().ifPresent(user -> {
                user.setUserRole(Role.USER);
                userRepository.save(user);
                Page page = getUI().get().getPage();
                page.reload();
                // System.out.printf("Edit: %s%n", person.getFullName());
            }));

            add(new Hr());

            addItem("Delete", e -> e.getItem().ifPresent(user -> {
                logRepository.deleteAll(logRepository.findAllByUser(user));
                friendshipRepository.deleteAll(friendshipRepository.getAllFriendshipsByUser(user));
                userRepository.delete(user);
                Page page = getUI().get().getPage();
                page.reload();
            }));
        }
    }

    private List<Sessions> getSessionsList() {
        return sessionRepository.findAll();
    }

    private List<Users> getUsersList() {
        return userRepository.findAllNotUser(VaadinSession.getCurrent().getAttribute(Users.class).getUserID());
    }


}
