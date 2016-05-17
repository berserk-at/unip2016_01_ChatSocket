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
    try {
      while (true) {
        System.out.println("Aguardar cliente");
        Socket con = server.accept();
        System.out.println("Cliente conectado");
        InterageCliente cliente = new InterageCliente(con);
        InterageCliente.clientes.add(cliente);
        cliente.start();

        Chat.atualizarListaClientes();
      }
    } catch (Exception ex) {

    }
  }

  public static Servidor servidor;

  public static void Iniciar(int porta) {
    try {
      server = new ServerSocket(porta);
      InterageCliente.clientes = new ArrayList<>();

      servidor = new Servidor();
      servidor.start();
      Chat.adicionarMensagem("Servidor local iniciado na porta " + porta);

    } catch (Exception e) {

      e.printStackTrace();
    }
  }

  public void enviarTodos(String mensagem) {
    BufferedWriter bwS;

    for (InterageCliente clienteSel : InterageCliente.clientes) {
      if (clienteSel.isAlive() && clienteSel.conexao.isConnected()){
        clienteSel.enviar(mensagem);
      }
    }
  }
}
