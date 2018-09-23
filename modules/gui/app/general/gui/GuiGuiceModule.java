package general.gui;

import be.objectify.deadbolt.java.cache.HandlerCache;
import com.google.inject.AbstractModule;
import controllers.gui.CustomAuthorizer;
import controllers.gui.DemoHttpActionAdapter;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.direct.AnonymousClient;
import org.pac4j.core.config.Config;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
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

    private final Configuration configuration;

    private static class MyPac4jRoleHandler implements Pac4jRoleHandler {
    }

    public GuiGuiceModule(final Environment environment, final Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
        bind(HandlerCache.class).to(Pac4jHandlerCache.class);

        bind(Pac4jRoleHandler.class).to(MyPac4jRoleHandler.class);
        final PlayCacheSessionStore playCacheSessionStore =
                new PlayCacheSessionStore(getProvider(CacheApi.class));
        bind(PlaySessionStore.class).toInstance(playCacheSessionStore);


        final String fbId = configuration.getString("fbId");
        final String fbSecret = configuration.getString("fbSecret");
        final String baseUrl = configuration.getString("baseUrl");

        // OAuth
        final FacebookClient facebookClient = new FacebookClient(fbId, fbSecret);

        // OpenID Connect
        final OidcConfiguration oidcConfiguration = new OidcConfiguration();
        oidcConfiguration.setClientId("343992089165-i1es0qvej18asl33mvlbeq750i3ko32k.apps.googleusercontent.com");
        oidcConfiguration.setSecret("unXK_RSCbCXLTic2JACTiAo9");
        oidcConfiguration.setDiscoveryURI("https://accounts.google.com/.well-known/openid-configuration");
        oidcConfiguration.addCustomParam("prompt", "consent");
        final OidcClient oidcClient = new OidcClient(oidcConfiguration);
        oidcClient.addAuthorizationGenerator((ctx, profile) -> { profile.addRole("ROLE_ADMIN"); return profile; });


        final Clients clients = new Clients(baseUrl + "/callback", facebookClient, oidcClient, new AnonymousClient());

        final Config config = new Config(clients);
        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer<>("ROLE_ADMIN"));
        config.addAuthorizer("custom", new CustomAuthorizer());
        config.setHttpActionAdapter(new DemoHttpActionAdapter());
        bind(Config.class).toInstance(config);

        // callback
        final CallbackController callbackController = new CallbackController();
        callbackController.setDefaultUrl("/pac4jAuth.html");
        callbackController.setMultiProfile(true);
        bind(CallbackController.class).toInstance(callbackController);

        // logout
        final LogoutController logoutController = new LogoutController();
        logoutController.setDefaultUrl("/pac4jAuth.html");
        //logoutController.setDestroySession(true);
        bind(LogoutController.class).toInstance(logoutController);
    }


}
