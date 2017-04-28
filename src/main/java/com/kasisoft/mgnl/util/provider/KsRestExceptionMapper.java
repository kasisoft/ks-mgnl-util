package com.kasisoft.mgnl.util.provider;

import static com.kasisoft.mgnl.util.internal.Messages.*;

import javax.annotation.*;
import javax.jcr.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

import java.util.*;

import lombok.extern.slf4j.*;

import lombok.experimental.*;

import lombok.*;

/**
 * This implementation also supports JAX-RS exceptions that indicate an invalid access. It can be configured
 * in this property:
 * 
 * <code>/modules/rest-integration/config/additionalProviders/restExceptionMapper@providerClass</code>
 * 
 * @author daniel.kasmeroglu@kasisoft.net
 */
@Provider
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KsRestExceptionMapper implements ExceptionMapper<Throwable> {

  List<Class<? extends Exception>>   forbiddenExceptions;
  
  public KsRestExceptionMapper() {
    forbiddenExceptions = new ArrayList<>();
    forbiddenExceptions.add( AccessDeniedException . class );
    forbiddenExceptions.add( ForbiddenException    . class );
  }
  
  public void addForbiddenException( @Nonnull Class<? extends Exception> ex ) {
    forbiddenExceptions.add( ex );
  }
  
  private boolean isForbidden( Throwable exception ) {
    for( Class<? extends Exception> cl : forbiddenExceptions ) {
      if( cl.isInstance( exception ) ) {
        return true;
      }
    }
    return false;
  }
  
  @Override
  public Response toResponse( Throwable exception ) {

    Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;

    if( isForbidden( exception ) ) {
      status = Response.Status.FORBIDDEN;
    } else {
      // We don't test for javax.jcr.PathNotFoundException because the 404 response should only be used if the
      // resource targeted isn't found, where as javax.jcr.PathNotFoundException could be encountered reading
      // something else.
      log.error( error_rest_endpoint.format( status.getStatusCode(), exception.getLocalizedMessage() ), exception );
    }

    return Response.status( status ).entity( exception.getMessage() ).type( MediaType.TEXT_PLAIN_TYPE ).build();
    
  }
  
} /* ENDCLASS */

