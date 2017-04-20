package com.kasisoft.mgnl.util;

import com.kasisoft.libs.common.function.*;

import javax.annotation.*;
import javax.jcr.*;

import java.util.function.*;

import java.util.*;

import lombok.experimental.*;

import lombok.*;

/**
 * A simple helper to provide an easier solution for querying jcr nodes. It's essentially representing a
 * binding for property values.
 * 
 * @author daniel.kasmeroglu@kasisoft.net
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JcrProperty<T> {
  
  @Getter
  String                         property;
  
  BiFunction<Node, String, T>    loader;
  TriConsumer<Node, String, T>   saver;
  T                              defaultValue;
  
  public JcrProperty( @Nonnull String name, @Nonnull BiFunction<Node, String, T> toload, @Nonnull TriConsumer<Node, String, T> tosave, T defValue ) {
    property      = name;
    loader        = toload;
    saver         = tosave;
    defaultValue  = defValue;
    LocalData.properties.put( name, this );
  }

  public T getValue( @Nonnull Node node ) {
    T result = loader.apply( node, property );
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
    JcrProperty result = null;
    if( name != null ) {
      result = LocalData.properties.get( name );
    }
    return result;
  }
  
  public static JcrProperty[] values() {
    return LocalData.properties.values().toArray( new JcrProperty[ LocalData.properties.size() ] );
  }
  
  private static class LocalData {
     
    private static Map<String, JcrProperty>    properties = new HashMap<>();
    
  } /* ENDCLASS */
  
} /* ENDCLASS */
