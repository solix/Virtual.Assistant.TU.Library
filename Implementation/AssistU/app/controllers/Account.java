package controllers;

import play.data.Form;
import play.data.format.Formats.NonEmpty;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.i18n.Messages;
import play.mvc.Controller;

import static play.data.Form.form;

/**
 * This class is the controller for all account-related actions.
 * Most functions in this class are commented as they were part
 * of a sample provided by the authentication plugin, can still be
 * used in future releases, but have no functionality in this version.
 */
public class Account extends Controller {

	public static class Accept {

		@Required
		@NonEmpty
		public Boolean accept;

		public Boolean getAccept() {
			return accept;
		}

		public void setAccept(Boolean accept) {
			this.accept = accept;
		}

	}

	public static class PasswordChange {
		@MinLength(6)
		@Required
		public String password;

		@MinLength(6)
		@Required
		public String repeatPassword;

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getRepeatPassword() {
			return repeatPassword;
		}

		public void setRepeatPassword(String repeatPassword) {
			this.repeatPassword = repeatPassword;
		}

		public String validate() {
			if (password == null || !password.equals(repeatPassword)) {
				return Messages
						.get("playauthenticate.change_password.error.passwords_not_same");
			}
			return null;
		}
	}

	private static final Form<Accept> ACCEPT_FORM = form(Accept.class);
	private static final Form<PasswordChange> PASSWORD_CHANGE_FORM = form(Account.PasswordChange.class);

//	@SubjectPresent
//	public static Result link() {
//		Authenticate.noCache(response());
////		return ok(link.render());
//		return TODO;
//	}

//	@Restrict(@Group(Authentication.USER_ROLE))
//	public static Result verifyEmail() {
//		Authenticate.noCache(response());
//		final Person person = Authentication.getLocalUser(session());
//		if (person.emailValidated) {
//			// E-Mail has been validated already
//			flash(Authentication.FLASH_MESSAGE_KEY,
//					Messages.get("playauthenticate.verify_email.error.already_validated"));
//		} else if (person.email != null && !person.email.trim().isEmpty()) {
//			flash(Authentication.FLASH_MESSAGE_KEY, Messages.get(
//					"playauthenticate.verify_email.message.instructions_sent",
//					person.email));
//			LocalUsernamePasswordAuthProvider.getProvider()
//					.sendVerifyEmailMailingAfterSignup(person, ctx());
//		} else {
//			flash(Authentication.FLASH_MESSAGE_KEY, Messages.get(
//					"playauthenticate.verify_email.error.set_email_first",
//					person.email));
//		}
////		return redirect(routes.Application.profile());
//	return TODO;
//	}

//	@Restrict(@Group(Authentication.USER_ROLE))
//	public static Result changePassword() {
//		Authenticate.noCache(response());
//		final Person u = Authentication.getLocalUser(session());
//
//		if (!u.emailValidated) {
////			return ok(unverified.render());
//		return TODO;
//		} else {
////			return ok(password_change.render(PASSWORD_CHANGE_FORM));
//		return TODO;
//		}
//	}

//	@Restrict(@Group(Authentication.USER_ROLE))
//	public static Result doChangePassword() {
//		Authenticate.noCache(response());
//		final Form<PasswordChange> filledForm = PASSWORD_CHANGE_FORM
//				.bindFromRequest();
//		if (filledForm.hasErrors()) {
//			// User did not select whether to link or not link
////			return badRequest(password_change.render(filledForm));
//		return TODO;
//		} else {
//			final Person person = Authentication.getLocalUser(session());
//			final String newPassword = filledForm.get().password;
//			person.changePassword(new LocalUsernamePasswordAuthUser(newPassword),
//					true);
//			flash(Authentication.FLASH_MESSAGE_KEY,
//					Messages.get("playauthenticate.change_password.success"));
////			return redirect(routes.Application.profile());
//		return TODO;
//		}
//	}

//	@SubjectPresent
//	public static Result askLink() {
//		Authenticate.noCache(response());
//		final AuthUser u = PlayAuthenticate.getLinkUser(session());
//		if (u == null) {
//			// account to link could not be found, silently redirect to signup
////			return redirect(routes.Application.index());
//			return TODO;
//		}
////		return ok(ask_link.render(ACCEPT_FORM, u));
//		return TODO;
//	}

//	@SubjectPresent
//	public static Result doLink() {
//		Authenticate.noCache(response());
//		final AuthUser u = PlayAuthenticate.getLinkUser(session());
//		if (u == null) {
//			// account to link could not be found, silently redirect to signup
////			return redirect(routes.Application.index());
//			return TODO;
//		}
//
//		final Form<Accept> filledForm = ACCEPT_FORM.bindFromRequest();
//		if (filledForm.hasErrors()) {
//			// User did not select whether to link or not link
////			return badRequest(ask_link.render(filledForm, u));
//			return TODO;
//		} else {
//			// User made a choice :)
//			final boolean link = filledForm.get().accept;
//			if (link) {
//				flash(Authentication.FLASH_MESSAGE_KEY,
//						Messages.get("playauthenticate.accounts.link.success"));
//			}
//			return PlayAuthenticate.link(ctx(), link);
//		}
//	}

//	@SubjectPresent
//	public static Result askMerge() {
//		Authenticate.noCache(response());
//		// this is the currently logged in user
//		final AuthUser aUser = PlayAuthenticate.getUser(session());
//
//		// this is the user that was selected for a signup
//		final AuthUser bUser = PlayAuthenticate.getMergeUser(session());
//		if (bUser == null) {
//			// user to merge with could not be found, silently redirect to signup
////			return redirect(routes.Application.index());
//			return TODO;
//		}
//
//		// You could also get the local user object here via
//		// User.findByAuthUserIdentity(newUser)
////		return ok(ask_merge.render(ACCEPT_FORM, aUser, bUser));
//		return TODO;
//	}

//	@SubjectPresent
//	public static Result doMerge() {
//		Authenticate.noCache(response());
//		// this is the currently logged in user
//		final AuthUser aUser = PlayAuthenticate.getUser(session());
//
//		// this is the user that was selected for a signup
//		final AuthUser bUser = PlayAuthenticate.getMergeUser(session());
//		if (bUser == null) {
//			// user to merge with could not be found, silently redirect to signup
////			return redirect(routes.Application.index());
//			return TODO;
//		}
//
//		final Form<Accept> filledForm = ACCEPT_FORM.bindFromRequest();
//		if (filledForm.hasErrors()) {
//			// User did not select whether to merge or not merge
////			return badRequest(ask_merge.render(filledForm, aUser, bUser));
//			return TODO;
//		} else {
//			// User made a choice :)
//			final boolean merge = filledForm.get().accept;
//			if (merge) {
//				flash(Authentication.FLASH_MESSAGE_KEY,
//						Messages.get("playauthenticate.accounts.merge.success"));
//			}
//			return PlayAuthenticate.merge(ctx(), merge);
//		}
//	}

}
