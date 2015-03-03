package plugins.providers.mendeley;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.PersonData;
import models.MendeleyDocument;
import models.Person;
import play.data.*;
import play.mvc.*;
import plugins.com.feth.play.module.pa.PlayAuthenticate;
import plugins.com.feth.play.module.pa.providers.oauth2.BasicOAuth2AuthUser;
import plugins.com.feth.play.module.pa.providers.oauth2.OAuth2AuthInfo;
import plugins.com.feth.play.module.pa.user.FirstLastNameIdentity;
import plugins.com.feth.play.module.pa.user.PicturedIdentity;
import plugins.com.feth.play.module.pa.user.EmailIdentity;
import plugins.com.feth.play.module.pa.user.ProfiledIdentity;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arnaud on 18-12-14.
 */
public class MendeleyAuthUser extends BasicOAuth2AuthUser implements FirstLastNameIdentity, PicturedIdentity, EmailIdentity, ProfiledIdentity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * From:
     * https://www.deviantart.com/developers/http/v1/20141204/user_profile/0b06f6d6c8aa25b33b52f836e53f4f65
     *
     */
    private abstract class Constants {
        public static final String ID = "id";
        public static final String NAME = "display_name";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String EMAIL = "email";
        public static final String PROFILE_URL = "link";
        public static final String ACADEMIC_STATUS = "academic_status";
        public static final String DISCIPLINE = "discipline"; //nested "name"
        public static final String PROFILE_PIC = "photo"; //nested "standard" or "square"
        public static final String EMAIL_VERIFIED = "verified";
        public static final String DOCUMENTS = "documents";
    }

    private String id;
    private String name;
    private String first_name;
    private String last_name;
    private String email;
    private String profile_url;
    private String academic_status;
    private String discipline;
    private String profile_pic;
    private Boolean email_verified; //TODO: should not matter, safer is to reverify here too
    private JsonNode documents;

    public MendeleyAuthUser(final JsonNode node, final OAuth2AuthInfo info, final String state) {
        super(node.get(Constants.ID).asText(), info, state);

        Logger.debug("THIS IS IN THE NODE: " + node.toString());

        if (node.has(Constants.NAME)) {
            this.name = node.get(Constants.NAME).asText();
        }
        if (node.has(Constants.FIRST_NAME)) {
            this.first_name = node.get(Constants.FIRST_NAME).asText();
        }
        if (node.has(Constants.LAST_NAME)) {
            this.last_name = node.get(Constants.LAST_NAME).asText();
        }
        if (node.has(Constants.PROFILE_URL)) {
            this.profile_url = node.get(Constants.PROFILE_URL).asText();
        }
        if (node.has(Constants.EMAIL)) {
            this.email = node.get(Constants.EMAIL).asText();
        }
        if (node.has(Constants.ACADEMIC_STATUS)) {
            this.academic_status = node.get(Constants.ACADEMIC_STATUS).asText();
        }
        if (node.has(Constants.DISCIPLINE) && node.get(Constants.DISCIPLINE).has("name")) {
            this.discipline = node.get(Constants.DISCIPLINE).get("name").asText();
        }
        if (node.has(Constants.PROFILE_PIC) && node.get(Constants.PROFILE_PIC).has("square")) {
            this.profile_pic = node.get(Constants.PROFILE_PIC).get("square").asText();
        }
        if (node.has(Constants.EMAIL_VERIFIED)) {
            this.email_verified = node.get(Constants.EMAIL_VERIFIED).asBoolean();
        }
        if (node.has(Constants.DOCUMENTS)) {
            this.documents = node.get(Constants.DOCUMENTS);
        }
    }

    @Override
    public String getProvider() {
        return MendeleyAuthProvider.PROVIDER_KEY;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getProfileLink() {
        return profile_url;
    }

    public String getAcademicStatus() {
        return academic_status;
    }

    public String getDiscipline() {
        return discipline;
    }

    public Boolean getEmailVerified() {
        return email_verified;
    }

    public String getPicture() {
        return profile_pic;
    }

    public JsonNode getDocuments() {return documents;}

    @Override
    public String getEmail() {
        return email;
    }
}

