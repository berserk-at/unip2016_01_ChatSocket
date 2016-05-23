package chat;

/**
 *
 * @author Antonio
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import javax.swing.*;

//Classe de cliente que é gerada quando o chat é iniciado a instância de cliente, utilizada pra conexão
// com o servidor, permitindo receber e enviar mensagens.
public class Cliente extends Thread {
  
  private String nome;
  private final Socket conexao;
  
  static Cliente cliente;
  public Cliente(Socket conexao) {
    this.conexao = conexao;
  }

  @Override
  public void run() {
    try {
      BufferedReader entrada
          = new BufferedReader(new InputStreamReader(this.conexao.getInputStream()));

      String mensagem = entrada.readLine();
      while (mensagem != null && !("/sair".equalsIgnoreCase(mensagem))) {
        if (mensagem.startsWith("/lista=")){
          StringBuilder lista=new StringBuilder(mensagem.substring(7));
          Chat.atualizarListaClientes(lista);
        } else {
          Chat.adicionarMensagem(mensagem);
        }
        mensagem = entrada.readLine();
      }
      desconectar();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    System.out.println("Passou aqui fim");
  }
  
  public void desconectar() {
    cliente = null;
    try{
      conexao.close();
    } catch(IOException ex){
      ex.printStackTrace();
    }
    Chat.adicionarMensagem("Desconectado do servidor");
  }

  
  public static void conectar(String servidor, int porta, String nome) {
    try{
      Socket socket = new Socket(servidor, porta);
      JOptionPane.showMessageDialog(null, "Conectado");
      cliente=new Cliente(socket);
      cliente.nome=nome;
      cliente.enviarMensagem("/nome="+nome);
      cliente.start();
    } catch(IOException ex) {
      JOptionPane.showMessageDialog(null, "Não foi possível conectar");
    }
  }

  public void enviarMensagem(String mensagem) throws IOException {
    OutputStream ou =  this.conexao.getOutputStream();
    Writer ouw = new OutputStreamWriter(ou);
    BufferedWriter bfw = new BufferedWriter(ouw);
    bfw.write(mensagem+"\n");
    bfw.flush();
  }
  
}
