package com.kasisoft.mgnl.filter;

import com.kasisoft.libs.common.constants.*;

import javax.servlet.http.*;

import javax.servlet.*;

import java.util.*;

import java.net.*;

import java.io.*;

import lombok.experimental.*;

import lombok.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomHttpServletResponse implements HttpServletResponse {

  String                characterEncoding;
  String                contentType;
  int                   contentLength;
  long                  contentLengthLong;
  int                   bufferSize;
  boolean               committed;
  Locale                locale;
  int                   status;
  Map<String, String>   headers = new HashMap<>();
  String                redirect;
  String                errorMessage;
  ServletOutputStream   outputStream;
  PrintWriter           writer;
  List<Cookie>          cookies = new ArrayList<>();

  @Override
  public void flushBuffer() throws IOException {
  }

  @Override
  public void resetBuffer() {
  }

  @Override
  public void reset() {
  }

  @Override
  public void addCookie( Cookie cookie ) {
    cookies.add( cookie );
  }

  @Override
  public boolean containsHeader( String name ) {
    return headers.containsKey( name );
  }

  @Override
  public String encodeURL( String url ) {
    try {
      URL urlObj = new URL( url );
      return urlObj.toExternalForm();
    } catch( Exception ex ) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public String encodeRedirectURL( String url ) {
    return encodeURL( url );
  }

  @SuppressWarnings("deprecation")
  @Override
  public String encodeUrl( String url ) {
    return encodeURL( url );
  }

  @SuppressWarnings("deprecation")
  @Override
  public String encodeRedirectUrl( String url ) {
    return encodeURL( url );
  }

  @Override
  public void sendError( int sc, String msg ) throws IOException {
    setStatus( sc );
    setErrorMessage( msg );
  }

  @Override
  public void sendError( int sc ) throws IOException {
    setStatus( sc );
  }

  @Override
  public void sendRedirect( String location ) throws IOException {
    setRedirect( location );
    setStatus( HttpStatusCode.MovedPermanently.getCode() );
  }

  @Override
  public Collection<String> getHeaders( String name ) {
    return Arrays.asList( headers.get( name ) );
  }

  @Override
  public void setDateHeader( String name, long date ) {
    headers.put( name, String.valueOf( date ) );
  }

  @Override
  public void addDateHeader( String name, long date ) {
    headers.put( name, String.valueOf( date ) );
  }

  @Override
  public void setHeader( String name, String value ) {
    headers.put( name, value );
  }

  @Override
  public void addHeader( String name, String value ) {
    headers.put( name, String.valueOf( value ) );
  }

  @Override
  public void setIntHeader( String name, int value ) {
    headers.put( name, String.valueOf( value ) );
  }

  @Override
  public void addIntHeader( String name, int value ) {
    headers.put( name, String.valueOf( value ) );
  }

  @Override
  public String getHeader( String name ) {
    return headers.get( name );
  }

  @Override
  public Collection<String> getHeaderNames() {
    return Collections.unmodifiableSet( headers.keySet() );
  }

  @SuppressWarnings("deprecation")
  @Override
  public void setStatus( int sc, String sm ) {
    setStatus( sc );
  }

} /* ENDCLASS */
