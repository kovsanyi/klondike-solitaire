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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a talon which shows the {@code Card} objects in triple
 * groups.
 */
public class Talon {

    class State {

        final int pointer;
        final int available;
        final Character prevOp;

        State(int pointer, int available, Character prevOp) {
            this.pointer = pointer;
            this.available = available;
            this.prevOp = prevOp;
        }

        int getPointer() {
            return pointer;
        }

        int getAvailable() {
            return available;
        }

        Character getPrevOp() {
            return prevOp;
        }

    }

    private final static Logger logger = LoggerFactory.getLogger(Talon.class);

    private List<Card> storedCards;
    private Card erasureBuffer;
    private int pointer;
    private int available;
    private List<State> prevState;
    private List<Card> prevCard;

    /**
     * Constructor for creating a {@code Talon} object.
     *
     * @param cards the {@code Card} objects to add
     */
    public Talon(List<Card> cards) {
        storedCards = new ArrayList<>();
        cards.stream().forEach(card -> storedCards.add(card));
        erasureBuffer = null;
        pointer = 0;
        available = 0;
        prevState = new ArrayList<>();
        prevCard = new ArrayList<>();
    }

    /**
     * This method returns a list of {@code Card} objects up to max 3 items
     * which ones are visible on the {@code Talon}.
     *
     * @return a list of {@code Card} objects up to max 3 items
     */
    public List<Card> getVisibleCards() {
        return available != 0 ? storedCards.subList(pointer, pointer + available).stream().collect(Collectors.toList()) : new ArrayList<>();
    }

    /**
     * This method moves the {@code Talon} to the next state and makes the next
     * triple group of {@code Card} objects visible. If the next state contains
     * less than 3 {@code Card}s, only the remaining {@code Card} objects will
     * be visible. When there is a turning point, no {@code Card} is available.
     */
    public void next() {
        prevState.add(new State(pointer, available, 'n'));
        if (erasureBuffer != null) {
            erasureBuffer = null;
            logger.warn("Erasure buffer had not been empty when the next() function was called! Cleared.");
        }
        if (storedCards.size() > 3 && pointer == 0 && available == 0) {
            available = 3;
        } else if (pointer + available + 3 < storedCards.size()) {
            pointer += available;
            available = 3;
        } else {
            if (storedCards.size() <= 3 && pointer == 0) {
                if (available != 0) {
                    available = 0;
                } else {
                    available = storedCards.size();
                }
            } else {
                pointer += available;
                available = storedCards.size() - pointer;
                if (storedCards.size() == pointer) {
                    pointer = 0;
                    available = 0;
                }
            }
        }
    }

    /**
     * This method prepares the available {@code Card} on the {@code Talon}
     * which is the last one of the visible group for deleting and returns it
     * for testing.
     * <p>
     * <b>Use this method to move {@code Card} objects only!</b>
     *
     * @return the available {@code Card} on the {@code Talon}
     */
    public Card get() {
        return erasureBuffer = available != 0 ? storedCards.get(pointer + available - 1) : null;
    }

    /**
     * This method deletes the {@code Card} previously added to the erasure
     * buffer by called {@link hu.unideb.inf.klondike.component.Talon#get()}
     * method.
     *
     * @see hu.unideb.inf.klondike.component.Talon#get()
     */
    public void apply() {
        if (erasureBuffer != null) {
            prevState.add(new State(pointer, available, '-'));
            prevCard.add(erasureBuffer);
            storedCards.remove(erasureBuffer);
        } else {
            logger.warn("cardToRemove's value had been null when the apply() function was called!");
        }
        available -= 1;
        erasureBuffer = null;
    }

    /**
     * This method restores the {@code Talon} to the previous state. Returns
     * {@code true} if the previous state restored successfully, {@code false}
     * if nothing changed.
     *
     * @return {@code true} if the previous state restored successfully,
     * {@code false} if nothing changed
     */
    public boolean undo() {
        if (!prevState.isEmpty()) {
            if (prevState.get(prevState.size() - 1).prevOp.equals('-')) {
                pointer = prevState.get(prevState.size() - 1).getPointer();
                available = prevState.get(prevState.size() - 1).getAvailable();
                storedCards.add(pointer + available - 1, prevCard.remove(prevCard.size() - 1));
                prevState.remove(prevState.size() - 1);
            } else {
                pointer = prevState.get(prevState.size() - 1).getPointer();
                available = prevState.get(prevState.size() - 1).getAvailable();
                prevState.remove(prevState.size() - 1);
            }
            return true;
        }
        return false;
    }

    /**
     * Returns a {@code String} representation of the {@code Talon} object. It
     * contains the {@code Card} objects string representation that are visible
     * on the talon.
     *
     * @return a {@code String} representation of the {@code Talon} object
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<Card> cards = getVisibleCards();
        for (int i = 0; i < cards.size(); i++) {
            sb.append(i == cards.size() - 1 ? "[x]" : "[ ]")
                    .append(cards.get(i))
                    .append(i == cards.size() - 1 ? "" : '\n');
        }
        return sb.toString();
    }

}
