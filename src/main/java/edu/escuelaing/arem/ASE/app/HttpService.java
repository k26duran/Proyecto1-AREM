package edu.escuelaing.arem.ASE.app;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;
import org.reflections.Reflections;


public class HttpService {
	
	private static HashMap<String, MethodHandler> listaURLHandler;
	private static Socket clientSocket;
	private static ServerSocket serverSocket = null;
	private static String address = "";
    private static BufferedReader in;
    private static Socket receiver;
    
	/**
	 * Método que inicializa el atributo listaURLHandler que tendrá todos los métodos con anotaciones del package app
	 */
	public void init(){
		try {
			listaURLHandler = new HashMap<String, MethodHandler>();
			Reflections reflections = new Reflections("app");
			Set<Class<? extends Object>> classes= reflections.getTypesAnnotatedWith(Web.class);
			//System.out.println(classes.toString());
			for(Class<?> c:classes) {
				receive(c.getName());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 *  
	 * @throws Exception
	 */
	public void listen() throws Exception{
		while (true){
            serverSocket = runServer();
            receiver = receiveRequest(serverSocket);
            setRequest(receiver);       
            postType(address,receiver);
            closeInput();
            receiver.close();
            serverSocket.close();
		}
	}
	/**
	 * ServerSocket
	 * @return
	 */
	public static ServerSocket runServer() {
        
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(getPort());
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + getPort());
            System.exit(1);
        }
        return serverSocket;
    }
	/**
	 * El socket se prepara para recibir solicitudes
	 * @param serverSocket
	 * @return
	 */
	public static Socket receiveRequest(ServerSocket serverSocket) {
	
	    Socket request = null;        
	    try {
	        System.out.println("Ready to receive...");
	        request = serverSocket.accept();
	    } catch (IOException e) {
	        System.err.println("Accept failed.");
	        System.exit(1);
	    }
	    return request;
	}

	 /**
     * En este método se identifica que tipo de solicitud se realizó al servidor
     * @param address corresponde a la dirección del recurso que se desea obtener
     * @param cc corresponde al socket que hace la solicitud
	 * @throws Exception 
     */
    public static void postType(String address, Socket cs) throws Exception{
        clientSocket = cs;
        System.out.println("POST: " + address);
        if (address.contains("/app")) {
        	postApp(address);
        }else if (address.contains(".html")){
            postHtml(address);
        }else if (address.contains(".png")){
            postImage(address);
        }else{
            notFound404();
        }
    }
    
    /**
     * Este método publica una página html que ha sido solicitada en el servidor,
     * @param address corresponde al nombre del archivo que se quiere obtener
     */
    private static void postHtml(String request){
        try{            
            String outputLine;
            String page = "HTTP/1.1 200 OK\r\n" +
                          "Content-Type: text/html\r\n" +
                          "\r\n" + readHTML(request);            
            outputLine = page;
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(outputLine);
            out.close();
        }catch (Exception ex){
            notFound404();
            System.err.println("Error: Archivo no encontrado");
        }
    }
    /**
     * Este método publica un recurso dentro de la listaURLHandler en el servidor
     * @param address corresponde al path del recurso que se quiere obtener
     * @throws IOException
     */
    private static void postApp(String address) throws IOException {
    	PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
    	int limit = address.indexOf("/app");
		String resource = "";
		for (int k = limit; k < address.length() && address.charAt(k) != ' '; k++) {resource += address.charAt(k);}
		try {
			out.write("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" + "\r\n");
			if(resource.contains(":")) {
				int i = resource.indexOf(":");
				if(resource.contains(",")) {
					String params=resource.substring(i+1);
					Object [] objects= params.split(",");
					out.write(listaURLHandler.get(resource.substring(0, i)).procesar(objects));
				}else {
					out.write(listaURLHandler.get(resource.substring(0, i)).procesar(new Object[]{resource.substring(i+1)}));
				}
				}else { out.write(listaURLHandler.get(resource).procesar());}
			out.close();
		} catch (Exception e) {
			notFound404();
			System.err.println("Error: No es posible cargar el recurso " + address);
		}
    }
    
    /**
     * Este método publica una imagen con extension PNG en el servidor,
     * @param address Corresponde al nombre de la imagen solicitada
     */
    private static void postImage(String address){
        try {
            byte[] imageBytes; 
            imageBytes = readImage(address);
            DataOutputStream imageCode;            
            imageCode = new DataOutputStream(clientSocket.getOutputStream());
            imageCode.writeBytes("HTTP/1.1 200 OK \r\n");
            imageCode.writeBytes("Content-Type: image/png\r\n");
            imageCode.writeBytes("Content-Length: " + imageBytes.length);
            imageCode.writeBytes("\r\n\r\n"); 
            //La imagen se hace visible en el servidor
            imageCode.write(imageBytes);            
            imageCode.close();
        } catch (IOException ex){
            
            notFound404();
            System.err.println("Error: No se encontro la imagen");
        }
    }
    
    /**
     * Se publica un página con un mensaje de error, esto se hace cuando un recurso no existe
     */
    private static void notFound404(){
        try{            
            String outputLine;
            String page = "HTTP/1.1 200 OK\r\n" +
                          "Content-Type: text/html\r\n" +
                          "\r\n" + readHTML("/notfound.html");
            
            outputLine = page;
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(outputLine);
            out.close();
        }catch (IOException ex){
            System.err.println("Error en notFound");
        }
    }
    /**
     * 
     * @param clientSocket 
     * @throws IOException
     */
    public static void setRequest(Socket clientSocket) throws IOException{
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String inputLine;        
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Received: " + inputLine);
            if (!in.ready()) {
                break;
            }
            if(inputLine.contains("GET")){
                address = inputLine.split(" ")[1];
            }
        }
    }
    
    /**
     * Este método cierra el archivo con el cual el servidor realiza su lectura
     * @throws IOException se estan leyendo archivos
     */
    public void closeInput() throws IOException {
        in.close();
    }
    
	/**
	 * Este método lee un archivo html para luego poder publicarlo en el servidor
	 * @param address correspone al nombre del archivo que se quiere leer
	 * @return
	 * @throws MalformedURLException
	 */
    public static String readHTML(String address) throws MalformedURLException {        
    	String html = "";
    	try {            
            FileReader file = new FileReader("src/main/java/app/html"+address);
            BufferedReader reader = new BufferedReader(file);
            String inputLine = "";
            
            while ((inputLine = reader.readLine()) != null) {
                html += inputLine + "\n";
            }
        } catch (IOException io) {
            System.err.println(io);
        }
        return html;
    }
    
    /**
     * Este método lee una imagen .PNG y la convierte en Bytes para poder visualizarla 
     * @param adress corresponde al nombre de la imagen que se quiere visualizar
     * @return imageBytes imagen parseada a Bytes
     * @throws MalformedURLException 
     */
    public static byte[] readImage(String address) throws MalformedURLException {        
        byte[] imageBytes = null;
        try {     
            File image = new File("src/main/java/app/images"+address);
            FileInputStream inputImage = new FileInputStream(image);
            imageBytes = new byte[(int) image.length()];
            inputImage.read(imageBytes);
            
        } catch (IOException io) {
            System.err.println(io);
        }
        return imageBytes;
    }
    
    /**
     * Este método busca en todas las clases del package /app los métodos que contengan la anotación '@Web'
     * y los ubica en un hashMap
     * @param direccion corresponde a la dirección del recurso que se desea obtener y que se encuentra ubicado en el package app
     */
    public void receive(String direccion){
		try {
			Class<?> c= Class.forName(direccion);
			Method[] listM= c.getDeclaredMethods();	
			for(Method e: listM) {
				if(e.isAnnotationPresent(Web.class)) {
					MethodHandler handler = new MethodHandler(e);
					listaURLHandler.put("/app/"+e.getDeclaredAnnotation(Web.class).value(), handler);
					//System.out.println(handler.procesar());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
    
	/**
	 * 
	 * @return retorna un puerto
	 */
	public static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567; //returns default port if heroku-port isn't set (i.e. on localhost)
    }
}
