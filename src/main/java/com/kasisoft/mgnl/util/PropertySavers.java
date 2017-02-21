package com.kasisoft.mgnl.util;

import info.magnolia.jcr.util.*;

import org.slf4j.*;

import javax.annotation.*;
import javax.jcr.*;

/**
 * A bunch of functions allowing to load a property of a certain datatype.
 * 
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class PropertySavers {

  private static final Logger log = LoggerFactory.getLogger( PropertySavers.class );
  
  public static void saveBoolean( @Nonnull Node jcr, @Nonnull String propertyName, boolean value ) {
    save( jcr, propertyName, value );
  }

  public static void saveCharacter( @Nonnull Node jcr, @Nonnull String propertyName, char value ) {
    save( jcr, propertyName, value );
  }
  
  public static void saveByte( @Nonnull Node jcr, @Nonnull String propertyName, byte value ) {
    save( jcr, propertyName, value );  
  }
  
  public static void saveShort( @Nonnull Node jcr, @Nonnull String propertyName, short value ) {
    save( jcr, propertyName, value );  
  }
  
  public static void saveInteger( @Nonnull Node jcr, @Nonnull String propertyName, int value ) {
    save( jcr, propertyName, value );  
  }
  
  public static void saveLong( @Nonnull Node jcr, @Nonnull String propertyName, long value ) {
    save( jcr, propertyName, value );  
  }
  
  public static void saveFloat( @Nonnull Node jcr, @Nonnull String propertyName, float value ) {
    save( jcr, propertyName, value );  
  }
  
  public static void saveDouble( @Nonnull Node jcr, @Nonnull String propertyName, double value ) {
    save( jcr, propertyName, value );  
  }

  public static void saveString( @Nonnull Node jcr, @Nonnull String propertyName, String value ) {
    save( jcr, propertyName, value );  
  }

  private static void save( Node jcr, String propertyName, Object value ) {
    try {
      PropertyUtil.setProperty( jcr, propertyName, value );
    } catch( RepositoryException ex ) {
      log.error( "Failed to save property '{}' with value '{}'", propertyName, value, ex );
    }
  }
  
} /* ENDCLASS */
