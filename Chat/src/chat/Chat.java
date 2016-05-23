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
 * @author APS
 * Projeto de chat com web Socket
 * Esta é a classe principal que fornece interface gráfica para uso tanto no cliente como no servidor
 * Existem 2 botões na parte superior, um com nome de Servidor e outro com nome de cliente,
 * Clicando no botão de menu servidor, pode-se habilitar o servidor localmente quando não estiver ativo 
 * fornecendo a porta de conexão, e quando está ativo, desabilitará o servidor.
 * Clicando no botão de menu cliente, se não conectado, irá pedir os dados e fará a conexão com o servidor, 
 * caso já esteja conectado, fará a desconexão com o servidor.
 * Todas as mensagens trocadas pelos clientes são públicas e mostradas no componente JTextPane na área central da tela.
 * No lado direito contém a lista dos usuários atualmente conectados.
 * E na parte de baixo permite que o usuário digite a mensagem, para enviar a mensagem basta o pressionar da tecla ENTER
 * Alguns comandos que a mensagem suporta:
 * /sair 
 * Desconecta do servidor
 * 
 * /nome=[novonome]
 * Substitua o [novonome] pelo nome que deseja para o seu usuário
 */
public final class Chat extends JFrame implements ActionListener, KeyListener {

  public static Chat chat; //Variável estática que armazenará a instância da classe Chat para ser usada por outros objetos

  //Main de execução, não é necessário nenhum parâmetro
  public static void main(String[] args) {
    chat = new Chat();
  }

  public Chat() {
    super();
    Componentes();//Desenha os componentes na tela
  }

  JMenuItem mnServidor; //Botão de menu servidor habilita/desabilita o servidor
  JMenuItem mnCliente; //Botão de menu cliente conecta/desconecta do servidor
  JTextPane txtConteudo; //Componente que mostra todas as mensagens na janela central
  JScrollPane rolagem; //Componente para permitir a rolagem das mensagens na janela central
  JTextField txtComando; //Componente onde o usuário digita a mensagem
  JList lstUsuarios; //Componente que mostra a lista com os usuários conectados
  static DefaultListModel lista; // Lista com os usuários conectados

  public void Componentes() {
    setTitle("Chat 1.0"); //Definindo o título da janela
    setSize(500, 500); // Definindo a largura e altura da janela
    
    Dimension dm = Toolkit.getDefaultToolkit().getScreenSize();//Obtendo as dimensões da tela
    setLocation((dm.width - getSize().width) / 2, (dm.height - getSize().height) / 2); //centralizando a janela na tela

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Definindo que ao fechar a janela deve finalizar o programa
    
    JPanel fundo = new JPanel(); //Painel de fundo
    fundo.setLayout(new BorderLayout(3, 3)); //Definindo o layout dos componentes contidos para usarem borda e manterem uma distância de 3 pixels na horizontal e vertical
    getContentPane().add(fundo); // adicionadno o painel de fundo a tela

    JLabel rotBemVindo = new JLabel("<html><b>Bem Vindo ao Chat 1.0</b></html>");//Rotulo para mostrar uma mensagem amigável ao usuário
    fundo.add(rotBemVindo, BorderLayout.NORTH); // Adiciona a mensagem ao topo da tela

    txtComando = new JTextField(); //Componente onde o usuário digita a mensagem
    txtComando.addKeyListener(this); // Definir para esta janela tratar os eventos de teclado
    fundo.add(txtComando, BorderLayout.SOUTH); // Adicionando ao rodapé da tela

    lista = new DefaultListModel(); //Inicializa a lista com nenhum elemento
    lstUsuarios = new JList(lista);//Cria o componente que mostrará a lista e a variável DefaultListModel que armazenará a lista
    lstUsuarios.setSelectionMode(SINGLE_SELECTION); //Define para permitir selecionar apenas um ítem por vez
    fundo.add(lstUsuarios, BorderLayout.EAST); // Adiciona o componente na lateral direita

    txtConteudo = new JTextPane();//Componente que mostrará as mensagens
    txtConteudo.setContentType("text/html");//Define para que ele aceite html
    rolagem= new JScrollPane(txtConteudo);//Componente para permitir a rolagem das mensagens na janela central
    rolagem.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() { 
      //Aqui fará o controle para garantir que a rolagem sempre que alterada irá rolar automaticamente para o fim, mostrando as últimas mensagens
		    BoundedRangeModel brm = rolagem.getVerticalScrollBar().getModel();//Define a estrutura que será observada
		    boolean wasAtBottom = true; // 
			@Override 
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				// Está é o evento responsável por capturar as mudanças
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
  //Sobrescreve a função que é executada quando um componente é clicado
  // Aqui será tratados os eventos para o botão servidor e para o botão cliente
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals(mnServidor.getActionCommand())) {
      clickServidor();
    } else if (e.getActionCommand().equals(mnCliente.getActionCommand())) {
      clickCliente();
    }
  }

  static StringBuilder conteudo = new StringBuilder();

  //Atualiza a janela central adicionando a mensagem recebida pela função
  public static void adicionarMensagem(String mensagem) {
    conteudo.append(mensagem).append("<br>");
    chat.txtConteudo.setText("<html><body>" + conteudo.toString() + "<body></html>");
  }

  //Quando o botão do servidor for clicado, pede a porta ao usuário e inicia a instância do servidor
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

  // Quando o botão do cliente é pressionado, pede os dados para conexão com o servidor(IP, porta e nome) e realiza a conexão com o servidor
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
        ex.printStackTrace();
      }

    } else {
      Cliente.cliente.desconectar();
    }
  }

  //Atualiza a lista do lado direito com o nome dos clientes conectados no servidor, usando o array de interageCliente
  public static void atualizarListaClientes() {
    lista.clear();
    InterageCliente.clientes.stream().filter((clienteSel) -> (clienteSel.conexao.isConnected())).forEach((clienteSel) -> {
      lista.addElement(clienteSel.nome);
    });
  }
  
  //Obtém a lista de clientes que estão no servidor para atualizar na instância de cliente a lista do lado direito
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

  //Captura o pressionar do enter para enviar a mensagem
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

  //Serve pra subtituir uma parte de string dentro de outra string
  public static String Substituir(String palavra, String encontrar, String substituir) {
    if (palavra==null || palavra.equals("")){
      return("");
    } else {
      StringBuilder linha=new StringBuilder(palavra);
      Substituir(linha, encontrar, substituir);
      return(linha.toString());
    }
  }

  //Subtitui o conteúdo de linha, de uma string para outra
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
