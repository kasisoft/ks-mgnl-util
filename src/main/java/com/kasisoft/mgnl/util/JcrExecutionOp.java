package com.kasisoft.mgnl.util;

import javax.annotation.*;
import javax.jcr.*;

import java.util.function.*;

import lombok.experimental.*;

import lombok.*;

import info.magnolia.cms.security.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JcrExecutionOp<T> extends JCRSessionOp<Void> {

  BiConsumer<Session, T>    consumer;
  
  @Getter
  String                    workspace;
  
  boolean                   save;
  T                         value;
  
  public JcrExecutionOp( @Nonnull String ws, @Nonnull BiConsumer<Session, T> biconsumer, T val ) {
    super( ws );
    consumer  = biconsumer;
    workspace = ws;
    value     = val;
    save      = false;
  }
  
  public JcrExecutionOp saveAfterExecution() {
    save = true;
    return this;
  }
  
  @Override
  public Void exec( Session session ) throws RepositoryException {
    try {
      consumer.accept( session, value );
      return null;
    } finally {
      if( save ) {
        session.save();
      }
    }
  }
  
} /* ENDCLASS */
