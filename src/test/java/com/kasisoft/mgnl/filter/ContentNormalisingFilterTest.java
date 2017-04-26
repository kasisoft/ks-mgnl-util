package com.kasisoft.mgnl.filter;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.kasisoft.libs.common.constants.*;

import org.testng.annotations.*;

import javax.servlet.http.*;

import javax.servlet.*;

import java.io.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class ContentNormalisingFilterTest {

  private static final String TEXT_JSON = "{ a: 'b' }";
  
  private static final String TEXT_HTML = "<html><head><title>dodo</title></head><body><p>Paragraph</p></body></html>";
  
  private static final String TEXT_HTML_FORMATTED = "" 
   + "<html>\n"
   + " <head>\n"
   + "  <title>dodo</title>\n"
   + " </head>\n"
   + " <body>\n"
   + "  <p>Paragraph</p>\n"
   + " </body>\n"
   + "</html>"
   ;
  
  private CustomHttpServletResponse newResponse( StringWriter writer ) {
    CustomHttpServletResponse result  = new CustomHttpServletResponse();
    result.setWriter( new PrintWriter( writer ) );
    return result;
  }
  
  @Test
  public void noHtml() throws Exception {
    
    StringWriter              strWriter = new StringWriter();
    
    HttpServletRequest        request   = mock( HttpServletRequest.class );
    CustomHttpServletResponse response  = newResponse( strWriter );
    
    ContentNormalisingFilter  filter    = new ContentNormalisingFilter();
    filter.doFilter( request, response, this::jsonResponse );
    
    assertThat( strWriter.toString(), is( TEXT_JSON ) );
    assertThat( response.getContentType(), is( "application/json" ) );
    
  }

  @Test
  public void html() throws Exception {
    
    StringWriter              strWriter = new StringWriter();
    
    HttpServletRequest        request   = mock( HttpServletRequest.class );
    CustomHttpServletResponse response  = newResponse( strWriter );
    
    ContentNormalisingFilter  filter    = new ContentNormalisingFilter();
    filter.doFilter( request, response, this::htmlResponse );
    
    assertThat( strWriter.toString(), is( TEXT_HTML_FORMATTED ) );
    assertThat( response.getContentType(), is( "text/html" ) );
    
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void writerAndStream() throws Exception {
    
    StringWriter              strWriter = new StringWriter();
    
    HttpServletRequest        request   = mock( HttpServletRequest.class );
    CustomHttpServletResponse response  = newResponse( strWriter );
    
    ContentNormalisingFilter  filter    = new ContentNormalisingFilter();
    filter.doFilter( request, response, this::writerAndStream );
    
    assertThat( strWriter.toString(), is( TEXT_HTML_FORMATTED ) );
    assertThat( response.getContentType(), is( "text/html" ) );
    
  }

  @Test
  public void errorCode() throws Exception {
    
    StringWriter              strWriter = new StringWriter();
    
    HttpServletRequest        request   = mock( HttpServletRequest.class );
    CustomHttpServletResponse response  = newResponse( strWriter );
    
    ContentNormalisingFilter  filter    = new ContentNormalisingFilter();
    filter.doFilter( request, response, this::errorCode );
    
    assertThat( response.getStatus(), is( HttpStatusCode.NotFound.getCode() ) );
    assertThat( response.getContentType(), is( "text/html" ) );
    
  }

  @Test
  public void redirect() throws Exception {
    
    StringWriter              strWriter = new StringWriter();
    
    HttpServletRequest        request   = mock( HttpServletRequest.class );
    CustomHttpServletResponse response  = newResponse( strWriter );
    
    ContentNormalisingFilter  filter    = new ContentNormalisingFilter();
    filter.doFilter( request, response, this::redirect );
    
    assertThat( response.getStatus(), is( HttpStatusCode.MovedPermanently.getCode() ) );
    assertThat( response.getRedirect(), is( "http://www.amiga-news.de" ) ); 
    
  }

  public void jsonResponse( ServletRequest request, ServletResponse response ) {
    try {
      String text = TEXT_JSON;
      response.getWriter().write( text );
      response.setContentType( "application/json" );
    } catch( Exception ex ) {
      throw new RuntimeException(ex);
    }
  }

  public void htmlResponse( ServletRequest request, ServletResponse response ) {
    try {
      String text = TEXT_HTML;
      response.getWriter().write( text );
      response.setContentType( "text/html" );
    } catch( Exception ex ) {
      throw new RuntimeException(ex);
    }
  }

  public void writerAndStream( ServletRequest request, ServletResponse response ) {
    try {
      String text = TEXT_HTML;
      response.setContentType( "text/html" );
      response.getWriter().write( text );
      response.getOutputStream().write( TEXT_HTML.getBytes() );
    } catch( Exception ex ) {
      throw new RuntimeException(ex);
    }
  }

  public void errorCode( ServletRequest request, ServletResponse response ) {
    try {
      String text = TEXT_HTML;
      response.setContentType( "text/html" );
      response.getWriter().write( text );
      ((HttpServletResponse) response).sendError( HttpStatusCode.NotFound.getCode() );
    } catch( Exception ex ) {
      throw new RuntimeException(ex);
    }
  }

  public void redirect( ServletRequest request, ServletResponse response ) {
    try {
      ((HttpServletResponse) response).sendRedirect( "http://www.amiga-news.de" );
    } catch( Exception ex ) {
      throw new RuntimeException(ex);
    }
  }

} /* ENDCLASS */
