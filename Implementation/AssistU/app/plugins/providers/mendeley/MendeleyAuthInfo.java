package plugins.providers.mendeley;

import plugins.com.feth.play.module.pa.providers.oauth2.OAuth2AuthInfo;

/**
 * Created by arnaud on 18-12-14.
 */
public class MendeleyAuthInfo extends OAuth2AuthInfo{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public MendeleyAuthInfo(final String accessToken) {
        super(accessToken);
    }
}
