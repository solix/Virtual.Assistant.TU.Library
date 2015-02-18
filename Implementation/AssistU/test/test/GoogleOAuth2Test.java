package test;

import com.feth.play.module.pa.providers.oauth2.google.GoogleAuthUser;

import static org.fest.assertions.Assertions.assertThat;

public class GoogleOAuth2Test extends GoogleOAuth2Base {

//    @Test
//    public void itShouldBePossibleToSignUp() {
//        signupUser();
//
//        Assertions.assertThat(browser.url()).isEqualTo("/");
//
//        final GoogleAuthUser authUser = (GoogleAuthUser) (OAuth2Test.MyTestUserServicePlugin.getLastAuthUser());
//        assertThat(authUser.getProfileLink()).isEqualTo("https://plus.google.com/109975614317978623876");
//        assertThat(authUser.getId()).isEqualTo("109975614317978623876");
//        assertThat(authUser.getGender()).isEqualTo("male");
//
//        final User user = User.findByEmail(GoogleOAuth2Base.GOOGLE_USER_EMAIL);
//        assertThat(user).isNotNull();
//        assertThat(user.first_name).isEqualTo("Joscha");
//        assertThat(user.last_name).isEqualTo("Feth");
//        assertThat(user.name).isEqualTo("Joscha Feth");
//    }

//    @Test
//    public void itShouldStillWorkIfCacheGetsCleared() {
//        signupFill();
//        CacheManager.getInstance().clearAll();
//        signupApprove();
//        Assertions.assertThat(browser.url()).isEqualTo("/");
//    }
}
