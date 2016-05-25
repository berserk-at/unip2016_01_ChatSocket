package chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author aps
 * Classe criada e executada pelo servidor pra realizar o envio de informações para o cliente
 */
public class InterageCliente extends Thread {

  public static ArrayList<InterageCliente> clientes;

  public String nome=null;
  public final Socket conexao;

  public InterageCliente(Socket conexao) {
    this.conexao = conexao;
  }

  //Sobrescreve a execução da thread de InterageCliente pra aguardar o recebimento de dados vindos do cliente
  @Override
  public void run() {
    try {
      BufferedReader entrada
          = new BufferedReader(new InputStreamReader(this.conexao.getInputStream()));

      PrintStream saida = new PrintStream(this.conexao.getOutputStream());
      // recebe o nome do cliente
      String mensagem = entrada.readLine();
      while (mensagem != null && !("/sair".equalsIgnoreCase(mensagem))) {
        if (mensagem.startsWith("/nome=")){
          if (mensagem.substring(6).trim().length()>0){
            if (nome==null){
              Servidor.servidor.enviarTodos("O usuário <b>"+mensagem.substring(6)+"</b> se conectou.");
            } else {
              Servidor.servidor.enviarTodos("O usuário <b>"+this.nome+"</b> agora se chama <b>"+mensagem.substring(6)+"</b>.");
            }
            this.nome=mensagem.substring(6);
            Chat.atualizarListaClientes();
            
            Servidor.servidor.enviarTodos("/lista="+listaClientes());
          }
        } else {
          Servidor.servidor.enviarTodos("<b>["+nome+"]</b>:"+mensagem);
        }
        mensagem = entrada.readLine();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    try{
      conexao.close();
      clientes.remove(this);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    Chat.adicionarMensagem("O usuário <b>"+this.nome+"</b> desconectou.");
    Chat.atualizarListaClientes();
  }

  //Envia mensagem para o cliente
  public void enviar(String mensagem) {
    try {
      OutputStream ou = conexao.getOutputStream();
      Writer ouw = new OutputStreamWriter(ou);
      BufferedWriter bfw = new BufferedWriter(ouw);

      bfw.write(mensagem + "\r\n");
      bfw.flush();
    } catch (IOException ex) {
      System.out.println("Problema ao enviar mensagem para o cliente=" + this.nome);
    }
  }
  
  //Lista os clientes conectados ao servidor
  public String listaClientes(){
    StringBuilder lista=new StringBuilder();
    boolean primeiro=true;
    for (InterageCliente clienteSel : InterageCliente.clientes) {
      if (clienteSel.conexao.isConnected()){
        if (primeiro){
          primeiro=false;
        } else {
          lista.append(",");
        }
        lista.append(Chat.Substituir(clienteSel.nome,",",""));
      }
    }
    return(lista.toString());
  }
  
}
