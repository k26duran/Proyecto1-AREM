package edu.escuelaing.arem.ASE.app;

import java.lang.reflect.Method;

public class MethodHandler implements Handler {
	
	Method m;
	
	public MethodHandler(Method m) {
		this.m=m;
	}
	/**
	 * No recibe parametros
	 */
	public String procesar() {
		try {
			return m.invoke(null, null).toString();
		} catch (Exception e) {
			 e.printStackTrace();
			 return e.getMessage();
		}
	}
	/**
	 * 
	 * @param arg
	 * @return los métodos de cierto objeto
	 * @throws Exception
	 */
	public  String procesar(Object[] arg) throws Exception {
        return m.invoke(m, arg).toString();
    }
	

}
