package providers.TuDelft;

import play.Application;
import play.Logger;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.feth.play.module.pa.exceptions.AccessTokenException;
import com.feth.play.module.pa.exceptions.AuthException;
import com.feth.play.module.pa.providers.oauth2.OAuth2AuthProvider;
import com.feth.play.module.pa.user.AuthUserIdentity;

public class NetidAuthProvider extends
		OAuth2AuthProvider<NetidAuthUser, NetidAuthInfo> {

	static final String PROVIDER_KEY = "netid";

	private static final String USER_INFO_URL_SETTING_KEY = "TODO";
	private static final String OAUTH_TOKEN = "oauth_token";
	private static final String VERSION = "20150101";

	public NetidAuthProvider(Application app) {
		super(app);
	}

	@Override
	protected NetidAuthInfo buildInfo(final WSResponse r)
			throws AccessTokenException {

		if (r.getStatus() >= 400) {
			throw new AccessTokenException(r.toString());
		} else {
			final JsonNode result = r.asJson();
			Logger.debug(result.asText());
			return new NetidAuthInfo(result.get(
					OAuth2AuthProvider.Constants.ACCESS_TOKEN).asText());
		}
	}

	@Override
	protected AuthUserIdentity transform(final NetidAuthInfo info, final String state)
			throws AuthException {


		final String url = getConfiguration().getString(
				USER_INFO_URL_SETTING_KEY);
		final WSResponse r = WS
				.url(url)
				.setQueryParameter(OAUTH_TOKEN,
						info.getAccessToken())
				.setQueryParameter("v", VERSION)
				.get()
				.get(getTimeout());

		final JsonNode result = r.asJson();
		if (r.getStatus() >= 400) {
			throw new AuthException(result.get("meta").get("errorDetail").asText());
		} else {
			Logger.debug(result.toString());
			return new NetidAuthUser(result.get("response").get("user"), info, state);
		}
	}

	@Override
	public String getKey() {
		return PROVIDER_KEY;
	}

}
