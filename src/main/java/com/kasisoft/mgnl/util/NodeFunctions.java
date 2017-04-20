package com.kasisoft.mgnl.util;

import static com.kasisoft.mgnl.util.JcrProperties.*;
import static com.kasisoft.mgnl.util.internal.Messages.*;

import info.magnolia.repository.*;

import info.magnolia.context.*;

import info.magnolia.jcr.util.*;

import info.magnolia.jcr.*;

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
    } catch( RepositoryException ex ) {
      throw new RuntimeRepositoryException(ex);
    }
  }

  @Nonnull
  public static String getName( @Nonnull Node node ) {
    try {
      return node.getName();
    } catch( RepositoryException ex ) {
      throw new RuntimeRepositoryException(ex);
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
    } catch( RepositoryException ex ) {
      throw new RuntimeRepositoryException(ex);
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
    } catch( RepositoryException ex ) {
      throw new RuntimeRepositoryException(ex);
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
      } catch( RepositoryException ex ) {
        throw new RuntimeRepositoryException(ex);
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
    } catch( RepositoryException ex ) {
      throw new RuntimeRepositoryException(ex);
    }
  }

  @Nullable
  public static Node getNode( @Nonnull Node node, @Nonnull String relpath ) {
    Node result = null;
    try {
      if( node.hasNode( relpath ) ) {
        result = node.getNode( relpath );
      }
    } catch( RepositoryException ex ) {
      throw new RuntimeRepositoryException(ex);
    }
    return result;
  }
  
  public static boolean isAuthor() {
    boolean result = false;
    try {
      Node node = MgnlContext.getJCRSession( RepositoryConstants.CONFIG ).getNode( "/server" );
      result    = Admin.getValue( node );
    } catch( RepositoryException ex ) {
      throw new RuntimeRepositoryException( ex );
    }
    return result;
  }
  
} /* ENDCLASS */
