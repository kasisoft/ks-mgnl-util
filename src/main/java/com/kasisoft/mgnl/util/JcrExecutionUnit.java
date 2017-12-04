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
public class JcrExecutionUnit<T> extends JCRSessionOp<T> {

  Supplier<T>         supplier;
  Consumer<Session>   consumer;
  
  @Getter
  String              workspace;
  
  boolean             save;
  
  public JcrExecutionUnit( @Nonnull String ws, @Nonnull Consumer<Session> con ) {
    super( ws );
    consumer  = con;
    workspace = ws;
    save      = false;
  }

  public JcrExecutionUnit( @Nonnull String ws, @Nonnull Supplier<T> sup ) {
    super( ws );
    supplier  = sup;
    workspace = ws;
    save      = false;
  }
  
  public JcrExecutionUnit saveAfterExecution() {
    save = true;
    return this;
  }
  
  @Override
  public T exec( Session session ) throws RepositoryException {
    try {
      if( supplier != null ) {
        return supplier.get();
      } else {
        consumer.accept( session );
        return null;
      }
    } finally {
      if( save ) {
        session.save();
      }
    }
  }
  
} /* ENDCLASS */
