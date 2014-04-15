/**
 * 06/Dic/2012
 * Alan Bonino
 * abonino@itmexicali.edu.mx
 * Last update:
 */
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
public class MainModule {
	final int pyrometer = 702;
	final int magnetometer = 703;
	final double epsilon = 0.2;
	final double time = 1;
	final double delta = time + epsilon;
	final double lambda = 1; //VALOR DE LAMBDA
	final double outputMean = 15.2;
	final double outputStandarDeviation = 1.67;
	final double inputMean = 14.25;
	final double inputStandarDeviation = 2.55;
	
	final double MagnetometerInputMean = 5;
	final double MagnetometerOutputMean = 5;
	final double PyrometerInputMean = 5;
	final double PyrometerOutputMean = 5;
	final double MagnetometerInputStandarDeviation  = 1;
	final double MagnetometerOutputStandarDeviation = 1;
	final double PyrometerInputStandarDeviation = 1;
	final double PyrometerOutputStandarDeviation = 1;
	
	//Contadores para mostrar salida [No necesario]
	int totalAccessCounter = 0;
	int totalExitCounter = 0;
	int totalPyrometerCounter = 0;
	int totalMagnetometerCounter = 0;
	
	

	int lastID = 0, actualID = 0; // Node ID
	Date lastTime = null, actualTime = null; // Sample hour
	//int event = 0;	//Contador de eventos sucedidos durante la ejecucion del programa [No es necesario]

	int initialNode = 0; 	// Event first node
	int lastNode = 0; 		// Event last node
	int sampleCounter = 0; 	// Sample counter 
	int initialCounter = 0;
	int finalCounter = 0;
	int intersectionCounter = 0;
	StringBuilder stringBuilder = new StringBuilder(); 				// Para concatenar y mostrar [No es necesario]
	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss"); 	// For time

	public static void main(String[] args)
	{
		MainModule main = new MainModule();
		main.reading();
	}

	/**
	 * Metodo para inicializar las variables cada vez que la diferencia 
	 * de tiempo entre dos muestras consecutivas es mayor a delta
	 */
	public void init()
	{
		sampleCounter = 0;
		intersectionCounter = 0;
		lastNode = 0;
		initialNode = 0; //*Verificar* debera ser =actualID??
		initialCounter = 0;
		finalCounter = 0;
		stringBuilder = new StringBuilder();
		
		lastID = 0; 		
		lastTime = null;
	}
	/**
	 * Metodo que verifica si la muestra es de un evento nuevo 
	 * o pertenece a un ciclo de conteo.
	 * Nota: Un ciclo de conteo es aquel en el que entre una muestra anterior
	 * y actual existe una diferencia de tiempo menor o igual a delta
	 * @param nodeID
	 * @param time
	 * @return "true" cuando comienza un ciclo nuevo de conteo, 
	 * "false" cuando la muestra pertenece a un ciclo de conteo.
	 */
	public boolean firstTimeSetup(int nodeID, Date time)
	{
		if (lastID == 0 && lastTime == null) 
		{
			//Si es la primera vez que alguna muestra llega en la ejecucion del programa
			if(actualID==0 && actualTime == null)
			{
				//Esto debe de suceder una UNICA vez cuando comienza la ejecucion del programa
				sampleCounter++;
				initialCounter++;
				actualID = lastID = nodeID;
				actualTime = lastTime = time;				
				if (actualID == pyrometer) 
					stringBuilder.append('P');
				else
					stringBuilder.append('M');
			}
			else
			{
				//Si no es asi, entonces es el comienzo de un nuevo ciclo de conteo 
				//por lo que hay un valor previo dentro de actualTime y actualID
				sampleCounter++;
				initialCounter++;
				initialNode = actualID; // *Verificar*
				if (nodeID == pyrometer) 
					stringBuilder.append('P');
				else
					stringBuilder.append('M');
				return false;
				
			}
			return true;
		}
		else
			return false;
	}
	/**
	 * Metodo principal que recibe las lecturas y hace el proceso de conteo cuando
	 * la diferencia de tiempo entre la muestra anterior y actual es menor o igual a delta
	 * @param nodeID
	 * @param time
	 */
	public void operation(int nodeID, Date time)
	{
		if(!firstTimeSetup(nodeID, time))
		{		
			lastID = actualID;
			lastTime = actualTime;

		actualID = nodeID;
		actualTime = time;
		double diff = (actualTime.getTime() - lastTime.getTime()) / 1000; // tiempo de diferencia en segundos
		
		// Si la diferencia es menor o igual a delta entonces comienza a verificar (estado de alerta)
		if (diff >= 0 && diff <= delta) 
		{
			sampleCounter++;			
			
			// si es distinto a cero guarda el sensor con el que empezo
			if (initialNode == 0)
				initialNode = actualID;
			
			// Si el nodo inicial es distinto al actual entonces esta terminando el evento
			if (lastNode == 0 && initialNode != actualID) 
				lastNode = actualID;
			
			// si la diferencia es menor a delta y hay lectura de otro sensor entonces es interseccion
			if (actualID != lastID) 
			{
				//Hay una interseccion, por lo tanto se incrementa el contador de interseccion
				intersectionCounter++;
				
				if (actualID == pyrometer) 
					stringBuilder.append('P');
				else
					stringBuilder.append('M');
				
			} else {
				// Si no entonces proviene del mismo sensor pero habra que revisar si es distinto del inicial
				if (actualID == lastNode) 
				{
					// entonces son muestras del sensor final (pero NO incluye los de interseccion)
					finalCounter++;
					
					if (actualID == pyrometer) 
						stringBuilder.append('P');
					else
						stringBuilder.append('M');
					
				} else {
					if (actualID == initialNode) {
						//Si no es ninguno de los anteriores, entonces es del sensor inicial
						initialCounter++;
						
						if (actualID == pyrometer) 
							stringBuilder.append('P');
						else
							stringBuilder.append('M');
					}
				}
			}
		} else {
			
			Functions f = new Functions();
			int val = f.validateEventType(initialNode, lastNode);
			
			// AQUI TERMINA EL CICLO DE CONTEO CUANDO LA DIFERENCIA ENTRE CADA MUESTRA ES MAYOR A DELTA
			if (sampleCounter > 0 && intersectionCounter > 0 /*&& initialCounter > 0
					&& finalCounter > 0*/) {
				
				
				if(val==1)//Exit
				{
					double formulaValue = 0;
					//Aqui hay que validar la media de salida + desviacion estandar de salida
					if(PyrometerOutputMean+PyrometerOutputStandarDeviation>=initialCounter)
					{
						//usar initialCounter
						formulaValue = f.evaluateAccessOrExit(initialCounter+intersectionCounter+finalCounter, outputMean, lambda, outputStandarDeviation);
					}
					else
					{
						//usar PyrometerOutputMean+PyrometerOutputStandarDeviation y mandar los demas a otro evento
						formulaValue = f.evaluateAccessOrExit(PyrometerOutputMean+PyrometerInputStandarDeviation+intersectionCounter+finalCounter, outputMean, lambda, outputStandarDeviation);
						//Ademas, tambien hay actividad adicional del pirometro al principio
						totalPyrometerCounter++;
						System.out.println("PYROMETER");
					}
					
					if(formulaValue>=0)
					{
						totalExitCounter++;
						System.out.println("-> Salida detectada");
					}
				}else if (val==2)//Access
				{
					double formulaValue =0;
					if(PyrometerInputMean+PyrometerInputStandarDeviation>=finalCounter)
					{
						//usar finalcounter
						 formulaValue = f.evaluateAccessOrExit(initialCounter+intersectionCounter+finalCounter, inputMean, lambda, inputStandarDeviation);
					}
					else
					{
						//usar PyrometerInputMean+PyrometerInputStandarDeviation y mandar los demas a otro evento
						formulaValue = f.evaluateAccessOrExit(PyrometerInputMean+PyrometerInputStandarDeviation+intersectionCounter+finalCounter, inputMean, lambda, inputStandarDeviation);
						//Ademas, tambien hay actividad adicional del pirometro al final que indica que quedo en la habitación
						totalPyrometerCounter++;
						System.out.println("PYROMETER");
					}
					if(formulaValue>=0)
					{
						totalAccessCounter++;
						System.out.println("<- Entrada detectada");
					}					
				}
				else
					System.out.println("El evento no cumple con los requerimientos para ser evaluado");
				//System.out.println();
			}
			else
			{
				if(val==3)
				{//Pyrotmeter
					totalPyrometerCounter++;
					System.out.println("PYROMETER");
				}
				else if(val==4)
				{
					totalMagnetometerCounter++;
					System.out.println("MAGNETOMETER");
				}
				else
					System.out.println("OTRO EVENTO; sample="+sampleCounter+" intersection="+intersectionCounter+"| initialNode="+initialNode+" lastNode="+lastNode);
			}
				
			init(); // initialize variables
		}
		}

	}

	/**
	 * Metodo para leer los datos desde los archivos
	 */
	public void reading()
	{	
		//for(int i=0; i<21;i++)
		//{
		try {
			// Open the file that is the first command line parameter
			FileInputStream fstream = new FileInputStream(
					"src/Files/ExperimentosTodos.txt");
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				String lineArray[] = strLine.split("\t");
				operation(Integer.parseInt(lineArray[1]), formatter.parse(lineArray[3]));				
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		//}
		System.out.println("Total de eventos");
		System.out.println("Entrada: "+totalAccessCounter);
		System.out.println("Salida: "+totalExitCounter);
		System.out.println("Magnetometro: "+totalMagnetometerCounter);
		System.out.println("Pyrometro: "+totalPyrometerCounter);
		
	}

}
