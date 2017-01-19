package com.kasisoft.mgnl.util.internal;

import com.kasisoft.libs.common.i18n.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class Messages {

  @I18N("executing query (%s): %s")
  public static I18NFormatter              executing_query;

  static {
    I18NSupport.initialize( Messages.class );
  }

} /* ENDCLASS */
