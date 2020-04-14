/* PersonPhoneNumber CLASS
 * CREATED BY CONNOR MEADS
 *
 * CLASS IS AS FOLLOWS:
 *
 * PersonPhoneNumber() //default constructor used to initialize data
 * PersonPhoneNumber(String l_phoneNumber, String l_type) //overloaded constructor
 * @Override public String toString() //method used to place this class' information in a String format
 *
 * getters & setters
 *
 * END OF CLASS FILE
 */
package com.company;

public class PersonPhoneNumber
{
    PersonPhoneNumber()
    {
        this.phoneNumber = "empty";
        this.type = "empty";
    }

    PersonPhoneNumber(String l_phoneNumber, String l_type)
    {
        this.phoneNumber = l_phoneNumber;
        this.type = l_type;
    }

    @Override public String toString()
    {
        return "Phone Number: " + this.phoneNumber
                + "\nType: " + this.type;
    }

    private String phoneNumber, type;

    //Getters & Setters
    public String getPhoneNumber() {return this.phoneNumber;}
    public void setPhoneNumber(String l_phoneNumber) {this.phoneNumber = l_phoneNumber;}

    public String getType() {return this.type;}
    public void setType(String l_type) {this.type = l_type;}

    public void setPersonPhoneNumber(PersonPhoneNumber l_newPhoneNumber)
    {
        this.phoneNumber = l_newPhoneNumber.getPhoneNumber();
        this.type = l_newPhoneNumber.getType();
    }
}
