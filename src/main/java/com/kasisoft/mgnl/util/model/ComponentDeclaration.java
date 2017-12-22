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
public final class ComponentDeclaration extends TemplateDeclaration<ComponentDeclaration> {

  public ComponentDeclaration( @Nonnull String templateName, @Nonnull String templateId, boolean renderer, String ... subIds ) {
    super( LocalData.byId, LocalData.byName, templateName, templateId, ENodeType.Component.getNodeType(), renderer, subIds );
  }
  
  @Override
  public ENodeType getMgnlType() {
    return ENodeType.Component;
  }
  
  public static ComponentDeclaration[] values() {
    return LocalData.byId.values().toArray( new ComponentDeclaration[ LocalData.byId.size() ] );
  }

  public static ComponentDeclaration valueById( @Nullable Node node ) {
    return valueBy( LocalData.byId, MgnlTemplate.getValue( node ) );
  }

  public static ComponentDeclaration valueById( @Nullable String templateId ) {
    return valueBy( LocalData.byId, templateId );
  }

  public static ComponentDeclaration valueByName( @Nullable String templateName ) {
    return valueBy( LocalData.byName, templateName );
  }
  
  private static ComponentDeclaration valueBy( Map<String, ComponentDeclaration> map, String key ) {
    ComponentDeclaration result = null;
    if( key != null ) {
      result = map.get( key );
    }
    return result;
  }
  
  private static class LocalData {
  
    private static Map<String, ComponentDeclaration>   byId    = new HashMap<>();
    private static Map<String, ComponentDeclaration>   byName  = new HashMap<>();
    
  } /* ENDCLASS */

} /* ENDCLASS */
