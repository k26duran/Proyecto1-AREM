package edu.escuelaing.arem.ASE.app;

import java.lang.reflect.Method;

public class MethodHandler implements Handler {
	
	Method m;
	
	public MethodHandler(Method m) {
		this.m=m;
	}
	
	public String procesar() {
		try {
			return (String) m.invoke(null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
			 return e.getMessage();
		}
	}
	

}
