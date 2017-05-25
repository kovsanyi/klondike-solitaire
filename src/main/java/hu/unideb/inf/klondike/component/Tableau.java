/*
 * Copyright 2017 Faculty of Informatics, Debrecen University, Hungary.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hu.unideb.inf.klondike.component;

import hu.unideb.inf.klondike.OffenseException;
import hu.unideb.inf.klondike.OffenseWhileAddingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents one tableau pile of the seven. Each {@code Foundation}
 * build up from <i>King</i> to <i>A</i>
 * {@link hu.unideb.inf.klondike.component.Card.RANK Rank} type of {@code Card}
 * objects.
 * <p>
 * Rules are the following:
 * <ul>
 * <li> the first {@link hu.unideb.inf.klondike.component.Card card} must be a
 *      <i>King</i> one;
 * <li> on each pile only different
 *      {@link hu.unideb.inf.klondike.component.Card.RANK color}ed ones can
 *      be put together; and
 * <li> the {@link hu.unideb.inf.klondike.component.Card card}s must be in
 *      descending order on every piles, from <i>King</i> to <i>A</i>.
 * </ul>
 * <p>
 * Sorting is provided by the
 * {@link hu.unideb.inf.klondike.component.Card.RANK Rank} of the {@code Card}:
 * <ul>
 * <li> Ace &lt; 2 &lt; 3 &lt; 4 &lt; 5 &lt; 6 &lt; 7 &lt; 8 &lt; 9 &lt; 10 &lt;
 * Jack &lt; Queen &lt; King
 * </ul>
 *
 * @see hu.unideb.inf.klondike.component.Card
 * @see hu.unideb.inf.klondike.component.Card.RANK
 * @see hu.unideb.inf.klondike.component.Card.COLOR
 */
public class Tableau {

    private final static Logger logger = LoggerFactory.getLogger(Tableau.class);

    private List<Card> storedCards;
    private List<Card> erasureBuffer;
    private List<List<Card>> prevState;
    private List<Character> prevOp;

    /**
     * Constructor for creating a {@code Tableau} with the given {@code Card}
     * objects. The {@code State} of the last one will be <i>UPSIDE</i>.
     *
     * @see hu.unideb.inf.klondike.component.Card.STATE
     * @param cards the starting {@code Card} objects to add
     */
    public Tableau(List<Card> cards) {
        storedCards = new ArrayList<>();
        erasureBuffer = new ArrayList<>();
        prevState = new ArrayList<>();
        prevOp = new ArrayList<>();
        cards.stream().forEach(card -> storedCards.add(card));
        if (!cards.isEmpty()) {
            storedCards.get(storedCards.size() - 1).setState(Card.STATE.UPSIDE);
        }
        logger.trace("Tableau created with {} card(s).", cards.size());
    }

    /**
     * This method returns all the {@code Card} objects stored on the
     * {@code Tableau}.
     *
     * @return the {@code Card} objects stored on the {@code Tableau}
     */
    public List<Card> getStoredCards() {
        return storedCards.stream().collect(Collectors.toList());
    }

    /**
     * This method adds the specified {@code Card} objects to the
     * {@code Tableau}.
     *
     * @param cards the list of {@code Card} objects to add
     * @throws OffenseWhileAddingException if the {@code Card} can not be added
     * due to rule offense
     */
    public void add(List<Card> cards) throws OffenseWhileAddingException {
        if (!erasureBuffer.isEmpty()) {
            erasureBuffer.clear();
            logger.warn("Erasure buffer had not been empty when the add() function was called! Cleared.");
        }
        if (storedCards.isEmpty()) {
            if (cards.get(0).getRank() == Card.RANK.KING) {
                cards.stream().peek(card -> card.setState(Card.STATE.UPSIDE)).forEach(card -> storedCards.add(card));
            } else {
                throw new OffenseWhileAddingException(cards.get(0) + " is not a King!");
            }
        } else {
            Card cardToComp = storedCards.get(storedCards.size() - 1);
            if ((cards.get(0).compareTo(cardToComp) == -1)
                    & cards.get(0).getColor() != cardToComp.getColor()) {
                cards.stream().peek(card -> card.setState(Card.STATE.UPSIDE)).forEach(card -> storedCards.add(card));
            } else {
                throw new OffenseWhileAddingException("The given card has too low/high rank or same color!");
            }
        }
        logger.trace("Card({}-{}) added to foundation.", cards.get(0).getFoundation(), cards.get(0).getRank());
        prevState.add(new ArrayList<>(cards));
        prevOp.add('+');
    }

    /**
     * This method prepares the {@code Card} objects from the specified to the
     * last one on the {@code Tableau} for deleting and returns them for
     * testing. To perform the wipe operation, call the
     * {@link hu.unideb.inf.klondike.component.Tableau#apply()} method.
     * <p>
     * <b>Use this method to move {@code Card} objects only!</b>
     *
     * @param index the grabbed {@code Card} index
     * @return the {@code Card} objects from the specified to the last one on
     * the {@code Tableau}
     * @throws OffenseException if {@code State} of the specified {@code Card}
     * is <i>DOWNSIDE</i>.
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public List<Card> get(int index) throws OffenseException, IndexOutOfBoundsException {
        erasureBuffer.clear();
        if (storedCards.get(index).getState() == Card.STATE.UPSIDE) {
            erasureBuffer.addAll(storedCards.subList(index, storedCards.size()));
            return erasureBuffer;
        } else {
            throw new OffenseException("Card state is downside!");
        }
    }

    /**
     * This method deletes the {@code Card} previously added to the erasure
     * buffer by called
     * {@link hu.unideb.inf.klondike.component.Tableau#get(int)} method.
     *
     * @see hu.unideb.inf.klondike.component.Tableau#get(int)
     */
    public void apply() {
        if (!erasureBuffer.isEmpty()) {
            prevState.add(new ArrayList<>(erasureBuffer));
            if (storedCards.size() - erasureBuffer.size() > 0) {
                if (storedCards.get(storedCards.size() - erasureBuffer.size() - 1).getState() == Card.STATE.DOWNSIDE) {
                    prevOp.add('d');
                } else {
                    prevOp.add('-');
                }
            } else {
                prevOp.add('-');
            }
            storedCards.removeAll(erasureBuffer);
            logger.trace("Erasure buffer cleared.");
        } else {
            logger.warn("Erasure buffer had been empty when the apply() function was called! Nothing changed.");
        }
        if (!storedCards.isEmpty()) {
            if (storedCards.get(storedCards.size() - 1).getState() == Card.STATE.DOWNSIDE) {
                storedCards.get(storedCards.size() - 1).setState(Card.STATE.UPSIDE);
                logger.trace("The bottom card of the tableau turned upside.");
            }
        }
        erasureBuffer.clear();
    }

    /**
     * This method restores the {@code Tableau} to the previous state. Returns
     * {@code true} if the previous state restored successfully, {@code false}
     * if nothing changed.
     *
     * @return {@code true} if the previous state restored successfully,
     * {@code false} if nothing changed
     */
    public boolean undo() {
        if (!prevState.isEmpty()) {
            switch (prevOp.get(prevOp.size() - 1)) {
                case 'd':
                    storedCards.get(storedCards.size() - 1).setState(Card.STATE.DOWNSIDE);
                case '-':
                    prevState.get(prevState.size() - 1).stream().forEach(card -> storedCards.add(card));
                    break;
                default:
                    storedCards.removeAll(prevState.get(prevState.size() - 1));
                    break;
            }
            prevOp.remove(prevOp.size() - 1);
            prevState.remove(prevState.size() - 1);
            logger.info("The tableau's previous state restored successfully.");
            return true;
        }
        logger.warn("The tableau's previous state did not restored! Is it in initial state?");
        return false;
    }

    /**
     * Returns a {@code String} representation of the {@code Card} has
     * <i>UPSIDE</i> type of {@code State} on the {@code Tableau}.
     *
     * @see hu.unideb.inf.klondike.component.Card.STATE
     * @return a {@code String} representation of the {@code Card} has
     * <i>UPSIDE</i> type of {@code State} on the {@code Tableau}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Tableau[ ");
        List<Card> cards = getStoredCards();
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getState() != Card.STATE.DOWNSIDE) {
                sb.append(i + 1).append('@').append(cards.get(i)).append(' ');
            }
        }
        return sb.append(']').toString();
    }

}
