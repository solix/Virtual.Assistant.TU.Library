package providers.mendeley;

import com.fasterxml.jackson.databind.JsonNode;
import com.feth.play.module.pa.exceptions.AccessTokenException;
import com.feth.play.module.pa.exceptions.AuthException;
import com.feth.play.module.pa.providers.oauth2.OAuth2AuthProvider;
import com.feth.play.module.pa.user.AuthUserIdentity;
import play.Application;
import play.Logger;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

/**
 * Created by arnaud on 18-12-14.
 */
public class MendeleyAuthProvider extends
        OAuth2AuthProvider<MendeleyAuthUser, MendeleyAuthInfo> {

    static final String PROVIDER_KEY = "mendeley";

    private static final String USER_INFO_URL_SETTING_KEY = "userInfoUrl";
    private static final String OAUTH_TOKEN = "access_token";

    public MendeleyAuthProvider(Application app) {
        super(app);
    }

    @Override
    protected MendeleyAuthInfo buildInfo(final WSResponse r)
            throws AccessTokenException {

        if (r.getStatus() >= 400) {
            throw new AccessTokenException(r.toString());
        } else {
            final JsonNode result = r.asJson();
            Logger.debug(result.asText());
            return new MendeleyAuthInfo(result.get(
                    OAuth2AuthProvider.Constants.ACCESS_TOKEN).asText());
        }
    }

    @Override
    protected AuthUserIdentity transform(final MendeleyAuthInfo info, final String state)
            throws AuthException {

        final String url = getConfiguration().getString(
                USER_INFO_URL_SETTING_KEY);
        Logger.debug("URL: " + url);
        final WSResponse r = WS
                .url(url)
                .setQueryParameter(OAUTH_TOKEN,
                        info.getAccessToken())
                .get()
                .get(getTimeout());

        final JsonNode result = r.asJson();
        Logger.debug(result.toString());
        if (r.getStatus() >= 400) {
            throw new AuthException(result.get("meta").get("errorDetail").asText());
        } else {
            Logger.debug(result.toString());
            return new MendeleyAuthUser(result, info, state);
        }
    }

    @Override
    public String getKey() {
        return PROVIDER_KEY;
    }

}