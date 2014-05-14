import java.awt.HeadlessException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Scanner;

import javax.swing.JOptionPane;


public class BitacoraExperimentos {
	StringBuilder log = new StringBuilder();
	/**
	 * @param args
	 */
	public static void main(String[] args) {		  
		BitacoraExperimentos bitacora = new BitacoraExperimentos();
		
        bitacora.menu();
        Scanner input = new Scanner(System.in);
        int option = 0;
		while(option!=666)
		{
			option = input.nextInt();
			switch (option) {
			case 11:
				System.out.println("Nuevo experimento creado");
				bitacora.newFile();
				break;
			case 22:
				System.out.println("Experimento terminado");
				bitacora.closeFile();
				break;
			case 33:
				System.out.println("Hora de entrada");
				bitacora.timeStamp(option);
				break;
			case 44:
				System.out.println("Hora de salida");
				bitacora.timeStamp(option);
				break;

			default:
				break;
			}
		}
		input.close();
		
	}
	private void menu() {
		System.out.println("Seleccione una opcion:");
        System.out.println("11: Nuevo experimento");
        System.out.println("22: Terminar Experimento");
        System.out.println("33: Marcar hora de entrada");
        System.out.println("44: Marcar hora de salida");
        System.out.println("666: Salir");	
	}
	private void closeFile() {
		log.append("FIN DEL EXPERIMENTO\n");
		System.out.println(log);
	}
	private void newFile() {
		log = new StringBuilder();
		log.append("INICIO DEL EXPERIMENTO\n");
		
	}
	private void timeStamp(int option) 
	{
		 java.util.Date date= new java.util.Date();
		 if(option==33)
			 log.append("1\t"+new Timestamp(date.getTime())+"\n");
		 if(option==44)
			 log.append("0\t"+new Timestamp(date.getTime())+"\n");
		 
	}

}
