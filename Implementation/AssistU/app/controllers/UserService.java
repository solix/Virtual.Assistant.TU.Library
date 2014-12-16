package controllers;

import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;
import com.feth.play.module.pa.user.NameIdentity;
import com.feth.play.module.pa.service.UserServicePlugin;
import models.User;
import play.*;

/**
 * Created by arnaud on 15-12-14.
 *
 * This class is an adaptation by the play-authenticate plugin that extends
 * a UserService class required by SecureSocial, a package on which play-authenticate
 * is built. This is meant as the translator between the SecureSocial implementation
 * and our own models. The following functions are imposed by the abstract
 * class UserServicePlugin which need to be extended.
 *
 * More info on: http://securesocial.ws/guide/user-service.html
 *
 */
public class UserService extends UserServicePlugin {

    public UserService(final play.Application app) {super(app);}

    /**
     * This function is called when an authUser has been defined, but is not
     * yet linked to our User model. When no existing user model
     * @param authUser
     * @return
     */
    @Override
    public User save(final AuthUser authUser) {
        User user = getLocalIdentity(authUser);
        if(user != null){
            return user;
        } else {
            String email = ((EmailIdentity)authUser).getEmail();
            return User.create("", email, "default", authUser.getId(), authUser.getProvider());
        }
    }

    /**
     * This function calls the user entity that
     * @param identity
     * @return
     */
    @Override
    public User getLocalIdentity(final AuthUserIdentity identity) {
        // For production: Caching might be a good idea here, and dont forget to sync the cache when users get deactivated/deleted [sic]
        final User user = User.find.where().eq("socialId", identity.getId()).eq("socialKey", identity.getProvider()).findUnique();
        if(user != null) {
            return user;
        } else {
            return null;
        }
    }

    /**
     * This function is used to merge two accounts associated to each other,
     * imposed by the interface of the plugin. Since this application does
     * not support account merging by design, this unused function returns null.
     * @param newUser
     * @param oldUser
     * @return
     */
    @Override
    public AuthUser merge(final AuthUser newUser, final AuthUser oldUser) {
        return null;
    }

    /**
     * This function is used to link two accounts associated to each other,
     * imposed by the interface of the plugin. Since this application does
     * not support account linking by design, this unused function returns null.
     * @param oldUser
     * @param newUser
     * @return
     */
    @Override
    public AuthUser link(final AuthUser oldUser, final AuthUser newUser) {
        return null;
    }

}