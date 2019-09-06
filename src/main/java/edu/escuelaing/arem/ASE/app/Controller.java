package edu.escuelaing.arem.ASE.app;


public class Controller {

	public static HttpService service;
	
	public static void main(String[] args) throws Exception {
		try {
		service = new HttpService ();
		service.init();
		service.listen();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
