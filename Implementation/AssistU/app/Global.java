import models.Person;
import play.*;
import play.mvc.*;

import play.Application;
import play.GlobalSettings;

import plugins.com.feth.play.module.pa.PlayAuthenticate;
import plugins.com.feth.play.module.pa.PlayAuthenticate.Resolver;
import plugins.com.feth.play.module.pa.exceptions.*;

import controllers.routes;
import plugins.providers.localUsernamePassword.LocalUsernamePasswordAuthProvider;
import views.html.login;


/**
 * this class  injects default data into the webapp  to load a YAML file at application load time
 */
public class Global extends GlobalSettings {
    public void onStart(Application app) {
        providerResolver.setUp(app);
    }

    /**
     * loads the data from yaml file and add rows in User table
     */


    static class providerResolver {
        public static void setUp(Application app) {

            PlayAuthenticate.setResolver(new Resolver() {

                @Override
                public Call login() {
                    Logger.debug("loading login");
                    // Your login page
//                    return routes.Authentication.relogin();
                    return routes.Application.reroute();
                }

                @Override
                public Call afterAuth() {
                    // The user will be redirected to this page after authentication
                    // if no original URL was saved
//                    return routes.Application.index();
                    return routes.Application.reroute();
                }


                @Override
                public Call afterLogout() {
                    return routes.Authentication.login();
                }

                @Override
                public Call auth(final String provider) {
                    // You can provide your own authentication implementation,
                    // however the default should be sufficient for most cases
                    return routes.Authentication.OAuth(provider);
                }

                @Override
                public Call onException(final AuthException e) {
                    Logger.debug("EXCEPTION: " + e.toString());
                    if (e instanceof AccessDeniedException) {
                        return routes.Authentication.OAuthDenied(((AccessDeniedException) e).getProviderKey());
                    }
                    if(e.getMessage().equals("playauthenticate.core.exception.signupuser_failed")){
                        return routes.Authentication.loginWithMessage("You already have a default account","danger");
                    }
                    if(e.getMessage().equals("Mendeley timed out")){
                        return routes.Application.reroute();
                    }
                    // more custom problem handling here...

                    return super.onException(e);
                }

                @Override
                public Call askLink() {
                    // We don't support moderated account linking in this sample.
                    // See the play-authenticate-usage project for an example
                    return null;
                }

                @Override
                public Call askMerge() {
                    // We don't support moderated account merging in this sample.
                    // See the play-authenticate-usage project for an example
                    return null;
                }
            });
        }
    }
}
