import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class GameLogic {
    private List<Deck.Card> dealerHand;
    private List<Deck.Card> playerHand;

    //compareHands
    //This method evaluates the hand values of each hand and compares it to each other
    //If the player hand value is greater than the dealer hand value, it returns 1, which is evaluated
    //as player wins = true.
    private int compareHands(List<Deck.Card> player, List<Deck.Card> dealer) {
        int pValue = handValue(player);
        int dValue = handValue(dealer);
        return Integer.compare(pValue, dValue);
    }

    //playerWins
    //This method evaluates if the players wins by dealer disqualification or greater hand value
    //returns true or false
    public boolean playerWins(List<Deck.Card> playerHand, List<Deck.Card> dealerHand) {
        if (dealerQualifies(dealerHand) == false) {
            return true;
        }
        int test = compareHands(playerHand, dealerHand);
        boolean playerWins = test > 0;
        return playerWins;
    }

    //winnings
    //This method evaluates both pair plus and the ante bet separately and adds them together towards the end.
    //It will return the total winnings of the function back to the client.
    public int winnings(List<Deck.Card> pHand, List<Deck.Card> dHand, int pairPlusBet, int anteBet) {
        if (isStraightAndFlush(pHand)) {
            pairPlusBet = pairPlusBet * 40;
        } else if (isThreePair(pHand)) {
            pairPlusBet = pairPlusBet * 30;
        } else if (isStraight(pHand)) {
            pairPlusBet = pairPlusBet * 6;
        } else if (isFlush(pHand)) {
            pairPlusBet = pairPlusBet * 3;
        } else if (isPair(pHand)) {
            pairPlusBet = pairPlusBet * 1;
        } else {
            pairPlusBet = 0;
        }

        if (playerWins(pHand, dHand)) {
            anteBet = anteBet + (anteBet * 1);
        }

        int totalWinnings = anteBet + pairPlusBet;
        return totalWinnings;
    }

    //dealerQualifies
    //Checks if the dealer qualifies to play. It must have a queen high or higher
    public boolean dealerQualifies(List<Deck.Card> dealerHand) {
        for (Deck.Card card : dealerHand) {
            if (card.getNumber().equals("queen") || card.getNumber().equals("king") || card.getNumber().equals("ace")) {
                return true;
            }
        }
        return false;
    }

    //handValue
    //The purpose of this method is to evaluate the hands based on hand rankings.
    //If the hand has no hand ranking, it will evaluate by the high card value.
    //If both dealer and player hands have the same hand ranking, it will break
    //the tie by higher card value.
    public int handValue(List<Deck.Card> hand) {
        if (isStraightAndFlush(hand)) {
            return 600 + getHighCardValue(hand);
        } else if (isThreePair(hand)) {
            return 500 + getHighCardValue(hand);
        } else if (isStraight(hand)) {
            return 400 + getHighCardValue(hand);
        } else if (isFlush(hand)) {
            return 300 + getHighCardValue(hand);
        } else if (isPair(hand)) {
            return 200 + getHighCardValue(hand);
        } else {
            return 100 + getHighCardValue(hand);
        }
    }

    //isStraight
    //Evaluates if the hand is a straight
    private boolean isStraight(List<Deck.Card> hand) {
        List<Integer> cardIntegers = new ArrayList<>();
        for (Deck.Card card : hand) {
            cardIntegers.add(cardValue(card));
        }
        Collections.sort(cardIntegers); //Sort the collections to grab the order of the cards for easier evaluation.

        //Ace, 2, 3 edge case
        if (cardIntegers.get(0) == 1 && cardIntegers.get(1) == 2 && cardIntegers.get(2) == 14) {
            return true;
        }
        return cardIntegers.get(2) - cardIntegers.get(1) == 1 && cardIntegers.get(1) - cardIntegers.get(0) == 1;
    }

    //isStraightAndFlush
    //Evaluates if the hand is a straight and a flush
    private boolean isStraightAndFlush(List<Deck.Card> hand) {
        return isFlush(hand) && isStraight(hand);
    }

    //isFlush
    //Evaluates if the hand is a flush
    private boolean isFlush (List<Deck.Card> hand) {
        return (hand.get(0).getSuit().equals(hand.get(1).getSuit()) && hand.get(0).getSuit().equals(hand.get(2).getSuit()));
    }

    //isThreePair
    //Evaluates if the hand is a pair of three
    private boolean isThreePair(List<Deck.Card> hand) {
        return (hand.get(0).getNumber().equals(hand.get(1).getNumber()) && hand.get(0).getNumber().equals(hand.get(2).getNumber()));
    }

    //isPair
    //Evaluates if the hand is a pair
    private boolean isPair(List<Deck.Card> hand) {
        return (hand.get(0).getNumber().equals(hand.get(1).getNumber()) ||
                hand.get(1).getNumber().equals(hand.get(2).getNumber()) ||
                hand.get(2).getNumber().equals(hand.get(0).getNumber()));
    }

    //getHighCardValue
    //This method loops through each card to check for the highest card value
    private int getHighCardValue(List<Deck.Card> hand) {
        int maxCardValue = 0;
        for (Deck.Card card: hand) {
            int cardValue = cardValue(card);
            if (cardValue > maxCardValue) {
                maxCardValue = cardValue;
            }
        }
        return maxCardValue;
    }

    //cardValue
    //This methods assign individual values for individual card numbers.
    private int cardValue(Deck.Card card) {
        String number = card.getNumber();
        switch (number) {
            case "ace":
                return 14;
            case "king":
                return 13;
            case "queen":
                return 12;
            case "jack":
                return 11;
            case "ten":
                return 10;
            case "nine":
                return 9;
            case "eight":
                return 8;
            case "seven":
                return 7;
            case "six":
                return 6;
            case "five":
                return 5;
            case "four":
                return 4;
            case "three":
                return 3;
            case "two":
                return 2;
            default:
                throw new IllegalArgumentException("Invalid card number: " + number);
        }
    }
}
