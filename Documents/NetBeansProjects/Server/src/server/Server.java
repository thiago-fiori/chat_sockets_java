/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Thiag
 */
public class Server {
    
    private ServerSocket serverSocket;
    
    private void criarServerSocket(int porta) throws IOException{
        serverSocket = new ServerSocket(porta);
    }
    
    private Socket esperaConexao() throws IOException{
        Socket socket = serverSocket.accept();
        return socket;
    }
    
    private void fechaSocket(Socket s) throws IOException{
        s.close();
    }
    
    private void trataConexao(Socket socket) throws IOException{
        // * Cliente -------- [Socket] -------- Servidor
        //protocolo da aplicação
        /*
        3 - criar streams de entrada e saída;
        4 - tratar a conversação entre cliente e servidor (tratar protocolo)
        */
        try {
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            /*protocolo
            Cliente ---> Hello
            Server <--- Hello World!
            */
            
            String msg = input.readUTF();
            System.out.println("Mensagem Recebida: " + '"' + msg + '"');
            output.writeUTF("HELLO WORLD!");
            output.flush();
            
            //fechar streams de entrada e saída
            input.close();
            output.close();
        } catch(IOException e) {
            //tratamento de falhas
            System.out.println("Problema no tratamento da conexão com o cliente: " + socket.getInetAddress());
            System.out.println("Erro: " + e.getMessage());
        } finally {
            //final do tratamento do protocolo
            fechaSocket(socket);
        }
    }
    // Testando commit
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Server server = new Server();
            System.out.println("Aguardando conexão...");
            server.criarServerSocket(5555);
            while (true){
                Socket socket = server.esperaConexao();//protocolo
                System.out.println("Cliente Conectado!");
                //Outro processo
                server.trataConexao(socket);
                System.out.println("Cliente finalizado!");
            }
        } catch (IOException e) {
            //trata exceções
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }
    
}
