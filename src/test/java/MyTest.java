import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class MyTest {

	static Deck deck;
	static Deck deck2;
	static GameLogic game;

	@BeforeAll
	static void setup() {
		deck = new Deck();
		deck2 = new Deck();
		game = new GameLogic();
	}

	@Test
	void testDrawCards() {
		List<Deck.Card> hand = deck.drawCards(5);
		assertEquals(5, hand.size());

		List<Deck.Card> hand2 = deck.drawCards(45);
		assertEquals(45, hand2.size());
	}

	@Test
	void testCardProperties() {
		Deck.Card card = deck2.new Card("hearts", "ace");
		assertEquals("hearts", card.getSuit());
		assertEquals("ace", card.getNumber());
		assertEquals("/PNG-cards-1.3/ace_of_hearts.png", card.getImage());
	}

	@Test
	void testGameLogic() {
		Deck.Card card = deck.new Card("hearts", "two");
		Deck.Card card2 = deck.new Card("hearts", "three");
		Deck.Card card3 = deck.new Card("hearts", "four");
		List<Deck.Card> playerHand = new ArrayList<>();
		playerHand.add(card);
		playerHand.add(card2);
		playerHand.add(card3);

		int handValue = game.handValue(playerHand);

		assertEquals(604, handValue); //Straight and flush 600 + high card value of 4

		Deck.Card card4 = deck.new Card("hearts", "queen");
		Deck.Card card5 = deck.new Card("spades", "two");
		Deck.Card card6 = deck.new Card("hearts", "two");
		List<Deck.Card> dealerHand = new ArrayList<>();
		dealerHand.add(card4);
		dealerHand.add(card5);
		dealerHand.add(card6);

		int handValue2  = game.handValue(dealerHand);

		assertEquals(212, handValue2); // Pair 200 + high card value of 12
		assertEquals(true, game.dealerQualifies(dealerHand)); //Dealer has a queen so this qualifies
		assertEquals(true, game.playerWins(playerHand, dealerHand)); //Player wins
	}
	@Test
	void testGameLogic2() {
		Deck.Card card = deck.new Card("hearts", "two");
		Deck.Card card2 = deck.new Card("spades", "three");
		Deck.Card card3 = deck.new Card("diamonds", "seven");
		List<Deck.Card> playerHand = new ArrayList<>();
		playerHand.add(card);
		playerHand.add(card2);
		playerHand.add(card3);
		int handValue = game.handValue(playerHand);

		Deck.Card card4 = deck.new Card("hearts", "ten");
		Deck.Card card5 = deck.new Card("spades", "two");
		Deck.Card card6 = deck.new Card("clubs", "nine");
		List<Deck.Card> dealerHand = new ArrayList<>();
		dealerHand.add(card4);
		dealerHand.add(card5);
		dealerHand.add(card6);
		int handValue2 = game.handValue(dealerHand);

		assertEquals(107, handValue); //Standard 100 + Highest card 7
		assertEquals(110, handValue2); //Standard 100 + Highest card 12
		assertEquals(true, game.playerWins(playerHand, dealerHand)); //Dealer is disqualified despite having higher card value so player wins.
		assertEquals(false, game.dealerQualifies(dealerHand)); //Dealer does not has a queen so this qualifies

	}

	@Test
	void testGameBet () {
		int pairPlusBet = 25;
		int anteBet = 10;
		Deck.Card card = deck.new Card("hearts", "seven");
		Deck.Card card2 = deck.new Card("spades", "seven");
		Deck.Card card3 = deck.new Card("diamonds", "seven");
		List<Deck.Card> playerHand = new ArrayList<>();
		playerHand.add(card);
		playerHand.add(card2);
		playerHand.add(card3);
		int handValue = game.handValue(playerHand);

		Deck.Card card4 = deck.new Card("hearts", "ten");
		Deck.Card card5 = deck.new Card("spades", "two");
		Deck.Card card6 = deck.new Card("clubs", "nine");
		List<Deck.Card> dealerHand = new ArrayList<>();
		dealerHand.add(card4);
		dealerHand.add(card5);
		dealerHand.add(card6);
		int handValue2 = game.handValue(dealerHand);

		//Three pair is 30 to 1. 30 * 25 plus 20 since player won with anteBet.
		assertEquals(770, game.winnings(playerHand, dealerHand, pairPlusBet, anteBet));
	}
}
