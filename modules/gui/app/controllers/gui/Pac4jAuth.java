package controllers.gui;

import com.google.inject.Inject;
import controllers.gui.actionannotations.GuiAccessLoggingAction.GuiAccessLogging;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.play.PlayWebContext;
import org.pac4j.play.java.Secure;
import org.pac4j.play.store.PlaySessionStore;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Singleton;
import java.util.List;

@GuiAccessLogging
@Singleton
public class Pac4jAuth extends Controller {

    private Config config;

    private PlaySessionStore playSessionStore;

    @Inject
    Pac4jAuth(Config config, PlaySessionStore playSessionStore) {
        this.config = config;
        this.playSessionStore = playSessionStore;
    }

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
        return ok(views.html.gui.pac4jAuth.render(getProfiles(), token, sessionId));
    }

    private Result protectedIndexView() {
        // profiles
        return ok(views.html.gui.protectedIndex.render(getProfiles()));
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

    @Secure(clients = "OidcClient")
    public Result oidcIndex() {
        return protectedIndexView();
    }

    @Secure
    public Result protectedIndex() {
        return protectedIndexView();
    }
}
