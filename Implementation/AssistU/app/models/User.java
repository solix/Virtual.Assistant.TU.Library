package models;

import javax.persistence.*;

import play.Logger;
import play.data.validation.Constraints;
import play.db.ebean.*;
import com.avaje.ebean.*;
import play.data.validation.Constraints.*;

import java.util.*;
import java.util.List;
import com.avaje.ebean.ExpressionList;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;
import com.feth.play.module.pa.user.NameIdentity;
import com.feth.play.module.pa.user.FirstLastNameIdentity;
import com.feth.play.module.pa.providers.oauth2.OAuth2AuthUser;
import com.feth.play.module.pa.providers.oauth2.google.GoogleAuthUser;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import providers.mendeley.MendeleyAuthUser;

@Entity
public class User extends Model {


    @Id @GeneratedValue
    public Long id;
    public String email;
    public String name;
    public String first_name;
    public String last_name;
    public String password;
    public boolean emailValidated;
    public boolean active=false;
    public boolean mendeleyConnected=false;


    @OneToMany(cascade = CascadeType.ALL)
    public List<LinkedAccount> linkedAccounts;

    @OneToMany(mappedBy = "user")
    public List<Task> tasks= new ArrayList<Task>();
    @OneToMany(mappedBy = "user")
    public List<Event> events;

    @OneToMany(mappedBy = "user")
    public List<Role> roles= new ArrayList<Role>();
    @OneToMany(mappedBy = "user")
    public List<Comment> comments=new ArrayList<Comment>();

//    public User(String name, String email, String password) {
//        this.name=name;
//        this.email = email;
//        this.password = password;
//        this.active= true;
//    }

    public static Model.Finder<Long,User> find = new Model.Finder(
            Long.class, User.class
    );

    /**
     * autheticates  user
     * @param email
     * @param password
     * @return
     */
//    public static User authenticate(String email, String password){
//        return find.where().eq("email", email).eq("password", password).findUnique();
//    }


//    public static User create(String name, String email, String password ) {
//        User user = new User(name, email, password);
//        user.save();
//        return user;
//    }

    public static User update(final AuthUser authUser) {
        final User user = User.findByAuthUserIdentity(authUser);
        Logger.debug("USER FOR UPDATING: " + user.name);
        user.active = true;
//        user.linkedAccounts = Collections.singletonList(LinkedAccount
//                .create(authUser));

//        if (authUser instanceof EmailIdentity) {
//            final EmailIdentity identity = (EmailIdentity) authUser;
//            Remember, even when getting them from FB & Co., emails should be
//            verified within the application as a security breach there might
//            break your security as well!
//            user.email = identity.getEmail();
//            user.emailValidated = false;
//        }

//        if (authUser instanceof NameIdentity) {
//            final NameIdentity identity = (NameIdentity) authUser;
//            final String name = identity.getName();
//            if (name != null) {
//                user.name = name;
//            }
//        }

//        if (authUser instanceof FirstLastNameIdentity) {
//            final FirstLastNameIdentity identity = (FirstLastNameIdentity) authUser;
//            final String first_name = identity.getFirstName();
//            if(first_name != null){
//                user.first_name = first_name;
//            }
//            final String last_name = identity.getLastName();
//            if(last_name != null){
//                user.last_name = last_name;
//            }
//        }


        //This is for extra provider-specific information

//        if(authUser instanceof GoogleAuthUser){

//        }

//        if(authUser instanceof MendeleyAuthUser){

//        }

        user.save();
        return user;
    }

    public static boolean existsByAuthUserIdentity(
            final AuthUserIdentity identity) {
        final ExpressionList<User> exp = getAuthUserFind(identity);
        return exp.findRowCount() > 0;
    }

    private static ExpressionList<User> getAuthUserFind(
            final AuthUserIdentity identity) {
        return find.where().eq("active", true)
                .eq("linkedAccounts.providerUserId", identity.getId())
                .eq("linkedAccounts.providerKey", identity.getProvider());
    }

//    public static User findByAuthUserIdentity(final AuthUserIdentity identity) {
//        if (identity == null) {
//            return null;
//        }
//        return getAuthUserFind(identity).findUnique();
//    }

    public static User findByAuthUserIdentity(final AuthUserIdentity identity) {
        if (identity == null) {
            return null;
        }
        if (identity instanceof UsernamePasswordAuthUser) {
            return findByUsernamePasswordIdentity((UsernamePasswordAuthUser) identity);
        } else {
            return getAuthUserFind(identity).findUnique();
        }
    }

    public static User findByUsernamePasswordIdentity(
            final UsernamePasswordAuthUser identity) {
        return getUsernamePasswordAuthUserFind(identity).findUnique();
    }

    private static ExpressionList<User> getUsernamePasswordAuthUserFind(
            final UsernamePasswordAuthUser identity) {
        return getEmailUserFind(identity.getEmail()).eq(
                "linkedAccounts.providerKey", identity.getProvider());
    }

    public void merge(final User otherUser) {
        for (final LinkedAccount acc : otherUser.linkedAccounts) {
            this.linkedAccounts.add(LinkedAccount.create(acc));
        }
        // do all other merging stuff here - like resources, etc.

        // deactivate the merged user that got added to this one
        otherUser.active = false;
        Ebean.save(Arrays.asList(new User[] { otherUser, this }));
    }

    /**
     * creates a User by binding social user information to user model
     * @param authUser
     * @return user
     */
    public static User create(final AuthUser authUser) {
        final User user = new User();
        user.active = true;
        user.linkedAccounts = Collections.singletonList(LinkedAccount
                .create(authUser));

        if (authUser instanceof EmailIdentity) {
            final EmailIdentity identity = (EmailIdentity) authUser;
//            Remember, even when getting them from FB & Co., emails should be
//            verified within the application as a security breach there might
//            break your security as well!
            user.email = identity.getEmail();
            user.emailValidated = false;
        }

        if (authUser instanceof NameIdentity) {
            final NameIdentity identity = (NameIdentity) authUser;
            final String name = identity.getName();
            if (name != null) {
                user.name = name;
            }
        }

        if (authUser instanceof FirstLastNameIdentity) {
            final FirstLastNameIdentity identity = (FirstLastNameIdentity) authUser;
            final String first_name = identity.getFirstName();
            if(first_name != null){
                user.first_name = first_name;
            }
            final String last_name = identity.getLastName();
            if(last_name != null){
                user.last_name = last_name;
            }
        }


        //This is for extra provider-specific information

        if(authUser instanceof GoogleAuthUser){

        }

        if(authUser instanceof MendeleyAuthUser){
            user.mendeleyConnected=true;
        }

        user.save();
        return user;
    }

    public static void merge(final AuthUser oldUser, final AuthUser newUser) {
        User.findByAuthUserIdentity(oldUser).merge(
                User.findByAuthUserIdentity(newUser));
    }

    public Set<String> getProviders() {
        final Set<String> providerKeys = new HashSet<String>(
                linkedAccounts.size());
        for (final LinkedAccount acc : linkedAccounts) {
            providerKeys.add(acc.providerKey);
        }
        return providerKeys;
    }

    public static void addLinkedAccount(final AuthUser oldUser,
                                        final AuthUser newUser) {
        final User u = User.findByAuthUserIdentity(oldUser);
        u.linkedAccounts.add(LinkedAccount.create(newUser));
        u.save();
    }

    public static User findByEmail(final String email) {
        return getEmailUserFind(email).findUnique();
    }

    private static ExpressionList<User> getEmailUserFind(final String email) {
        return find.where().eq("active", true).eq("email", email);
    }

    public LinkedAccount getAccountByProvider(final String providerKey) {
        return LinkedAccount.findByProviderKey(this, providerKey);
    }

    public static void verify(final User unverified) {
        // You might want to wrap this into a transaction
        unverified.emailValidated = true;
        unverified.save();
        TokenAction.deleteByUser(unverified, TokenAction.Type.EMAIL_VERIFICATION);
    }

    public void resetPassword(final UsernamePasswordAuthUser authUser,
                              final boolean create) {
        // You might want to wrap this into a transaction
        this.changePassword(authUser, create);
        TokenAction.deleteByUser(this, TokenAction.Type.PASSWORD_RESET);
    }

    public void changePassword(final UsernamePasswordAuthUser authUser,
                               final boolean create) {
        LinkedAccount a = this.getAccountByProvider(authUser.getProvider());
        Logger.debug("CHANGEPASSWORD: " + a + ", " + create);
        if (a == null) {
            if (create) {
                a = LinkedAccount.create(authUser);
                a.user = this;
            } else {
                throw new RuntimeException(
                        "Account not enabled for password usage");
            }
        }
        a.providerUserId = authUser.getHashedPassword();
        a.save();
    }
}