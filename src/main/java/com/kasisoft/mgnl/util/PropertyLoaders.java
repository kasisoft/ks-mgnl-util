package com.kasisoft.mgnl.util;

import info.magnolia.jcr.util.*;

import com.kasisoft.libs.common.text.*;

import com.kasisoft.libs.common.util.*;

import javax.annotation.*;
import javax.jcr.*;

import java.util.function.*;

import java.util.*;

/**
 * A bunch of functions allowing to load a property of a certain datatype.
 * 
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class PropertyLoaders {

  public static Date toDate( Node jcr, @Nonnull String propertyName, Date defVal ) {
    Calendar calendar = toCalendar( jcr, propertyName );
    Date     result   = defVal;
    if( calendar != null ) {
      result = calendar.getTime();
    }
    return result;
  }

  public static Date toDate( Node jcr, @Nonnull String propertyName ) {
    return toDate( jcr, propertyName, null );
  }

  public static Calendar toCalendar( Node jcr, @Nonnull String propertyName, Calendar defVal ) {
    return PropertyUtil.getDate( jcr, propertyName, defVal );
  }

  public static Calendar toCalendar( Node jcr, @Nonnull String propertyName ) {
    return toCalendar( jcr, propertyName, null );
  }

  public static boolean toBoolean( Node jcr, @Nonnull String propertyName, boolean defVal ) {
    Boolean result = toBoolean( jcr, propertyName );
    return result != null ? result.booleanValue() : defVal;
  }

  public static Boolean toBoolean( Node jcr, @Nonnull String propertyName ) {
    return toBoolean( jcr, propertyName, null );
  }
  
  public static Boolean toBoolean( Node jcr, @Nonnull String propertyName, Boolean defVal ) {
    return toType( jcr, propertyName, defVal, MiscFunctions::parseBoolean );
  }

  public static char toCharacter( Node jcr, @Nonnull String propertyName, char defVal ) {
    Character result = toCharacter( jcr, propertyName );
    return result != null ? result.charValue() : defVal;
  }
  
  public static Character toCharacter( Node jcr, @Nonnull String propertyName ) {
    return toCharacter( jcr, propertyName, null );
  }
  
  public static Character toCharacter( Node jcr, @Nonnull String propertyName, Character defVal ) {
    return toType( jcr, propertyName, defVal, $ -> $.length() > 0 ? Character.valueOf( $.charAt(0) ) : defVal );
  }

  public static byte toByte( Node jcr, @Nonnull String propertyName, byte defVal ) {
    Byte result = toByte( jcr, propertyName );
    return result != null ? result.byteValue() : defVal;
  }
  
  public static Byte toByte( Node jcr, @Nonnull String propertyName ) {
    return toByte( jcr, propertyName, null );
  }
  
  public static Byte toByte( Node jcr, @Nonnull String propertyName, Byte defVal ) {
    return toType( jcr, propertyName, defVal, Byte::parseByte );
  }

  public static short toShort( Node jcr, @Nonnull String propertyName, short defVal ) {
    Short result = toShort( jcr, propertyName );
    return result != null ? result.shortValue() : defVal;
  }
  
  public static Short toShort( Node jcr, @Nonnull String propertyName ) {
    return toShort( jcr, propertyName, null );
  }
  
  public static Short toShort( Node jcr, @Nonnull String propertyName, Short defVal ) {
    return toType( jcr, propertyName, defVal, Short::parseShort );
  }

  public static int toInteger( Node jcr, @Nonnull String propertyName, int defVal ) {
    Integer result = toInteger( jcr, propertyName );
    return result != null ? result.intValue() : defVal;
  }
  
  public static Integer toInteger( Node jcr, @Nonnull String propertyName ) {
    return toInteger( jcr, propertyName, null ); 
  }
  
  public static Integer toInteger( Node jcr, @Nonnull String propertyName, Integer defVal ) {
    return toType( jcr, propertyName, defVal, Integer::parseInt );
  }

  public static long toLong( Node jcr, @Nonnull String propertyName, long defVal ) {
    Long result = toLong( jcr, propertyName );
    return result != null ? result.longValue() : defVal;
  }
  
  public static Long toLong( Node jcr, @Nonnull String propertyName ) {
    return toLong( jcr, propertyName, null );
  }
  
  public static Long toLong( Node jcr, @Nonnull String propertyName, Long defVal ) {
    return toType( jcr, propertyName, defVal, Long::parseLong );
  }

  public static float toFloat( Node jcr, @Nonnull String propertyName, float defVal ) {
    Float result = toFloat( jcr, propertyName );
    return result != null ? result.floatValue() : defVal;
  }
  
  public static Float toFloat( Node jcr, @Nonnull String propertyName ) {
    return toFloat( jcr, propertyName, null );
  }
  
  public static Float toFloat( Node jcr, @Nonnull String propertyName, Float defVal ) {
    return toType( jcr, propertyName, defVal, Float::parseFloat );
  }

  public static double toDouble( Node jcr, @Nonnull String propertyName, double defVal ) {
    Double result = toDouble( jcr, propertyName );
    return result != null ? result.doubleValue() : defVal;
  }
  
  public static Double toDouble( Node jcr, @Nonnull String propertyName ) {
    return toDouble( jcr, propertyName, null );
  }
  
  public static Double toDouble( Node jcr, @Nonnull String propertyName, Double defVal ) {
    return toType( jcr, propertyName, defVal, Double::parseDouble );
  }

  public static String toStringDefaultNodeName( Node jcr, @Nonnull String propertyName ) {
    String result = StringFunctions.cleanup( toString( jcr, propertyName ) );
    if( result == null ) {
      result = NodeFunctions.getName( jcr );
    }
    return result;
  }

  public static String toStringDefaultNodePath( Node jcr, @Nonnull String propertyName ) {
    String result = StringFunctions.cleanup( toString( jcr, propertyName ) );
    if( result == null ) {
      result = NodeFunctions.getPath( jcr );
    }
    return result;
  }

  public static String toStringDefaultIdentifier( Node jcr, @Nonnull String propertyName ) {
    String result = StringFunctions.cleanup( toString( jcr, propertyName ) );
    if( result == null ) {
      result = NodeFunctions.getIdentifier( jcr );
    }
    return result;
  }

  public static String toString( Node jcr, @Nonnull String propertyName ) {
    return toString( jcr, propertyName, null );
  }
  
  public static String toString( Node jcr, @Nonnull String propertyName, String defVal ) {
    return toType( jcr, propertyName, defVal, $ -> $ );
  }

  public static <T> T toType( Node jcr, String propertyName, T defVal, Function<String, T> function ) {
    T result = null;
    if( jcr != null ) {
      String strValue = StringFunctions.cleanup( PropertyUtil.getString( jcr, propertyName ) );
      if( strValue != null ) {
        if( strValue.length() > 0 ) {
          result = function.apply( strValue );
        }
      }
    }
    if( result == null ) {
      result = defVal;
    }
    return result;
  }

} /* ENDCLASS */
