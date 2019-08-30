package edu.escuelaing.arem.ASE.app;
import java.io.IOException;


public class Controller {

	public static HttpService service;
	
	public Controller() {
		
	}
	
	public static void main(String[] args) throws IOException {
		service=new HttpService ();
		service.init();
	}
}
