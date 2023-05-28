package com.example.application.views;

import com.example.application.data.entity.Actions;
import com.example.application.data.entity.Logs;
import com.example.application.data.entity.Users;
import com.example.application.data.repository.LogRepository;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.sql.Timestamp;

@Route("logout")
@PageTitle("Logout | PlayHub")
public class LogoutView extends Composite<VerticalLayout> {

    private LogRepository logRepository;
    public LogoutView(LogRepository logRepository) {
        this.logRepository = logRepository;

        // Set the log: Registration successful
        Logs registrationLog = new Logs();
        registrationLog.setUser(VaadinSession.getCurrent().getAttribute(Users.class));
        registrationLog.setAction(Actions.LOGOUT.name());
        registrationLog.setActionStatus("Success");
        registrationLog.setActionDate(new Timestamp(System.currentTimeMillis()));
        logRepository.save(registrationLog);

        UI.getCurrent().getPage().setLocation("login");
        VaadinSession.getCurrent().getSession().invalidate();
        VaadinSession.getCurrent().close();
    }
}
