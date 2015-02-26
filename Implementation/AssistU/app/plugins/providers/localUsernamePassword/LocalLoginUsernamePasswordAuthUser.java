package plugins.providers.localUsernamePassword;

import plugins.com.feth.play.module.pa.providers.password.DefaultUsernamePasswordAuthUser;

public class LocalLoginUsernamePasswordAuthUser extends
		DefaultUsernamePasswordAuthUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The session timeout in seconds
	 * Defaults to two weeks
	 */
	final static long SESSION_TIMEOUT = 24 * 14 * 3600;
	private long expiration;

	/**
	 * For logging the user in automatically
	 * 
	 * @param email
	 */
	public LocalLoginUsernamePasswordAuthUser(final String email) {
		this(null, email);
	}

	public LocalLoginUsernamePasswordAuthUser(final String clearPassword,
											  final String email) {
		super(clearPassword, email);

		expiration = System.currentTimeMillis() + 1000 * SESSION_TIMEOUT;
	}

	@Override
	public long expires() {
		return expiration;
	}

}
