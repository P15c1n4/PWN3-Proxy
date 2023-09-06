package proxy; 

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.Thread.sleep;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HexFormat;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;


public class ForwardingServer {

static long tempoAtualTradutor = 0;
static long tempoAtualAutoInject = 0;

static ArrayList<String> fila = new ArrayList();

    public static void main(String[] args) {
        if(args.length > 0){
            ForwardingThread forwardingThread = new ForwardingThread(3333, 3333, args[0]);
            forwardingThread.start();  
        }else{
            ForwardingThread forwardingThread = new ForwardingThread(3333, 3333, null);
            forwardingThread.start();  
        }
  
        for(int i = 3000; i < 3005 ; i++){
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
            
            if(args.length > 0){
                ForwardingThread forwardingThread = new ForwardingThread(i, i, args[0]);
                forwardingThread.start();  
            }else{
                ForwardingThread forwardingThread = new ForwardingThread(i, i, null);
                forwardingThread.start();  
            }
            
        }
            Command command = new Command(fila);
            command.start();
    }

    private static class ForwardingThread extends Thread {
        private int listeningPort;
        private int forwardingPort;
        private static String SERVER_IP = "127.0.0.1";

        public ForwardingThread(int listeningPort, int forwardingPort, String arg1) {
            this.listeningPort = listeningPort;
            this.forwardingPort = forwardingPort;
            
            if(arg1 != null){
                this.SERVER_IP = arg1;
            }

        }

        @Override
        public void run() {
            ServerSocket serverPoxySocket = null;
                try{
                    serverPoxySocket = new ServerSocket(listeningPort);
                    System.out.println("Servidor de encaminhamento iniciado. Aguardando conexões na porta " + listeningPort + "...");
                }catch(Exception e){
                        System.out.println("\nPorta já em uso!\n");
                }

                while (true) {
                    try {
                        Socket clientSocket = serverPoxySocket.accept();
                        System.out.println("Conexão recebida de: " + clientSocket.getInetAddress().getHostAddress()+":"+clientSocket.getLocalPort());

                        Socket serverSocket = new Socket(SERVER_IP, forwardingPort);
                        System.out.println("Conectado ao servidor: " + SERVER_IP + ":" + forwardingPort);

                        ForwardingConnection clientToServer = new ForwardingConnection(clientSocket, serverSocket, SERVER_IP);
                        ForwardingConnection serverToClient = new ForwardingConnection(serverSocket, clientSocket, SERVER_IP);

                        clientToServer.start();
                        serverToClient.start();
                    } catch (IOException e) {
                    //e.printStackTrace();
                    System.out.println("\n|*|*|*|*|*|*| Erro de conexão =P |*|*|*|*|*|*|\n");
                    }
                }

        }
    }

    private static class ForwardingConnection extends Thread {
        private Socket inputSocket;
        private Socket outputSocket;
        private String SERVER_IP;
        
        public ForwardingConnection(Socket inputSocket, Socket outputSocket, String serverIP) {
            this.inputSocket = inputSocket;
            this.outputSocket = outputSocket;
            this.SERVER_IP = serverIP;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = inputSocket.getInputStream();
                OutputStream outputStream = outputSocket.getOutputStream();

                byte[] buffer = new byte[4096];
                int bytesRead;
                
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    
                    String hexDump = bytesToHex(buffer, bytesRead).replaceAll(" ", "");
                    
                    String queu = IniciaTradutor(hexDump, inputSocket, "./AutoInject.java", "AutoInject");

                    if(queu != null){
                        fila.add(queu);
                    }
                    
                    //Processo de Saida do conteudo
                    if(inputSocket.getInetAddress().getHostAddress().equals(SERVER_IP)){
                        try{
                            String result = IniciaTradutor(hexDump, inputSocket, "./Tradutor.java", "Tradutor");
                            if(result.length() > 0){
                                System.out.println("\nServer --> "+result);
                            }
                            if(!fila.isEmpty()){ 
                                for(int i = 0; i < fila.size(); i++){
                                    String[] split = fila.get(i).split(" ");

                                    if(split.length > 1 && split[0].equals("S_")){

                                       byte[] bytes = HexFormat.of().parseHex(split[1]);
                                       int bytesTotal = bytes.length;

                                       outputStream.write(bytes, 0, bytesTotal);
                                       fila.  remove(i);
                                    }
                                }
                            }
                        }catch(Exception e){
                            //sem tratamento para não poluir a saida do proxy
                        }
                        
                    }else{
                        try{
                            String result = IniciaTradutor(hexDump, inputSocket, "./Tradutor.java", "Tradutor");
                            if(result.length() > 0){
                                System.out.println("\nClient --> "+result);
                            }
                            if(!fila.isEmpty()){ 
                                for(int i = 0; i < fila.size(); i++){
                                    String[] split = fila.get(i).split(" ");

                                    if(split.length > 1 && split[0].equals("C_")){

                                       byte[] bytes = HexFormat.of().parseHex(split[1]);
                                       int bytesTotal = bytes.length;

                                       outputStream.write(bytes, 0, bytesTotal);
                                       fila.remove(i);
                                    }
                                }
                            }
                        }catch(Exception e){
                            //sem tratamento para não poluir a saida do proxy
                        }
                    }
                    
                }
            } catch (IOException e) {
                //sem tratamento para não poluir a saida do proxy
            } finally {
                try {
                    if (inputSocket != null) {
                        inputSocket.close();
                    }
                    if (outputSocket != null) {
                        outputSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
        private static String IniciaTradutor(String hex, Socket inputSocket, String filePath, String className) {
            
            try{
                return compileAndExecuteJavaFile(filePath, className, hex, inputSocket);
            }catch(Exception e){
                //sem tratamento para não poluir a saida do proxy
            }
                return null;
        }
        private static String compileAndExecuteJavaFile(String filePath, String className, String hex, Socket inputSocket) throws Exception {
            Class<?> cls;

            // Compila o código Java em tempo de execução
            if((System.currentTimeMillis() - tempoAtualTradutor) > 4000 && className.equals("Tradutor")){
                compileJavaCode(filePath);
                tempoAtualTradutor = System.currentTimeMillis();
                
            }else if((System.currentTimeMillis() - tempoAtualAutoInject) > 10000 && className.equals("AutoInject")){
                compileJavaCode(filePath);
                tempoAtualAutoInject = System.currentTimeMillis();
            }
            
            // Carrega a classe compilada
            while(true){
                try{
                       
                    URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{Paths.get("./classe/").toUri().toURL()});
                    cls = classLoader.loadClass(className);
                    classLoader.close();
                    break;
                    
                }catch(ClassFormatError e){
                    //sem tratamento para não poluir a saida do proxy
                    continue;
                }

            }
            // Executa um método da classe
            Object instance = cls.getDeclaredConstructor().newInstance();
            Method method = cls.getMethod(className, String.class, Socket.class);

            return (String) method.invoke(instance, hex, inputSocket);

        }

        private static String readFile(String filePath) throws IOException {
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            return content.toString();
        }

        private static void compileJavaCode(String filePath) throws IOException {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

            // Define a pasta de destino para as classes compiladas
            File outputDir = new File("./classe/");
            outputDir.mkdirs();
            
            compiler.run(null, null, null, "-d", outputDir.getAbsolutePath(), "-cp", "", filePath);
    
        }
        
        private static String bytesToHex(byte[] bytes, int length) {
           StringBuilder sb = new StringBuilder();

           for (int i = 0; i < length; i++) {
               sb.append(String.format("%02X ", bytes[i]));
           }

           return sb.toString().trim();
       }
}
