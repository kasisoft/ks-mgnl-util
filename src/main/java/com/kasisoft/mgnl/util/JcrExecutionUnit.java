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

  Supplier<T>            supplier;
  Function<Session, T>   function;
  
  @Getter
  String                 workspace;
  
  boolean                save;
  
  public JcrExecutionUnit( @Nonnull String ws, @Nonnull Function<Session, T> func ) {
    super( ws );
    function  = func;
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
        return function.apply( session );
      }
    } finally {
      if( save ) {
        session.save();
      }
    }
  }
  
} /* ENDCLASS */
