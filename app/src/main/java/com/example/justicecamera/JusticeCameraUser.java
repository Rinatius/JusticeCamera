package com.example.justicecamera;

import com.backendless.BackendlessUser;

public class JusticeCameraUser extends BackendlessUser
{
  public String getEmail()
  {
    return super.getEmail();
  }

  public void setEmail( String email )
  {
    super.setEmail( email );
  }

  public String getPassword()
  {
    return super.getPassword();
  }

  public String getCarNumber()
  {
    return (String) super.getProperty( "carNumber" );
  }

  public void setCarNumber( String carNumber )
  {
    super.setProperty( "carNumber", carNumber );
  }

  public Integer getDayBirthday()
  {
    return (Integer) super.getProperty( "dayBirthday" );
  }

  public void setDayBirthday( Integer dayBirthday )
  {
    super.setProperty( "dayBirthday", dayBirthday );
  }

  public String getFirstName()
  {
    return (String) super.getProperty( "firstName" );
  }

  public void setFirstName( String firstName )
  {
    super.setProperty( "firstName", firstName );
  }

  public String getMiddleName() {
    return (String) super.getProperty("middleName");
  }
  public void setMiddleName (String middleName) {
    super.setProperty("middleName", middleName);
  }

  public String getLastName()
  {
    return (String) super.getProperty( "lastName" );
  }

  public void setLastName( String lastName )
  {
    super.setProperty( "lastName", lastName );
  }

  public Integer getMonthBirthday()
  {
    return (Integer) super.getProperty( "monthBirthday" );
  }

  public void setMonthBirthday( Integer monthBirthday )
  {
    super.setProperty( "monthBirthday", monthBirthday );
  }

  public String getOfferVersion()
  {
    return (String) super.getProperty( "offerVersion" );
  }

  public void setOfferVersion( String offerVersion )
  {
    super.setProperty( "offerVersion", offerVersion );
  }

  public String getPassportNo()
  {
    return (String) super.getProperty( "passportNo" );
  }

  public void setPassportNo( String passportNo )
  {
    super.setProperty( "passportNo", passportNo );
  }

  public String getPhoneNumber()
  {
    return (String) super.getProperty( "phoneNumber" );
  }

  public void setPhoneNumber( String phoneNumber )
  {
    super.setProperty( "phoneNumber", phoneNumber );
  }

  public String getPhotoUrl()
  {
    return (String) super.getProperty( "photoUrl" );
  }

  public void setPhotoUrl( String photoUrl )
  {
    super.setProperty( "photoUrl", photoUrl );
  }

  public Integer getRating()
  {
    return (Integer) super.getProperty( "rating" );
  }

  public void setRating( Integer rating )
  {
    super.setProperty( "rating", rating );
  }

  public Boolean getSex()
  {
    return (Boolean) super.getProperty( "sex" );
  }

  public void setSex( Boolean sex )
  {
    super.setProperty( "sex", sex );
  }

  public String getStatus()
  {
    return (String) super.getProperty( "status" );
  }

  public void setStatus( String status )
  {
    super.setProperty( "status", status );
  }

  public Integer getYearBirhday()
  {
    return (Integer) super.getProperty( "yearBirhday" );
  }

  public void setYearBirhday( Integer yearBirhday )
  {
    super.setProperty( "yearBirhday", yearBirhday );
  }
}