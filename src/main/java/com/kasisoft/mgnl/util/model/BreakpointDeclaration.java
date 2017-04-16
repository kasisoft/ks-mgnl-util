package com.kasisoft.mgnl.util.model;

import com.kasisoft.libs.common.constants.*;

import com.kasisoft.libs.common.text.*;

import com.kasisoft.libs.common.xml.adapters.*;

import com.kasisoft.libs.common.base.*;
import com.kasisoft.libs.common.config.*;

import javax.annotation.*;

import java.util.*;

import java.net.*;

import java.io.*;

import lombok.experimental.*;

import lombok.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BreakpointDeclaration implements Comparable<BreakpointDeclaration> {

  private static final SimpleProperty<Integer> MAX  = new SimpleProperty<>( "max", new IntegerAdapter() );
  private static final SimpleProperty<Integer> COLS = new SimpleProperty<>( "columns", new IntegerAdapter() );
  private static final SimpleProperty<Boolean> DEF  = new SimpleProperty<>( "default", new BooleanAdapter() ).withDefault( false );

  public static BreakpointDeclaration DEFAULT = null;
  
  int      maxWidth;
  String   key;
  int      columns;
  
  public BreakpointDeclaration( int max, String bpkey, int cols ) {
    maxWidth = max;
    key      = bpkey;
    columns  = cols;
    LocalData.byKey.put( key, this );
    LocalData.breakpoints.add( this );
    Collections.sort( LocalData.breakpoints );
  }
  
  @Override
  public String toString() {
    return key;
  }
  
  @Override
  public int compareTo( BreakpointDeclaration o ) {
    return Integer.compare( maxWidth, o.maxWidth );
  }

  public static BreakpointDeclaration[] values() {
    return LocalData.breakpoints.toArray( new BreakpointDeclaration[ LocalData.breakpoints.size() ] );
  }
  
  public static BreakpointDeclaration valueByKey( @Nullable String key ) {
    return key != null ? LocalData.byKey.get( key ) : null;
  }

  public static void loadDefaultBreakpoints() {
    loadBreakpoints( "breakpoints.properties" );
  }
  
  public static void loadBreakpoints( String resource ) {
    URL url = BreakpointDeclaration.class.getClassLoader().getResource( resource );
    if( url != null ) {
      loadBreakpoints( url );
    }
  }

  public static void loadBreakpoints( URL resource ) {
    Properties properties = new Properties();
    try( Reader reader = Encoding.UTF8.openReader( resource ) ) {
      properties.load( reader );
    } catch( IOException ex ) {
      throw FailureCode.IO.newException( ex );
    }
    loadKeys( properties ).forEach( $ -> loadBreakpoint( properties, $ ) );
  }

  private static void loadBreakpoint( Properties properties, String key ) {
    MapProperty<String> prop   = new MapProperty<>( key, new StringAdapter() );
    Map<String, String> record = prop.getValue( properties );
    Integer             max    = MAX.getValue( record );
    Integer             cols   = COLS.getValue( record );
    Boolean             isdef  = DEF.getValue( record );
    if( (max != null) && (cols != null) ) {
      BreakpointDeclaration decl = new BreakpointDeclaration( max, key, cols );
      if( isdef.booleanValue() ) {
        DEFAULT = decl;
      }
    }
  }
  
  private static Set<String> loadKeys( Properties properties ) {
    Set<String>         result  = new HashSet<>();
    Enumeration<String> names   = (Enumeration<String>) properties.propertyNames();
    while( names.hasMoreElements() ) {
      String name = names.nextElement();
      int    idx  = StringFunctions.indexOf( name, '.', '[' );
      if( idx > 0 ) {
        result.add( name.substring( 0, idx ) );
      }
    }
    return result;
  }

  private static class LocalData {
    
    private static Map<String, BreakpointDeclaration>   byKey       = new HashMap<>(4);
    private static List<BreakpointDeclaration>          breakpoints = new ArrayList<>(4);
    
  } /* ENDCLASS */

} /* ENDCLASS */
