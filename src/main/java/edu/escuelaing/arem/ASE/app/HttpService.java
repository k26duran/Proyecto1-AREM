package edu.escuelaing.arem.ASE.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
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
	private static Socket clientSocket;
	private static ServerSocket serverSocket = null;
	String request ="";
	private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
	
	public void init() {
		try {
			//receive("WebServiceHello");
			List<Class<?>> classes= new ArrayList<Class<?>>();
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
	
	public void listen() throws Exception{
		try{
			int port= getPort();
			serverSocket = new ServerSocket(port);
			System.out.println("Listen for port : " + port);
		} catch(IOException e){
			System.err.println("Could not listen on port: " + getPort());
	        System.exit(1);
		}
		while(true){
			try{
				System.out.println("Ready to receive...");
				clientSocket = serverSocket.accept();
			} catch(IOException e){
				System.err.println("Accept failed.");
                System.exit(1);
			}
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String inputLine; 
			while ((inputLine = in.readLine()) != null) {
				System.out.println("Received: " + inputLine);
				if (!in.ready()) {
					break;
				}
				if(inputLine.contains("GET")){
					request = inputLine.split(" ")[1];
					System.out.println("Recurso solicitado:"+request);
				}
			}
			if (request.contains("/apps")) {
				returnApp(request);
			} else if (request.contains("html")) {
				System.out.println(request);
				returnHtml(request);
			} else if (request.contains("jpg")) {
				returnImages(request, clientSocket);
			} else{
				notFound();
			}
			out.close();
			in.close();

		}

	}
	private static void notFound() throws IOException{
		try{
			PrintWriter out;
			BufferedReader in = new BufferedReader(new FileReader(classLoader.getResource("notFound.html").getFile()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			String result = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" + "\r\n";
			out.println(result);
			while (in.ready()) {
				out.println(in.readLine());
			}
			out.close();
			in.close();

		} catch(IOException e){
			System.err.println("Error en notFound");
		}
	}

	private static void returnApp(String request) throws IOException{

	}
	private static void returnHtml(String request) throws IOException{
		try{
			PrintWriter out;
			BufferedReader in = new BufferedReader(new FileReader(classLoader.getResource(request).getFile()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			String result = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" + "\r\n";
			out.println(result);
			while (in.ready()) {
				out.println(in.readLine());
			}
			out.close();
			in.close();

		} catch(IOException e){
			System.err.println("Error en returnHtml");
		}
	}
	private static void returnImages(String request, Socket clientS) throws IOException{

	}
	
	public void receive(String direccion){
		try {
			Class<?> c= Class.forName(direccion);
			listM= c.getDeclaredMethods();
			listaURLHandler = new HashMap<String, MethodHandler>();
			
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
	    List<Class<?>> classes = new ArrayList<Class<?>>();
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

	public static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567; //returns default port if heroku-port isn't set (i.e. on localhost)
    }
}
