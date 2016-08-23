package com.example.justicecamera;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.BackendlessDataQuery;

public class Offerta
{
  private java.util.Date updated;
  private String objectId;
  private String ownerId;
  private java.util.Date created;
  private String text;
  private String name;
  public java.util.Date getUpdated()
  {
    return updated;
  }

  public String getObjectId()
  {
    return objectId;
  }

  public String getOwnerId()
  {
    return ownerId;
  }

  public java.util.Date getCreated()
  {
    return created;
  }

  public String getText()
  {
    return text;
  }

  public void setText( String text )
  {
    this.text = text;
  }

  public String getName()
  {
    return name;
  }

  public void setName( String name )
  {
    this.name = name;
  }

                                                    
  public Offerta save()
  {
    return Backendless.Data.of( Offerta.class ).save( this );
  }

  public Future<Offerta> saveAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<Offerta> future = new Future<Offerta>();
      Backendless.Data.of( Offerta.class ).save( this, future );

      return future;
    }
  }

  public void saveAsync( AsyncCallback<Offerta> callback )
  {
    Backendless.Data.of( Offerta.class ).save( this, callback );
  }

  public Long remove()
  {
    return Backendless.Data.of( Offerta.class ).remove( this );
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
      Backendless.Data.of( Offerta.class ).remove( this, future );

      return future;
    }
  }

  public void removeAsync( AsyncCallback<Long> callback )
  {
    Backendless.Data.of( Offerta.class ).remove( this, callback );
  }

  public static Offerta findById( String id )
  {
    return Backendless.Data.of( Offerta.class ).findById( id );
  }

  public static Future<Offerta> findByIdAsync( String id )
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<Offerta> future = new Future<Offerta>();
      Backendless.Data.of( Offerta.class ).findById( id, future );

      return future;
    }
  }

  public static void findByIdAsync( String id, AsyncCallback<Offerta> callback )
  {
    Backendless.Data.of( Offerta.class ).findById( id, callback );
  }

  public static Offerta findFirst()
  {
    return Backendless.Data.of( Offerta.class ).findFirst();
  }

  public static Future<Offerta> findFirstAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<Offerta> future = new Future<Offerta>();
      Backendless.Data.of( Offerta.class ).findFirst( future );

      return future;
    }
  }

  public static void findFirstAsync( AsyncCallback<Offerta> callback )
  {
    Backendless.Data.of( Offerta.class ).findFirst( callback );
  }

  public static Offerta findLast()
  {
    return Backendless.Data.of( Offerta.class ).findLast();
  }

  public static Future<Offerta> findLastAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<Offerta> future = new Future<Offerta>();
      Backendless.Data.of( Offerta.class ).findLast( future );

      return future;
    }
  }

  public static void findLastAsync( AsyncCallback<Offerta> callback )
  {
    Backendless.Data.of( Offerta.class ).findLast( callback );
  }

  public static BackendlessCollection<Offerta> find( BackendlessDataQuery query )
  {
    return Backendless.Data.of( Offerta.class ).find( query );
  }

  public static Future<BackendlessCollection<Offerta>> findAsync( BackendlessDataQuery query )
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<BackendlessCollection<Offerta>> future = new Future<BackendlessCollection<Offerta>>();
      Backendless.Data.of( Offerta.class ).find( query, future );

      return future;
    }
  }

  public static void findAsync( BackendlessDataQuery query, AsyncCallback<BackendlessCollection<Offerta>> callback )
  {
    Backendless.Data.of( Offerta.class ).find( query, callback );
  }
}