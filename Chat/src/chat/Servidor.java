/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author Antonio
 */
public class Servidor extends Thread {

  private static ArrayList<Cliente> clientes;
  private static ServerSocket server;

  /**
   * Método construtor
   *
   * @param com do tipo Socket
   */
 

  /**
   * Método run
   */
  public void run() {
    try{
      while (true) {
        System.out.println("Aguardar cliente");
        Socket con = server.accept();
        System.out.println("Cliente conectado");
        clientes.add(new Cliente(con));
        Chat.atualizarListaClientes();
      }
    } catch(Exception ex){
      
    }

//    try {
//
//
//    } catch (Exception e) {
//      e.printStackTrace();
//
//    }
  }

  /**
   * *
   * Método usado para enviar mensagem para todos os clients
   *
   * @param bwSaida do tipo BufferedWriter
   * @param msg do tipo String
   * @throws IOException
   */
//  public void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {
//    BufferedWriter bwS;
//
//    for (BufferedWriter bw : clientes) {
//      bwS = (BufferedWriter) bw;
//      if (!(bwSaida == bwS)) {
//        bw.write(nome + " -> " + msg + "\r\n");
//        bw.flush();
//      }
//    }
//  }

  public static Servidor servidor;
  
  public static void Iniciar(int porta) {
    try {
      server = new ServerSocket(porta);
      clientes = new ArrayList<>();

      servidor=new Servidor();
      servidor.start();
      Chat.adicionarMensagem("Servidor local iniciado na porta "+porta);
      
    } catch (Exception e) {

      e.printStackTrace();
    }
  }

} //Fim da classe
