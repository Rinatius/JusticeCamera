package com.example.justicecamera;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.BackendlessDataQuery;

public class Violation
{
  private String comment;
  private java.util.Date updated;
  private String carModel;
  private String name;
  private String objectId;
  private java.util.Date created;
  private String lat;
  private String ownerId;
  private String information;
  private String videoUrl;
  private String longt;
  private String carNumber;
  private String carMake;
  private String color;
  private Category_id category;
  private BackendlessUser user_id;
  private City city;
  private VideoStatus videoStatus;
  public String getComment()
  {
    return comment;
  }

  public void setComment( String comment )
  {
    this.comment = comment;
  }

  public java.util.Date getUpdated()
  {
    return updated;
  }

  public String getCarModel()
  {
    return carModel;
  }

  public void setCarModel( String carModel )
  {
    this.carModel = carModel;
  }

  public String getName()
  {
    return name;
  }

  public void setName( String name )
  {
    this.name = name;
  }

  public String getObjectId()
  {
    return objectId;
  }

  public java.util.Date getCreated()
  {
    return created;
  }

  public String getLat()
  {
    return lat;
  }

  public void setLat( String lat )
  {
    this.lat = lat;
  }

  public String getOwnerId()
  {
    return ownerId;
  }

  public String getInformation()
  {
    return information;
  }

  public void setInformation( String information )
  {
    this.information = information;
  }

  public String getVideoUrl()
  {
    return videoUrl;
  }

  public void setVideoUrl( String videoUrl )
  {
    this.videoUrl = videoUrl;
  }

  public String getLongt()
  {
    return longt;
  }

  public void setLongt( String longt )
  {
    this.longt = longt;
  }

  public String getCarNumber()
  {
    return carNumber;
  }

  public void setCarNumber( String carNumber )
  {
    this.carNumber = carNumber;
  }

  public String getCarMake()
  {
    return carMake;
  }

  public void setCarMake( String carMake )
  {
    this.carMake = carMake;
  }

  public String getColor()
  {
    return color;
  }

  public void setColor( String color )
  {
    this.color = color;
  }

  public Category_id getCategory()
  {
    return category;
  }

  public void setCategory( Category_id category )
  {
    this.category = category;
  }

  public BackendlessUser getUser_id()
  {
    return user_id;
  }

  public void setUser_id( BackendlessUser user_id )
  {
    this.user_id = user_id;
  }

  public City getCity()
  {
    return city;
  }

  public void setCity( City city )
  {
    this.city = city;
  }

  public VideoStatus getVideoStatus()
  {
    return videoStatus;
  }

  public void setVideoStatus( VideoStatus videoStatus )
  {
    this.videoStatus = videoStatus;
  }

                                                    
  public Violation save()
  {
    return Backendless.Data.of( Violation.class ).save( this );
  }

  public Future<Violation> saveAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<Violation> future = new Future<Violation>();
      Backendless.Data.of( Violation.class ).save( this, future );

      return future;
    }
  }

  public void saveAsync( AsyncCallback<Violation> callback )
  {
    Backendless.Data.of( Violation.class ).save( this, callback );
  }

  public Long remove()
  {
    return Backendless.Data.of( Violation.class ).remove( this );
  }

  public Future<Long> removeAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<Long> future = new Future<Long>();
      Backendless.Data.of( Violation.class ).remove( this, future );

      return future;
    }
  }

  public void removeAsync( AsyncCallback<Long> callback )
  {
    Backendless.Data.of( Violation.class ).remove( this, callback );
  }

  public static Violation findById( String id )
  {
    return Backendless.Data.of( Violation.class ).findById( id );
  }

  public static Future<Violation> findByIdAsync( String id )
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<Violation> future = new Future<Violation>();
      Backendless.Data.of( Violation.class ).findById( id, future );

      return future;
    }
  }

  public static void findByIdAsync( String id, AsyncCallback<Violation> callback )
  {
    Backendless.Data.of( Violation.class ).findById( id, callback );
  }

  public static Violation findFirst()
  {
    return Backendless.Data.of( Violation.class ).findFirst();
  }

  public static Future<Violation> findFirstAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<Violation> future = new Future<Violation>();
      Backendless.Data.of( Violation.class ).findFirst( future );

      return future;
    }
  }

  public static void findFirstAsync( AsyncCallback<Violation> callback )
  {
    Backendless.Data.of( Violation.class ).findFirst( callback );
  }

  public static Violation findLast()
  {
    return Backendless.Data.of( Violation.class ).findLast();
  }

  public static Future<Violation> findLastAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<Violation> future = new Future<Violation>();
      Backendless.Data.of( Violation.class ).findLast( future );

      return future;
    }
  }

  public static void findLastAsync( AsyncCallback<Violation> callback )
  {
    Backendless.Data.of( Violation.class ).findLast( callback );
  }

  public static BackendlessCollection<Violation> find( BackendlessDataQuery query )
  {
    return Backendless.Data.of( Violation.class ).find( query );
  }

  public static Future<BackendlessCollection<Violation>> findAsync( BackendlessDataQuery query )
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<BackendlessCollection<Violation>> future = new Future<BackendlessCollection<Violation>>();
      Backendless.Data.of( Violation.class ).find( query, future );

      return future;
    }
  }

  public static void findAsync( BackendlessDataQuery query, AsyncCallback<BackendlessCollection<Violation>> callback )
  {
    Backendless.Data.of( Violation.class ).find( query, callback );
  }
}