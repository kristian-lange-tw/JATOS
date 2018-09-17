package controllers.gui;

import com.google.inject.Inject;
import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.play.PlayWebContext;
import org.pac4j.play.java.Secure;
import org.pac4j.play.store.PlaySessionStore;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

public class Auth extends Controller {

    @Inject
    private Config config;

    @Inject
    private PlaySessionStore playSessionStore;

    private List<CommonProfile> getProfiles() {
        final PlayWebContext context = new PlayWebContext(ctx(), playSessionStore);
        final ProfileManager<CommonProfile> profileManager = new ProfileManager(context);
        return profileManager.getAll(true);
    }

    @Secure(clients = "AnonymousClient", authorizers = "csrfToken")
    public Result index() throws Exception {
        final PlayWebContext context = new PlayWebContext(ctx(), playSessionStore);
        final String sessionId = context.getSessionIdentifier();
        final String token = (String) context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN);
        // profiles (maybe be empty if not authenticated)
        return ok(views.html.auth.render(getProfiles(), token, sessionId));
    }

    private Result protectedIndexView() {
        // profiles
        return ok(views.html.protectedIndex.render(getProfiles()));
    }

    //@Secure(clients = "FacebookClient")
    public Result facebookIndex() {
        return protectedIndexView();
    }

    @Secure(clients = "FacebookClient", authorizers = "admin")
    public Result facebookAdminIndex() {
        return protectedIndexView();
    }

    @Secure(clients = "FacebookClient", authorizers = "custom")
    public Result facebookCustomIndex() {
        return protectedIndexView();
    }

    @Secure
    public Result protectedIndex() {
        return protectedIndexView();
    }
}
