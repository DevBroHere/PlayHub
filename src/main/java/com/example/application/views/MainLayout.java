package com.example.application.views;

import com.example.application.data.entity.Users;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;

import java.util.Optional;

@Route("mainlayout")
public class MainLayout extends AppLayout {
    private Tabs menu;
    public MainLayout() {
        try {
            DrawerToggle toggle = new DrawerToggle();
            // Configure the custom CSS styles
//            Html contentHtmlNavbar = new Html(
//                    "<style>" +
//                            "vaadin-app-layout::part(navbar) {" +
//                            "background-color: #67597a;" +
//                            "</style>"
//            );
//            Html contentHtmlDrawer = new Html(
//                    "<style>" +
//                            "vaadin-app-layout::part(drawer) {" +
//                            "background-color: #67597a;" +
//                            "</style>"
//            );

            H1 title = new H1("PlayHub");
            title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                    .set("margin", "0");

            menu = createMenu();
            addToDrawer(menu);
            addToNavbar(toggle, title);

            setPrimarySection(Section.DRAWER);

        } catch (Exception NullPointerException) {
            UI.getCurrent().getPage().setLocation("goback");
        }

    }

    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.add(createMenuItems());
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        return tabs;
    }

    private Component[] createMenuItems() {
        if (VaadinSession.getCurrent()
                .getAttribute(Users.class).getUserRole().name().equals("ADMIN")) {
            return new Tab[] {
                    createTab(VaadinIcon.DASHBOARD, "Dashboard", DashboardView.class),
                    createTab(VaadinIcon.LIST, "Sessions", SessionsView.class),
                    createTab(VaadinIcon.USER_HEART, "Friends", FriendsView.class),
                    createTab(VaadinIcon.RECORDS, "Admin", AdminView.class),
                    createTab(VaadinIcon.POWER_OFF, "Logout", LogoutView.class)};
        }
        else {
            return new Tab[] {
                    createTab(VaadinIcon.DASHBOARD, "Dashboard", DashboardView.class),
                    createTab(VaadinIcon.LIST, "Sessions", SessionsView.class),
                    createTab(VaadinIcon.USER_HEART, "Friends", FriendsView.class),
                    createTab(VaadinIcon.POWER_OFF, "Logout", LogoutView.class)};
        }

    }

    private static Tab createTab(VaadinIcon viewIcon, String text,
                                 Class<? extends Component> navigationTarget) {
        Icon icon = viewIcon.create();
        icon.getStyle().set("box-sizing", "border-box")
                .set("margin-inline-end", "var(--lumo-space-m)")
                .set("margin-inline-start", "var(--lumo-space-xs)")
                .set("padding", "var(--lumo-space-xs)");

        final Tab tab = new Tab();
        RouterLink link = new RouterLink(navigationTarget);
        link.add(icon, new Span(text));
        tab.add(link);
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    @Override
    protected void afterNavigation() {
        try {
            super.afterNavigation();

            // Select the tab corresponding to currently shown view
            getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);
        } catch (Exception NullPointerException) {
            UI.getCurrent().getPage().setLocation("goback");
        }


        // Set the view title in the header
//        viewTitle.setText(getCurrentPageTitle());
    }

    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren()
                .filter(tab -> ComponentUtil.getData(tab, Class.class)
                        .equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }

    private String getCurrentPageTitle() {
        return getContent().getClass().getAnnotation(PageTitle.class).value();
    }
}
