package providers.TuDelft;

import com.feth.play.module.pa.providers.oauth2.OAuth2AuthInfo;

public class NetidAuthInfo extends OAuth2AuthInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NetidAuthInfo(final String accessToken) {
		super(accessToken);
	}
}
