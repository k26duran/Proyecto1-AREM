package app;


import javax.jws.WebService;

import edu.escuelaing.arem.ASE.app.Web;

public class WebServiceHello {
	
	@Web("cuadrado")
	public static String square() {
		return "<html><body><h1> Cuadrado </h1></body></html>";
	}
	
	@Web("cuadrado1")
	public static String square1() {
		return "<html><body><h1> Cuadrado 1 </h1></body></html>";
	}

}
