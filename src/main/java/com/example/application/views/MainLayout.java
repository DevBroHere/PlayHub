package com.example.application.views;

import com.example.application.data.entity.Users;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
    private final Tabs menu;
    public MainLayout() {
        H1 title = new H1("PlayHub");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("left", "var(--lumo-space-l)").set("margin", "0")
                .set("position", "absolute");

       H1 welcomeTitle = new H1("Welcome " + VaadinSession.getCurrent().getAttribute(Users.class).getUserName());
        welcomeTitle.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("right", "var(--lumo-space-l)").set("margin", "3")
                .set("position", "absolute");

        menu = createMenu();

        addToNavbar(title, welcomeTitle, menu);

    }

    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.getStyle().set("margin", "auto");
        tabs.setId("tabs");
        tabs.add(createMenuItems());
        return tabs;
    }

    private Component[] createMenuItems() {
        return new Tab[] { createTab("Dashboard", DashboardView.class),
                createTab("Sessions", SessionsView.class),
                createTab("Friends", FriendsView.class),
                createTab("Logout", LogoutView.class)};
    }

    private static Tab createTab(String text,
                                 Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        tab.add(new RouterLink(text, navigationTarget));
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();

        // Select the tab corresponding to currently shown view
        getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);

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
