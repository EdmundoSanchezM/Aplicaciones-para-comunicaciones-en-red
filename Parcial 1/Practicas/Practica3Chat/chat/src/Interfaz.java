import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Interfaz extends JFrame {
    private static final long serialVersionUID = 2L;

    public Interfaz(String host, int puerto, String nombre) {
        // -----------------Recibiendo Parametros------------------//
        this.host = host;
        this.puerto = puerto;
        this.nombre = nombre;
        // ----------------Creando Interfaz-------------------------//
        setBounds(325, 100, 400, 400);
        setTitle("Chat " + nombre);
        setResizable(false);

        panelPrincipal = new JPanel();
        panelCentral = new JPanel();
        panelInferior = new JPanel();
        panelEmojis = new JPanel();
        panelFunciones = new JPanel();
        panelUsuarios = new JPanel();
        panelCombo = new JPanel();
        editor = new JEditorPane("text/html", null);
        editor.setEditable(false);
        areaMensaje = new JTextArea();
        areaMensaje.setLineWrap(true);
        botonesEmojis = new JButton[textoBotonesEmojis.length];
        enviar = new JButton("Enviar");
        seleccion = new JButton("Seleccionar");
        usuariosConectados = new JLabel("    Usuarios Conectados   ");
        escuchaEmojis = new ManejoEmojis();
        usuarioConectado = new JComboBox<>();

        enviar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                miCliente.enviar(new Mensaje("" + nombre + " : " + areaMensaje.getText(), nombre, "", 1));
                areaMensaje.setText("");
            }
        });


        seleccion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                miCliente.enviar(new Mensaje("[" + nombre + "]: " + areaMensaje.getText(), nombre,
                (String) usuarioConectado.getSelectedItem(), 4));
                areaMensaje.setText("");
            }
        });

        panelPrincipal.setLayout(new BorderLayout(5, 5));
        panelCentral.setLayout(new BorderLayout(5, 5));
        panelInferior.setLayout(new BoxLayout(this.panelInferior, BoxLayout.Y_AXIS));
        panelFunciones.setLayout(new BoxLayout(this.panelFunciones, BoxLayout.X_AXIS));

        colocarBotones();
        addWindowListener(new CorreCliente());
        panelCentral.add(new JScrollPane(editor), BorderLayout.CENTER);
        panelFunciones.add(new JScrollPane(areaMensaje));
        panelFunciones.add(enviar);
        panelInferior.add(panelEmojis);
        panelInferior.add(panelFunciones);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
        add(panelPrincipal);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void colocarBotones() {
        for(int i = 0; i < botonesEmojis.length; i += 1) {
            botonesEmojis[i] = new JButton("" + textoBotonesEmojis[i]);
            botonesEmojis[i].addActionListener(escuchaEmojis);
            panelEmojis.add(botonesEmojis[i]);
        }
    }

    private class ManejoEmojis implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            areaMensaje.append(" " + e.getActionCommand() + " ");
        }
    }

    private class CorreCliente extends WindowAdapter {
        public void windowOpened(WindowEvent we) {
            System.out.println("Ventana abierta");
            miCliente = new Cliente(nombre, host, puerto, editor, usuarioConectado);
            miCliente.saludo(nombre);
        }
    }

    private String host;
    private int puerto;
    private String nombre;
    private JPanel panelPrincipal;
    private JPanel panelCentral;
    private JPanel panelInferior;
    private JPanel panelEmojis;
    private JPanel panelFunciones;
    private JPanel panelUsuarios; 
    private JEditorPane editor;
    private JButton[] botonesEmojis;
    private String[] textoBotonesEmojis = {
        ":|",
        ":'|",
        ":v",
        "|:v",
        "<3",
        "7u7",
        "UwU"
    };
    private JTextArea areaMensaje;
    private JButton enviar;
    private JButton archivo;
    public static JComboBox<String> usuarioConectado;
    private JButton seleccion;
    private JLabel usuariosConectados;
    private ActionListener escuchaEmojis;
    private Cliente miCliente;
    private JPanel panelCombo;
}