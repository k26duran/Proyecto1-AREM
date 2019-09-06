package app;

import edu.escuelaing.arem.ASE.app.Web;

@Web("Service")
public class WebServiceHello {
	/**
	 * 
	 * @return un HTML de ejemplo
	 */
	@Web("cuadrado")
	public static String square() {
		return "<html><body><h1> Cuadrado </h1></body></html>";
	}
	/**
	 * 
	 * @return un HTML de ejemplo
	 */
	@Web("cuadrado1")
	public static String square1() {
		return "<html><body><h1> Cuadrado 1 </h1></body></html>";
	}
	/**
	 * 
	 * @param name
	 * @return un HTML haciendo uso del paramétro solicitado
	 */
	@Web("ejemplo")
    public static String ejemploConParametro(String name) {
        return "<!DOCTYPE html>\n" + 
        		"<html>\n" + 
        		"<head>\n" + 
        		"  <meta charset=\"utf-8\" />\n" + 
        		"  <title>Proyecto AREM</title>  \n" + 
        		"</head>\n" + 
        		"<body style=\"text-align: center\">   \n" + 
        		"    <h1>Hola "+name+"! , esto es un ejemplo de HTML que recibe parametros </h1>	\n" + 
        		"</body>\n" + 
        		"</html>\n" + 
        		"";
    }

}
