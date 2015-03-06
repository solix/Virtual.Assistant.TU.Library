package plugins.providers.mendeley;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Person;
import play.libs.ws.WSRequest;
import plugins.com.feth.play.module.pa.PlayAuthenticate;
import plugins.com.feth.play.module.pa.exceptions.AccessTokenException;
import plugins.com.feth.play.module.pa.exceptions.AuthException;
import plugins.com.feth.play.module.pa.providers.oauth2.OAuth2AuthInfo;
import plugins.com.feth.play.module.pa.providers.oauth2.OAuth2AuthProvider;
import plugins.com.feth.play.module.pa.user.AuthUserIdentity;
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
    private static final String USER_DOCUMENTS_URL_SETTING_KEY = "userDocumentsUrl";
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

        final String infoUrl = getConfiguration().getString(
                USER_INFO_URL_SETTING_KEY);
        final WSResponse infoResult = WS
                .url(infoUrl)
                .setQueryParameter(OAUTH_TOKEN,
                        info.getAccessToken())
                .get()
                .get(getTimeout());

        final ObjectNode userinfo = (ObjectNode)infoResult.asJson();

        final String libraryUrl = getConfiguration().getString(
                USER_DOCUMENTS_URL_SETTING_KEY);

        final WSResponse libraryResult = WS
                .url(libraryUrl)
                .setQueryParameter(OAUTH_TOKEN,
                        info.getAccessToken())
                .get()
                .get(getTimeout());

        if(libraryResult.getStatus() == 504) {
            throw new AuthException("Mendeley timed out");
        }else{
            JsonNode libraryNode = libraryResult.asJson();
            Logger.debug("NODE: " + libraryNode.toString());
            if(libraryNode instanceof ArrayNode) {
                ArrayNode libraryinfo = (ArrayNode) libraryNode;
                userinfo.put("documents", libraryinfo);

                final JsonNode result = userinfo;

                if (infoResult.getStatus() >= 400) {
                    throw new AuthException(result.get("meta").get("errorDetail").asText());
                } else {
                    Logger.debug(result.toString());
                    return new MendeleyAuthUser(result, info, state);
                }
            }else {
                throw new AuthException(libraryNode.get("message").asText());
            }
        }
    }

    @Override
    public String getKey() {
        return PROVIDER_KEY;
    }
}