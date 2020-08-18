/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Mensagem;
import util.Status;

/**
 *
 * @author Thiag
 */
public class Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            /*
            1. Estabelecer Conexão com o servidor
            2. Trocar mensagens com o servidor
            */
            //cria a conexão entre o cliente e o servidor
            System.out.println("Estabelecendo Conexão");
            Socket socket = new Socket("localhost", 5555);
            System.out.println("Conexão Estabelecida");
            
            //criação dos streams de entrada e saída
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            System.out.println("Enviando mensagem...");
             /*protocolo
            HELLO
            nome : string
            
            HELLOREPLY
            OK, ERRO, PARAMERROR
            mensagem : String
            */
            Mensagem m = new Mensagem("HELLO");
            m.setStatus(Status.SOLICITACAO);
            m.setParam("nome", "Thiago");
            m.setParam("sobrenome", " Fiori");
            
            output.writeObject(m);
            output.flush(); //libera buffer para envio
            
            System.out.println("Mensagem " + m + " enviada");
            
            m = (Mensagem)input.readObject();
            System.out.println("Resposta: " + m);
            if (m.getStatus() == Status.OK) {
                String resposta = (String) m.getParam("mensagem");
                System.out.println("Mensagem: \n" + resposta);
            } else {
                System.out.println("Erro: " + m.getStatus());
            }
            
            input.close();
            output.close();
            socket.close();
            
        } catch (IOException ex) {
            System.out.println("Erro no cliente: " + ex);
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("Erro no cast: " + ex.getMessage());
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
}
