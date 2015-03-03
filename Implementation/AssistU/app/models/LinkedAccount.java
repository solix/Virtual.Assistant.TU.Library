package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;

import plugins.com.feth.play.module.pa.user.AuthUser;

@Entity
public class LinkedAccount extends Model {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    public Long id;

    @ManyToOne
    public Person person;

    public String providerUserId;
    public String providerKey;
    /**
     * finder method to make queries to DB
     */
    public static final Finder<Long, LinkedAccount> find = new Finder<Long, LinkedAccount>(
            Long.class, LinkedAccount.class);

    /**
     * finds linked account
     * @param person
     * @param key
     * @return
     */
    public static LinkedAccount findByProviderKey(final Person person, String key) {
        return find.where().eq("person", person).eq("providerKey", key)
                .findUnique();
    }

    /**
     *
     * @param authUser
     * @return
     */
    public static LinkedAccount create(final AuthUser authUser) {
        final LinkedAccount ret = new LinkedAccount();
        ret.update(authUser);
        return ret;
    }

    /**
     *
     * @param authUser
     */
    public void update(final AuthUser authUser) {
        this.providerKey = authUser.getProvider();
        this.providerUserId = authUser.getId();
    }

    /**
     *
     * @param acc
     * @return
     */
    public static LinkedAccount create(final LinkedAccount acc) {
        final LinkedAccount ret = new LinkedAccount();
        ret.providerKey = acc.providerKey;
        ret.providerUserId = acc.providerUserId;

        return ret;
    }
}