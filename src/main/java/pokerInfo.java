import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;


public class pokerInfo implements Serializable {
     // Serializing all these variables for the Client
     private static final long serialVersionUID = 1L;
     List<Deck.Card> dealerHand;
     List<Deck.Card> playerHand;
     int ante;
     int pairPlus;
     int totalWinnings;
     int gameWinnings;
     String message;
     boolean winsGame;
     boolean playerFolded;
     boolean requestCards;
}