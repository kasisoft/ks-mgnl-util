package com.kasisoft.mgnl.util.provider;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import com.kasisoft.libs.common.constants.*;

import org.testng.annotations.*;

import javax.jcr.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class KsRestExceptionMapperTest {

  @Test
  public void internalError() {
    
    KsRestExceptionMapper mapper = new KsRestExceptionMapper();
    
    Response response = mapper.toResponse( new RuntimeException( "oops" ) );
    assertNotNull( response );
    
    assertThat( response.getStatus(), is( HttpStatusCode.InternalServerError.getCode() ) );
    
  }

  @Test
  public void forbiddenException() {
    
    KsRestExceptionMapper mapper = new KsRestExceptionMapper();
    
    Response response = mapper.toResponse( new ForbiddenException( "oops" ) );
    assertNotNull( response );
    
    assertThat( response.getStatus(), is( HttpStatusCode.Forbidden.getCode() ) );
    
  }

  @Test
  public void accessDeniedException() {
    
    KsRestExceptionMapper mapper = new KsRestExceptionMapper();
    
    Response response = mapper.toResponse( new AccessDeniedException( "oops" ) );
    assertNotNull( response );
    
    assertThat( response.getStatus(), is( HttpStatusCode.Forbidden.getCode() ) );
    
  }

  @Test
  public void customForbiddenException() {
    
    KsRestExceptionMapper mapper = new KsRestExceptionMapper();
    mapper.addForbiddenException( CustomForbiddenException.class );
    
    Response response = mapper.toResponse( new CustomForbiddenException() );
    assertNotNull( response );
    
    assertThat( response.getStatus(), is( HttpStatusCode.Forbidden.getCode() ) );
    
  }

  private static class CustomForbiddenException extends RuntimeException {
  } /* ENDCLASS */
  
} /* ENDCLASS */
