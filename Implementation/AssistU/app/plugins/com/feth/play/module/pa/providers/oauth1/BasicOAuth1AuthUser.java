package plugins.com.feth.play.module.pa.providers.oauth1;

import plugins.com.feth.play.module.pa.user.AuthUserIdentity;
import plugins.com.feth.play.module.pa.user.NameIdentity;
import plugins.com.feth.play.module.pa.user.AuthUser;

public abstract class BasicOAuth1AuthUser extends OAuth1AuthUser implements NameIdentity, AuthUserIdentity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BasicOAuth1AuthUser(final String id, final OAuth1AuthInfo info,
			final String state) {
		super(id, info, state);
	}

	@Override
	public String toString() {
		return AuthUser.toString(this);
	}

}