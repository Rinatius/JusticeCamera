package com.example.justicecamera;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.BackendlessDataQuery;

public class Category_id
{
  private java.util.Date updated;
  private String ownerId;
  private String objectId;
  private java.util.Date created;
  private String type;
  public java.util.Date getUpdated()
  {
    return updated;
  }

  public String getOwnerId()
  {
    return ownerId;
  }

  public String getObjectId()
  {
    return objectId;
  }

  public java.util.Date getCreated()
  {
    return created;
  }

  public String getType()
  {
    return type;
  }

  public void setType( String type )
  {
    this.type = type;
  }

                                                    
  public Category_id save()
  {
    return Backendless.Data.of( Category_id.class ).save( this );
  }

  public Future<Category_id> saveAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<Category_id> future = new Future<Category_id>();
      Backendless.Data.of( Category_id.class ).save( this, future );

      return future;
    }
  }

  public void saveAsync( AsyncCallback<Category_id> callback )
  {
    Backendless.Data.of( Category_id.class ).save( this, callback );
  }

  public Long remove()
  {
    return Backendless.Data.of( Category_id.class ).remove( this );
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
      Backendless.Data.of( Category_id.class ).remove( this, future );

      return future;
    }
  }

  public void removeAsync( AsyncCallback<Long> callback )
  {
    Backendless.Data.of( Category_id.class ).remove( this, callback );
  }

  public static Category_id findById( String id )
  {
    return Backendless.Data.of( Category_id.class ).findById( id );
  }

  public static Future<Category_id> findByIdAsync( String id )
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<Category_id> future = new Future<Category_id>();
      Backendless.Data.of( Category_id.class ).findById( id, future );

      return future;
    }
  }

  public static void findByIdAsync( String id, AsyncCallback<Category_id> callback )
  {
    Backendless.Data.of( Category_id.class ).findById( id, callback );
  }

  public static Category_id findFirst()
  {
    return Backendless.Data.of( Category_id.class ).findFirst();
  }

  public static Future<Category_id> findFirstAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<Category_id> future = new Future<Category_id>();
      Backendless.Data.of( Category_id.class ).findFirst( future );

      return future;
    }
  }

  public static void findFirstAsync( AsyncCallback<Category_id> callback )
  {
    Backendless.Data.of( Category_id.class ).findFirst( callback );
  }

  public static Category_id findLast()
  {
    return Backendless.Data.of( Category_id.class ).findLast();
  }

  public static Future<Category_id> findLastAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<Category_id> future = new Future<Category_id>();
      Backendless.Data.of( Category_id.class ).findLast( future );

      return future;
    }
  }

  public static void findLastAsync( AsyncCallback<Category_id> callback )
  {
    Backendless.Data.of( Category_id.class ).findLast( callback );
  }

  public static BackendlessCollection<Category_id> find( BackendlessDataQuery query )
  {
    return Backendless.Data.of( Category_id.class ).find( query );
  }

  public static Future<BackendlessCollection<Category_id>> findAsync( BackendlessDataQuery query )
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<BackendlessCollection<Category_id>> future = new Future<BackendlessCollection<Category_id>>();
      Backendless.Data.of( Category_id.class ).find( query, future );

      return future;
    }
  }

  public static void findAsync( BackendlessDataQuery query, AsyncCallback<BackendlessCollection<Category_id>> callback )
  {
    Backendless.Data.of( Category_id.class ).find( query, callback );
  }
}