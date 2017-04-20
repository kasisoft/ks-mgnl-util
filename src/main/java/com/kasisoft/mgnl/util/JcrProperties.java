package com.kasisoft.mgnl.util;

import info.magnolia.jcr.util.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class JcrProperties {

  public static JcrFixedProperty<Boolean>  Admin           = new JcrFixedProperty<>( "/server", "admin"                        , PropertyLoaders::toBoolean , PropertySavers::saveBoolean , Boolean.FALSE );
  public static JcrFixedProperty<String>   DefaultBaseUrl  = new JcrFixedProperty<>( "/server", "defaultBaseUrl"               , PropertyLoaders::toString  , PropertySavers::saveString  , null          );
  
  public static JcrProperty<String>        MgnlTemplate    = new JcrProperty<>( NodeTypes.Renderable.TEMPLATE  , PropertyLoaders::toString  , PropertySavers::saveString  , ""            );
  
} /* ENDCLASS */
