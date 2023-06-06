package com.example.application.views;

import com.example.application.data.entity.*;
import com.example.application.data.repository.FriendshipRepository;
import com.example.application.data.repository.LogRepository;
import com.example.application.data.repository.UserRepository;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Route(value = "friends", layout = MainLayout.class)
@PageTitle("Friends | PlayHub")
public class FriendsView extends VerticalLayout {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final LogRepository logRepository;

    public FriendsView(UserRepository userRepository, FriendshipRepository friendshipRepository, LogRepository logRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.logRepository = logRepository;

        // Add friend button
        Button plusButton = new Button(new Icon(VaadinIcon.PLUS));
        plusButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        plusButton.getElement().setAttribute("aria-label", "Add item");
        plusButton.setText("Add New Friend");
        plusButton.addClickListener(buttonClickEvent -> openAddFriendPopUp());

        HorizontalLayout horizontalLayout = new HorizontalLayout(plusButton);
        add(horizontalLayout);

        setSpacing(true);
        setPadding(true);

        // Get list of friends to be able to view them on the site
        List<Friendships> friendsList = getFriendshipsList();

        // Create a responsive grid layout
        Grid<Friendships> grid = new Grid<>(Friendships.class);
        grid.setItems(friendsList);
        grid.setColumns();
        grid.setSizeFull();

        // Configure the grid to display tiles
        grid.setMultiSort(false);
        grid.setColumnReorderingAllowed(true);
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        // Create an Html component to include CSS styling
        Html contentHtml = new Html(
                "<style>" +
                        ".friends-tiles {" +
                        "    display: flex;" +
                        "    flex-wrap: wrap;" +
                        "}" +
                        ".friend-tile {" +
                        "    flex-basis: 33.33%;" +
                        "    margin: 0.5rem;" +
                        "    padding: 1rem;" +
                        "    border: 1px solid #ddd;" +
                        "    border-radius: 5px;" +
                        "    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);" +
                        "    cursor: pointer;" +
                        "    transition: background-color 0.3s ease-in-out;" +
                        "    background-color: #96adc8;" +
                        "}" +
                        ".friend-tile:hover {" +
                        "    background-color: #67597a;" +
                        "}" +
                        ".friend-tile.clicked {" +
                        "    background-color: #800e13;" +
                        "}" +
                        ".friend-tile.friend-name {" +
                        "    color: #566246;" +
                        "}" +
                        ".friend-tile.game-session {" +
                        "    color: #aaaaaa;" +
                        "}" +
                        "@media (max-width: 768px) {" +
                        "    .friend-tile {" +
                        "        flex-basis: 100%;" +
                        "    }" +
                        "}" +
                        "</style>"
        );

        // Create a layout for the friend tiles
        Div tilesLayout = new Div();
        tilesLayout.setWidth("100%");
        tilesLayout.addClassName("friends-tiles");

        // Create friend tiles
        for (Friendships friendship : friendsList) {
            Div tile = new Div();
            tile.addClassName("friend-tile");
            tile.setWidth("100%");

            HorizontalLayout tileContent = new HorizontalLayout();
            Avatar avatar = new Avatar();
            avatar.setName(friendship.getFriend().getUserName());
            avatar.getStyle().set("width", "100px")
                    .set("height", "100px")
                    .set("border-radius", "50%");

            // Add user-related content to the 2nd column in the tile
            VerticalLayout userContent = new VerticalLayout();
            userContent.add(new Label(friendship.getFriend().getUserName()));
            userContent.add(new Label(friendship.getFriend().getEmail()));


            userContent.add(new Label(friendship.getFriendshipStatus()));
            tileContent.add(avatar, userContent);

            tile.addClickListener(divClickEvent -> {
                // Remove the 'clicked' class from all tiles
                tilesLayout.getChildren().forEach(child -> child.removeClassName("clicked"));

                // Add the 'clicked' class to the clicked tile
                tile.addClassName("clicked");

                // Handle the tile click event
                // You can perform actions like opening a details view for the friend
                if (Objects.equals(friendship.getFriendshipStatus(), FriendshipStatuses.PENDING.name())){
                    openAcceptOrDeclinePopUp(friendship);
                } else if (Objects.equals(friendship.getFriendshipStatus(), FriendshipStatuses.WAITING.name())) {
                    Notification.show("Please wait for the response from " + friendship.getFriend().getUserName());
                } else if (Objects.equals(friendship.getFriendshipStatus(), FriendshipStatuses.ACCEPTED.name())) {
                    Notification.show("New Dialog window under construction.");
                } else if (Objects.equals(friendship.getFriendshipStatus(), FriendshipStatuses.DECLINED.name())) {
                    declineInfoPopUp(friendship);
                }
            });

            tile.add(tileContent);
            tilesLayout.add(tile);
        }
        // Create a Div component and add the grid and friend tiles layout to it
        Div contentDiv = new Div();
        contentDiv.setWidth("100%");
        contentDiv.setHeight("100%");
        contentDiv.add(grid, tilesLayout);

        add(contentHtml, contentDiv);
    }

    private void openAddFriendPopUp() {
        Dialog dialog = new Dialog();

        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.setSizeFull();

        dialog.setHeaderTitle("Add new Friend");

        // Create the Grid
        Grid<Users> grid = new Grid<>();

        // Set up the Grid columns
        grid.addColumn(Users::getUserNick).setHeader("Nick");
        grid.addColumn(Users::getUserName).setHeader("Username");
        grid.addColumn(Users::getEmail).setHeader("Email");

        // Populate the grid with data
        List<Users> usersList = getUsersList();
        GridListDataView<Users> dataView = grid.setItems(usersList);

        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(textFieldStringComponentValueChangeEvent -> dataView.refreshAll());

        dataView.addFilter(user -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty()){
                return true;
            }

            boolean matchesUserName = matchesTerm(user.getUserName(), searchTerm);
            boolean matchesEmail = matchesTerm(user.getEmail(), searchTerm);

            return matchesUserName || matchesEmail;
        });

        VerticalLayout layout = new VerticalLayout(searchField, grid);
        layout.setPadding(false);

        dialog.add(layout);

        // Add Send Invitation button to the Dialog
        Button sendInvitationButton = new Button("Invite");
        sendInvitationButton.setEnabled(false);
        sendInvitationButton.addClickListener(buttonClickEvent -> {
            Users selectedUser = grid.asSingleSelect().getValue();
            if (selectedUser != null) {
                sendInvitation(selectedUser);
                dialog.close();
            }
        });
        dialog.add(sendInvitationButton);

        // Add Close button to the Dialog
        Button closeButton = new Button("Close");
        closeButton.addClickListener(e -> dialog.close());
        dialog.add(closeButton);

        // Set up selection listener
        grid.asSingleSelect().addValueChangeListener(gridUsersComponentValueChangeEvent -> {
            if (gridUsersComponentValueChangeEvent.getValue() != null) {
                sendInvitationButton.setEnabled(true);
            } else {
                sendInvitationButton.setEnabled(false);
            }
        });

        dialog.open();
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private void openAcceptOrDeclinePopUp(Friendships friendship) {
        Users currentUser = VaadinSession.getCurrent().getAttribute(Users.class);
        Dialog dialog = new Dialog();

        dialog.setDraggable(true);
        dialog.setResizable(true);

        dialog.setHeaderTitle("Do you accept invitation?");
        dialog.add(String.format("Incoming invitation to friends from \"%s", friendship.getFriend().getUserName()));

        Button declineButton = new Button("Decline");
        declineButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        declineButton.getStyle().set("margin-right", "auto");
        dialog.getFooter().add(declineButton);
        declineButton.addClickListener(buttonClickEvent -> {
            friendshipRepository.delete(
                    friendshipRepository.getFriendshipsByUserAndFriend(currentUser, friendship.getFriend()));
            Friendships reverseFriendship = friendshipRepository.getFriendshipsByUserAndFriend(
                    friendship.getFriend(), currentUser);
            reverseFriendship.setFriendshipStatus(FriendshipStatuses.DECLINED.name());
            friendshipRepository.save(reverseFriendship);

            // Log the invitation action
            Logs authenticationLog = new Logs();
            authenticationLog.setUser(VaadinSession.getCurrent().getAttribute(Users.class));
            authenticationLog.setAction(Actions.INVITATION.name());
            authenticationLog.setActionStatus("Decline");
            authenticationLog.setActionDate(new Timestamp(System.currentTimeMillis()));
            logRepository.save(authenticationLog);

            dialog.close();

            Page page = getUI().get().getPage();
            page.reload();
        });

        Button acceptButton = new Button("Accept");
        acceptButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.getFooter().add(acceptButton);
        acceptButton.addClickListener(buttonClickEvent -> {
            friendship.setFriendshipStatus(FriendshipStatuses.ACCEPTED.name());
            friendshipRepository.save(friendship);
            Friendships reverseFriendship = friendshipRepository.getFriendshipsByUserAndFriend(
                    friendship.getFriend(), currentUser);
            reverseFriendship.setFriendshipStatus(FriendshipStatuses.ACCEPTED.name());
            friendshipRepository.save(reverseFriendship);

            // Log the invitation action
            Logs authenticationLog = new Logs();
            authenticationLog.setUser(VaadinSession.getCurrent().getAttribute(Users.class));
            authenticationLog.setAction(Actions.INVITATION.name());
            authenticationLog.setActionStatus("Accept");
            authenticationLog.setActionDate(new Timestamp(System.currentTimeMillis()));
            logRepository.save(authenticationLog);

            dialog.close();

            Page page = getUI().get().getPage();
            page.reload();
        });

        dialog.open();
    }

    private void declineInfoPopUp(Friendships friendship) {
        Dialog dialog = new Dialog();
        dialog.getElement().setAttribute("aria-label", "Invitation Declined");

        H2 headline = new H2("Friendship proposal declined");
        headline.getStyle().set("margin", "var(--lumo-space-m) 0")
                .set("font-size", "1.5em").set("font-weight", "bold");

        Paragraph paragraph = new Paragraph(
                "The user " + friendship.getFriend().getUserName() + " declined your friendship proposal.");

        Button closeButton = new Button("Close");
        closeButton.addClickListener(e -> {
            friendshipRepository.delete(friendship);
            dialog.close();

            Page page = getUI().get().getPage();
            page.reload();
        });

        dialog.add(paragraph);
        dialog.getFooter().add(closeButton);

        dialog.open();
    }

    private void sendInvitation(Users selectedUser) {
        // Logic to send invitation to the selected person
        friendshipRepository.save(new Friendships(VaadinSession.getCurrent()
                .getAttribute(Users.class), selectedUser, FriendshipStatuses.WAITING.name()));
        friendshipRepository.save(new Friendships(selectedUser, VaadinSession.getCurrent()
                .getAttribute(Users.class), FriendshipStatuses.PENDING.name()));
        Notification.show("Invitation sent to: " + selectedUser.getUserName());

        // Log the invitation action
        Logs authenticationLog = new Logs();
        authenticationLog.setUser(VaadinSession.getCurrent().getAttribute(Users.class));
        authenticationLog.setAction(Actions.INVITATION.name());
        authenticationLog.setActionStatus("Send");
        authenticationLog.setActionDate(new Timestamp(System.currentTimeMillis()));
        logRepository.save(authenticationLog);
    }

    private List<Users> getUsersList() {
        // Should get all users that are not already somehow in the list of friends
        // Also user cannot see himself
        return userRepository.findNonFriendActiveUsers(VaadinSession.getCurrent()
                .getAttribute(Users.class).getUserID());
    }

    private List<Users> getFriendsList() {
        // Get all friends of user
        return userRepository.findFriendActiveUsers(VaadinSession.getCurrent()
                .getAttribute(Users.class).getUserID());
    }

    private List<Friendships> getFriendshipsList() {
        // Get all friendships objects regarding the current logged user
        return friendshipRepository.findAllFriendshipsByUserId(VaadinSession.getCurrent()
                .getAttribute(Users.class).getUserID());
    }
}
