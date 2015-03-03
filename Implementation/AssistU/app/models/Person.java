package models;

import javax.persistence.*;

import play.Logger;
import play.db.ebean.*;
import com.avaje.ebean.*;

import java.util.*;
import java.util.List;
import com.avaje.ebean.ExpressionList;
import plugins.com.feth.play.module.pa.user.AuthUser;
import plugins.com.feth.play.module.pa.user.AuthUserIdentity;
import plugins.com.feth.play.module.pa.user.EmailIdentity;
import plugins.com.feth.play.module.pa.user.NameIdentity;
import plugins.com.feth.play.module.pa.user.FirstLastNameIdentity;
import plugins.com.feth.play.module.pa.providers.oauth2.google.GoogleAuthUser;
import plugins.com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import plugins.providers.localUsernamePassword.LocalUsernamePasswordAuthUser;
import plugins.providers.mendeley.MendeleyAuthUser;

@Entity
public class Person extends Model {


    @Id @GeneratedValue
    public Long id;
    public String email;
    public String name;
    public String first_name;
    public String last_name;
    public String password;
    public boolean emailValidated=false;
    public boolean active=false;
    public boolean mendeleyConnected=false;
    public boolean native_account=false;


    @OneToMany(cascade = CascadeType.ALL)
    public List<LinkedAccount> linkedAccounts;

    @OneToMany(mappedBy = "person")
    public List<Task> tasks= new ArrayList<Task>();
    @OneToMany(mappedBy = "person")
    public List<Event> events;

//    @OneToMany(mappedBy = "person")
//    public List<DocumentFile> documentFiles = new ArrayList<DocumentFile>();

    @OneToMany(mappedBy = "person")
    public List<S3File> documentFiles = new ArrayList<S3File>();

    @OneToMany(mappedBy = "person")
    public List<Role> roles= new ArrayList<Role>();
    @OneToMany(mappedBy = "person")
    public List<Comment> comments=new ArrayList<Comment>();



    /**
     * sending queries to DB using finder class
     */
    public static Model.Finder<Long, Person> find = new Model.Finder(
            Long.class, Person.class
    );


    /**
     * updates person credentials
     * @param authUser
     * @return
     */
    public static Person update(final AuthUser authUser) {
        final Person person = Person.findByAuthUserIdentity(authUser);
        person.active = true;
        person.save();
        return person;
    }

    /**
     *checks if user exists
     * @param identity
     * @return true/false
     */
    public static boolean existsByAuthUserIdentity(
            final AuthUserIdentity identity) {
        final ExpressionList<Person> exp = getAuthUserFind(identity);
        return exp.findRowCount() > 0;
    }

    /**
     * query to find list of active users
     * @param identity
     * @return list f active users
     */
    private static ExpressionList<Person> getAuthUserFind(
            final AuthUserIdentity identity) {
        return find.where().eq("active", true)
                .eq("linkedAccounts.providerUserId", identity.getId())
                .eq("linkedAccounts.providerKey", identity.getProvider());
    }


    /**
     * finds a user by his/her Identity
     * @param identity
     * @return
     */
    public static Person findByAuthUserIdentity(final AuthUserIdentity identity) {
        if (identity == null) {
            return null;
        }
        if (identity instanceof UsernamePasswordAuthUser) {
            return findByUsernamePasswordIdentity((UsernamePasswordAuthUser) identity);
        } else {
            return getAuthUserFind(identity).findUnique();
        }
    }

    /**
     * finds user by username password identity
     * @param identity
     * @return
     */
    public static Person findByUsernamePasswordIdentity(
            final UsernamePasswordAuthUser identity) {
        return getUsernamePasswordAuthUserFind(identity).findUnique();
    }

    /**
     * getter method fin user using email
     * @param identity
     * @return
     */
    private static ExpressionList<Person> getUsernamePasswordAuthUserFind(
            final UsernamePasswordAuthUser identity) {
        return getEmailUserFind(identity.getEmail()).eq(
                "linkedAccounts.providerKey", identity.getProvider());
    }

    public void merge(final Person otherPerson) {
        for (final LinkedAccount acc : otherPerson.linkedAccounts) {
            this.linkedAccounts.add(LinkedAccount.create(acc));
        }
        otherPerson.active = false;
        Ebean.save(Arrays.asList(new Person[] {otherPerson, this }));
    }

    /**
     * creates a User by binding social user information to user model
     * @param authUser
     * @return user
     */
    public static Person create(final AuthUser authUser) {
        //This application should only support email-type of authentication
        String email = ((EmailIdentity)authUser).getEmail();
        Person old = Person.find.where().eq("email", email).findUnique();
        if(old != null){
            TokenAction.find.where().eq("targetPerson", old).findUnique().delete();
            Person.deleteAccount(old.id);
        }
        final Person person = new Person();
        person.active = true;
        person.linkedAccounts = Collections.singletonList(LinkedAccount
                .create(authUser));

        if (authUser instanceof LocalUsernamePasswordAuthUser) {
            person.native_account=true;
        }

        if (authUser instanceof EmailIdentity) {
            final EmailIdentity identity = (EmailIdentity) authUser;
//            Remember, even when getting them from FB & Co., emails should be
//            verified within the application as a security breach there might
//            break your security as well!
            person.email = identity.getEmail();
            person.emailValidated = false;
        }

        if (authUser instanceof NameIdentity) {
            final NameIdentity identity = (NameIdentity) authUser;
            final String name = identity.getName();
            if (name != null) {
                person.name = name;
            }
        }

        if (authUser instanceof FirstLastNameIdentity) {
            final FirstLastNameIdentity identity = (FirstLastNameIdentity) authUser;
            final String first_name = identity.getFirstName();
            if(first_name != null){
                person.first_name = first_name;
            }
            final String last_name = identity.getLastName();
            if(last_name != null){
                person.last_name = last_name;
            }
        }

        //This is for extra provider-specific information

        if(authUser instanceof GoogleAuthUser){
            final GoogleAuthUser identity = (GoogleAuthUser) authUser;
            final Boolean is_verified = identity.isEmailVerified();
            person.emailValidated = is_verified;
        }

        if(authUser instanceof MendeleyAuthUser){
            person.mendeleyConnected=true;
        }

        person.save();
        return person;
    }

    /**
     * merges two account if there are same users
     * @param oldUser
     * @param newUser
     */
    public static void merge(final AuthUser oldUser, final AuthUser newUser) {
        Person.findByAuthUserIdentity(oldUser).merge(
                Person.findByAuthUserIdentity(newUser));
    }

    public Set<String> getProviders() {
        final Set<String> providerKeys = new HashSet<String>(
                linkedAccounts.size());
        for (final LinkedAccount acc : linkedAccounts) {
            providerKeys.add(acc.providerKey);
        }
        return providerKeys;
    }

    /**
     *
     * @param oldUser
     * @param newUser
     */
    public static void addLinkedAccount(final AuthUser oldUser,
                                        final AuthUser newUser) {
        final Person u = Person.findByAuthUserIdentity(oldUser);
        u.linkedAccounts.add(LinkedAccount.create(newUser));
        u.save();
    }

    /**
     * find a person by email
     * @param email
     * @return
     */
    public static Person findByEmail(final String email) {
        return getEmailUserFind(email).findUnique();
    }

    /**
     * find users hat are active thorigh their emails
     * @param email
     * @return
     */
    private static ExpressionList<Person> getEmailUserFind(final String email) {
        return find.where().eq("active", true).eq("email", email);
    }


    public LinkedAccount getAccountByProvider(final String providerKey) {
        return LinkedAccount.findByProviderKey(this, providerKey);
    }

    /**
     * verifies the user and saves it to DB
     * @param unverified
     */
    public static void verify(final Person unverified) {

        unverified.emailValidated = true;
        unverified.save();
        TokenAction.deleteByUser(unverified, TokenAction.Type.EMAIL_VERIFICATION);
    }

    /**
     * resets password for the user by creating new token
     * @param authUser
     * @param create
     */
    public void resetPassword(final UsernamePasswordAuthUser authUser,
                              final boolean create) {
        // You might want to wrap this into a transaction
        this.changePassword(authUser, create);
        TokenAction.deleteByUser(this, TokenAction.Type.PASSWORD_RESET);
    }

    /**
     * change the current password to new password
     * @param authUser
     * @param create
     */
    public void changePassword(final UsernamePasswordAuthUser authUser,
                               final boolean create) {
        LinkedAccount a = this.getAccountByProvider(authUser.getProvider());
        Logger.debug("CHANGEPASSWORD: " + a + ", " + create);
        if (a == null) {
            if (create) {
                a = LinkedAccount.create(authUser);
                a.person = this;
            } else {
                throw new RuntimeException(
                        "Account not enabled for password usage");
            }
        }
        a.providerUserId = authUser.getHashedPassword();
        a.save();
    }

    /**
     * delete account from db
     * @param uid
     */
    public static void deleteAccount(Long uid){
        Person.find.byId(uid).delete();
    }
}