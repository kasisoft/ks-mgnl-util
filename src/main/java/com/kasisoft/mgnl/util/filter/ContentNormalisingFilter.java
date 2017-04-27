package com.kasisoft.mgnl.util.filter;

import org.apache.commons.lang3.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

import javax.servlet.http.*;

import javax.servlet.*;

import java.io.*;

import info.magnolia.cms.filters.*;


/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class ContentNormalisingFilter extends AbstractMgnlFilter {

  @Override
  public void doFilter( HttpServletRequest request, HttpServletResponse response, FilterChain chain ) throws IOException, ServletException {
    CaptureReplayWrapper buffering = new CaptureReplayWrapper( response );
    chain.doFilter( request, buffering );
    String html = buffering.getBufferAsString();
    if( StringUtils.isNotBlank( html ) && isHtmlByContentType( response.getContentType() ) ) {
      Document document = Jsoup.parse( html );
      document.normalise();
      if( html.contains( "<html" ) ) {
        buffering.writeText( document.html() );
      } else {
        buffering.writeText( document.body().html() );
      }
    }
    buffering.replay();
  }
  
  private boolean isHtmlByContentType( String contentType ) {
    return (contentType != null) && contentType.contains( "text/html" );
  }
  
} /* ENDCLASS */
