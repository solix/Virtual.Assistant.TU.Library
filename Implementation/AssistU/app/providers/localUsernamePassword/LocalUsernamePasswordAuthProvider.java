package providers.localUsernamePassword;

import com.feth.play.module.mail.Mailer.Mail.Body;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import controllers.Emailer;
import models.LinkedAccount;
import models.TokenAction;
import models.TokenAction.Type;
import models.Person;
import play.Application;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Call;
import play.mvc.Http.Context;
import controllers.routes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static play.data.Form.form;

public class LocalUsernamePasswordAuthProvider
		extends
		UsernamePasswordAuthProvider<String,
				LocalLoginUsernamePasswordAuthUser,
				LocalUsernamePasswordAuthUser,
				LocalUsernamePasswordAuthProvider.NativeLogin,
				LocalUsernamePasswordAuthProvider.NativeSignup> {

	private static final String SETTING_KEY_VERIFICATION_LINK_SECURE = SETTING_KEY_MAIL
			+ "." + "verificationLink.secure";
	private static final String SETTING_KEY_PASSWORD_RESET_LINK_SECURE = SETTING_KEY_MAIL
			+ "." + "passwordResetLink.secure";
	private static final String SETTING_KEY_LINK_LOGIN_AFTER_PASSWORD_RESET = "loginAfterPasswordReset";

	private static final String EMAIL_TEMPLATE_FALLBACK_LANGUAGE = "en";

	@Override
	protected List<String> neededSettingKeys() {
		final List<String> needed = new ArrayList<String>(
				super.neededSettingKeys());
		needed.add(SETTING_KEY_VERIFICATION_LINK_SECURE);
		needed.add(SETTING_KEY_PASSWORD_RESET_LINK_SECURE);
		needed.add(SETTING_KEY_LINK_LOGIN_AFTER_PASSWORD_RESET);
		return needed;
	}

	public static LocalUsernamePasswordAuthProvider getProvider() {
		return (LocalUsernamePasswordAuthProvider) PlayAuthenticate
				.getProvider(UsernamePasswordAuthProvider.PROVIDER_KEY);
	}

	public static class NativeIdentity {

		public NativeIdentity() {
		}

		public NativeIdentity(final String email) {

//			this.username = username;
			this.email = email;
		}

		@Required
		@Email
		public String email;

//		@Required
//		public String username;

	}

	public static class NativeLogin extends NativeIdentity
			implements
			com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider.UsernamePassword {

		@Required
		@MinLength(6)
		public String password;

		@Override
		public String getEmail() {
			return email;
		}

		@Override
		public String getPassword() {
			return password;
		}
	}

	public static class NativeSignup extends NativeLogin {

		@Required
		@MinLength(6)
		public String repeatPassword;

		@Required
		public String first_name;

		@Required
		public String last_name;

		public String validate() {
			if (password == null || !password.equals(repeatPassword)) {
//				return Messages.get("playauthenticate.password.signup.error.passwords_not_same");
			}
			return null;
		}
	}

	public static final Form<NativeSignup> SIGNUP_FORM = form(NativeSignup.class);
	public static final Form<NativeLogin> LOGIN_FORM = form(NativeLogin.class);

	public LocalUsernamePasswordAuthProvider(Application app) {
		super(app);
	}

	protected Form<NativeSignup> getSignupForm() {
		return SIGNUP_FORM;
	}

	protected Form<NativeLogin> getLoginForm() {
		return LOGIN_FORM;
	}

	protected com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider.SignupResult signupUser(final LocalUsernamePasswordAuthUser user) {
		final Person u = Person.findByUsernamePasswordIdentity(user);
		if (u != null) {
			if (u.emailValidated) {
				// This user exists, has its email validated and is active
				return SignupResult.USER_EXISTS;
			} else {
				// this user exists, is active but has not yet validated its
				// email
				return SignupResult.USER_EXISTS_UNVERIFIED;
			}
		}
		// The user either does not exist or is inactive - create a new one
		@SuppressWarnings("unused")
		final Person newPerson = Person.create(user);
		// Usually the email should be verified before allowing login, however
		// if you return
		// return SignupResult.USER_CREATED;
		// then the user gets logged in directly
		return SignupResult.USER_CREATED_UNVERIFIED;
	}

	protected com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider.LoginResult loginUser(
			final LocalLoginUsernamePasswordAuthUser authUser) {
		final Person u = Person.findByUsernamePasswordIdentity(authUser);
		if (u == null) {
			return LoginResult.NOT_FOUND;
		} else {
			if (!u.emailValidated) {
				return LoginResult.USER_UNVERIFIED;
			} else {
				for (final LinkedAccount acc : u.linkedAccounts) {
					if (getKey().equals(acc.providerKey)) {
						if (authUser.checkPassword(acc.providerUserId,
								authUser.getPassword())) {
							// Password was correct
							return LoginResult.USER_LOGGED_IN;
						} else {
							// if you don't return here,
							// you would allow the user to have
							// multiple passwords defined
							// usually we don't want this
							return LoginResult.WRONG_PASSWORD;
						}
					}
				}
				return LoginResult.WRONG_PASSWORD;
			}
		}
	}

	protected Call userExists(final UsernamePasswordAuthUser authUser) {
		return routes.Signup.exists();
	}

	protected Call userUnverified(final UsernamePasswordAuthUser authUser) {
		return routes.Signup.unverified();
	}

	protected LocalUsernamePasswordAuthUser buildSignupAuthUser(
			final NativeSignup signup, final Context ctx) {
		return new LocalUsernamePasswordAuthUser(signup);
	}

	protected LocalLoginUsernamePasswordAuthUser buildLoginAuthUser(
			final NativeLogin login, final Context ctx) {
		return new LocalLoginUsernamePasswordAuthUser(login.getPassword(),
				login.getEmail());
	}


	protected LocalLoginUsernamePasswordAuthUser transformAuthUser(final LocalUsernamePasswordAuthUser authUser, final Context context) {
		return new LocalLoginUsernamePasswordAuthUser(authUser.getEmail());
	}

	protected String getVerifyEmailMailingSubject(
			final LocalUsernamePasswordAuthUser user, final Context ctx) {
		return Messages.get("[Verify your email] assisTU Web application");
	}

	protected String onLoginUserNotFound(final Context context) {
		context.flash()
				.put(controllers.Authentication.FLASH_ERROR_KEY,
						Messages.get("[unknown] assisTU Web application"));
		return super.onLoginUserNotFound(context);
	}

	protected Body getVerifyEmailMailingBody(final String token,
											 final LocalUsernamePasswordAuthUser user, final Context ctx) {

		final boolean isSecure = getConfiguration().getBoolean(
				SETTING_KEY_VERIFICATION_LINK_SECURE);
		final String url = routes.Signup.verify(token).absoluteURL(
				ctx.request(), isSecure);

		final Lang lang = Lang.preferred(ctx.request().acceptLanguages());
		final String langCode = lang.code();

		final String html = getEmailTemplate(
				"views.html.email.verify_email", langCode, url,
				token, user.getName(), user.getEmail());
		final String text = getEmailTemplate(
				"views.txt.email.verify_email", langCode, url,
				token, user.getName(), user.getEmail());

		return new Body(text, html);
	}

	private static String generateToken() {
		return UUID.randomUUID().toString();
	}

	protected String generateVerificationRecord(
			final LocalUsernamePasswordAuthUser user) {
		return generateVerificationRecord(Person.findByAuthUserIdentity(user));
	}

	protected String generateVerificationRecord(final Person person) {
		final String token = generateToken();
		// Do database actions, etc.
		TokenAction.create(Type.EMAIL_VERIFICATION, token, person);
		return token;
	}

	protected String generatePasswordResetRecord(final Person u) {
		final String token = generateToken();
		TokenAction.create(Type.PASSWORD_RESET, token, u);
		return token;
	}

	protected String getPasswordResetMailingSubject(final Person person,
													final Context ctx) {
		return Messages.get("[Reset your Password] assisTU Web application");
	}

	protected Body getPasswordResetMailingBody(final String token,
											   final Person person, final Context ctx) {

		final boolean isSecure = getConfiguration().getBoolean(
				SETTING_KEY_PASSWORD_RESET_LINK_SECURE);
		final String url = routes.Signup.resetPassword(token).absoluteURL(
				ctx.request(), isSecure);

		final Lang lang = Lang.preferred(ctx.request().acceptLanguages());
		final String langCode = lang.code();

		final String html = getEmailTemplate(
				"views.html.email.password_reset", langCode, url,
				token, person.name, person.email);
		final String text = getEmailTemplate(
				"views.txt.email.password_reset", langCode, url, token,
				person.name, person.email);

		return new Body(text, html);
	}

	public void sendPasswordResetMailing(final Person person, final Context ctx) {
		final String token = generatePasswordResetRecord(person);
		final String subject = getPasswordResetMailingSubject(person, ctx);
		final Body body = getPasswordResetMailingBody(token, person, ctx);
		//sendMail(subject, body, getEmailName(person));

	}

	public boolean isLoginAfterPasswordReset() {
		return getConfiguration().getBoolean(
				SETTING_KEY_LINK_LOGIN_AFTER_PASSWORD_RESET);
	}

	protected String getVerifyEmailMailingSubjectAfterSignup(final Person person,
															 final Context ctx) {
		return Messages.get("[Verify your email] assisTU Web application");
	}

	protected String getEmailTemplate(final String template,
									  final String langCode, final String url, final String token,
									  final String name, final String email) {
		Class<?> cls = null;
		String ret = null;
		try {
			cls = Class.forName(template + "_" + langCode);
		} catch (ClassNotFoundException e) {
			Logger.warn("Template: '"
					+ template
					+ "_"
					+ langCode
					+ "' was not found! Trying to use English fallback template instead.");
		}
		if (cls == null) {
			try {
				cls = Class.forName(template + "_"
						+ EMAIL_TEMPLATE_FALLBACK_LANGUAGE);
			} catch (ClassNotFoundException e) {
				Logger.error("Fallback template: '" + template + "_"
						+ EMAIL_TEMPLATE_FALLBACK_LANGUAGE
						+ "' was not found either!");
			}
		}
		if (cls != null) {
			Method htmlRender = null;
			try {
				htmlRender = cls.getMethod("render", String.class,
						String.class, String.class, String.class);
				ret = htmlRender.invoke(null, url, token, name, email)
						.toString();

			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	protected Body getVerifyEmailMailingBodyAfterSignup(final String token,
														final Person person, final Context ctx) {

		final boolean isSecure = getConfiguration().getBoolean(
				SETTING_KEY_VERIFICATION_LINK_SECURE);
		final String url = routes.Signup.verify(token).absoluteURL(
				ctx.request(), isSecure);

		final Lang lang = Lang.preferred(ctx.request().acceptLanguages());
		final String langCode = lang.code();

		final String html = getEmailTemplate(
				"views.html.email.verify_email", langCode, url, token,
				person.name, person.email);
		final String text = getEmailTemplate(
				"views.txt.email.verify_email", langCode, url, token,
				person.name, person.email);

		return new Body(text, html);
	}

	public void sendVerifyEmailMailingAfterSignup(final Person person,
												  final Context ctx) {

		final String subject = getVerifyEmailMailingSubjectAfterSignup(person,
				ctx);
		final String token = generateVerificationRecord(person);
		final Body body = getVerifyEmailMailingBodyAfterSignup(token, person, ctx);
		sendMail(subject, body, getEmailName(person));

	}

	private String getEmailName(final Person person) {
		return getEmailName(person.email, person.name);
	}
}
