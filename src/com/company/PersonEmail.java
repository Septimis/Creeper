/* PersonEmail CLASS
 * CREATED BY CONNOR MEADS
 *
 * CLASS IS AS FOLLOWS:
 *
 * PersonEmail() //default constructor used to initialize data
 * PersonEmail(String l_emailAddress, String l_type) //overloaded constructor
 * @Override public String toString() //method used to place this class' information in a String format
 *
 * getters & setters
 *
 * END OF CLASS FILE
 */
package com.company;

public class PersonEmail
{
    //Constructors
    PersonEmail()
    {
        this.emailAddress = "empty";
        this.type = "emtpy";
    }

    PersonEmail(String l_emailAddress, String l_type)
    {
        this.emailAddress = l_emailAddress;
        this.type = l_type;
    }

    @Override public String toString()
    {
        return "Email Address: " + this.emailAddress
                + "\nType: " + this.type;
    }

    //Class Variables
    private String emailAddress, type;

    //Getters & Setters
    public String getEmailAddress() {return this.emailAddress;}
    public void setEmailAddress(String l_emailAddress) {this.emailAddress = l_emailAddress;}

    public String getType() {return this.type;}
    public void setType(String l_type) {this.type = l_type;}

    public void setPersonEmail(PersonEmail l_newEmail)
    {
        this.emailAddress = l_newEmail.getEmailAddress();
        this.type = l_newEmail.getType();
    }
}
