package com.kasisoft.mgnl.util;

import static com.kasisoft.mgnl.util.JcrProperties.*;
import static com.kasisoft.mgnl.util.internal.Messages.*;

import info.magnolia.context.*;

import info.magnolia.jcr.util.*;

import info.magnolia.jcr.*;

import com.kasisoft.libs.common.text.*;

import com.kasisoft.libs.common.function.*;

import org.apache.commons.lang3.*;

import javax.annotation.*;
import javax.jcr.*;

import java.util.function.*;

import java.util.*;

import info.magnolia.dam.jcr.*;

/**
 * Collection of helpful node related functionalities. These functions will generate {@link RuntimeRepositoryException}
 * rather than {@link RepositoryException}.
 * 
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class NodeFunctions {
  
  public static final Predicate<Node> IS_PAGE = NodeFunctions::isPageNode;
  
  private static final String PREFIX_JCR  = String.format( "%s:", DamConstants.DEFAULT_JCR_PROVIDER_ID );

  private static final int LEN_UUID       = 36;
  private static final int LEN_JCR_UUID   = LEN_UUID + PREFIX_JCR.length();
  
  @Nullable
  public static String getPath( @Nullable Node node ) {
    String result = null;
    if( node != null ) {
      try {
        result = node.getPath();
      } catch( RepositoryException ex ) {
        throw new RuntimeRepositoryException(ex);
      }
    }
    return result;
  }

  @Nullable
  public static String getPath( @Nullable JcrAsset asset ) {
    String result = null;
    if( asset != null ) {
      result = getPath( asset.getNode() );
    }
    return result;
  }

  @Nullable
  public static Node getNodeByIdentifier( @Nonnull String workspace, @Nullable String identifier ) {
    Node result = null;
    if( identifier != null ) {
      if( identifier.length() == LEN_JCR_UUID ) {
        identifier = StringUtils.removeStart( identifier, PREFIX_JCR );
      }
      if( identifier.length() == LEN_UUID ) {
        result = SessionUtil.getNodeByIdentifier( workspace, identifier );
      }
    }
    return result;
  }
  
  @Nonnull
  public static String getIdentifier( @Nonnull Node node ) {
    try {
      return node.getIdentifier();
    } catch( Exception ex ) {
      throw toRuntimeRepositoryException(ex);
    }
  }

  @Nonnull
  public static String getName( @Nonnull Node node ) {
    try {
      return node.getName();
    } catch( Exception ex ) {
      throw toRuntimeRepositoryException(ex);
    }
  }

  @Nonnull
  public static List<Node> getChildNodes( @Nonnull Node parent ) {
    return getChildNodes( parent, (Predicate<Node>) null );
  }

  @Nonnull
  public static List<Node> getChildNodes( @Nonnull Node parent, @Nullable Predicate<Node> test ) {
    return getChildNodes( parent, test, null );
  }

  @Nonnull
  public static <R> List<R> getChildNodes( @Nonnull Node parent, @Nullable Predicate<Node> test, @Nullable Function<Node, R> transform ) {
    if( test == null ) {
      test = Predicates.acceptAll();
    }
    if( transform == null ) {
      transform = (Function<Node, R>) Function.<Node>identity();
    }
    List<R> result = new ArrayList<>();
    try {
      NodeIterator iterator = parent.getNodes();
      while( iterator.hasNext() ) {
        Node nextNode = iterator.nextNode();
        if( test.test( nextNode ) ) {
          result.add( transform.apply( nextNode ) );
        }
      }
    } catch( Exception ex ) {
      throw toRuntimeRepositoryException(ex);
    }
    return result;
  }

  @Nullable
  public static Node getLastChild( @Nonnull Node parent ) {
    return getLastChild( parent, null, null );
  }
  
  @Nullable
  public static Node getLastChild( @Nonnull Node parent, @Nullable Predicate<Node> test ) {
    return getLastChild( parent, test, null );
  }

  @Nonnull
  public static <R> R getLastChild( @Nonnull Node parent, @Nullable Predicate<Node> test, @Nullable Function<Node, R> transform ) {
    return getSpecificChild( parent, test, transform, true );
  }

  @Nullable
  public static Node getFirstChild( @Nonnull Node parent ) {
    return getFirstChild( parent, null, null );
  }
  
  @Nullable
  public static Node getFirstChild( @Nonnull Node parent, @Nullable Predicate<Node> test ) {
    return getFirstChild( parent, test, null );
  }

  @Nonnull
  public static <R> R getFirstChild( @Nonnull Node parent, @Nullable Predicate<Node> test, @Nullable Function<Node, R> transform ) {
    return getSpecificChild( parent, test, transform, false );
  }

  @Nonnull
  private static <R> R getSpecificChild( @Nonnull Node parent, @Nullable Predicate<Node> test, @Nullable Function<Node, R> transform, boolean last ) {
    if( test == null ) {
      test = Predicates.acceptAll();
    }
    if( transform == null ) {
      transform = (Function<Node, R>) Function.<Node>identity();
    }
    Predicate<R> docontinue = $ -> (last ? true : $ == null);
    R            result     = null;
    try {
      NodeIterator iterator = parent.getNodes();
      while( docontinue.test( result ) && iterator.hasNext() ) {
        Node nextNode = iterator.nextNode();
        if( test.test( nextNode ) ) {
          result = transform.apply( nextNode );
        }
      }
    } catch( Exception ex ) {
      throw toRuntimeRepositoryException(ex);
    }
    return result;
  }

  @Nonnull
  public static List<Node> getChildNodes( @Nonnull Node parent, @Nonnull String templateId ) {
    return getChildNodes( parent, $ -> templateId.equals( getTemplate($) ) ); 
  }

  @Nonnull
  public static List<Node> getPageNodes( @Nonnull Node parent ) {
    return getChildNodes( parent, NodeFunctions::isPageNode ); 
  }

  @Nonnull
  public static List<Node> getComponentNodes( @Nonnull Node parent ) {
    return getChildNodes( parent, NodeFunctions::isComponentNode ); 
  }

  @Nonnull
  public static List<Node> getAreaNodes( @Nonnull Node parent ) {
    return getChildNodes( parent, NodeFunctions::isAreaNode ); 
  }

  public static boolean isAreaNode( @Nonnull Node node ) {
    return isNodeType( node, NodeTypes.Area.NAME );
  }

  public static boolean isComponentNode( @Nonnull Node node ) {
    return isNodeType( node, NodeTypes.Component.NAME );
  }

  public static boolean isPageNode( @Nonnull Node node ) {
    return isNodeType( node, NodeTypes.Page.NAME );
  }
  
  public static boolean isNodeType( @Nonnull Node node, @Nonnull String nodetype ) {
    try {
      return node.isNodeType( nodetype );
    } catch( Exception ex ) {
      throw toRuntimeRepositoryException(ex);
    }
  }

  @Nullable
  public static String getTemplate( @Nullable Node node ) {
    String result = null;
    if( node != null ) {
      try {
        result = NodeTypes.Renderable.getTemplate( node );
      } catch( RepositoryException ex ) {
        // not of our interest
      }
    }
    return result;
  }
  
  @Nullable
  public static Node getFirstContentNode( @Nullable Node node ) {
    return getFirstNode( node, $ -> isNodeType( $, NodeTypes.ContentNode.NAME ) );
  }

  @Nullable
  public static Node getFirstNode( @Nullable Node node, @Nonnull Predicate<Node> test ) {
    Node result = null;
    if( node != null ) {
      try {
        NodeIterator iterator = node.getNodes();
        while( iterator.hasNext() && (result == null) ) {
          Node current = iterator.nextNode();
          if( test.test( current ) ) {
            result = current;
          }
        }
      } catch( Exception ex ) {
        throw toRuntimeRepositoryException(ex);
      }
    }
    return result;
  }
  
  @Nonnull
  public static Node getPageNode( @Nonnull Node currentNode ) {
    try {
      if( isPageNode( currentNode ) ) {
        return currentNode;
      } else if( currentNode.getDepth() > 0 ) {
        return getPageNode( currentNode.getParent() );
      } else {
        throw new RepositoryException( error_missing_page_node.format( currentNode.getIdentifier() ) );
      }
    } catch( Exception ex ) {
      throw toRuntimeRepositoryException(ex);
    }
  }

  @Nullable
  public static Node getNode( @Nonnull Node node, @Nonnull String relpath ) {
    Node result = null;
    try {
      if( node.hasNode( relpath ) ) {
        result = node.getNode( relpath );
      }
    } catch( Exception ex ) {
      throw toRuntimeRepositoryException(ex);
    }
    return result;
  }

  @Nullable
  public static Node getParent( @Nonnull Node node ) {
    Node result = null;
    try {
      result = node.getParent();
    } catch( Exception ex ) {
      throw toRuntimeRepositoryException(ex);
    }
    return result;
  }

  public static Node getOrCreateNode( @Nonnull Node base, @Nonnull String relpath ) {
    Node   result  = null;
    String dirname = relpath;
    String newpath = null;
    int    idx     = relpath.indexOf('/');
    if( idx != -1 ) {
      dirname = relpath.substring( 0, idx );
      newpath = StringFunctions.cleanup( relpath.substring( idx + 1 ) );
    }
    String nodetype = extractNodeType( dirname );
    dirname         = cleanupNodeName( dirname );
    result          = getNode( base, dirname );
    if( result == null ) {
      try {
        result = base.addNode( dirname, getNodeType( base, dirname, nodetype ) );
      } catch( Exception ex ) {
        throw toRuntimeRepositoryException(ex);
      }
    }
    if( newpath != null ) {
      result = getOrCreateNode( result, newpath );
    }
    return result;
  }
  
  private static String cleanupNodeName( String str ) {
    int open  = str.indexOf('{');
    int close = str.indexOf('}');
    if( (open != -1) && (close != -1) && (close > open)) {
      return str.substring( 0, open );
    } else {
      return str;
    }
  }

  private static String extractNodeType( String str ) {
    int open  = str.indexOf('{');
    int close = str.indexOf('}');
    if( (open != -1) && (close != -1) && (close > open)) {
      return str.substring( open + 1, close );
    } else {
      return null;
    }
  }
  
  // basic attempt to select a proper nodetype
  private static String getNodeType( Node parent, String name, String nodetype ) throws RepositoryException {
    String result = nodetype;
    if( result == null ) {
      // determine the node type depending on the nodes position (assuming config like workspace)
      result = ENodeType.ContentNode.getNodeType();
      if( parent.getDepth() == 0 ) {
        result = ENodeType.Content.getNodeType();
      } else if( parent.getDepth() == 1 ) {
        if( "modules".equals( name ) ) {
          result = ENodeType.Content.getNodeType();
        }
      } else if( parent.getDepth() == 2 ) {
        if( "config".equals( name ) ) {
          result = ENodeType.Content.getNodeType();
        }
      }
    }
    return result;
  }
  
  public static boolean isAuthor() {
    return Admin.getValue().booleanValue();
  }

  public static <T, C> BiConsumer<T, C> createBiForWorkspaceDo( @Nonnull String workspace, ETriConsumer<Session, T, C, RepositoryException> consumer ) {
    return ($1, $2) -> {
      forWorkspaceDo( workspace, consumer, $1, $2 );
    };
  }
  
  private static <T, C> void forWorkspaceDo( String workspace, ETriConsumer<Session, T, C, RepositoryException> consumer, T obj1, C obj2 ) {
    Session                    session = null;
    RuntimeRepositoryException rrex    = null;
    try {
      session = MgnlContext.getJCRSession( workspace );
      consumer.accept( session, obj1, obj2 );
    } catch( Exception ex ) {
      rrex = toRuntimeRepositoryException( ex ); 
      throw rrex;
    } finally {
      if( session != null ) {
        try {
          session.save();
        } catch( Exception ex ) {
          if( rrex == null ) {
            throw toRuntimeRepositoryException(ex);
          }
        }
      }
    }
  }

  public static <T> Consumer<T> createForWorkspaceDo( @Nonnull String workspace, EBiConsumer<Session, T, RepositoryException> consumer ) {
    return ($_) -> {
      forWorkspaceDo( workspace, consumer, $_ );
    };
  }
  
  private static <T> void forWorkspaceDo( String workspace, EBiConsumer<Session, T, RepositoryException> consumer, T obj  ) {
    Session                    session = null;
    RuntimeRepositoryException rrex    = null;
    try {
      session = MgnlContext.getJCRSession( workspace );
      consumer.accept( session, obj );
    } catch( Exception ex ) {
      rrex = toRuntimeRepositoryException( ex ); 
      throw rrex;
    } finally {
      if( session != null ) {
        try {
          session.save();
        } catch( Exception ex ) {
          if( rrex == null ) {
            throw toRuntimeRepositoryException(ex);
          }
        }
      }
    }
  }

  public static <T, C, R> BiFunction<T, C, R> createBiForWorkspace( @Nonnull String workspace, ETriFunction<Session, T, C, R, RepositoryException> function ) {
    return ($1, $2) -> forWorkspace( workspace, function, $1, $2 );
  }

  private static <T, C, R> R forWorkspace( String workspace, ETriFunction<Session, T, C, R, RepositoryException> function, T obj1, C obj2 ) {
    Session                    session = null;
    RuntimeRepositoryException rrex    = null;
    try {
      session = MgnlContext.getJCRSession( workspace );
      return function.apply( session, obj1, obj2 );
    } catch( Exception ex ) {
      rrex = toRuntimeRepositoryException( ex ); 
      throw rrex;
    } finally {
      if( session != null ) {
        try {
          session.save();
        } catch( Exception ex ) {
          if( rrex == null ) {
            throw toRuntimeRepositoryException(ex);
          }
        }
      }
    }
  }
  
  public static <T, R> Function<T, R> createForWorkspace( @Nonnull String workspace, EBiFunction<Session, T, R, RepositoryException> function ) {
    return ($_) -> forWorkspace( workspace, function, $_ );
  }

  private static <T, R> R forWorkspace( String workspace, EBiFunction<Session, T, R, RepositoryException> function, T obj  ) {
    Session                    session = null;
    RuntimeRepositoryException rrex    = null;
    try {
      session = MgnlContext.getJCRSession( workspace );
      return function.apply( session, obj );
    } catch( Exception ex ) {
      rrex = toRuntimeRepositoryException( ex ); 
      throw rrex;
    } finally {
      if( session != null ) {
        try {
          session.save();
        } catch( Exception ex ) {
          if( rrex == null ) {
            throw toRuntimeRepositoryException(ex);
          }
        }
      }
    }
  }

  public static RuntimeRepositoryException toRuntimeRepositoryException( Exception ex ) {
    if( ex instanceof RuntimeRepositoryException ) {
      return (RuntimeRepositoryException) ex;
    } else if( ex instanceof RepositoryException ) {
      return new RuntimeRepositoryException( (RepositoryException) ex );
    } else {
      return toRuntimeRepositoryException( new RepositoryException(ex) );
    }
  }
  
  @SuppressWarnings("null")
  @Nullable
  public static <R> R getParentalModel( @Nullable Object current, @Nonnull Class<R> resultType, @Nonnull Function<Object, Object> getparent ) {
    R result = null;
    if( current != null ) {
      if( resultType.isInstance( current ) ) {
        result = (R) current;
      } else {
        result = getParentalModel( getparent.apply( current ), resultType, getparent );
      }
    }
    return result;
  }

  @Nonnull
  public static String toComponentId( @Nonnull Node node ) {
    String uuid = getIdentifier( node );
    int    idx  = uuid.indexOf('-');
    if( idx != -1 ) {
      uuid = uuid.substring( 0, idx );
    }
    return String.format( "id%s", uuid );
  }

  public static <R> R forContext( JcrExecutionUnit<R> unit ) {
    try {
      if( MgnlContext.hasInstance() ) {
        return unit.exec( MgnlContext.getJCRSession( unit.getWorkspace() ) );
      } else {
        return MgnlContext.doInSystemContext( unit, true );
      }
    } catch( Exception ex ) {
      throw toRuntimeRepositoryException(ex);
    }
  }
  
} /* ENDCLASS */
