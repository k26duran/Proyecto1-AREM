package edu.escuelaing.arem.ASE.app;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


public class HttpService {
	
	Method[] listM;
	HashMap<String, MethodHandler> listaURLHandler;
	
	public void init() {
		try {
			//receive("WebServiceHello");
			List<Class<?>> classes= new ArrayList<>();
			classes= getClassesInPackage("app");
			System.out.println(classes.toString());
			for(Class<?> c:classes) {
				System.out.println("------");
				receive(c.getCanonicalName());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void listen() {
	

	}
	public void receive(String direccion){
		try {
			Class<?> c= Class.forName(direccion);
			listM= c.getDeclaredMethods();
			listaURLHandler = new HashMap<>();
			
			for(Method e: listM) {
				if(e.isAnnotationPresent(Web.class)) {
					MethodHandler handler = new MethodHandler(e);
					listaURLHandler.put(e.getDeclaredAnnotation(Web.class).value(), handler);
					System.out.println(handler.procesar());
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static final List<Class<?>> getClassesInPackage(String packageName) {
	    String path = packageName.replaceAll("\\.", File.separator);
	    List<Class<?>> classes = new ArrayList<>();
	    String[] classPathEntries = System.getProperty("java.class.path").split(
	            System.getProperty("path.separator")
	    );

	    String name;
	    for (String classpathEntry : classPathEntries) {
	        if (classpathEntry.endsWith(".jar")) {
	            File jar = new File(classpathEntry);
	            try {
	                JarInputStream is = new JarInputStream(new FileInputStream(jar));
	                JarEntry entry;
	                while((entry = is.getNextJarEntry()) != null) {
	                    name = entry.getName();
	                    if (name.endsWith(".class")) {
	                        if (name.contains(path) && name.endsWith(".class")) {
	                            String classPath = name.substring(0, entry.getName().length() - 6);
	                            classPath = classPath.replaceAll("[\\|/]", ".");
	                            classes.add(Class.forName(classPath));
	                        }
	                    }
	                }
	            } catch (Exception ex) {
	                // Silence is gold
	            }
	        } else {
	            try {
	                File base = new File(classpathEntry + File.separatorChar + path);
	                for (File file : base.listFiles()) {
	                    name = file.getName();
	                    if (name.endsWith(".class")) {
	                        name = name.substring(0, name.length() - 6);
	                        classes.add(Class.forName(packageName + "." + name));
	                    }
	                }
	            } catch (Exception ex) {
	                // Silence is gold
	            }
	        }
	    }

	    return classes;
	}
}
