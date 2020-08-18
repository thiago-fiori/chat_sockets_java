/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import util.Mensagem;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import jdk.net.SocketFlow;
import util.Estados;
import util.Status;

/**
 *
 * @author Thiag
 */
public class Server {

    private ServerSocket serverSocket;

    private void criarServerSocket(int porta) throws IOException {
        serverSocket = new ServerSocket(porta);
    }

    private Socket esperaConexao() throws IOException {
        Socket socket = serverSocket.accept();
        return socket;
    }

    private void fechaSocket(Socket s) throws IOException {
        s.close();
    }

    private void trataConexao(Socket socket) throws IOException, ClassNotFoundException {
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
            HELLO
            nome : string
            sobrenome : string
            
            HELLOREPLY
            OK, ERRO, PARAMERROR
            mensagem : String
             */

            Estados estado = Estados.CONECTADO;
            
            while (estado != Estados.SAIR) {
                Mensagem m = (Mensagem) input.readObject();
                System.out.println("Mensagem do cliente: \n" + m);

                String operacao = (String) m.getOperacao();
                Mensagem reply = new Mensagem(operacao + "REPLY");
                //estados conectado autenticado
                switch (estado) {
                    case CONECTADO:
                        switch (operacao) {
                            case "LOGIN":
                                try {
                                    String user = (String) m.getParam("user");
                                    String pass = (String) m.getParam("pass");

                                    if (user.equals("ALUNO") && pass.equals("ESTUDIOSO")) {
                                        reply.setStatus(Status.OK);
                                        estado = Estados.AUTENTICADO;
                                    } else {
                                        reply.setStatus(Status.ERROR);
                                    }
                                } catch (Exception e) {
                                    reply.setStatus(Status.PARAMERROR);
                                    reply.setParam("msg", "Erro nos parâmetros do protocolo.");
                                }
                                break;
                            case "HELLO":
                                String nome = (String) m.getParam("nome");
                                String sobrenome = (String) m.getParam("sobrenome");

                                reply = new Mensagem("HELLOREPLY");
                                if (nome == null || sobrenome == null) {
                                    reply.setStatus(Status.PARAMERROR);
                                } else {
                                    reply.setStatus(Status.OK);
                                    reply.setParam("mensagem", "Hello World, " + nome + sobrenome);
                                }
                                break;
                            case "SAIR":
                                reply.setStatus(Status.OK);
                                estado = Estados.SAIR;
                                break;
                            default:
                                //responder mensagem de erro: Não autorizado ou inválida
                                reply.setStatus(Status.ERROR);
                                reply.setParam("msg", "MENSAGEM NAO AUTORIZADA OU INVALIDA!");
                                break;
                        }
                        break;
                    case AUTENTICADO:
                        switch (operacao) {
                            case "DIV":
                                try {
                                    Integer op1 = (Integer) m.getParam("op1");
                                    Integer op2 = (Integer) m.getParam("op2");
                                    //testar os dados
                                    reply = new Mensagem("DIVREPLY");
                                    if (op2 == 0) {
                                        reply.setStatus(Status.DIVZERO);
                                    } else {
                                        reply.setStatus(Status.OK);
                                        float div = (float) op1 / op2;
                                        reply.setParam("res", div);
                                    }
                                } catch (Exception e) {
                                    reply = new Mensagem("DIVREPLY");
                                    reply.setStatus(Status.PARAMERROR);
                                }
                                break;
                            case "SUB":
                                break;
                            case "MUL":
                                break;
                            case "SOMA":
                                break;
                            case "LOGOUT":
                                reply.setStatus(Status.OK);
                                estado = Estados.CONECTADO;
                                break;
                            case "SAIR":
                                //DESIGN PATTERN STATE
                                reply.setStatus(Status.OK);
                                estado = Estados.SAIR;
                                break;
                            default:
                                reply.setStatus(Status.ERROR);
                                reply.setParam("msg", "MENSAGEM NAO AUTORIZADA OU INVALIDA!");
                                break;
                        }
                        break;
                    case SAIR: //ESTADO
                        break;
                }

                output.writeObject(reply);
                output.flush();
            }
            //fechar streams de entrada e saída
            input.close();
            output.close();
        } catch (IOException e) {
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
            while (true) {
                Socket socket = server.esperaConexao();//protocolo
                System.out.println("Cliente Conectado!");
                //Outro processo
                server.trataConexao(socket);
                System.out.println("Cliente finalizado!");
            }
        } catch (IOException e) {
            //trata exceções
            System.out.println("Erro no servidor: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Erro no cast: " + e.getMessage());
        }
    }

}
