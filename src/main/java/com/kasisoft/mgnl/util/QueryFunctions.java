package com.kasisoft.mgnl.util;

import static com.kasisoft.mgnl.util.internal.Messages.*;

import info.magnolia.context.*;

import info.magnolia.jcr.*;

import javax.annotation.*;
import javax.jcr.*;
import javax.jcr.Node;
import javax.jcr.query.*;

import java.util.function.*;

import java.util.stream.*;

import java.util.regex.*;

import java.util.*;

import lombok.extern.slf4j.*;

import lombok.experimental.*;

import lombok.*;

import info.magnolia.cms.security.*;

/**
 * A simple helper allowing to process queries using different jcr languages.
 * 
 * @author daniel.kasmeroglu@kasisoft.net
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings("deprecation")
public enum QueryFunctions {
    
  // jqom  ( Query.JCR_JQOM ),
  sql   ( Query.SQL      ), 
  sql2  ( Query.JCR_SQL2 ), 
  xpath ( Query.XPATH    );

  private static final Pattern INVALID_XPATH = Pattern.compile( "(^[0-9]|[/][0-9])" );
  
  @Getter
  String   language;

  QueryFunctions( String lang ) {
    language = lang;
  }
  
  public static boolean isInvalidXpath( String value ) {
    return INVALID_XPATH.matcher( value ).find();
  }

  public static void loggingErrorHandler( Exception ex ) {
    log.error( ex.getLocalizedMessage(), ex );
  }

  public static void runtimeExErrorHandler( Exception ex ) {
    if( ex instanceof RuntimeRepositoryException ) {
      throw (RuntimeRepositoryException) ex;
    } else if( ex instanceof RepositoryException ) {
      throw new RuntimeRepositoryException( (RepositoryException) ex );
    } else {
      throw new RuntimeRepositoryException( new RepositoryException( ex ) );
    }
  }

  private void handle( Exception ex, Consumer<Exception> handler ) {
    if( handler == null ) {
      handler = QueryFunctions::loggingErrorHandler;
    }
    handler.accept( ex );
  }
  
  @Nullable
  public QueryResult executeQuery( @Nonnull String workspace, @Nonnull String fmt, Object ... args ) {
    return executeQuery( workspace, fmt, null, args );
  }
  
  @Nullable
  public QueryResult executeQuery( @Nonnull String workspace, @Nonnull String fmt, @Nullable Consumer<Exception> handler, Object ... args ) {
    if( (args != null) && (args.length > 0) ) {
      fmt = String.format( fmt, args );
    }
    try {
      JcrExecution exec = new JcrExecution( workspace, fmt );
      if( MgnlContext.hasInstance() ) {
        return exec.exec();
      } else {
        return MgnlContext.doInSystemContext( exec, true );
      }
    } catch( Exception ex ) {
      handle( ex, handler );
      return null;
    }
  }
  
  @Nullable
  public Node find( @Nonnull String workspace, @Nonnull String fmt, Object... args ) {
    return find( workspace, fmt, null, args );
  }
  
  @Nullable
  public Node find( @Nonnull String workspace, @Nonnull String fmt, @Nullable Consumer<Exception> handler, Object... args ) {
    Node result = null;
    try {
      List<Node> nodes = list( workspace, fmt, handler, args );
      if( (nodes != null) && (nodes.size() > 0) ) {
        result = nodes.get(0);
      }
    } catch( Exception ex ) {
      handle( ex, handler );
    }
    return result;
  }
  
  @Nonnull
  public List<Node> list( @Nonnull String workspace, @Nonnull String fmt, @Nullable Consumer<Exception> handler, Object... args ) {
    try {
      return loadRows( executeQuery( workspace, fmt, handler, args ) );
    } catch( Exception ex ) {
      handle( ex, handler );
      return Collections.emptyList();
    }
  }

  @Nonnull
  public List<String> listUuids( @Nonnull String workspace, @Nonnull String fmt, @Nullable Consumer<Exception> handler, Object... args ) {
    try {
      return list( workspace, fmt, handler, args ).stream()
              .map( NodeFunctions::getIdentifier )
              .collect( Collectors.toList() );
    } catch( Exception ex ) {
      handle( ex, handler );
      return Collections.emptyList();
    }
  }
  
  private List<Node> loadRows( QueryResult queryResult ) throws RepositoryException {
    List<Node> result = null;
    if( queryResult != null ) {
      result               = new ArrayList<>();
      RowIterator iterator = queryResult.getRows();
      while( iterator.hasNext() ) {
        result.add( iterator.nextRow().getNode() );
      }
    } else {
      result = Collections.emptyList();
    }
    return result;
  }

  @Nonnull
  public <R> List<R> list( @Nonnull String workspace, Function<Node, R> transform, @Nonnull String fmt, @Nullable Consumer<Exception> handler, Object... args ) {
    try {
      return list( workspace, fmt, handler, args ).stream()
              .map( transform )
              .collect( Collectors.toList() );
    } catch( Exception ex ) {
      handle( ex, handler );
      return Collections.emptyList();
    }
  }

  private class JcrExecution extends JCRSessionOp<QueryResult> {
    
    private String   queryStr;
    
    public JcrExecution( String workspace, String query ) {
      super( workspace );
      queryStr = query;
    }
    
    @Override
    public QueryResult exec( Session session ) throws RepositoryException {
      log.trace( executing_query.format( name(), queryStr ) );
      QueryManager qm     = session.getWorkspace().getQueryManager();
      Query        query  = qm.createQuery( queryStr, language );
      return query.execute();
    }
    
  } /* ENDCLASS */
  
} /* ENDCLASS */
