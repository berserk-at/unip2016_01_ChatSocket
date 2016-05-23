package chat;

import java.io.BufferedWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author aps
 * Classe responsável pela instância de conexão no servidor
 * 
 */
public class Servidor extends Thread {

  private static ServerSocket server;

  //Sobrescreve a execução da thread para aguardar a conexão de clientes
  @Override
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

  //Iniciar o servidor na porta especificada
  public static void Iniciar(int porta) {
    try {
      server = new ServerSocket(porta);
      InterageCliente.clientes = new ArrayList<>();

      servidor = new Servidor();
      servidor.start();
      Chat.adicionarMensagem("Servidor local iniciado na porta " + porta);

    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Não foi possível iniciar o servidor");
      e.printStackTrace();
    }
  }

  //Envia mensagem para todos os clientes
  public void enviarTodos(String mensagem) {
    BufferedWriter bwS;

    InterageCliente.clientes.stream().filter((clienteSel) -> (clienteSel.isAlive() && clienteSel.conexao.isConnected())).forEach((clienteSel) -> {
      clienteSel.enviar(mensagem);
    });
  }
}
