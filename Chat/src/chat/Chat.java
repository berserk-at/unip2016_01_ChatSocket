package chat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

/**
 *
 * @author Antonio
 */
public final class Chat extends JFrame implements ActionListener, KeyListener {

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
  JScrollPane rolagem;
  JTextField txtComando;
  JList lstUsuarios;
  static DefaultListModel lista;

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

    txtComando = new JTextField();
    txtComando.addKeyListener(this);
    fundo.add(txtComando, BorderLayout.SOUTH);

    lista = new DefaultListModel();
    lstUsuarios = new JList(lista);
    lstUsuarios.setSelectionMode(SINGLE_SELECTION);
    fundo.add(lstUsuarios, BorderLayout.EAST);

    txtConteudo = new JTextPane();
    txtConteudo.setContentType("text/html");
    rolagem= new JScrollPane(txtConteudo);
    rolagem.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
		    BoundedRangeModel brm = rolagem.getVerticalScrollBar().getModel();
		    boolean wasAtBottom = true;
			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				// TODO Auto-generated method stub
				 if (!brm.getValueIsAdjusting()) {
			           if (wasAtBottom)
			              brm.setValue(brm.getMaximum());
			        } else
			           wasAtBottom = ((brm.getValue() + brm.getExtent()) == brm.getMaximum());
			     }
		});
    fundo.add(rolagem);

    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);

    mnServidor = new JMenuItem("Servidor");
    mnServidor.addActionListener(this);
    menuBar.add(mnServidor);

    mnCliente = new JMenuItem("Cliente");
    mnCliente.addActionListener(this);
    menuBar.add(mnCliente);

    setVisible(true);
    txtComando.requestFocus();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals(mnServidor.getActionCommand())) {
      clickServidor();
    } else if (e.getActionCommand().equals(mnCliente.getActionCommand())) {
      clickCliente();
    }
  }

  static StringBuilder conteudo = new StringBuilder();

  public static void adicionarMensagem(String mensagem) {
    conteudo.append(mensagem).append("<br>");
    chat.txtConteudo.setText("<html><body>" + conteudo.toString() + "<body></html>");
  }

  public void clickServidor() {
    if (Servidor.servidor == null) {
      try {
        JLabel lblMessage = new JLabel("Porta do Servidor:");
        JTextField txtPorta = new JTextField("20000");
        Object[] texts = {lblMessage, txtPorta};
        if (JOptionPane.showConfirmDialog(null, texts) == JOptionPane.OK_OPTION) {
          adicionarMensagem("Iniciando servidor local na porta " + txtPorta.getText());
          Servidor.Iniciar(Integer.parseInt(txtPorta.getText()));
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Não foi possível se conectar com o servidor");
      }

    } else {
      Servidor.servidor.enviarTodos("/sair");
      Servidor.servidor.stop();
      Servidor.servidor = null;
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

        Object[] texts = {rotServidor, txtServidor, rotPorta, txtPorta, rotNome, txtNome};
        if (JOptionPane.showConfirmDialog(null, texts) == JOptionPane.OK_OPTION) {
          Cliente.conectar(txtServidor.getText(), Integer.parseInt(txtPorta.getText()), txtNome.getText());
        }
      } catch (Exception ex) {

      }

    } else {
      Cliente.cliente.desconectar();
    }
  }

  public static void atualizarListaClientes() {
    lista.clear();
    for (InterageCliente clienteSel : InterageCliente.clientes) {
      System.out.println("Lista="+clienteSel.nome);
      if (clienteSel.conexao.isConnected()){
        lista.addElement(clienteSel.nome);
      }
    }
  }
  public static void atualizarListaClientes(StringBuilder dados) {
    int inicio=0;
    int fim=dados.indexOf(",");
    lista.clear();
    System.out.println("listaDados="+dados.toString());
    while (fim>0){
      lista.addElement(dados.substring(inicio, fim));
      inicio=fim+1;
      fim=dados.indexOf(",",inicio);
    }
    lista.addElement(dados.substring(inicio));
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
      try {
        if (Cliente.cliente == null) {
          JOptionPane.showMessageDialog(null, "Você não está conectado a nenhum servidor");
        } else {
          Cliente.cliente.enviarMensagem(txtComando.getText());
          txtComando.setText("");
        }
      } catch (IOException ex) {
        JOptionPane.showMessageDialog(null, "Servidor offline");
        Cliente.cliente.desconectar();
        ex.printStackTrace();
      }
    }

  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }

  public static String Substituir(String palavra, String encontrar, String substituir) {
    if (palavra==null || palavra.equals("")){
      return("");
    } else {
      StringBuilder linha=new StringBuilder(palavra);
      Substituir(linha, encontrar, substituir);
      return(linha.toString());
    }
  }

  public static void Substituir(StringBuilder linha, String encontrar, String substituir) {
    if (substituir == null) {
      substituir = "";
    }
    int p =-1;
    while((p=linha.indexOf(encontrar,p))>=0){
      linha.replace(p, p+encontrar.length(), substituir);
      p=p+substituir.length();
    }
  }
  
}
