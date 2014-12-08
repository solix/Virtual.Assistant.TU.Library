package models;

public class Profile{

    public String titles;
    public String firstname;
    public String lastname;
    public String institution;
    public String email;
    public String avatar;

    public Profile(String titles,
                  String firstname,
                  String lastname,
                  String institution,
                  String email,
                  String avatar){
        this.titles = titles;
        this.firstname = firstname;
        this.lastname = lastname;
        this.institution = institution;
        this.email = email;
        this.avatar = avatar;
    }




}