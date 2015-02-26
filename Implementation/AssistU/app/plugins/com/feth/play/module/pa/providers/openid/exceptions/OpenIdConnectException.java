package plugins.com.feth.play.module.pa.providers.openid.exceptions;

import plugins.com.feth.play.module.pa.exceptions.AuthException;

public class OpenIdConnectException extends AuthException {

	public OpenIdConnectException(String message) {
		super(message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
