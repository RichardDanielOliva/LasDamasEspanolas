import java.util.Scanner;
public class LasDamas extends JuegoDeMesa{

	Scanner teclado = new Scanner (System.in);
	
	public LasDamas(){
		inicializar();
	}

	/*
	//jugar(): Metodo que controla el turno de cada jugador. Las blancas juegan en turnos impares mientras las negras lo hacen en multiplos de dos. 
	El metodo jugarTurno() regresa un valor determinado. Si este es 0 implica que puede volver a comer, si es 1 el toca al siguiente jugador
	*/
	public void jugar(){ 
		int turnoSinComer = 0;
		int numTurnos= 1;

		while((auxNegras>0) && (auxBlancas>0) && (turnoSinComer<20)){
			mostrar();

			int auxNumTurnos = 0;
			if ((numTurnos % 2) != 0) {
				auxNumTurnos = jugarTurno("Juegan Blancas", numTurnos);
			} else {
				auxNumTurnos = jugarTurno("Juegan negras", numTurnos);
			}

			convertirADama();
			if (auxNumTurnos == 1) turnoSinComer++;
				else turnoSinComer = 0;

			numTurnos+= auxNumTurnos;
		}
	}

	/*
	//jugarTurno() Interactua con el usuario pidiendole la ficha y el movimiento a realizar. 
	Diferencia entre ambos jugadores (blancas o negras) y el tipo de ficha (peon o dama)
	*/
	public int jugarTurno(String quienJuega, int numTurnos){
		int x, y, xDest, yDest, orientacionNum, auxcoordenada;
		String coordenada;
		boolean puedeVolverAComer;
		int resultado = 0;


		System.out.println(quienJuega);
			if (numTurnos>1) coordenada = pedirCoordenadas(" "); // If sirve de auxiliar para poder jugar en el terminal del ordenador

			coordenada = pedirCoordenadas("Senala la coordenada de la ficha: ");
			x = coordenada.charAt(0) - 'A';
			y = obtenerCoorY(coordenada);

			try{
				if ((numTurnos % 2) == 0) {
					if (tablero [x][y]==EstadoTablero.n) {
						orientacionNum = pedirDireccion();
						puedeVolverAComer = moverPeon(x, y, orientacionNum, 1);
						if (puedeVolverAComer) resultado = 1;

					} else {
						if (tablero [x][y]==EstadoTablero.N){
							coordenada = pedirCoordenadas("Senala la coordenada de destino: ");
							xDest = coordenada.charAt(0) - 'A';
							yDest = obtenerCoorY(coordenada);
							puedeVolverAComer = moverDama(x, xDest, y, yDest);
							if (!puedeVolverAComer) resultado = 1;
						} else System.out.println("Tienes que mover las fichas negras"); //Se puede colocar como un error
					}

				} else { 
					if (tablero [x][y]==EstadoTablero.b) {
						orientacionNum = pedirDireccion();
						puedeVolverAComer = moverPeon(x, y, orientacionNum, 0);
						if (puedeVolverAComer) resultado = 1;

					} else {
						if (tablero [x][y]==EstadoTablero.B){
							coordenada = pedirCoordenadas("Senala la coordenada de destino: ");
							xDest = coordenada.charAt(0) - 'A';
							yDest = obtenerCoorY(coordenada);
							puedeVolverAComer = moverDama(x, xDest, y, yDest);
							if (!puedeVolverAComer) resultado = 1;
						} else System.out.println("Tienes que mover las fichas Blancas");
					}
				}
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		return resultado;
	}

	public boolean moverPeon(int x, int y, int orientacionNum, int colorFicha) throws NoSePuedeMover{ //podemos unir moverBlancas y moverNegras
		int posProximaX = 0;
		int posProximaY = 0;
		int posProximaAuxX = 0;
		int posProximaAuxY = 0;
		boolean resultado = false;

		switch (colorFicha){
			case 0: switch (orientacionNum){
						case 1: posProximaX = 1;
								posProximaY = -1;
								posProximaAuxX = 1;
								posProximaAuxY = 1;
								break;
						case 2: posProximaX = 1;
								posProximaY = 1;
								posProximaAuxX = 1;
								posProximaAuxY = -1;
								break;
					}	
					break;
			case 1: switch (orientacionNum){
						case 1: posProximaX = -1;
								posProximaY = -1;
								posProximaAuxX = -1;
								posProximaAuxY = 1;
								break;
						case 2: posProximaX = -1;
								posProximaY = 1;
								posProximaAuxX = -1;
								posProximaAuxY = -1;
								break;
					}
		}

		if (saleDelTablero(x, y, posProximaX, posProximaY))
			throw new NoSePuedeMover("Te estas saliendo del tablero");

		if (posicionOcupada(x, y, posProximaX, posProximaY))
			throw new NoSePuedeMover("Posicion ocupada");

		if(tablero[x + posProximaX][y + posProximaY] == EstadoTablero.V){
			tablero[x + posProximaX][y + posProximaY] = tablero[x][y];
			tablero[x][y] = EstadoTablero.V;
			resultado = true;

		} else {
			if (!noPuedesComer(x, y, (2*posProximaX), (2*posProximaY)))
				throw new NoSePuedeMover("Posicion no valida");
			comer(x,y, posProximaX, posProximaY);
			tablero[x][y] = EstadoTablero.V;

			if (noPuedesComerNuevamente(x + 2*posProximaX, y + 2*posProximaY, posProximaX, posProximaY, posProximaAuxX, posProximaAuxY)) 
				resultado = true;
		}
		return resultado;
	}

	public boolean noPuedesComerNuevamente(int x, int y, int posProximaX, int posProximaY, int posProximaAuxX, int posProximaAuxY){
		boolean resultado = false;

		if (saleDelTablero(x, y, posProximaX, posProximaY)) {
			posProximaX = x;
			posProximaY = y;
		}
		if (saleDelTablero(x, y, posProximaAuxX, posProximaAuxY)) {
			posProximaAuxX = x;
			posProximaAuxY = y;
		}
		if (tablero[x][y] != EstadoTablero.N && tablero[x][y] != EstadoTablero.B) {
			if (posicionValida(x, y, posProximaX, posProximaY) || posicionValida(x, y, posProximaAuxX, posProximaAuxY)) {
				if(noPuedesComer(x, y, 2*posProximaX, 2*posProximaY) || noPuedesComer(x, y, 2*posProximaAuxX, 2*posProximaAuxY)){
				} else resultado = true;
			} else resultado = true;
		} else resultado = true; //Aqui debemos colocar el metodo que comprueba si la dama puede volver a comer

		return resultado;
	}

	public boolean saleDelTablero(int x, int y, int posProximaX, int posProximaY){
		return ((x + posProximaX) >= TAMANOTABLERO) || //crear metodo, empleamos dos veces
			((x + posProximaX) < 0) ||
			((y + posProximaY) >= TAMANOTABLERO) ||
			((y + posProximaY) < 0);
	}

	public boolean posicionValida(int x, int y, int posProximaX, int posProximaY){
		boolean resultado = false;
		if (tablero[x+posProximaX][y+posProximaY] != tablero[x][y] && tablero[x+posProximaX][y+posProximaY] != EstadoTablero.V)
			resultado = true;

		return resultado;
	}

	public boolean posicionOcupada(int x, int y, int posProximaX, int posProximaY){
		boolean resultado = false;
		if (tablero[x+posProximaX][y+posProximaY] == tablero[x][y])
			resultado = true;
		return resultado;
	}

	public boolean noPuedesComer(int x, int y, int posProximaX2, int posProximaY2){
		boolean resultado = true;
		if (saleDelTablero(x, y, posProximaX2, posProximaY2)) resultado = false;
			else if (tablero[x+posProximaX2][y+posProximaY2] != EstadoTablero.V)
				resultado = false;
		return resultado;
	}
	

	private String pedirCoordenadas(String mensaje){
		String resultado;
		if (mensaje.length()>1) System.out.println(mensaje);
		resultado = teclado.nextLine();
		resultado = resultado.toUpperCase();
		return resultado;
	}

	private int obtenerCoorY(String coordenada){
		String auxResultado = coordenada.substring(1);
		int resultado = Integer.parseInt(auxResultado);
		return resultado;
	}

	private int pedirDireccion(){
		System.out.println("Senala hacia donde quieres moverla 1.izquierda 2.Derecha: ");
		int resultado = teclado.nextInt();
		return resultado;
	}

	public void comer(int x, int y, int posProximaX, int posProximaY){
		if (!(((x + posProximaX) >= TAMANOTABLERO) ||
			((x + posProximaX) < 0) ||
			((y + posProximaY) >= TAMANOTABLERO) ||
			((y + posProximaY) < 0))){
			tablero[x + 2*posProximaX][y + 2*posProximaY] = tablero[x][y];
			tablero[x + posProximaX][y + posProximaY] = EstadoTablero.V;
		} else {
			tablero[x + posProximaX][y + posProximaY] = tablero[x + posProximaX][y + posProximaY];
		}
	}

	public boolean posicionValida(int x, int y){
		return (x >= TAMANOTABLERO) || //crear metodo, empleamos dos veces
			(x < 0) ||
			(y >= TAMANOTABLERO) ||
			(y < 0);
	}

	public boolean moverDama(int xOrig, int xDest, int yOrig, int yDest) throws NoSePuedeMover {
		boolean resultado = true;

		EstadoTablero estadoAuxIgual = obtenerAuxDama(xOrig, yOrig, 0);
		EstadoTablero estadoAuxDif = obtenerAuxDama(xOrig, yOrig, 1);
		int numPosiIntermX = obtenerAuxDama(xOrig, xDest, yOrig, yDest, 2);
		int numPosiIntermY = obtenerAuxDama(xOrig, xDest, yOrig, yDest, 3);
		int auxX = obtenerAuxDama(xOrig, xDest, yOrig, yDest, 4);
		int auxY = obtenerAuxDama(xOrig, xDest, yOrig, yDest, 5);

		try {
			resultado = comprobarErroresDama(xOrig, xDest, yOrig, yDest, numPosiIntermX, numPosiIntermY, auxX, auxY, estadoAuxIgual);
		} catch (NoSePuedeMover e1){
			System.out.println(e1.getMessage());
		}
		
		if (resultado == true) { //aqui movemos
			tablero[xDest][yDest] = tablero[xOrig][yOrig];
			tablero[xOrig][yOrig] = EstadoTablero.V;
			comerDama(xOrig, yOrig, auxX, auxY, numPosiIntermX, estadoAuxDif);
		}

		return resultado;
	}

	public void comerDama(int xOrig, int yOrig, int auxX, int auxY, int numPosiIntermX, EstadoTablero estadoAuxDif){
		for (int c = 1; c < numPosiIntermX; c++) {
			if (tablero[xOrig + (auxX*c)][yOrig + (auxY*c)] == estadoAuxDif) {
				tablero[xOrig + (auxX*c)][yOrig + (auxY*c)] = EstadoTablero.V;
			} 
		}
	}

	public EstadoTablero obtenerAuxDama(int xOrig, int yOrig, int selec){
		EstadoTablero resultado = EstadoTablero.V;
		if (tablero[xOrig][yOrig]==EstadoTablero.N){
			if (selec==0) resultado = EstadoTablero.n;
				else if (selec==1)resultado = EstadoTablero.b;
		} else { 
			if (selec==0) resultado = EstadoTablero.b;
			else if (selec==1) resultado = EstadoTablero.n;
		}

		return resultado;
	}

	public int obtenerAuxDama(int xOrig, int xDest, int yOrig, int yDest, int selec){
		int resultado = 0;

		if (xDest - xOrig < 0) { //VERIFICAR AUXILIARES
			if (selec==2) resultado = -1 * (xDest - xOrig );
			if (selec==4) resultado = -1;
		}else {
			if (selec==2) resultado = (xDest - xOrig);
			if (selec==4) resultado = 1;
		}

		if (yDest - yOrig < 0) {
			if (selec==3) resultado = -1 * (yDest - yOrig );
			if (selec==5) resultado = -1;
		}else {
			if (selec==3) resultado = (yDest - yOrig);
			if (selec==5) resultado = 1;
		}

		return resultado;
	}

	public boolean comprobarErroresDama(int xOrig, int xDest, int yOrig, int yDest, int numPosiIntermX, int numPosiIntermY, int auxX, int auxY, EstadoTablero estadoAuxIgual) throws NoSePuedeMover {
		boolean resultado = true;

		if (posicionValida(xOrig, yOrig)){
			resultado = false;
			throw new NoSePuedeMover("Te sales del tablero!"); 
		}

		if (tablero[xDest][yDest] != EstadoTablero.V) {
			resultado = false;
			throw new NoSePuedeMover("Posicion ocupada!");
		}

		if (numPosiIntermX - numPosiIntermY != 0){
			resultado = false;
			throw new NoSePuedeMover("Posicion Invalida, debes moverte en vertical");
		} 

		for (int c = 1; c < numPosiIntermX; c++) {
			if (tablero[xOrig + (auxX*c)][yOrig + (auxY*c)] == estadoAuxIgual) {
				resultado = false;
				throw new NoSePuedeMover("No puedes saltarte una de tus fichas!");
			}
		}

		return resultado;
	}

}
