package chat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

/**
 *
 * @author Antonio
 */
public final class Chat extends JFrame implements ActionListener {

  public static Chat chat;
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    chat = new Chat();
  }

  public Chat() {
    super();
    Componentes();
  }

  JMenuItem mnServidor;
  JMenuItem mnCliente;
  JTextPane txtConteudo;

  public void Componentes() {
    setTitle("Chat 1.0");
    setSize(500, 500);
    Dimension dm = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation((dm.width - getSize().width) / 2,
        (dm.height - getSize().height) / 2);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel fundo = new JPanel();
    fundo.setLayout(new BorderLayout(3, 3));
    getContentPane().add(fundo);

    JLabel rotBemVindo = new JLabel("<html><b>Bem Vindo ao Chat 1.0</b></html>");
    fundo.add(rotBemVindo, BorderLayout.NORTH);

    JTextField txtComando = new JTextField();
    fundo.add(txtComando, BorderLayout.SOUTH);

    DefaultListModel lista = new DefaultListModel();
    lista.addElement("Jane Doe");
    lista.addElement("John Smith");
    lista.addElement("Kathy Green");

    JList lstUsuarios = new JList(lista);
    lstUsuarios.setSelectionMode(SINGLE_SELECTION);
    fundo.add(lstUsuarios, BorderLayout.EAST);

    txtConteudo = new JTextPane();
    txtConteudo.setContentType("text/html");
    fundo.add(txtConteudo);

    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);

    mnServidor = new JMenuItem("Servidor");
    mnServidor.addActionListener(this);
    menuBar.add(mnServidor);

    mnCliente = new JMenuItem("Cliente");
    mnCliente.addActionListener(this);
    menuBar.add(mnCliente);

    setVisible(true);
  }


  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals(mnServidor.getActionCommand())) {
      clickServidor();
    } else if (e.getActionCommand().equals(mnCliente.getActionCommand())) {
      clickCliente();
    }
  }

  static StringBuilder conteudo= new StringBuilder();
  public static void adicionarMensagem(String mensagem){
    conteudo.append(mensagem).append("<br>");
    chat.txtConteudo.setText("<html>"+conteudo.toString()+"</html>");
  }
      
  public void clickServidor() {
    if (Servidor.servidor == null) {
      try {
        JLabel lblMessage = new JLabel("Porta do Servidor:");
        JTextField txtPorta = new JTextField("20000");
        Object[] texts = {lblMessage, txtPorta};
        JOptionPane.showMessageDialog(null, texts);
        adicionarMensagem("Iniciando servidor local na porta "+txtPorta.getText());
        Servidor.Iniciar(Integer.parseInt(txtPorta.getText()));
      } catch (Exception ex) {

      }

    } else {
      Servidor.servidor.stop();
      Servidor.servidor=null;
      adicionarMensagem("Servidor local finalizado");
    }
  }

  public void clickCliente() {
    if (Cliente.cliente == null) {
      try {
        JLabel rotServidor = new JLabel("Servidor:");
        JTextField txtServidor = new JTextField("127.0.0.1");
        
        JLabel rotPorta = new JLabel("Porta do Servidor:");
        JTextField txtPorta = new JTextField("20000");
        
        JLabel rotNome = new JLabel("Nome:");
        JTextField txtNome = new JTextField("Convidado");
        
        Object[] texts = {rotServidor, txtServidor, rotPorta, txtPorta};
        JOptionPane.showMessageDialog(null, texts);
        Cliente.conectar(txtServidor.getText(), Integer.parseInt(txtPorta.getText()), txtNome.getText());
      } catch (Exception ex) {

      }

    } else {
      Cliente.cliente.desconectar();
      Cliente.cliente=null;
      adicionarMensagem("Desconectado do servidor");
    }
  }
  
  public static void atualizarListaClientes(){
      adicionarMensagem("Cliente conectado");
  }

}
