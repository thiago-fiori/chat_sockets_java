/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chatzap.app.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Thiag
 */
public class ChatMessage implements Serializable{ //O chat trabalhará com o Objeto da classe ChatMessage, e não com uma String.
    
    private String name; //Contem o nome do cliente.
    private String text; //Contem o texto da mensagem.
    private String nameReserved; //Armazena o nome de um cliente que receberá uma mensagem privada.
    private Set<String> setOnlines = new HashSet<String>(); //Armazena todos os clientes conectados ao servidor.
    private Action action; //Ação que será executada para cada mensagem enviada ao servidor
    
    public enum Action {
        CONNECT, DISCONNECT, SEND_ONE, SEND_ALL, USERS_ONLINE
    }
    //Getters e Setters.
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNameReserved() {
        return nameReserved;
    }

    public void setNameReserved(String nameReserved) {
        this.nameReserved = nameReserved;
    }

    public Set<String> getSetOnlines() {
        return setOnlines;
    }

    public void setSetOnlines(Set<String> setOnlines) {
        this.setOnlines = setOnlines;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
    
    
}

