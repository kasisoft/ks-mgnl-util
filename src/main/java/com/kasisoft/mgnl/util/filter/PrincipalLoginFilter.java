package com.kasisoft.mgnl.util.filter;

import info.magnolia.context.*;

import javax.servlet.http.*;

import javax.servlet.*;

import java.security.*;

import java.io.*;

import info.magnolia.cms.filters.*;
import info.magnolia.cms.security.*;

/**
 * This filter allows to use the current mgnl user as a Principal if necessary. This comes in handy in 
 * combination with webservices.
 * 
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class PrincipalLoginFilter extends AbstractMgnlFilter {

  @Override
  public void doFilter( HttpServletRequest request, HttpServletResponse response, FilterChain chain ) throws IOException, ServletException {
    chain.doFilter( new PrincipalRequestWrapper( request ), response );
  }

  private static class PrincipalRequestWrapper extends HttpServletRequestWrapper {

    public PrincipalRequestWrapper( HttpServletRequest request ) {
      super( request );
    }

    @Override
    public Principal getUserPrincipal() {
      Principal result = super.getUserPrincipal();
      if( result == null ) {
        result = MgnlContext.getUser();
      }
      return result;
    }

    @Override
    public boolean isUserInRole( String role ) {
      Principal principal = getUserPrincipal();
      if( principal instanceof User ) {
        return ((User) principal).hasRole( role );
      } else {
        return super.isUserInRole( role );
      }
    }
    
  } /* ENDCLASS */
  
} /* ENDCLASS */
