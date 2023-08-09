import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Server{

	int count = 1;
	int maxClients = 4;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;

	private Deck deck;
	

	//Constructor with callback function as parameter
	Server(Consumer<Serializable> call){
		callback = call;
		server = new TheServer();
		server.start();
		deck = new Deck();
	}

	//Close function to close server and its connections
	public void close() {
		try {
			server.socket.close();
			for (ClientThread c : clients) {
				c.in.close();
				c.out.close();
				c.connection.close();
			}
			clients.clear();
		} catch (Exception e) {
			callback.accept("Error closing server: " + e.getMessage());
		}
	}

	public class TheServer extends Thread{
		ServerSocket socket;

		public void run() {
		
			try(ServerSocket mysocket = new ServerSocket(5555);){
		    System.out.println("Server is waiting for a client!");
		  
			//Keep waiting for clients until the maximum number of clients is reached
		    while(clients.size() < maxClients) {
				//Accept a new client connection and create a new thread
				ClientThread c = new ClientThread(mysocket.accept(), count, deck);
				clients.add(c);
				c.start();
				
				count++;
				
			    }
			}//end of try
				catch(Exception e) {
					callback.accept("Server socket did not launch");
				}
			}//end of while
		}
	

		class ClientThread extends Thread{

			private final Deck deck;
			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;

			//Constructor with parameters
			ClientThread(Socket s, int count, Deck deck){
				this.connection = s;
				this.count = count;
				this.deck = deck;
			}

			//Update all clients with given poker information
			public void updateClients(pokerInfo object) {
				for(int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					try {
						t.out.writeObject(object);
						t.out.flush();
					}
					catch(Exception e) {}
				}
			}
			
			public void run(){
				try {
					//Announce that a new player has joined the game
					callback.accept("Player " + count + " joins the game.");
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);
				} catch (EOFException e) {
					// Handle cases where the player disconnects
					callback.accept("Player " + count + " has disconnected from the server!");
					clients.remove(this);
				}
				catch(Exception e) {
					e.printStackTrace();
					System.out.println("Streams not open");
				}
				while(true) {
					try {
						//Read a pokerInfo object from the client
						pokerInfo info = (pokerInfo) in.readObject();
						if (info.requestCards) {
							// Checks if player wants new game
							if(info.ante <= 1) {
								//Reset variables for serialization for the client
								info.dealerHand = deck.drawCards(3);
								info.playerHand = deck.drawCards(3);
								info.ante = 0;
								info.pairPlus = 0;
								info.totalWinnings = 0;
								info.requestCards = true;
								info.playerFolded = false;
								info.winsGame = false;
								info.message = "Hi Client. Here are your cards";
								// Announce that the player has decided to play
								callback.accept("Player " + count + " decided to play");
							}
							// If the player wants to continue with the current game.
							else if(info.ante >= 5) {
								GameLogic game = new GameLogic();
								callback.accept("Player " + count + " decided to wage " + (info.ante + info.pairPlus) + " dollars");
								// Evaluate the result of the game and update the player's pokerInfo object accordingly
								//If dealer does not qualify or player hand beats dealer hand, evaluate winnings
								if (game.playerWins(info.playerHand, info.dealerHand)) {
									if (info.playerFolded == false) {
										//Player has won the game
										info.totalWinnings = game.winnings(info.playerHand, info.dealerHand, info.pairPlus, info.ante);
										info.gameWinnings += info.totalWinnings;
										info.winsGame = true;
										callback.accept("Player " + count + " has won the game!");
										callback.accept("Player " + count + " has won " + info.totalWinnings + " dollars");
										callback.accept("Player " + count + " has won in total " + info.gameWinnings + " dollars");
									} else {
										//Client has decided to fold
										callback.accept("Player " + count + " had decided to fold and lost " + (info.ante + info.pairPlus) + " dollars");
									}
								} else {
									//Client has lost
									callback.accept("Player " + count + " has lost " + (info.ante + info.pairPlus) + " dollars");
									info.winsGame = false;
									info.pairPlus = 0;
									info.ante = 0;
								}
							}
							// Sends the object back to client
							out.writeObject(info);
						}
					}
					catch(Exception e) {
						callback.accept("Player " + count + " has disconnected from the server!");
						clients.remove(this);
						break;
					}
				}
			}//end of run
		}//end of client thread
}






