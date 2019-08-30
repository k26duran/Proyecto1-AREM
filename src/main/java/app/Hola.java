package app;

import edu.escuelaing.arem.ASE.app.Web;

public class Hola {
	
	@Web("cuadrado")
	public static String square() {
		return "<html><body><h1> Cuadrado </h1></body></html>";
	}

}
