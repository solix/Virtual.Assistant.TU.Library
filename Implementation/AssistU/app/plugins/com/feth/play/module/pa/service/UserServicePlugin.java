package plugins.com.feth.play.module.pa.service;

import play.Application;
import play.Plugin;

import plugins.com.feth.play.module.pa.user.AuthUser;
import plugins.com.feth.play.module.pa.PlayAuthenticate;

public abstract class UserServicePlugin extends Plugin implements UserService {

    private Application application;

    public UserServicePlugin(final Application app) {
        application = app;
    }

    protected Application getApplication() {
        return application;
    }

    @Override
    public void onStart() {
        if (PlayAuthenticate.hasUserService()) {
            final String oldServiceClass = PlayAuthenticate.getUserService().getClass().getName();

//            Logger.warn("A user plugins.service was already registered - replacing the old one (" + oldServiceClass + ") with the new one (" + getClass().getName() + "), " +
//                    "however this might hint to a configuration problem if this is a production environment.");
        }
        PlayAuthenticate.setUserService(this);
    }

    @Override
    public AuthUser update(AuthUser knownUser) {
        // Default: just do nothing when user logs in again
        return knownUser;
    }
}
