package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.QueryIterator;
import com.avaje.ebean.annotation.EnumValue;
import play.data.format.Formats;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class TokenAction extends Model {

	public enum Type {
		@EnumValue("EV")
		EMAIL_VERIFICATION,

		@EnumValue("PR")
		PASSWORD_RESET
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Verification time frame (until the user clicks on the link in the email)
	 * in seconds
	 * Defaults to one week
	 */
	private final static long VERIFICATION_TIME = 7 * 24 * 3600;

	@Id
	public Long id;

	@Column(unique = true)
	public String token;

	@ManyToOne
	public Person targetPerson;

	public Type type;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date expires;

	/**
	 * finder to make queries in db
	 */
	public static final Finder<Long, TokenAction> find = new Finder<Long, TokenAction>(
			Long.class, TokenAction.class);

	/**
	 * find by token
	 * @param token
	 * @param type
	 * @return
	 */
	public static TokenAction findByToken(final String token, final Type type) {
		return find.where().eq("token", token).eq("type", type).findUnique();
	}

	/**
	 * delete token actions
	 * @param u
	 * @param type
	 */
	public static void deleteByUser(final Person u, final Type type) {
		QueryIterator<TokenAction> iterator = find.where()
				.eq("targetPerson.id", u.id).eq("type", type).findIterate();
		Ebean.delete(iterator);
		iterator.close();
	}

	/**
	 * if date is still valid
	 * @return
	 */
	public boolean isValid() {
		return this.expires.after(new Date());
	}

	/**
	 * creates a token action
	 * @param type
	 * @param token
	 * @param targetPerson
	 * @return
	 */
	public static TokenAction create(final Type type, final String token,
			final Person targetPerson) {
		final TokenAction ua = new TokenAction();
		ua.targetPerson = targetPerson;
		ua.token = token;
		ua.type = type;
		final Date created = new Date();
		ua.created = created;
		ua.expires = new Date(created.getTime() + VERIFICATION_TIME * 1000);
		ua.save();
		return ua;
	}
}
