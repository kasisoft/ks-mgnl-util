package com.kasisoft.mgnl.util.filter;

import static com.kasisoft.mgnl.util.internal.Messages.*;

import info.magnolia.context.*;

import org.apache.commons.lang3.*;

import javax.servlet.http.*;

import javax.servlet.*;

import javax.annotation.*;

import java.io.*;

import lombok.extern.slf4j.*;

import lombok.experimental.*;

import lombok.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CaptureReplayWrapper extends HttpServletResponseWrapper {
  
  @Setter
  int       status;
  
  boolean   error;
  String    message;
  String    redirection;
  Storage   storage;
  
  public CaptureReplayWrapper( HttpServletResponse target ) {
    super( target );
    status = SC_OK;
  }

  @Nonnull
  @Override
  public String getCharacterEncoding() {
    String result = super.getCharacterEncoding();
    if( result == null ) {
      result = CharEncoding.UTF_8;
    }
    return result;
  }

  @Override
  public void sendError( int newStatus, String errorMessage ) throws IOException {
    message = errorMessage;
    status  = newStatus;
    error   = true;
  }

  @Override
  public void sendError( int newStatus ) throws IOException {
    status  = newStatus;
    error   = true;
  }

  @Override
  public void sendRedirect( String location ) throws IOException {
    status      = SC_MOVED_TEMPORARILY;
    redirection = location;
  }

  private ServletOutputStream getSuperOutputStream() throws IOException {
    return super.getOutputStream();
  }
  
  @Override
  public ServletOutputStream getOutputStream() {
    checkStorage( StreamStorage.class, "getWriter", "getOutputStream" );
    if( storage == null ) {
      storage = new StreamStorage();
    }
    return (ServletOutputStream) storage;
  }
  
  private PrintWriter getSuperWriter() throws IOException {
    return super.getWriter();
  }
  
  @Override
  public PrintWriter getWriter() throws IOException {
    checkStorage( WriterStorage.class, "getOutputStream", "getWriter" );
    if( storage == null ) {
      storage = new WriterStorage();
    }
    return (PrintWriter) storage;
  }

  private void checkStorage( Class<?> expected, String firstMethod, String secondMethod ) {
    if( (storage != null) && (! expected.isInstance( storage )) ) {
      String msg = String.format( "Cannot call method '%s' prior to '%s' !", firstMethod, secondMethod );
      log.error( msg );
      throw new IllegalStateException( msg );
    }
  }
  
  @Nonnull
  public String getBufferAsString() {
    String result = "";
    if( storage != null ) {
      result = storage.readText( getCharacterEncoding() );
    }
    return result;
  }
  
  public void serveBuffer() throws IOException {
    if( storage != null ) {
      storage.serve( this );
    }
  }

  public void replay() throws IOException {
    flushBuffer();
    replayHeadersAndStatus();
    serveBuffer();
  }
  
  public void writeText( String text ) throws IOException {
    if( (text != null) && (storage != null) ) {
      storage.reset();
      storage.writeText( text, getCharacterEncoding() );
    }
  }
  
  public void replayHeadersAndStatus() throws IOException {
    if( error ) {
      if( message != null ) {
        super.sendError( status, message );
      } else {
        super.sendError( status );
      }
    } else if( redirection != null ) {
      super.sendRedirect( redirection );
    } else {
      super.setStatus( status );
    }
  }

  @Override
  public void flushBuffer() throws IOException {
    if( storage != null ) {
      storage.flush();
    }
  }
  
  private interface Storage<S> extends Flushable {

    @Nonnull
    S getContent();
    
    @Nonnull
    String readText( @Nonnull String encoding );
    
    void writeText( @Nonnull String text, @Nonnull String encoding ) throws IOException ;
    
    void reset();
    
    void serve( @Nonnull CaptureReplayWrapper wrapper ) throws IOException;
    
  } /* ENDINTERFACE */
  
  private static class StreamStorage extends ServletOutputStream implements Storage<ByteArrayOutputStream> {

    @Getter
    ByteArrayOutputStream content = new ByteArrayOutputStream();
    
    @Nonnull
    @Override
    public String readText( @Nonnull String encoding ) {
      try {
        return getContent().toString( encoding );
      } catch( UnsupportedEncodingException ex) {
        log.error( error_unsupported_encoding.format( encoding ), ex );
        return "";
      }
    }

    @Override
    public void writeText( @Nonnull String text, @Nonnull String encoding ) throws IOException {
      try {
        write( text.getBytes( encoding ) );
      } catch( UnsupportedEncodingException ex ) {
        log.error( error_unsupported_encoding.format( encoding ), ex );
      }
    }
    
    @Override
    public void reset() {
      content.reset();
    }

    @Override
    public void write( int b ) throws IOException {
      content.write(b);
    }

    @Override
    public void write( byte[] b, int off, int len ) throws IOException {
      content.write( b, off, len );
    }

    @Override
    public void serve( CaptureReplayWrapper wrapper ) throws IOException {
      try( OutputStream out = wrapper.getSuperOutputStream() ) {
        byte[] data = content.toByteArray();
        wrapper.setContentLength( data.length );
        out.write( data );
      }
    }

    @Override
    public void flush() throws IOException {
      content.flush();
    }
    
    @Override
    public boolean isReady() {
      return false;
    }

    @Override
    public void setWriteListener( WriteListener listener ) {
    }

  } /* ENDCLASS */

  private static class WriterStorage extends PrintWriter implements Storage<StringWriter> {

    public WriterStorage() {
      super( new StringWriter() );
    }

    @Nonnull
    @Override
    public String readText( @Nonnull String encoding ) {
      StringWriter content = getContent();
      if( content != null ) {
        return content.toString();
      } else {
        log.error( error_accessed_stream.format( MgnlContext.getWebContext().getRequest().getRequestURL() ) );
        return "";
      }
    }

    @Override
    public void writeText( @Nonnull String text, @Nonnull String encoding ) {
      print( text );
    }
    
    @Override
    public void reset() {
      getContent().getBuffer().setLength(0);
    }

    @Nonnull
    @Override
    public StringWriter getContent() {
      return (StringWriter) out;
    }

    @Override
    public void serve( @Nonnull CaptureReplayWrapper wrapper ) throws IOException {
      try( PrintWriter writer = wrapper.getSuperWriter() ) {
        writer.print( getContent().toString() );
      }
    }

  } /* ENDCLASS */

} /* ENDCLASS */