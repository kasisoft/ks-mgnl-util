package com.kasisoft.mgnl.util.internal;

import com.kasisoft.libs.common.i18n.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class Messages {

  @I18N("failed to save property '%s' with value '%s'")
  public static I18NFormatter               error_failed_to_save_property;
  
  @I18N("missing page node with id '%s'")
  public static I18NFormatter               error_missing_page_node;

  @I18N("missing node '%s' in workspace '%s'")
  public static I18NFormatter               error_missing_node;

  @I18N("cannot register '%s' as there's a conflict with '%s' !")
  public static I18NFormatter               error_template_decl_conflict;
  
  @I18N("executing query (%s): %s")
  public static I18NFormatter               executing_query;

  static {
    I18NSupport.initialize( Messages.class );
  }

} /* ENDCLASS */
