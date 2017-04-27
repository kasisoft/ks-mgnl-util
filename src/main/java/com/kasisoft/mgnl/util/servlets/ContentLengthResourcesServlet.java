package com.kasisoft.mgnl.util.servlets;

import static com.kasisoft.mgnl.util.internal.Messages.*;

import info.magnolia.module.resources.*;

import com.kasisoft.libs.common.util.*;

import com.kasisoft.libs.common.io.*;

import org.apache.commons.lang3.*;

import org.apache.http.*;

import javax.servlet.http.*;

import javax.inject.*;

import java.io.*;

import lombok.extern.slf4j.*;

import info.magnolia.cms.beans.config.*;
import info.magnolia.resourceloader.*;

/**
 * This extension makes sure that the content length is provided within the response.
 * 
 * @author daniel.kasmeroglu@kasisoft.net
 */
@Slf4j
public class ContentLengthResourcesServlet extends ResourcesServlet {

  Bucket<ByteArrayOutputStream>   byteouts = new Bucket<>( BucketFactories.newByteArrayOutputStreamFactory() );
  
  @Inject
  public ContentLengthResourcesServlet( ResourceLinker linker ) {
    super( linker );
  }

  @Override
  protected void serveResource( HttpServletResponse response, Resource resource ) throws IOException {
    
    prepareResponse( response, resource );

    byte[] data = byteouts.forInstance( $ -> loadData( $, resource ) );
    if( data != null ) {
      
      response.setContentLength( data.length );
      writeResponse( resource, data, response );
      
    } else {
      log.debug( error_resource_not_found.format( resource ) );
      response.sendError( HttpServletResponse.SC_NOT_FOUND );
    }
      
  }
  
  private void writeResponse( Resource resource, byte[] content, HttpServletResponse response ) throws IOException {
    try(
      InputStream  in  = new ByteArrayInputStream( content ); 
      OutputStream out = response.getOutputStream()
    ) {
      IoFunctions.copy( in, out );
      out.flush();
    } catch( Exception ex ) {
      
      log.debug( error_resource_not_found.format( resource ), ex );
      response.sendError( HttpServletResponse.SC_NOT_FOUND );
  
      if( ! response.isCommitted() ) {
        // abortion by the client
        response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
      }
      
    }
      
  }

  @SuppressWarnings("deprecation")
  private void prepareResponse( HttpServletResponse response, Resource resource ) {
    String extension = StringUtils.substringAfterLast( resource.getName(), "." );
    String mimeType  = MIMEMapping.getMIMEType( extension );
    response.setContentType( mimeType );
    response.setDateHeader( HttpHeaders.LAST_MODIFIED, resource.getLastModified() );
  }

  private byte[] loadData( ByteArrayOutputStream byteout, Resource resource ) {
    try( InputStream in = resource.openStream() ) {
      IoFunctions.copy( in, byteout );
      return byteout.toByteArray();
    } catch( Exception ex ) {
      log.error( ex.getLocalizedMessage(), ex );
      return null;
    }
  }

} /* ENDCLASS */
