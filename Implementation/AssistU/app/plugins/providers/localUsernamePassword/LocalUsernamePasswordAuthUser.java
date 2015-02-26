package plugins.providers.localUsernamePassword;

import plugins.com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import plugins.com.feth.play.module.pa.user.NameIdentity;
import plugins.providers.localUsernamePassword.LocalUsernamePasswordAuthProvider.NativeSignup;

public class LocalUsernamePasswordAuthUser extends UsernamePasswordAuthUser
		implements NameIdentity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String username;

	public LocalUsernamePasswordAuthUser(final NativeSignup signup) {
		super(signup.password, signup.email);
		this.username = signup.first_name + " " + signup.last_name;
	}

	/**
	 * Used for password reset only - do not use this to signup a user!
	 * @param password
	 */
	public LocalUsernamePasswordAuthUser(final String password) {
		super(password, null);
		username = null;
	}

	@Override
	public String getName() {
		return username;
	}
}
