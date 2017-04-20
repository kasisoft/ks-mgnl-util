package com.kasisoft.mgnl.util;

import info.magnolia.jcr.util.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class JcrProperties {

  public static JcrProperty<String>   DefaultBaseUrl  = new JcrProperty<>( "defaultBaseUrl"               , PropertyLoaders::toString  , PropertySavers::saveString  , null          );
  public static JcrProperty<String>   MgnlTemplate    = new JcrProperty<>( NodeTypes.Renderable.TEMPLATE  , PropertyLoaders::toString  , PropertySavers::saveString  , ""            );
  public static JcrProperty<Boolean>  Admin           = new JcrProperty<>( "admin"                        , PropertyLoaders::toBoolean , PropertySavers::saveBoolean , Boolean.FALSE );
  
} /* ENDCLASS */
