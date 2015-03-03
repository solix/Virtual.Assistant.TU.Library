package controllers;

import plugins.com.feth.play.module.pa.controllers.Authenticate;
import plugins.com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import plugins.com.feth.play.module.pa.PlayAuthenticate;
import models.TokenAction;
import models.TokenAction.Type;
import models.Person;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import plugins.providers.localUsernamePassword.LocalUsernamePasswordAuthProvider.NativeIdentity;
import plugins.providers.localUsernamePassword.*;
import views.html.*;

import static play.data.Form.form;

public class Signup extends Controller {

	/**
	 * This function catches the form from the signup page and handles it
	 * @return Result
	 */
	public static Result doSignup() {
		Authenticate.noCache(response());
		final Form<LocalUsernamePasswordAuthProvider.NativeSignup> filledForm = LocalUsernamePasswordAuthProvider.SIGNUP_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(signup.render(filledForm, true, "danger", "The form contained errors, please make sure everything is filled in correctly"));
		} else if (!filledForm.get().password.equals(filledForm.get().repeatPassword)) {
			return badRequest(signup.render(filledForm, true, "danger", "Your passwords were not the same"));
		} else if (!Application.allowedNameRegex(filledForm.get().first_name) || !Application.allowedNameRegex(filledForm.get().last_name)){
			return badRequest(signup.render(filledForm, true, "danger", "Your names were invalid. Only letters separated with '-'s are allowed"));
		} else if (filledForm.get().first_name.length() + filledForm.get().last_name.length() > 70) {
			return badRequest(signup.render(filledForm, true, "danger", "The length of the combination of your names exceeded our limit"));
		} else if (Person.find.where().eq("emailValidated", true).eq("email", filledForm.get().email).findUnique() != null){
			String provider = Person.findByEmail(filledForm.get().email).linkedAccounts.get(0).providerKey;
			return badRequest(login.render(LocalUsernamePasswordAuthProvider.LOGIN_FORM, true, "danger", "You already have a " + provider + " account"));
		} else {
			return UsernamePasswordAuthProvider.handleSignup(ctx());
		}
	}

	/**
	 * This function shows the login page with the alert that the user needs to verify himself
	 * @return Result
	 */
	public static Result unverified() {
		Authenticate.noCache(response());
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

	/**
	 * Render the forgotpassword page
	 * @return Result
	 */
	public static Result forgotPassword() {
		Authenticate.noCache(response());
		Form<NativeIdentity> form = FORGOT_PASSWORD_FORM;
		return ok(forgotPassword.render(form, false, "", ""));
	}

	/**
	 * Catches the forgotpassword form and handles it
	 * @return Result
	 */
	public static Result doForgotPassword() {
		Authenticate.noCache(response());
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
			final Person person = Person.findByEmail(email);
			if (person != null) {
				// yep, we have a user with this email that is active - we do
				// not know if the user owning that account has requested this
				// reset, though.
				final LocalUsernamePasswordAuthProvider provider = LocalUsernamePasswordAuthProvider
						.getProvider();
				// User exists
				if (person.emailValidated) {
					provider.sendPasswordResetMailing(person, ctx());
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
					provider.sendVerifyEmailMailingAfterSignup(person, ctx());
				}
			}

			return ok(login.render(LocalUsernamePasswordAuthProvider.LOGIN_FORM, true, "success", "A link has been sent to " + email + " to reset your password"));
		}
	}

	/**
	 * Returns a token object if valid, null if not
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

	/**
	 * Renders the resetpassword page
	 * @param token: The token needed
	 * @return Result
	 */
	public static Result resetPassword(final String token) {
		Authenticate.noCache(response());
		final TokenAction ta = tokenIsValid(token, Type.PASSWORD_RESET);
		if (ta == null) {
			return badRequest(forgotPassword.render(FORGOT_PASSWORD_FORM, true, "danger", "Your link was no longer valid, please try again"));
		}
		Form<PasswordReset> new_form = PASSWORD_RESET_FORM;
		new_form.fill(new PasswordReset(token));
		new_form.data().put("token", token);
		return ok(resetPassword.render(new_form, false, "", ""));
	}

	/**
	 * Handles the form caught from the resetpasswordpage
	 * @return Result
	 */
	public static Result doResetPassword() {
		Authenticate.noCache(response());
		final Form<PasswordReset> filledForm = PASSWORD_RESET_FORM.bindFromRequest();
		if (filledForm.hasErrors()  || !filledForm.get().password.equals(filledForm.get().repeatPassword)) {
			return badRequest(resetPassword.render(filledForm, true, "danger", "You did not provide a valid password"));
		} else {
			final String newPassword = filledForm.get().password;
			final String token = filledForm.get().token;
			final TokenAction ta = tokenIsValid(token, Type.PASSWORD_RESET);
			if (ta == null) {
				return badRequest(forgotPassword.render(FORGOT_PASSWORD_FORM, true, "danger", "Your link was no longer valid, please try again"));
			}
			final Person u = ta.targetPerson;
			try {
				// Pass true for the second parameter if you want to
				// automatically create a password and the exception never to
				// happen
				u.resetPassword(new LocalUsernamePasswordAuthUser(newPassword), false);
			} catch (final RuntimeException re) {

			}
			final boolean dologin = LocalUsernamePasswordAuthProvider.getProvider()
					.isLoginAfterPasswordReset();
			if (dologin) {
				// automatically log in
				return PlayAuthenticate.loginAndRedirect(ctx(),
						new LocalLoginUsernamePasswordAuthUser(u.email));
			} else {
				// send the user to the signup page
			}
			return ok(login.render(LocalUsernamePasswordAuthProvider.LOGIN_FORM, true, "success", "Your password has been successfully reset"));
		}
	}

	/**
	 * This function shows the login page with the alert that OAuth has been denied
	 * @param getProviderKey: The string of the type of OAuth
	 * @return Result
	 */
	public static Result oAuthDenied(final String getProviderKey) {
		Authenticate.noCache(response());
		return ok(login.render(LocalUsernamePasswordAuthProvider.LOGIN_FORM, true, "danger", "Could not log you in with " + getProviderKey));
	}

	/**
	 * This function renders the login page with the alert that the email address is already assigned to an existing user
	 * @return Result
	 */
	public static Result exists() {
		Authenticate.noCache(response());
		return ok(login.render(LocalUsernamePasswordAuthProvider.LOGIN_FORM, true, "danger", "There is already a user signed up with this email"));
	}

	/**
	 * This function checks for the validity of a verification link
	 * @param token: The token needed
	 * @return Result
	 */
	public static Result verify(final String token) {
		Authenticate.noCache(response());
		final TokenAction ta = tokenIsValid(token, Type.EMAIL_VERIFICATION);
		if (ta == null) {
			return ok(login.render(LocalUsernamePasswordAuthProvider.LOGIN_FORM, true, "danger", "The link you followed is not valid"));
		}
		final String email = ta.targetPerson.email;
		Person.verify(ta.targetPerson);
		flash(Authentication.FLASH_MESSAGE_KEY,
				Messages.get("playauthenticate.verify_email.success", email));
		if (Authentication.getLocalUser(session()) != null) {
			Person person = Authentication.getLocalUser(session());
			if(person != null)
				Authentication.OAuthLogout();
		}
		return ok(login.render(LocalUsernamePasswordAuthProvider.LOGIN_FORM, true, "success", "You have been verified, please log in below"));

	}
}
