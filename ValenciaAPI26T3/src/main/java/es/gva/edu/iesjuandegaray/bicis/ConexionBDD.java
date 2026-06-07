package es.gva.edu.iesjuandegaray.bicis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JScrollPane;

public class ConexionBDD extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textFieldNEstaciones;

	private static Connection con;
	private static Statement s;
	private static DatosJSon djSon;
	private static int numEst = 3;
	
	private static final String driver="com.mysql.cj.jdbc.Driver";
	private static final String user="root";
	private static final String pass="123456";
	private static final String url="jdbc:mysql://localhost:3306/valenbicibd";
	
	/**
	 * Launch the application.a
	 */
	
	public Connection conector() {
		con = null;
		try {
			con = DriverManager.getConnection(url,user,pass);
			s = con.createStatement();
			return con;
		} catch (SQLException e) {
			return null;
		}
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConexionBDD frame = new ConexionBDD();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ConexionBDD() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 341);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Introduce el numero de estaciones a consultar");
		lblNewLabel.setBounds(48, 11, 232, 14);
		contentPane.add(lblNewLabel);
		
		textFieldNEstaciones = new JTextField();
		textFieldNEstaciones.setBounds(318, 8, 86, 20);
		contentPane.add(textFieldNEstaciones);
		textFieldNEstaciones.setColumns(10);
		textFieldNEstaciones.setText(""+numEst);
		
		JLabel lblNewLabel_1 = new JLabel("Obtener Datos de Estaciones:");
		lblNewLabel_1.setBounds(126, 39, 154, 14);
		contentPane.add(lblNewLabel_1);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(126, 64, 285, 112);
		contentPane.add(textArea);
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(126, 64, 285, 112);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		contentPane.add(scrollPane);
		
		JLabel lblNewLabel_2 = new JLabel("Estado Conexión:");
		lblNewLabel_2.setBounds(126, 190, 285, 14);
		contentPane.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Primero Obtener Datos de Estaciones y Conectar con BDD");
		lblNewLabel_3.setBounds(126, 224, 308, 14);
		contentPane.add(lblNewLabel_3);
		
		JButton btnNewButton_Datos = new JButton("Datos");
		btnNewButton_Datos.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String[] valor;
				djSon = new DatosJSon(numEst);
				djSon.mostrarDatos(numEst);
				
				valor = djSon.getValues();
				
				for (int i = 0; i < valor.length; i++) {
					String[] campos = valor[i].split(";");
					
					String numero = campos[0];
					String nombre = campos[1];
					String abierto = campos[2];
					String bici = campos[3];
					String anclaje = campos[4];
					String lat = campos[5];
					String lon = campos[6];
					
					
					String frase = "- - - - - - - - - - - - - - - - -\nNumero: "+numero+"\nEstacion: "+nombre+"\nAbierto: "+abierto+"\nBicis: "+bici+"\nAnclajes: "+anclaje+"\nLat: "+lat+"\nLon: "+lon+"\n";
					textArea.append(frase);
				}
					
				
			}
		});
		btnNewButton_Datos.setBounds(10, 35, 95, 23);
		contentPane.add(btnNewButton_Datos);
		
		JButton btnNewButton_Conectar = new JButton("Conectar");
		btnNewButton_Conectar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Connection con = conector();
					if (con != null)
						lblNewLabel_2.setText("Estado conexion: Conectado");
					else
						lblNewLabel_2.setText("Estado conexion: No conectado");
						
			}
		});
		btnNewButton_Conectar.setBounds(10, 186, 95, 23);
		contentPane.add(btnNewButton_Conectar);
		
		
		
		JButton btnNewButton_Anyadir = new JButton("Añadir a BDD");
		btnNewButton_Anyadir.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				if (con != null) {
					try {
						String[] valor;
						String[] camper;						
						djSon.mostrarDatos(numEst);
						valor = djSon.getValues();
						String sql = "INSERT INTO historico (estacion_id,direccion,bicis_disponibles,anclajes_libres,estado_operativo,ubicacion)"
								+ " VALUES (?,?,?,?,?,ST_GeomFromText(?))";
						for (int i = 0; i < numEst; i++) {
							camper = valor[i].split(";");
							
							String numero = camper[0];
							String nombre = camper[1];
							String abierto = camper[2];
							Boolean open;
							if (abierto == "T")
								open = true;
							else
								open = false;
							String bici = camper[3];
							String ancl = camper[4];
							String lat = camper[5];
							String lon = camper[6];
							
							PreparedStatement ps = con.prepareStatement(sql);
							ps.setInt(1, Integer.parseInt(numero));
							ps.setString(2,nombre);
							ps.setInt(3, Integer.parseInt(bici));
							ps.setInt(4, Integer.parseInt(ancl));
							ps.setBoolean(5, open);
							ps.setString(6, "POINT("+ Double.parseDouble(lon) +" "+ Double.parseDouble(lat) + ")");
							
							ps.executeUpdate();
						}

					} catch (SQLException sle) {
						sle.printStackTrace();
					}
				}
			}
		});
		btnNewButton_Anyadir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton_Anyadir.setBounds(10, 220, 95, 23);
		contentPane.add(btnNewButton_Anyadir);
		
		
		
		JButton btnNewButton_Cerrar = new JButton("Cerrar Conexión");
		btnNewButton_Cerrar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					con.close();
					con = null;
					
					if (con == null)
						lblNewLabel_2.setText("Estado conexion: Cerrada");
				} catch (SQLException sle) {
					sle.printStackTrace();
				}
			}
		});
		btnNewButton_Cerrar.setBounds(126, 249, 111, 23);
		contentPane.add(btnNewButton_Cerrar);
		
		

	}
	
	
	
}
