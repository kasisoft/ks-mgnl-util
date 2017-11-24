package com.kasisoft.mgnl.util.model;

import static com.kasisoft.mgnl.util.QueryFunctions.*;
import static com.kasisoft.mgnl.util.internal.Messages.*;

import info.magnolia.repository.*;

import com.kasisoft.mgnl.util.*;

import org.apache.commons.lang3.*;

import javax.annotation.*;
import javax.jcr.*;

import java.util.function.*;

import java.util.*;

import lombok.extern.slf4j.*;

import lombok.experimental.*;

import lombok.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
@Slf4j
public class TemplateDeclaration<T extends TemplateDeclaration> implements Predicate<Node> {

  // a speaking name
  @Getter String        name;
  
  // the magnolia template id
  @Getter String        id;

  // a list of allowed template ids. if empty all templates are considered to be allowed.
  @Getter Set<String>   childIds;
  
  // if true this component provides a webview for a record and thus isn't dedicated to a single page
  @Getter boolean       rendererPage;
  
  String                str;
  
  TemplateDeclaration( @Nonnull Map<String, T> byId, @Nonnull Map<String, T> byName, @Nonnull String templateName, @Nonnull String templateId, boolean renderer, String ... subIds ) {
    name          = templateName;
    id            = templateId;
    rendererPage  = renderer;
    str           = String.format( "%s(%s)", name, id );
    childIds      = Collections.unmodifiableSet( new HashSet<>( Arrays.asList( subIds ) ) );
    duplicateCheck( byId   , id   , name, id );
    duplicateCheck( byName , name , name, id );
    byId   . put( id   , (T) this );
    byName . put( name , (T) this );
  }
  
  private void duplicateCheck( Map<String, T> map, String candidate, String name, String id ) {
    if( map.containsKey( candidate ) ) {
      log.error( error_template_decl_conflict.format( this, map.get( candidate ) ) );
      throw new IllegalStateException( error_template_decl_conflict.format( this, map.get( candidate ) ) );
    }
  }
  
  /**
   * Returns <code>true</code> if the supplied node provides the data for this component type.
   * 
   * @param node   The node under test.
   * 
   * @return   <code>true</code> <=> The supplied node provides the data for this component type.
   */
  @Override
  public boolean test( @Nullable Node t ) {
    boolean result = false;
    if( t != null ) {
      result = id.equals( NodeFunctions.getTemplate(t) );
    }
    return result;
  }

  /**
   * Returns all components below the supplied parent node. Errors will be logged.
   * 
   * @param parent    The root to start looking from.
   * 
   * @return   A list of all components.
   */
  @Nonnull
  public List<Node> getNodes( @Nonnull Node parent ) {
    return getNodes( parent, QueryFunctions::loggingErrorHandler );
  }

  /**
   * Returns all components below the supplied parent node.
   * 
   * @param parent    The root to start looking from.
   * @param handler   An error handler.
   * 
   * @return   A list of all components.
   */
  @Nonnull
  public List<Node> getNodes( @Nonnull Node parent, @Nonnull Consumer<Exception> handler ) {
    String query = "/jcr:root/%s//element(*,mgnl:page)[@mgnl:template='%s']";
    try {
      String basepath = StringUtils.removeEnd( StringUtils.removeStart( parent.getPath(), "/" ), "/" );
      return xpath.list( RepositoryConstants.WEBSITE, query, handler, basepath, id );
    } catch( Exception ex ) {
      handler.accept( ex );
      return Collections.emptyList();
    }
  }

  /**
   * Returns all components within the website workspace. Errors will be logged. 
   * 
   * @return   A list of all components.
   */
  @Nonnull
  public List<Node> getNodes() {
    return getNodes( QueryFunctions::loggingErrorHandler );
  }

  /**
   * Returns all components within the website workspace. 
   * 
   * @param handler   An error handler.
   * 
   * @return   A list of all components.
   */
  @Nonnull
  public List<Node> getNodes( @Nonnull Consumer<Exception> handler ) {
    String query = "//element(*,mgnl:page)[@mgnl:template='%s']";
    return xpath.list( RepositoryConstants.WEBSITE, query, handler, id );
  }
  
  /**
   * Returns <code>true</code> if the supplied template id is allowed to be used as a child of this template
   * declaration.
   * 
   * @param templateId   The id that is supposed to be tested.
   * 
   * @return   <code>true</code> <=> If the supplied template id is allowed to be used as a child of this 
   *                                 template declaration. 
   */
  public boolean isChildAllowed( @Nullable String templateId ) {
    boolean result = false;
    if( templateId != null ) {
      if( childIds.isEmpty() ) {
        result = true;
      } else {
        result = childIds.contains( templateId );
      }
    }
    return result;
  }
  
  @Override
  public String toString() {
    return str;
  }
  
} /* ENDCLASS */
