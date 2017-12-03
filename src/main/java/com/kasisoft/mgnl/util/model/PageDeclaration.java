package com.kasisoft.mgnl.util.model;

import static com.kasisoft.mgnl.util.JcrProperties.*;

import com.kasisoft.mgnl.util.*;

import javax.annotation.*;
import javax.jcr.*;

import java.util.*;

import lombok.experimental.*;

import lombok.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class PageDeclaration extends TemplateDeclaration<PageDeclaration> {
  
  public PageDeclaration( @Nonnull String templateName, @Nonnull String templateId, boolean renderer, String ... subIds ) {
    super( LocalData.byId, LocalData.byName, templateName, templateId, ENodeType.Page.getNodeType(), renderer, subIds );
  }
  
  public static PageDeclaration[] values() {
    return LocalData.byId.values().toArray( new PageDeclaration[ LocalData.byId.size() ] );
  }

  public static PageDeclaration valueById( @Nullable Node node ) {
    return valueBy( LocalData.byId, MgnlTemplate.getValue( node ) );
  }

  public static PageDeclaration valueById( @Nullable String templateId ) {
    return valueBy( LocalData.byId, templateId );
  }

  public static PageDeclaration valueByName( @Nullable String templateName ) {
    return valueBy( LocalData.byName, templateName );
  }
  
  private static PageDeclaration valueBy( Map<String, PageDeclaration> map, String key ) {
    PageDeclaration result = null;
    if( key != null ) {
      result = map.get( key );
    }
    return result;
  }

  private static class LocalData {
  
    private static Map<String, PageDeclaration>   byId    = new HashMap<>();
    private static Map<String, PageDeclaration>   byName  = new HashMap<>();
    
  } /* ENDCLASS */

} /* ENDCLASS */
