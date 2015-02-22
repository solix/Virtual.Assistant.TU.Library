package plugins.com.feth.play.module.pa.providers.oauth2.github;

import com.fasterxml.jackson.databind.JsonNode;
import plugins.com.feth.play.module.pa.providers.oauth2.OAuth2AuthInfo;
import plugins.com.feth.play.module.pa.providers.oauth2.OAuth2AuthProvider;

public class GithubAuthInfo extends OAuth2AuthInfo {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String SCOPE = "scope";
    private String bearer;
    private String scope;

    public GithubAuthInfo(final JsonNode node) {
        super(node.get(OAuth2AuthProvider.Constants.ACCESS_TOKEN) != null ? node.get(OAuth2AuthProvider.Constants.ACCESS_TOKEN).asText() : null);

        bearer = node.get(OAuth2AuthProvider.Constants.TOKEN_TYPE).asText();
        scope = node.get(SCOPE).asText();
    }

    public String getBearer() {
        return bearer;
    }

    public String getScope() {
        return scope;
    }
}
