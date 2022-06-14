/**
 * @author Raquel Facchini Batista Franco 
 * @author Rafael Lima Honorato
 * @author Raphael Santos
 * @apiNote Trabalho de Linguagem de Programação Java
 * @version 1.0  
 * Fatec Praia Grande 
 * @since 15 de Junho de 2022.
 */
package vacinometro;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class AppendingObjectOutputStream extends ObjectOutputStream {

	  public AppendingObjectOutputStream(OutputStream out) throws IOException {
	    super(out);
	  }

	  @Override
	  protected void writeStreamHeader() throws IOException {
	    // do not write a header, but reset:
	    // this line added after another question
	    // showed a problem with the original
	    reset();
	  }

	}