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

import hu.unideb.inf.klondike.OffenseWhileAddingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents one foundation pile of the four. Each
 * {@code Foundation} build up from <i>Ace</i> to <i>King</i>
 * {@link hu.unideb.inf.klondike.component.Card.RANK Rank} type of {@code Card}
 * objects.
 * <p>
 * Rules are the following:
 * <ul>
 * <li> the first {@code Card} must be an <i>Ace</i> one;
 * <li> every piles should be built down by alternate
 *      {@link hu.unideb.inf.klondike.component.Card.COLOR color}ed ones; and
 * <li> the {@code cards} must be in ascending order on each piles, from
 *      <i>A</i> to <i>King</i>.
 * </ul>
 * <p>
 * Sorting is provided by the
 * {@link hu.unideb.inf.klondike.component.Card.RANK Rank} of the {@code Card}:
 * <ul>
 * <li> Ace &lt; 2 &lt; 3 &lt; 4 &lt; 5 &lt; 6 &lt; 7 &lt; 8 &lt; 9 &lt; 10 &lt;
 *      Jack &lt; Queen &lt; King
 * </ul>
 *
 * @see hu.unideb.inf.klondike.component.Card
 * @see hu.unideb.inf.klondike.component.Card.RANK
 * @see hu.unideb.inf.klondike.component.Card.COLOR
 */
public class Foundation {

    static Logger logger = LoggerFactory.getLogger(Foundation.class);

    private List<Card> storedCards;
    private Card erasureBuffer;
    private List<Card> prevState;
    private List<Character> prevOp;

    /**
     * Constructor for creating an empty {@code Foundation} instance.
     */
    public Foundation() {
        storedCards = new ArrayList<>();
        erasureBuffer = null;
        prevState = new ArrayList<>();
        prevOp = new ArrayList<>();
        logger.trace("An empty foundation created.");
    }

    /**
     * This method returns all the {@code Card} objects stored on the
     * {@code Foundation}.
     *
     * @return all the {@code Card} objects stored on the {@code Foundation}
     */
    public List<Card> getStoredCards() {
        return storedCards.stream().collect(Collectors.toList());
    }

    /**
     * This method adds the specified {@code Card} to the {@code Foundation}.
     *
     * @param card the {@code Card} to add
     * @throws OffenseWhileAddingException if the {@code Card} can not be added
     * due to rule offense
     */
    public void add(Card card) throws OffenseWhileAddingException {
        if (erasureBuffer != null) {
            erasureBuffer = null;
            logger.warn("Erasure buffer had not been empty when the add() function was called! Cleared.");
        }
        if (storedCards.isEmpty()) {
            if (card.getRank() == Card.RANK.ACE) {
                card.setState(Card.STATE.UPSIDE);
                storedCards.add(card);
            } else {
                throw new OffenseWhileAddingException("The first card of the foundation must be an Ace one!");
            }
        } else {
            Card cardToComp = storedCards.get(storedCards.size() - 1);
            if ((card.compareTo(cardToComp) == 1)
                    & card.getFoundation() == cardToComp.getFoundation()) {
                card.setState(Card.STATE.UPSIDE);
                storedCards.add(card);
            } else {
                throw new OffenseWhileAddingException("The given card has too low/high rank or different color!");
            }
        }
        logger.trace("Card({}-{}) added to foundation.", card.getFoundation(), card.getRank());
        prevState.add(card);
        prevOp.add('+');
    }

    /**
     * This method prepares the top {@code Card} of the {@code Foundation} for
     * deleting and returns it for testing. To perform the wipe operation, call
     * the {@link hu.unideb.inf.klondike.component.Foundation#apply()} method.
     * <p>
     * <b>Use this method to move {@code Card} objects only!</b>
     *
     * @see hu.unideb.inf.klondike.component.Foundation#apply()
     * @return he top {@code Card} of the {@code Foundation}
     */
    public Card get() {
        return erasureBuffer = ((storedCards.size() > 0) ? storedCards.get(storedCards.size() - 1) : null);
    }

    /**
     * This method deletes the {@code Card} previously added to the erasure
     * buffer by called
     * {@link hu.unideb.inf.klondike.component.Foundation#get()} method.
     *
     * @see hu.unideb.inf.klondike.component.Foundation#get()
     */
    public void apply() {
        if (erasureBuffer != null) {
            storedCards.remove(erasureBuffer);
            prevState.add(erasureBuffer);
            prevOp.add('-');
            erasureBuffer = null;
            logger.trace("Erasure buffer cleared.");
        } else {
            logger.warn("Erasure buffer had been empty when the apply() function was called! Nothing changed.");
        }
    }

    /**
     * This method returns {@code true} if the top {@code Card} of the
     * {@code Foundation} has <i>King</i> type of {@code Rank}.
     *
     * @see hu.unideb.inf.klondike.component.Card.RANK
     * @return {@code true} if the top {@code Card} of the {@code Foundation}
     * has <i>King</i> type of {@code Rank}
     */
    public boolean isKing() {
        if (!storedCards.isEmpty()) {
            if (storedCards.get(storedCards.size() - 1).getRank() == Card.RANK.KING) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method restores the {@code Foundation} to the previous state.
     * Returns {@code true} if the previous state restored successfully,
     * {@code false} if nothing changed.
     *
     * @return {@code true} if the previous state restored successfully,
     * {@code false} if nothing changed
     */
    public boolean undo() {
        if (!prevState.isEmpty()) {
            if (prevOp.get(prevOp.size() - 1).equals('-')) {
                storedCards.add(prevState.get(prevState.size() - 1));
            } else {
                storedCards.remove(prevState.get(prevState.size() - 1));
            }
            prevOp.remove(prevOp.size() - 1);
            prevState.remove(prevState.size() - 1);
            logger.info("The foundation's previous state restored successfully.");
            return true;
        }
        logger.warn("The foundation's previous state did not restored! Is it in initial state?");
        return false;
    }

    /**
     * Returns a {@code String} representation of top {@code Card} of the
     * {@code Foundation}.
     *
     * @return a {@code String} representation of top {@code Card} of the
     * {@code Foundation}
     */
    @Override
    public String toString() {
        return !storedCards.isEmpty() ? "Foundation[ " + storedCards.get(storedCards.size() - 1) + " ]" : "Foundation[ ]";
    }

}
