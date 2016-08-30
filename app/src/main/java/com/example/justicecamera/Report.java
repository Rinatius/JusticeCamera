package com.example.justicecamera;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.BackendlessDataQuery;

public class Report
{
  private String ownerId;
  private String text;
  private java.util.Date created;
  private java.util.Date updated;
  private String name;
  private String objectId;
  public String getOwnerId()
  {
    return ownerId;
  }

  public String getText()
  {
    return text;
  }

  public void setText( String text )
  {
    this.text = text;
  }

  public java.util.Date getCreated()
  {
    return created;
  }

  public java.util.Date getUpdated()
  {
    return updated;
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

                                                    
  public Report save()
  {
    return Backendless.Data.of( Report.class ).save( this );
  }

  public Future<Report> saveAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<Report> future = new Future<Report>();
      Backendless.Data.of( Report.class ).save( this, future );

      return future;
    }
  }

  public void saveAsync( AsyncCallback<Report> callback )
  {
    Backendless.Data.of( Report.class ).save( this, callback );
  }

  public Long remove()
  {
    return Backendless.Data.of( Report.class ).remove( this );
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
      Backendless.Data.of( Report.class ).remove( this, future );

      return future;
    }
  }

  public void removeAsync( AsyncCallback<Long> callback )
  {
    Backendless.Data.of( Report.class ).remove( this, callback );
  }

  public static Report findById( String id )
  {
    return Backendless.Data.of( Report.class ).findById( id );
  }

  public static Future<Report> findByIdAsync( String id )
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<Report> future = new Future<Report>();
      Backendless.Data.of( Report.class ).findById( id, future );

      return future;
    }
  }

  public static void findByIdAsync( String id, AsyncCallback<Report> callback )
  {
    Backendless.Data.of( Report.class ).findById( id, callback );
  }

  public static Report findFirst()
  {
    return Backendless.Data.of( Report.class ).findFirst();
  }

  public static Future<Report> findFirstAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<Report> future = new Future<Report>();
      Backendless.Data.of( Report.class ).findFirst( future );

      return future;
    }
  }

  public static void findFirstAsync( AsyncCallback<Report> callback )
  {
    Backendless.Data.of( Report.class ).findFirst( callback );
  }

  public static Report findLast()
  {
    return Backendless.Data.of( Report.class ).findLast();
  }

  public static Future<Report> findLastAsync()
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<Report> future = new Future<Report>();
      Backendless.Data.of( Report.class ).findLast( future );

      return future;
    }
  }

  public static void findLastAsync( AsyncCallback<Report> callback )
  {
    Backendless.Data.of( Report.class ).findLast( callback );
  }

  public static BackendlessCollection<Report> find( BackendlessDataQuery query )
  {
    return Backendless.Data.of( Report.class ).find( query );
  }

  public static Future<BackendlessCollection<Report>> findAsync( BackendlessDataQuery query )
  {
    if( Backendless.isAndroid() )
    {
      throw new UnsupportedOperationException( "Using this method is restricted in Android" );
    }
    else
    {
      Future<BackendlessCollection<Report>> future = new Future<BackendlessCollection<Report>>();
      Backendless.Data.of( Report.class ).find( query, future );

      return future;
    }
  }

  public static void findAsync( BackendlessDataQuery query, AsyncCallback<BackendlessCollection<Report>> callback )
  {
    Backendless.Data.of( Report.class ).find( query, callback );
  }
}