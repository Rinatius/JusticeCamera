package com.example.justicecamera;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.BackendlessDataQuery;

public class ModeratorStatus
{
  private java.util.Date created;
  private String objectId;
  private java.util.Date updated;
  private Integer status;
  private String ownerId;
  public java.util.Date getCreated()
  {
    return created;
  }

  public String getObjectId()
  {
    return objectId;
  }

  public java.util.Date getUpdated()
  {
    return updated;
  }

  public Integer getStatus()
  {
    return status;
  }

  public void setStatus( Integer status )
  {
    this.status = status;
  }

  public String getOwnerId()
  {
    return ownerId;
  }

                                                    
  public ModeratorStatus save()
  {
    return Backendless.Data.of( ModeratorStatus.class ).save( this );
  }

  public Future<ModeratorStatus> saveAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<ModeratorStatus> future = new Future<ModeratorStatus>();
      Backendless.Data.of( ModeratorStatus.class ).save( this, future );

      return future;
    }
  }

  public void saveAsync( AsyncCallback<ModeratorStatus> callback )
  {
    Backendless.Data.of( ModeratorStatus.class ).save( this, callback );
  }

  public Long remove()
  {
    return Backendless.Data.of( ModeratorStatus.class ).remove( this );
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
      Backendless.Data.of( ModeratorStatus.class ).remove( this, future );

      return future;
    }
  }

  public void removeAsync( AsyncCallback<Long> callback )
  {
    Backendless.Data.of( ModeratorStatus.class ).remove( this, callback );
  }

  public static ModeratorStatus findById( String id )
  {
    return Backendless.Data.of( ModeratorStatus.class ).findById( id );
  }

  public static Future<ModeratorStatus> findByIdAsync( String id )
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<ModeratorStatus> future = new Future<ModeratorStatus>();
      Backendless.Data.of( ModeratorStatus.class ).findById( id, future );

      return future;
    }
  }

  public static void findByIdAsync( String id, AsyncCallback<ModeratorStatus> callback )
  {
    Backendless.Data.of( ModeratorStatus.class ).findById( id, callback );
  }

  public static ModeratorStatus findFirst()
  {
    return Backendless.Data.of( ModeratorStatus.class ).findFirst();
  }

  public static Future<ModeratorStatus> findFirstAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<ModeratorStatus> future = new Future<ModeratorStatus>();
      Backendless.Data.of( ModeratorStatus.class ).findFirst( future );

      return future;
    }
  }

  public static void findFirstAsync( AsyncCallback<ModeratorStatus> callback )
  {
    Backendless.Data.of( ModeratorStatus.class ).findFirst( callback );
  }

  public static ModeratorStatus findLast()
  {
    return Backendless.Data.of( ModeratorStatus.class ).findLast();
  }

  public static Future<ModeratorStatus> findLastAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<ModeratorStatus> future = new Future<ModeratorStatus>();
      Backendless.Data.of( ModeratorStatus.class ).findLast( future );

      return future;
    }
  }

  public static void findLastAsync( AsyncCallback<ModeratorStatus> callback )
  {
    Backendless.Data.of( ModeratorStatus.class ).findLast( callback );
  }

  public static BackendlessCollection<ModeratorStatus> find( BackendlessDataQuery query )
  {
    return Backendless.Data.of( ModeratorStatus.class ).find( query );
  }

  public static Future<BackendlessCollection<ModeratorStatus>> findAsync( BackendlessDataQuery query )
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<BackendlessCollection<ModeratorStatus>> future = new Future<BackendlessCollection<ModeratorStatus>>();
      Backendless.Data.of( ModeratorStatus.class ).find( query, future );

      return future;
    }
  }

  public static void findAsync( BackendlessDataQuery query, AsyncCallback<BackendlessCollection<ModeratorStatus>> callback )
  {
    Backendless.Data.of( ModeratorStatus.class ).find( query, callback );
  }
}