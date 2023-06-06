package com.example.application.views;

import com.example.application.data.entity.Sessions;
import com.example.application.data.entity.Users;
import com.example.application.data.repository.SessionRepository;
import com.example.application.data.repository.UserRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("admin")
public class AdminView extends VerticalLayout {

    private SessionRepository sessionRepository;
    private UserRepository userRepository;
    @Autowired
    public AdminView(SessionRepository sessionRepository, UserRepository userRepository) {
        try {
            VaadinSession.getCurrent()
                    .getAttribute(Users.class).getUserName();
        } catch (Exception NullPointerException) {
            UI.getCurrent().getPage().setLocation("goback");
        }
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;

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
        H2 logo = new H2("PlayHub | Admin Tools");
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

        Grid<Sessions> sessionsGrid = new Grid<>(Sessions.class);
        sessionsGrid.addColumn(Sessions::getSessionName).setHeader("Session Name");
        sessionsGrid.addColumn(session -> session.getGame().getGameTitle()).setHeader("Game Title");
        sessionsGrid.addColumn(session -> session.getUser().getUserName()).setHeader("Session Owner");

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

        SessionsContextMenu contextMenu = new SessionsContextMenu(sessionsGrid);

        Grid<Users> usersGrid = new Grid<>(Users.class);
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

        UsersContextMenu usersContextMenu = new UsersContextMenu(usersGrid);

        add(navBar, searchFieldSessions, sessionsGrid, searchFieldUsers, usersGrid);
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private static class SessionsContextMenu extends GridContextMenu<Sessions> {
        public SessionsContextMenu(Grid<Sessions> target) {
            super(target);

            addItem("Edit", e -> e.getItem().ifPresent(session -> {
                Notification.show("Edit: " + session.getSessionID());
                // System.out.printf("Edit: %s%n", person.getFullName());
            }));
            addItem("Delete", e -> e.getItem().ifPresent(session -> {
                Notification.show("Delete: " + session.getSessionID());
                // System.out.printf("Delete: %s%n", person.getFullName());
            }));

            add(new Hr());
        }
    }

    private static class UsersContextMenu extends GridContextMenu<Users> {
        public UsersContextMenu(Grid<Users> target) {
            super(target);

            addItem("Edit", e -> e.getItem().ifPresent(user -> {
                Notification.show("Edit: " + user.getUserName());
                // System.out.printf("Edit: %s%n", person.getFullName());
            }));
            addItem("Delete", e -> e.getItem().ifPresent(user -> {
                Notification.show("Delete: " + user.getUserName());
                // System.out.printf("Delete: %s%n", person.getFullName());
            }));

            add(new Hr());
        }
    }

    private List<Sessions> getSessionsList() {
        return sessionRepository.findAll();
    }

    private List<Users> getUsersList() {
        return userRepository.findAllNotAdmins();
    }
}
