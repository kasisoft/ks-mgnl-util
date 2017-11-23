package com.kasisoft.mgnl.util;

import info.magnolia.jcr.util.*;

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
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ENodeType implements Predicate<Node>, Function<Node, Optional<ENodeType>>, Comparable<ENodeType> {

  public static final ENodeType LastModified    = new ENodeType( NodeTypes . LastModified  . NAME );
  public static final ENodeType Activatable     = new ENodeType( NodeTypes . Activatable   . NAME );
  public static final ENodeType Created         = new ENodeType( NodeTypes . Created       . NAME );
  public static final ENodeType Renderable      = new ENodeType( NodeTypes . Renderable    . NAME );
  public static final ENodeType Deleted         = new ENodeType( NodeTypes . Deleted       . NAME );
  public static final ENodeType Versionable     = new ENodeType( NodeTypes . Versionable   . NAME );
  public static final ENodeType Folder          = new ENodeType( NodeTypes . Folder        . NAME );
  public static final ENodeType Resource        = new ENodeType( NodeTypes . Resource      . NAME );
  public static final ENodeType Content         = new ENodeType( NodeTypes . Content       . NAME );
  public static final ENodeType ContentNode     = new ENodeType( NodeTypes . ContentNode   . NAME );
  public static final ENodeType NodeData        = new ENodeType( NodeTypes . NodeData      . NAME );
  public static final ENodeType Page            = new ENodeType( NodeTypes . Page          . NAME );
  public static final ENodeType Area            = new ENodeType( NodeTypes . Area          . NAME );
  public static final ENodeType Component       = new ENodeType( NodeTypes . Component     . NAME );
  public static final ENodeType User            = new ENodeType( NodeTypes . User          . NAME );
  public static final ENodeType Role            = new ENodeType( NodeTypes . Role          . NAME );
  public static final ENodeType Group           = new ENodeType( NodeTypes . Group         . NAME );
  public static final ENodeType System          = new ENodeType( NodeTypes . System        . NAME );
  public static final ENodeType MetaData        = new ENodeType( NodeTypes . MetaData      . NAME );
  public static final ENodeType HasVersion      = new ENodeType( NodeTypes . HasVersion    . NAME );
  ;
  
  String  nodetype;
  
  public ENodeType( String ntName ) {
    nodetype = ntName;
    LocalData.values.put( ntName, this );
  }
  
  public String getNodeType() {
    return nodetype;
  }
  
  public Node addNode( @Nonnull Node parent, @Nonnull String name ) {
    try {
      return parent.addNode( name, nodetype );
    } catch( Exception ex ) {
      throw NodeFunctions.toRuntimeRepositoryException( ex );
    }
  }
  
  public void addMixin( @Nonnull Node node ) {
    try {
      node.addMixin( nodetype );
    } catch( Exception ex ) {
      throw NodeFunctions.toRuntimeRepositoryException( ex );
    }
  }
  
  public Consumer<Node> ifThen( @Nonnull Consumer<Node> consumer ) {
    return $ -> {
      if( test($) ) {
        consumer.accept($);
      }
    };
  }

  public <C> Consumer<Node> ifThen( @Nonnull BiConsumer<Node, C> consumer, C context ) {
    return $ -> { 
      if( test($) ) {
        consumer.accept( $, context );
      }
    };
  }

  public Consumer<Node> ifNot( @Nonnull Consumer<Node> consumer ) {
    return $ -> { 
      if( ! test($) ) {
        consumer.accept($);
      }
    };
  }

  public <C> Consumer<Node> ifNot( @Nonnull BiConsumer<Node, C> consumer, C context ) {
    return $ -> { 
      if( ! test($) ) {
        consumer.accept( $, context );
      }
    };
  }

  public Consumer<Node> ifThenElse( @Nonnull Consumer<Node> then, @Nonnull Consumer<Node> elseC ) {
    return $ -> { 
      if( test($) ) {
        then.accept($);
      } else {
        elseC.accept($);
      }
    };
  }

  public <C> Consumer<Node> ifThenElse( @Nonnull BiConsumer<Node, C> then, @Nonnull BiConsumer<Node, C> elseC, C context ) {
    return $ -> { 
      if( test($) ) {
        then.accept( $, context );
      } else {
        elseC.accept( $, context );
      }
    };
  }
  
  @Override
  public boolean test( @Nullable Node node ) {
    boolean result = false;
    if( node != null ) {
      try {
        result = node.isNodeType( nodetype );
      } catch( Exception ex ) {
        log.warn( "The node {} could not be tested if it matches the type {}. Assuming that it's not matching. Cause: {}", node, nodetype, ex.getLocalizedMessage(), ex );
      }
    }
    return result;
  }

  @Override
  public Optional<ENodeType> apply( @Nullable Node node ) {
    Optional<ENodeType> result = Optional.empty();
    if( node != null ) {
      try {
        ENodeType en = findValueOf( node.getPrimaryNodeType().getName() );
        if( en != null ) {
          result = Optional.of(en);
        }
      } catch( Exception ex ) {
        log.warn( "Failed to get primary type of node {}. Cause: {}", node, ex.getLocalizedMessage(), ex );
      }
    }
    return result;
  }
  
  @Nullable 
  public ENodeType findValueOf( @Nullable String nodetype ) {
    ENodeType result = null;
    try {
      if( nodetype != null ) {
        result = valueOf( nodetype );
      }
    } catch( IllegalArgumentException ex ) {
      // no reporting necessary as this is a valid use case
    }
    return result;
  }

  public static ENodeType valueByName( String ntName ) {
    ENodeType result = null;
    if( (ntName != null) && LocalData.values.containsKey( ntName ) ) {
      result = LocalData.values.get( ntName );
    }
    return result;
  }

  public static List<ENodeType> values() {
    List<ENodeType> result = new ArrayList<>( LocalData.values.values() );
    Collections.sort( result );
    return result;
  }
  
  public static ENodeType valueOf( String ntName ) {
    ENodeType result = valueByName( ntName );
    if( result != null ) {
      return result;
    } else {
      throw new IllegalArgumentException();
    }
  }
  
  @Override
  public int compareTo( @Nonnull ENodeType other ) {
    return nodetype.compareTo( other.nodetype );
  }
  
  private static class LocalData {
    
    public static Map<String, ENodeType> values = new HashMap<>();
    
  } /* ENDCLASS */

} /* ENDCLASS */