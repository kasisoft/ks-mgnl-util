package com.kasisoft.mgnl.util;

import static com.kasisoft.mgnl.util.internal.Messages.*;

import info.magnolia.repository.*;

import info.magnolia.context.*;

import com.kasisoft.libs.common.function.*;

import javax.annotation.*;
import javax.jcr.*;

import java.util.function.*;

import java.util.*;

import lombok.extern.slf4j.*;

import lombok.experimental.*;

import lombok.*;

/**
 * Like {@link JcrProperty} but with the difference that these properties operate on a fixed path which means
 * that it's not necessary to provide a node. 
 * 
 * @author daniel.kasmeroglu@kasisoft.net
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JcrFixedProperty<T> {
  
  JcrProperty<T>   jcrProperty;
  String           path;
  T                defaultValue;
  String           workspace;
  
  public JcrFixedProperty( @Nonnull String nodePath, @Nonnull String name, @Nonnull BiFunction<Node, String, T> toload, @Nonnull TriConsumer<Node, String, T> tosave, T defValue ) {
    jcrProperty   = new JcrProperty<T>( name, toload, tosave, defValue );
    path          = nodePath;
    defaultValue  = defValue;
    workspace     = RepositoryConstants.CONFIG;
    LocalData.properties.put( name, this );
  }
  
  public JcrFixedProperty<T> workspace( @Nonnull String ws ) {
    workspace = ws;
    return this;
  }

  @Nullable
  private Node findNode() {
    try {
      Session session  = MgnlContext.getJCRSession( workspace );
      Node    rootnode = session.getRootNode();
      if( rootnode.hasNode( path ) ) {
        return rootnode.getNode( path );
      } else {
        return null;
      }
    } catch( Exception ex ) {
      log.error( error_missing_node.format( path, workspace ), ex );
      return null;
    }
  }
  
  public T getValue() {
    Node node   = findNode();
    T    result = null;
    if( node != null ) {
      result = jcrProperty.getValue( node );
    } else {
      log.warn( error_missing_node.format( path, workspace ) );
    }
    if( result == null ) {
      result = defaultValue;
    }
    return result;
  }
  
  public void setValue( T value ) {
    Node node = findNode();
    if( node != null ) {
      jcrProperty.setValue( node, value );
    } else {
      log.warn( error_missing_node.format( path, workspace ) );
    }
  }
  
  public boolean hasValue( T val ) {
    T value = getValue();
    if( value != null ) {
      return value.equals( val );
    } else {
      return val == null;
    }
  }
  
  @Override
  public String toString() {
    return jcrProperty.getProperty();
  }

  @Nullable
  public static JcrFixedProperty valueByName( @Nullable String name ) {
    JcrFixedProperty result = null;
    if( name != null ) {
      result = LocalData.properties.get( name );
    }
    return result;
  }
  
  public static JcrFixedProperty[] values() {
    return LocalData.properties.values().toArray( new JcrFixedProperty[ LocalData.properties.size() ] );
  }
  
  private static class LocalData {
     
    private static Map<String, JcrFixedProperty>    properties = new HashMap<>();
    
  } /* ENDCLASS */
  
} /* ENDCLASS */
