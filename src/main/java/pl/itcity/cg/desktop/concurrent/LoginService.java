package pl.itcity.cg.desktop.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.itcity.cg.desktop.model.LoginResult;
import pl.itcity.cg.desktop.model.Principal;

/**
 * @author Michal Adamczyk, HYCOM S.A.
 */
@Component
@Scope("prototype")
public class LoginService extends Service<LoginResult>{

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    private Principal principal;


    protected Task<LoginResult> createTask() {
        return new Task<LoginResult>() {

            private final Principal taskPrincipal = principal;
            @Override
            protected LoginResult call() throws Exception {
                //todo [michal.adamczyk] perform login call to service
                LOGGER.info("about to login principal: "+taskPrincipal);
                Thread.sleep(1000);
                return new LoginResult();
            }
        };
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }
}
