package com.example.justicecamera;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.BackendlessDataQuery;

public class City
{
  private String ownerId;
  private String name;
  private java.util.Date updated;
  private java.util.Date created;
  private String objectId;
  public String getOwnerId()
  {
    return ownerId;
  }

  public String getName()
  {
    return name;
  }

  public void setName( String name )
  {
    this.name = name;
  }

  public java.util.Date getUpdated()
  {
    return updated;
  }

  public java.util.Date getCreated()
  {
    return created;
  }

  public String getObjectId()
  {
    return objectId;
  }

                                                    
  public City save()
  {
    return Backendless.Data.of( City.class ).save( this );
  }

  public Future<City> saveAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<City> future = new Future<City>();
      Backendless.Data.of( City.class ).save( this, future );

      return future;
    }
  }

  public void saveAsync( AsyncCallback<City> callback )
  {
    Backendless.Data.of( City.class ).save( this, callback );
  }

  public Long remove()
  {
    return Backendless.Data.of( City.class ).remove( this );
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
      Backendless.Data.of( City.class ).remove( this, future );

      return future;
    }
  }

  public void removeAsync( AsyncCallback<Long> callback )
  {
    Backendless.Data.of( City.class ).remove( this, callback );
  }

  public static City findById( String id )
  {
    return Backendless.Data.of( City.class ).findById( id );
  }

  public static Future<City> findByIdAsync( String id )
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<City> future = new Future<City>();
      Backendless.Data.of( City.class ).findById( id, future );

      return future;
    }
  }

  public static void findByIdAsync( String id, AsyncCallback<City> callback )
  {
    Backendless.Data.of( City.class ).findById( id, callback );
  }

  public static City findFirst()
  {
    return Backendless.Data.of( City.class ).findFirst();
  }

  public static Future<City> findFirstAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<City> future = new Future<City>();
      Backendless.Data.of( City.class ).findFirst( future );

      return future;
    }
  }

  public static void findFirstAsync( AsyncCallback<City> callback )
  {
    Backendless.Data.of( City.class ).findFirst( callback );
  }

  public static City findLast()
  {
    return Backendless.Data.of( City.class ).findLast();
  }

  public static Future<City> findLastAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<City> future = new Future<City>();
      Backendless.Data.of( City.class ).findLast( future );

      return future;
    }
  }

  public static void findLastAsync( AsyncCallback<City> callback )
  {
    Backendless.Data.of( City.class ).findLast( callback );
  }

  public static BackendlessCollection<City> find( BackendlessDataQuery query )
  {
    return Backendless.Data.of( City.class ).find( query );
  }

  public static Future<BackendlessCollection<City>> findAsync( BackendlessDataQuery query )
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<BackendlessCollection<City>> future = new Future<BackendlessCollection<City>>();
      Backendless.Data.of( City.class ).find( query, future );

      return future;
    }
  }

  public static void findAsync( BackendlessDataQuery query, AsyncCallback<BackendlessCollection<City>> callback )
  {
    Backendless.Data.of( City.class ).find( query, callback );
  }
}