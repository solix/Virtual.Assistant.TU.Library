package plugins.com.feth.play.module.pa.providers.oauth1.linkedin;

import plugins.com.feth.play.module.pa.providers.oauth1.OAuth1AuthInfo;

public class LinkedinAuthInfo extends OAuth1AuthInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LinkedinAuthInfo(final String token, final String tokenSecret) {
		super(token, tokenSecret);
	}

}
