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

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a card from the 52-card French card deck.
 * <p>
 * Each {@code Card} has a foundation, rank and state value that are represented
 * as enums:
 * <ul>
 * <li> {@link hu.unideb.inf.klondike.component.Card.FOUNDATION Foundation}: the
 *      foundation of the {@code Card} such as <i>Heart</i>, <i>Diamond</i>,
 *      <i>Spade</i> or <i>Club</i>.
 * <li> {@link hu.unideb.inf.klondike.component.Card.RANK Rank}: the rank of the
 *      {@code Card} for example <i>King</i>, <i>Queen</i>, <i>Ace</i>, <i>10</i>,
 *      <i>9</i> and so on.
 * <li> {@link hu.unideb.inf.klondike.component.Card.STATE State}: that
 *      indicates if the {@code Card} is visible or not.
 * </ul>
 * <p>
 * Furthermore, there is a fourth component named
 * {@link hu.unideb.inf.klondike.component.Card.COLOR Color} but this enum's
 * value is not stored as part of the {@code Card} object. It's value can be
 * obtained by calling {@link hu.unideb.inf.klondike.component.Card#getColor()}
 * method.
 *
 * @see hu.unideb.inf.klondike.component.Card.FOUNDATION
 * @see hu.unideb.inf.klondike.component.Card.RANK
 * @see hu.unideb.inf.klondike.component.Card.STATE
 * @see hu.unideb.inf.klondike.component.Card.STATE
 * @see hu.unideb.inf.klondike.component.Card.COLOR
 * @see hu.unideb.inf.klondike.component.Card#getColor()
 */
public class Card implements Comparable<Card> {

    private static final Logger logger = LoggerFactory.getLogger(Card.class);

    /**
     * Enum for representing the foundation of the {@code Card}.
     */
    public static enum FOUNDATION {
        HEART,
        DIAMOND,
        SPADE,
        CLUB
    }

    /**
     * Enum for representing the rank of the {@code Card}.
     */
    public static enum RANK {
        ACE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN,
        JACK,
        QUEEN,
        KING
    }

    /**
     * Enum for representing the color of the {@code Card}.
     */
    public static enum COLOR {
        /**
         * Red such as <i>Heart</i> or <i>Diamond</i>.
         */
        RED,
        /**
         * Black such as <i>Spade</i> or <i>Club</i>.
         */
        BLACK
    }

    /**
     * Enum for representing the state of the {@code Card}.
     */
    public static enum STATE {
        /**
         * When the {@code Card} is visible.
         */
        UPSIDE,
        /**
         * When the {@code Card} is not visible.
         */
        DOWNSIDE
    }

    private final FOUNDATION foundation;
    private final RANK rank;
    private STATE state;

    private Card() {
        this.foundation = null;
        this.rank = null;
        this.state = STATE.DOWNSIDE;
    }

    /**
     * Creates a {@code Card} instance with the specified {@code Foundation} and
     * specified {@code Rank}. The {@code State} of {@code Card} object will be
     * {@link hu.unideb.inf.klondike.component.Card.STATE#DOWNSIDE State.DOWNSIDE}.
     *
     * @see hu.unideb.inf.klondike.component.Card.FOUNDATION
     * @see hu.unideb.inf.klondike.component.Card.RANK
     * @see hu.unideb.inf.klondike.component.Card.STATE
     * @param foundation {@code Foundation} to be set
     * @param rank {@code Rank} to be set
     * @throws NullPointerException if one of the parameters is null
     */
    public Card(FOUNDATION foundation, RANK rank) throws NullPointerException {
        Objects.requireNonNull(foundation);
        Objects.requireNonNull(rank);

        this.foundation = foundation;
        this.rank = rank;
        this.state = STATE.DOWNSIDE;

        logger.trace("Card({}-{}) created.", foundation, rank);
    }

    /**
     * This method returns the {@code Foundation} of the {@code Card} object.
     *
     * @see hu.unideb.inf.klondike.component.Card.FOUNDATION
     * @return the {@code Foundation} of the {@code Card} object
     */
    public FOUNDATION getFoundation() {
        return foundation;
    }

    /**
     * This method returns the {@code Rank} of the {@code Card} object.
     *
     * @see hu.unideb.inf.klondike.component.Card.RANK
     * @return the {@code Rank} of the {@code Card} object
     */
    public RANK getRank() {
        return rank;
    }

    /**
     * This method returns the {@code Color} of the {@code Card} object.
     *
     * @see hu.unideb.inf.klondike.component.Card.COLOR
     * @return the {@code Color} of the {@code Card} object
     */
    public COLOR getColor() {
        switch (foundation) {
            case HEART:
            case DIAMOND:
                return COLOR.RED;
            default:
                return COLOR.BLACK;
        }
    }

    /**
     * This method returns the {@code State} of the {@code Card} object.
     *
     * @see hu.unideb.inf.klondike.component.Card.STATE
     * @return the {@code State} of the {@code Card} object
     */
    public STATE getState() {
        return state;
    }

    /**
     * This method sets the {@code State} of the {@code Card} object.
     *
     * @see hu.unideb.inf.klondike.component.Card.STATE
     * @param state the {@code State} to be set
     */
    public void setState(STATE state) {
        logger.trace("Card({}-{})'s state updated({}-->{}).", foundation, rank, this.state, state);

        this.state = state;
    }

    /**
     * Compares this {@code Card} to the specified {@code Card}. Returns a
     * negative integer, zero, or a positive integer as this {@code Card}'s
     * {@code Rank} level is less than, equal to, or greater than the specified
     * {@code Card} object.
     *
     * @see hu.unideb.inf.klondike.component.Card.RANK
     * @param o the {@code Card} to be compared
     * @return a negative integer, zero, or a positive integer as this
     * {@code Card}'s {@code Rank} level is less than, equal to, or greater than
     * the specified {@code Card} object
     */
    @Override
    public int compareTo(Card o) {
        return rank.ordinal() - o.getRank().ordinal();
    }

    /**
     * Compares this object to the specified object. The result is {@code true}
     * if the given object is a {@code Card} object that contains the same
     * {@code Foundation} and {@code Rank} value, {@code false} otherwise.
     *
     * @see hu.unideb.inf.klondike.component.Card.FOUNDATION
     * @see hu.unideb.inf.klondike.component.Card.RANK
     * @param obj the object to compare with
     * @return {@code true} if the given object is a {@code Card} object and
     * equivalent to this {@code Card}, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Card) {
            return (this.foundation == ((Card) obj).foundation) && (this.rank == ((Card) obj).rank);
        }
        return false;
    }

    /**
     * Returns a {@code String} representation of the {@code Card} object,
     * including it's {@code Foundation}, {@code Rank} and {@code Color}.
     *
     * @see hu.unideb.inf.klondike.component.Card.FOUNDATION
     * @see hu.unideb.inf.klondike.component.Card.RANK
     * @see hu.unideb.inf.klondike.component.Card.COLOR
     * @return a {@code String} representation of the {@code Card} object,
     * including it's {@code Foundation}, {@code Rank} and {@code Color}
     */
    @Override
    public String toString() {
        return "Card{"
                + "foundation=" + String.format("%7s", foundation)
                + ", rank=" + String.format("%5s", rank)
                + ", color=" + String.format("%5s", getColor())
                + '}';
    }

}
