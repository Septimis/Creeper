/* PersonAddress CLASS
 * CREATED BY CONNOR MEADS
 *
 * CLASS IS AS FOLLOWS:
 *
 * PersonAddress() //default constructor used to initialize data
 * PersonAddress(String l_streetAddress, String l_city, String l_zipcode, String l_state, String l_type) //overloaded constructor
 * @Override public String toString() //method used to place this class' information in a String format
 *
 * getters & setters
 *
 * END OF CLASS FILE
 */
package com.company;

public class PersonAddress
{
    PersonAddress()
    {
        this.streetAddress = "empty";
        this.city = "empty";
        this.zipcode = "empty";
        this.state = "empty";
        this.type = "empty";
    }

    PersonAddress(String l_streetAddress, String l_city, String l_zipcode, String l_state, String l_type)
    {
        this.streetAddress = l_streetAddress;
        this.city = l_city;
        this.zipcode = l_zipcode;
        this.state = l_state;
        this.type = l_type;
    }

    @Override public String toString()
    {
        return "Address: " + this.streetAddress
                + "\nCity: " + this.city
                + "\nZipcode: " + this.zipcode
                + "\nState: " + this.state
                + "\nType: " + this.type;
    }

    //Class Variables
    private String streetAddress, city, zipcode, state, type;

    public String getStreetAddress() {return this.streetAddress;}
    public void setStreetAddress(String l_streetAddress){this.streetAddress = l_streetAddress;}

    public String getCity() {return this.city;}
    public void setCity(String l_city) {this.city = l_city;}

    public String getZipcode() {return this.zipcode;}
    public void setZipcode(String l_zipcode) {this.zipcode = l_zipcode;}

    public String getState() {return this.state;}
    public void setState(String l_state) {this.state = l_state;}

    public String getType() {return this.type;}
    public void setType(String l_type) {this.type = l_type;}

    public void setPersonAddress(PersonAddress l_newUserAddress)
    {
        this.streetAddress = l_newUserAddress.getStreetAddress();
        this.city = l_newUserAddress.getCity();
        this.zipcode = l_newUserAddress.getZipcode();
        this.state = l_newUserAddress.getState();
        this.type = l_newUserAddress.getType();
    }
}
