package plugins.com.feth.play.module.pa.providers.oauth2.google;

import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;

import plugins.com.feth.play.module.pa.providers.oauth2.OAuth2AuthInfo;
import plugins.com.feth.play.module.pa.providers.oauth2.OAuth2AuthProvider;

public class GoogleAuthInfo extends OAuth2AuthInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String ID_TOKEN = "id_token";
	private String bearer;
	private String idToken;

	public GoogleAuthInfo(final JsonNode node) {
		super(	node.get(OAuth2AuthProvider.Constants.ACCESS_TOKEN) != null ? node.get(OAuth2AuthProvider.Constants.ACCESS_TOKEN).asText() : null,
				node.get(OAuth2AuthProvider.Constants.EXPIRES_IN) != null ? new Date().getTime() + node.get(OAuth2AuthProvider.Constants.EXPIRES_IN).asLong() * 1000 : -1,
				node.get(OAuth2AuthProvider.Constants.REFRESH_TOKEN) != null ? node.get(OAuth2AuthProvider.Constants.REFRESH_TOKEN).asText() : null);

		bearer = node.get(OAuth2AuthProvider.Constants.TOKEN_TYPE).asText();
		idToken = node.get(ID_TOKEN).asText();
	}

	public String getBearer() {
		return bearer;
	}

	public String getIdToken() {
		return idToken;
	}
}
