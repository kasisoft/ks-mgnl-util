package com.kasisoft.mgnl.util;

import com.kasisoft.libs.common.function.*;

import javax.annotation.*;
import javax.jcr.*;

import java.util.function.*;

import java.util.*;

/**
 * A simple helper to provide an easier solution for querying jcr nodes. 
 * 
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class JcrProperty<T> {
  
  private String                         property;
  private BiFunction<Node, String, T>    loader;
  private TriConsumer<Node, String, T>   saver;
  private T                              defaultValue;
  
  public JcrProperty( @Nonnull String name, @Nonnull BiFunction<Node, String, T> converter, @Nonnull TriConsumer<Node, String, T> tosave, T defValue ) {
    property      = name;
    loader        = converter;
    saver         = tosave;
    defaultValue  = defValue;
    synchronized( LocalData.properties ) {
      LocalData.properties.put( name, this );
    }
  }

  @Nonnull
  public String getProperty() {
    return property;
  }
  
  public T getValue( @Nonnull Node node ) {
    T      result = loader.apply( node, property );
    if( result == null ) {
      result = defaultValue;
    }
    return result;
  }
  
  public void setValue( @Nonnull Node node, T value ) {
    saver.accept( node, property, value );
  }
  
  public boolean hasValue( @Nonnull Node node, T val ) {
    T value = getValue( node );
    if( value != null ) {
      return value.equals( val );
    } else {
      return val == null;
    }
  }
  
  public Predicate<Node> isOfValue( T expected ) {
    return $ -> hasValue( $, expected );
  }

  @Override
  public String toString() {
    return property;
  }
  
  @Nullable
  public static JcrProperty valueByName( @Nullable String name ) {
    synchronized( LocalData.properties ) {
      return LocalData.properties.get( name );
    }
  }
  
  public static JcrProperty[] values() {
    synchronized( LocalData.properties ) {
      return LocalData.properties.values().toArray( new JcrProperty[ LocalData.properties.size() ] );
    }
  }
  
  private static class LocalData {
     
    private static Map<String, JcrProperty>    properties = new HashMap<>();
    
  } /* ENDCLASS */
  
} /* ENDCLASS */
