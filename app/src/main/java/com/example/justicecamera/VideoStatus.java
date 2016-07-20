package com.example.justicecamera;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.BackendlessDataQuery;

public class VideoStatus
{
  private java.util.Date created;
  private String objectId;
  private java.util.Date updated;
  private Integer name;
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

  public Integer getName()
  {
    return name;
  }

  public void setName( Integer name )
  {
    this.name = name;
  }

  public String getOwnerId()
  {
    return ownerId;
  }

                                                    
  public VideoStatus save()
  {
    return Backendless.Data.of( VideoStatus.class ).save( this );
  }

  public Future<VideoStatus> saveAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<VideoStatus> future = new Future<VideoStatus>();
      Backendless.Data.of( VideoStatus.class ).save( this, future );

      return future;
    }
  }

  public void saveAsync( AsyncCallback<VideoStatus> callback )
  {
    Backendless.Data.of( VideoStatus.class ).save( this, callback );
  }

  public Long remove()
  {
    return Backendless.Data.of( VideoStatus.class ).remove( this );
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
      Backendless.Data.of( VideoStatus.class ).remove( this, future );

      return future;
    }
  }

  public void removeAsync( AsyncCallback<Long> callback )
  {
    Backendless.Data.of( VideoStatus.class ).remove( this, callback );
  }

  public static VideoStatus findById( String id )
  {
    return Backendless.Data.of( VideoStatus.class ).findById( id );
  }

  public static Future<VideoStatus> findByIdAsync( String id )
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<VideoStatus> future = new Future<VideoStatus>();
      Backendless.Data.of( VideoStatus.class ).findById( id, future );

      return future;
    }
  }

  public static void findByIdAsync( String id, AsyncCallback<VideoStatus> callback )
  {
    Backendless.Data.of( VideoStatus.class ).findById( id, callback );
  }

  public static VideoStatus findFirst()
  {
    return Backendless.Data.of( VideoStatus.class ).findFirst();
  }

  public static Future<VideoStatus> findFirstAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<VideoStatus> future = new Future<VideoStatus>();
      Backendless.Data.of( VideoStatus.class ).findFirst( future );

      return future;
    }
  }

  public static void findFirstAsync( AsyncCallback<VideoStatus> callback )
  {
    Backendless.Data.of( VideoStatus.class ).findFirst( callback );
  }

  public static VideoStatus findLast()
  {
    return Backendless.Data.of( VideoStatus.class ).findLast();
  }

  public static Future<VideoStatus> findLastAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<VideoStatus> future = new Future<VideoStatus>();
      Backendless.Data.of( VideoStatus.class ).findLast( future );

      return future;
    }
  }

  public static void findLastAsync( AsyncCallback<VideoStatus> callback )
  {
    Backendless.Data.of( VideoStatus.class ).findLast( callback );
  }

  public static BackendlessCollection<VideoStatus> find( BackendlessDataQuery query )
  {
    return Backendless.Data.of( VideoStatus.class ).find( query );
  }

  public static Future<BackendlessCollection<VideoStatus>> findAsync( BackendlessDataQuery query )
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<BackendlessCollection<VideoStatus>> future = new Future<BackendlessCollection<VideoStatus>>();
      Backendless.Data.of( VideoStatus.class ).find( query, future );

      return future;
    }
  }

  public static void findAsync( BackendlessDataQuery query, AsyncCallback<BackendlessCollection<VideoStatus>> callback )
  {
    Backendless.Data.of( VideoStatus.class ).find( query, callback );
  }
}