package com.example.application.data.service;

import com.example.application.data.entity.Actions;
import com.example.application.data.entity.Logs;
import com.example.application.data.entity.Role;
import com.example.application.data.entity.Users;
import com.example.application.data.repository.LogRepository;
import com.example.application.data.repository.UserRepository;
import com.example.application.views.HomeView;
import com.example.application.views.LogoutView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {

    public record AuthorizedRoute(String route, String name, Class<? extends Component> view) {

    }

    public class AuthException extends Exception {

    }

    private final UserRepository userRepository;
    private final LogRepository logRepository;
    private final MailSender mailSender;

    public AuthService(UserRepository userRepository, LogRepository logRepository, MailSender mailSender) {
        this.userRepository = userRepository;
        this.logRepository = logRepository;
        this.mailSender = mailSender;
    }

    public void authenticate(String username, String password) throws AuthException {
        Users user = userRepository.getByUserName(username);
        if (user != null && user.checkPassword(password) && user.isActive()) {
            // Set the log: Authentication successful
            Logs authenticationLog = new Logs();
            authenticationLog.setUser(user);
            authenticationLog.setAction(Actions.AUTHENTICATION.name());
            authenticationLog.setActionStatus("Success");
            authenticationLog.setActionDate(new Timestamp(System.currentTimeMillis()));
            logRepository.save(authenticationLog);

            VaadinSession.getCurrent().setAttribute(Users.class, user);
            createRoutes(user.getUserRole());
        } else {
            // Set the log: Authentication failure
            Logs authenticationLog = new Logs();
            authenticationLog.setUser(user);
            authenticationLog.setAction(Actions.AUTHENTICATION.name());
            authenticationLog.setActionStatus("Fail");
            authenticationLog.setActionDate(new Timestamp(System.currentTimeMillis()));
            logRepository.save(authenticationLog);

            throw new AuthException();
        }
    }

    private void createRoutes(Role role) {
        getAuthorizedRoutes(role).stream()
                .forEach(route ->
                        RouteConfiguration.forSessionScope().setRoute(
                                route.route, route.view));
    }

    public List<AuthorizedRoute> getAuthorizedRoutes(Role role) {
        var routes = new ArrayList<AuthorizedRoute>();

        if (role.equals(Role.USER)) {
//            routes.add(new AuthorizedRoute("home", "Home", HomeView.class));
            routes.add(new AuthorizedRoute("logout", "Logout", LogoutView.class));
            routes.add(new AuthorizedRoute("home", "Home", HomeView.class));

        } else if (role.equals(Role.ADMIN)) {
//            routes.add(new AuthorizedRoute("home", "Home", HomeView.class));
//            routes.add(new AuthorizedRoute("admin", "Admin", AdminView.class));
            routes.add(new AuthorizedRoute("logout", "Logout", LogoutView.class));
            routes.add(new AuthorizedRoute("home", "Home", HomeView.class));
        }

        return routes;
    }

    public void register(String username, String email, String password) {
        Users user = userRepository.save(new Users(username, email, password, Role.USER.name()));
        String text = "http://localhost:8080/activate?code=" + user.getActivationCode();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@example.com");
        message.setSubject("Confirmation email");
        message.setText(text);
        message.setTo(email);
        mailSender.send(message);
    }

    public void activate(String activationCode) throws AuthException {
        Users user = userRepository.getByActivationCode(activationCode);
        if (user != null) {
            user.setActive(true);
            userRepository.save(user);

            // Set the log: Registration successful
            Logs registrationLog = new Logs();
            registrationLog.setUser(user);
            registrationLog.setAction(Actions.REGISTRATION.name());
            registrationLog.setActionStatus("Success");
            registrationLog.setActionDate(new Timestamp(System.currentTimeMillis()));
            logRepository.save(registrationLog);
        } else {
            throw new AuthException();
        }
    }

}
