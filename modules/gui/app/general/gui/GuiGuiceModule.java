package general.gui;

import com.google.inject.AbstractModule;
import controllers.gui.CustomAuthorizer;
import controllers.gui.DemoHttpActionAdapter;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.play.CallbackController;
import org.pac4j.play.LogoutController;
import org.pac4j.play.deadbolt2.Pac4jHandlerCache;
import org.pac4j.play.deadbolt2.Pac4jRoleHandler;
import org.pac4j.play.store.PlayCacheSessionStore;
import org.pac4j.play.store.PlaySessionStore;
import play.Configuration;
import play.Environment;
import play.cache.CacheApi;

/**
 * Configuration of Guice dependency injection for Gui module
 *
 * @author Kristian Lange (2018)
 */
public class GuiGuiceModule extends AbstractModule {

    public final static String JWT_SALT = "12345678901234567890123456789012";

    private final Configuration configuration;

    private static class MyPac4jRoleHandler implements Pac4jRoleHandler {
    }

    public GuiGuiceModule(final Environment environment, final Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
        bind(Pac4jHandlerCache.class).to(Pac4jHandlerCache.class);

        bind(Pac4jRoleHandler.class).to(MyPac4jRoleHandler.class);
        final PlayCacheSessionStore playCacheSessionStore =
                new PlayCacheSessionStore(getProvider(CacheApi.class));
        bind(PlaySessionStore.class).toInstance(playCacheSessionStore);

        final String fbId = configuration.getString("fbId");
        final String fbSecret = configuration.getString("fbSecret");
        final String baseUrl = configuration.getString("baseUrl");
        final FacebookClient facebookClient = new FacebookClient(fbId, fbSecret);

        final Clients clients = new Clients(baseUrl + "/callback", facebookClient);

        final Config config = new Config(clients);
        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
        config.addAuthorizer("custom", new CustomAuthorizer());
        config.setHttpActionAdapter(new DemoHttpActionAdapter());
        bind(Config.class).toInstance(config);

        final CallbackController callbackController = new CallbackController();
        callbackController.setDefaultUrl("/");
        callbackController.setMultiProfile(true);
        bind(CallbackController.class).toInstance(callbackController);

        final LogoutController logoutController = new LogoutController();
        logoutController.setDefaultUrl("/jatos/login");
        bind(LogoutController.class).toInstance(logoutController);
    }


}
