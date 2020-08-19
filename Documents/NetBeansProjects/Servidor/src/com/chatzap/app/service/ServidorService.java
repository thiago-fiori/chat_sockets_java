/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chatzap.app.service;

import com.chatzap.app.bean.ChatMessage;
import com.chatzap.app.bean.ChatMessage.Action;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thiag
 */
public class ServidorService {

    private ServerSocket serverSocket;
    private Socket socket;
    private Map<String, ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>(); //Lista de usuários que se conectaram ao servidor

    public ServidorService() {
        //Quando o servidor é inicializado, o serverSocket também é inicializado na porta 5555
        try {
            serverSocket = new ServerSocket(5555); //Inicializando o objeto serverSocket adicionando a porta de conexão entre o cliente e o servidor
            
            System.out.println("Servidor on!");
            
            while (true) { //Repetição que mantem o servidor sempre esperando por uma nova conexão
                socket = serverSocket.accept(); //Inicializando socket a partir do objeto ServerSocket e do método accept

                new Thread(new ListenerSocket(socket)).start(); //Thread que receberá o socket

            }

        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class ListenerSocket implements Runnable { //Classe ouvinte do servidor

        private ObjectOutputStream output; //Variável que executa o envio de mensagens do servidor
        private ObjectInputStream input; //Variável que recebe as mensagens enviadas pelos clientes

        public ListenerSocket(Socket socket) {
            try {
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            ChatMessage message = null;
            try {
                while ((message = (ChatMessage) input.readObject()) != null) {
                    Action action = message.getAction();

                    if (action.equals(Action.CONNECT)) { //Teste para descobrir qual é o tipo de mensagem enviada pelo cliente
                        boolean isConnect = connect(message, output);
                        if (isConnect) {
                            mapOnlines.put(message.getName(), output);
                        }
                    } else if (action.equals(Action.DISCONNECT)) { //O servidor recebe a mensagem do cliente e chama o método disconnect
                        disconnect(message, output);
                        return;
                    } else if (action.equals(Action.SEND_ONE)) {
                        sendOne(message, output);
                    } else if (action.equals(Action.SEND_ALL)) {
                        sendAll(message);
                    } else if (action.equals(Action.USERS_ONLINE)) {

                    }
                }
            } catch (IOException ex) {
                disconnect(message, output);//Removendo nome da lista caso o cliente feche o chat
                System.out.println(message.getName() + "Deixou o chat!");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean connect(ChatMessage message, ObjectOutputStream output) { //A conexão ocorre se a lista estiver vazia ou se o nome do cliente for diferente dos nomes ja presentes
        if (mapOnlines.size() == 0) { //Se o tamanho é 0 não existe nenhum cliente conectado
            message.setText("YES"); //Resposta de confirmação ao cliente
            sendOne(message, output);
            return true;
        }

        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) { //Teste para ver se algum nome de cliente se repete
            if (kv.getKey().equals(message.getName())) {
                message.setText("NO");
                sendOne(message, output);
                return false; //Se o nome for igual, o retorno é falso
            } else {
                message.setText("YES");
                sendOne(message, output);
                return true; //Se o nome for diferente, o retorno é verdadeiro
            }
        }

        return false;
    }

    private void disconnect(ChatMessage message, ObjectOutputStream output) {
        mapOnlines.remove(message.getName()); //Removendo cliente do chat a partir do nome

        message.setText(" até mais :)");

        message.setAction(Action.SEND_ONE); //Preparando a mensagem avisando que o usuário saiu

        sendAll(message); //Enviar uma mensagem para todos os clientes anunciando a saída de um cliente

        System.out.println(message.getName() + " desconectou-se"); //Mensagem avisando que o cliente saiu
    }

    private void sendOne(ChatMessage message, ObjectOutputStream output) {
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendAll(ChatMessage message) {
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            if (!kv.getKey().equals(message.getName())) {
                message.setAction(Action.SEND_ONE);
                try {
                    kv.getValue().writeObject(message);
                } catch (IOException ex) {
                    Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    
}
