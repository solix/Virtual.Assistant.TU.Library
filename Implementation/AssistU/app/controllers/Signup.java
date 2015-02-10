package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import models.TokenAction;
import models.TokenAction.Type;
import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import providers.localUsernamePassword.LocalUsernamePasswordAuthProvider.NativeIdentity;
import providers.localUsernamePassword.*;
import views.html.*;

import java.util.HashMap;

import static play.data.Form.form;

public class Signup extends Controller {

	public static Result doSignup() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<LocalUsernamePasswordAuthProvider.NativeSignup> filledForm = LocalUsernamePasswordAuthProvider.SIGNUP_FORM
				.bindFromRequest();
		if (filledForm.hasErrors() || !filledForm.get().password.equals(filledForm.get().repeatPassword)) {
			return badRequest(signup.render(filledForm, true, "danger", "The form contained errors, please make sure everything is filled in correctly"));
		} else {
			return com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider.handleSignup(ctx());
		}
	}

	public static Result unverified() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		return ok(login.render(LocalUsernamePasswordAuthProvider.LOGIN_FORM, true, "info", "Please verify your email before continuing"));
	}

	public static class PasswordReset extends Account.PasswordChange {

		public PasswordReset() {
		}

		public PasswordReset(final String token) {
			this.token = token;
		}

		public String token;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}
	}

	private static final Form<PasswordReset> PASSWORD_RESET_FORM = form(PasswordReset.class);

	private static final Form<NativeIdentity> FORGOT_PASSWORD_FORM = form(NativeIdentity.class);

	public static Result forgotPassword() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		Form<NativeIdentity> form = FORGOT_PASSWORD_FORM;
		return ok(forgotPassword.render(form, false, "", ""));
	}

	public static Result doForgotPassword() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<NativeIdentity> filledForm = FORGOT_PASSWORD_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not fill in his/her email
			return badRequest(forgotPassword.render(filledForm, true, "danger", "You did not provide a valid email address"));
		} else {
			// The email address given *BY AN UNKNWON PERSON* to the form - we
			// should find out if we actually have a user with this email
			// address and whether password signup is enabled for him/her. Also
			// only send if the email address of the user has been verified.
			final String email = filledForm.get().email;

			// We don't want to expose whether a given email address is signed
			// up, so just say an email has been sent, even though it might not
			// be true - that's protecting our user privacy.
//			flash(Authentication.FLASH_MESSAGE_KEY,
//					Messages.get(
//							"playauthenticate.reset_password.message.instructions_sent",
//							email));

			final User user = User.findByEmail(email);
			if (user != null) {
				// yep, we have a user with this email that is active - we do
				// not know if the user owning that account has requested this
				// reset, though.
				final LocalUsernamePasswordAuthProvider provider = LocalUsernamePasswordAuthProvider
						.getProvider();
				// User exists
				if (user.emailValidated) {
					provider.sendPasswordResetMailing(user, ctx());
					// In case you actually want to let (the unknown person)
					// know whether a user was found/an email was sent, use,
					// change the flash message
				} else {
					// We need to change the message here, otherwise the user
					// does not understand whats going on - we should not verify
					// with the password reset, as a "bad" user could then sign
					// up with a fake email via OAuth and get it verified by an
					// a unsuspecting user that clicks the link.
					flash(Authentication.FLASH_MESSAGE_KEY,
							Messages.get("playauthenticate.reset_password.message.email_not_verified"));

					// You might want to re-send the verification email here...
					provider.sendVerifyEmailMailingAfterSignup(user, ctx());
				}
			}

			return ok(login.render(LocalUsernamePasswordAuthProvider.LOGIN_FORM, true, "success", "A link has been sent to " + email + " to reset your password"));
		}
	}

	/**
	 * Returns a token object if valid, null if not
	 * 
	 * @param token
	 * @param type
	 * @return
	 */
	private static TokenAction tokenIsValid(final String token, final Type type) {
		TokenAction ret = null;
		if (token != null && !token.trim().isEmpty()) {
			final TokenAction ta = TokenAction.findByToken(token, type);
			if (ta != null && ta.isValid()) {
				ret = ta;
			}
		}
		return ret;
	}

	public static Result resetPassword(final String token) {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final TokenAction ta = tokenIsValid(token, Type.PASSWORD_RESET);
		if (ta == null) {
			return badRequest(forgotPassword.render(FORGOT_PASSWORD_FORM, true, "danger", "Your link was no longer valid, please try again"));
		}
		Form<PasswordReset> new_form = PASSWORD_RESET_FORM;
		new_form.fill(new PasswordReset(token));
		new_form.data().put("token", token);
		Logger.debug("RESET PASSWORD FORM FIRST: " + new_form);
		return ok(resetPassword.render(new_form, false, "", ""));
	}

	public static Result doResetPassword() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<PasswordReset> filledForm = PASSWORD_RESET_FORM.bindFromRequest();
		Logger.debug("RESET PASSWORD FORM AFTER: " + filledForm);
		if (filledForm.hasErrors()  || !filledForm.get().password.equals(filledForm.get().repeatPassword)) {
			return badRequest(resetPassword.render(filledForm, true, "danger", "You did not provide a valid password"));
		} else {
			final String newPassword = filledForm.get().password;
			final String token = filledForm.get().token;
			final TokenAction ta = tokenIsValid(token, Type.PASSWORD_RESET);
			if (ta == null) {
				return badRequest(forgotPassword.render(FORGOT_PASSWORD_FORM, true, "danger", "Your link was no longer valid, please try again"));
			}
			final User u = ta.targetUser;
			try {
				// Pass true for the second parameter if you want to
				// automatically create a password and the exception never to
				// happen
				u.resetPassword(new LocalUsernamePasswordAuthUser(newPassword), false);
			} catch (final RuntimeException re) {
//				flash(Authentication.FLASH_MESSAGE_KEY,
//						Messages.get("playauthenticate.reset_password.message.no_password_account"));
			}
			final boolean dologin = LocalUsernamePasswordAuthProvider.getProvider()
					.isLoginAfterPasswordReset();
			if (dologin) {
				// automatically log in
//				flash(Authentication.FLASH_MESSAGE_KEY,
//						Messages.get("playauthenticate.reset_password.message.success.auto_login"));

				return PlayAuthenticate.loginAndRedirect(ctx(),
						new LocalLoginUsernamePasswordAuthUser(u.email));
			} else {
				// send the user to the signup page
//				flash(Authentication.FLASH_MESSAGE_KEY,
//						Messages.get("playauthenticate.reset_password.message.success.manual_login"));
			}
			return ok(login.render(LocalUsernamePasswordAuthProvider.LOGIN_FORM, true, "success", "Your password has been successfully reset"));
		}
	}

	public static Result oAuthDenied(final String getProviderKey) {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		return ok(login.render(LocalUsernamePasswordAuthProvider.LOGIN_FORM, true, "danger", "Could not log you in with " + getProviderKey));
	}

	public static Result exists() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		return ok(testpage.render("existing user?"));
	}

	/**
	 * This function checks for the validity of a verification link
	 * @param token
	 * @return
	 */
	public static Result verify(final String token) {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final TokenAction ta = tokenIsValid(token, Type.EMAIL_VERIFICATION);
		if (ta == null) {
//			return badRequest("Bad token or something");
//			return ok(home.render("Invalid Link", null, true, "danger", "The verification link you just followed is no longer valid."));
			return TODO;
		}
		final String email = ta.targetUser.email;
		User.verify(ta.targetUser);
		flash(Authentication.FLASH_MESSAGE_KEY,
				Messages.get("playauthenticate.verify_email.success", email));
		if (Authentication.getLocalUser(session()) != null) {
			User user = Authentication.getLocalUser(session());
			if(user != null)
				Authentication.OAuthLogout();
//			return ok(index.render("Welcome " + user.name, user));
		}
		return ok(login.render(LocalUsernamePasswordAuthProvider.LOGIN_FORM, true, "success", "You have been verified, please log in below"));
	}
}
