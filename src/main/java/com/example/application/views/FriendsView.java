package com.example.application.views;

import com.example.application.data.entity.Friendships;
import com.example.application.data.entity.Users;
import com.example.application.data.repository.FriendshipRepository;
import com.example.application.data.repository.UserRepository;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;

@Route(value = "friends", layout = MainLayout.class)
@PageTitle("Friends | PlayHub")
public class FriendsView extends VerticalLayout {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    public FriendsView(UserRepository userRepository, FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;

        Button plusButton = new Button(new Icon(VaadinIcon.PLUS));
        plusButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        plusButton.getElement().setAttribute("aria-label", "Add item");
        plusButton.setText("Add New Friend");
        plusButton.addClickListener(buttonClickEvent -> openAddFriendPopUp());

        HorizontalLayout horizontalLayout = new HorizontalLayout(plusButton);
        add(horizontalLayout);

        List<Users> friendsList = getFriendsList();

        for (Users friend : friendsList) {
            Div tile = createFriendTile(friend);
            add(tile);
        }
    }

    private Div createFriendTile(Users friend) {
        Div tile = new Div();
        tile.addClassName("friend-tile");

        Avatar avatar = new Avatar();
        avatar.setName(friend.getUserName());

        H3 username = new H3(friend.getUserName());
        Paragraph nickname = new Paragraph("Nickname: " + friend.getUserNick());
        Paragraph email = new Paragraph("Email: " + friend.getEmail());

        tile.add(avatar, username, nickname, email);

        return tile;
    }

    private void openAddFriendPopUp() {
        Dialog dialog = new Dialog();

        dialog.setDraggable(true);
        dialog.setResizable(true);

        dialog.setHeaderTitle("New Friend");

        // Create the Grid
        Grid<Users> grid = createGrid();
        dialog.add(grid);

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

        // Populate the grid with data
        List<Users> usersList = getUsersList();
        grid.setItems(usersList);

        dialog.open();
    }

    private void sendInvitation(Users selectedUser) {
        // Logic to send invitation to the selected person
        friendshipRepository.save(new Friendships(VaadinSession.getCurrent()
                .getAttribute(Users.class), selectedUser, "WAITING"));
        friendshipRepository.save(new Friendships(selectedUser, VaadinSession.getCurrent()
                .getAttribute(Users.class), "PENDING"));
        Notification.show("Invitation sent to: " + selectedUser.getUserName());
    }

    private Grid<Users> createGrid() {
        Grid<Users> grid = new Grid<>();

        // Set up the Grid columns
        grid.addColumn(Users::getUserName).setHeader("Name");
        grid.addColumn(Users::getEmail).setHeader("Email");

        return grid;
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
}
