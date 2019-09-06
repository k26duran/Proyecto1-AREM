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
	 * @return un HTML haciendo uso del param�tro solicitado
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
	@Web("ejemploParams")
    public static String ejemploConParametros(String name, String name1) {
        return "<!DOCTYPE html>\n" + 
        		"<html>\n" + 
        		"<head>\n" + 
        		"  <meta charset=\"utf-8\" />\n" + 
        		"  <title>Proyecto AREM</title>  \n" + 
        		"</head>\n" + 
        		"<body style=\"text-align: center\">   \n" + 
        		"    <h1>Hola "+name+" y "+name1+"!, esto es un ejemplo de HTML que recibe parametros </h1>	\n" + 
        		"</body>\n" + 
        		"</html>\n" + 
        		"";
    }
	
	@Web("example")
	public static String ejemploIndex() {
		return"<!DOCTYPE html>\n" + 
        		"<html>\n" + 
        		"<head>\n" + 
        		"  <meta charset=\"utf-8\" />\n" + 
        		"  <title>Proyecto AREM</title>  \n" + 
        		"</head>\n" + 
        		"<body style=\"text-align: center\">   \n" +
        		"<h1>Escribe tu nombre </h1>"+
        		"<form action=\"/app/ejemplo\">	\n" +
        		"  <input id=\"texto\" name=\"name\" type=\"text\">"+
        		"</form>"+
        		"</body>\n" +
        		"</html>\n" + 
        		"";
	}

}
