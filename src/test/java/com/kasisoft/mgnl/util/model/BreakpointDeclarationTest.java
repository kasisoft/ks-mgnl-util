package com.kasisoft.mgnl.util.model;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import org.testng.annotations.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class BreakpointDeclarationTest {

  @Test
  public void basic() {

    assertNull( BreakpointDeclaration.DEFAULT );

    BreakpointDeclaration.loadDefaultBreakpoints();
    
    assertNotNull( BreakpointDeclaration.DEFAULT );
    
    BreakpointDeclaration[] declarations = BreakpointDeclaration.values();
    assertNotNull( declarations );
    
    assertThat( declarations.length, is(3) );
    
    assertThat( declarations[0].getKey(), is( "ss" ) );
    assertThat( declarations[0].getMaxWidth(), is( 400 ) );
    assertThat( declarations[0].getColumns(), is( 4 ) );

    assertThat( declarations[1].getKey(), is( "ms" ) );
    assertThat( declarations[1].getMaxWidth(), is( 836 ) );
    assertThat( declarations[1].getColumns(), is( 8 ) );

    assertThat( declarations[2].getKey(), is( "ls" ) );
    assertThat( declarations[2].getMaxWidth(), is( 1200 ) );
    assertThat( declarations[2].getColumns(), is( 12 ) );
    
    assertThat( BreakpointDeclaration.DEFAULT, is( declarations[2] ) );

  }
  
} /* ENDCLASS */
