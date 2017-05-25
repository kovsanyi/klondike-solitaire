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
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author sanyi
 */
public class TableauTest {

    private static final Logger logger = LoggerFactory.getLogger(TableauTest.class);

    private Tableau instance;

    public TableauTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new Tableau(new ArrayList<>());
    }

    @After
    public void tearDown() {
        instance = null;
    }

    /**
     * Test of add method, of class Tableau.
     */
    @Test
    public void testAdd() {
        Card.RANK ranks[] = Card.RANK.values();
        for (int i = ranks.length - 1; i >= 0; i--) {
            try {
                instance.add(new ArrayList<>(Arrays.asList(new Card(i % 2 == 0 ? Card.FOUNDATION.CLUB : Card.FOUNDATION.HEART, ranks[i]))));
            } catch (OffenseWhileAddingException ex) {
                fail(String.format("Unexpected OffenseWhileAddingException! Message: %s", ex.getMessage()));
            }
        }

    }

    /**
     * Test of get method, of class Tableau.
     */
    @Test
    public void testGet() {
        List<Card> cardList = new ArrayList<>();
        Card.RANK ranks[] = Card.RANK.values();
        for (int i = ranks.length - 1; i >= 0; i--) {
            try {
                Card card = new Card(i % 2 == 0 ? Card.FOUNDATION.CLUB : Card.FOUNDATION.HEART, ranks[i]);
                cardList.add(card);
                instance.add(new ArrayList<>(Arrays.asList(card)));
                if (!instance.get(0).equals(cardList)) {
                    fail("The received list of cards is not equal to the expected one!");
                }
            } catch (OffenseException | IndexOutOfBoundsException ex) {
                fail(String.format("Unexpected %s! Message: %s", ex.getClass().getSimpleName(), ex.getMessage()));
            }
        }
        for (int i = 0; i < ranks.length; i++) {
            try {
                if (!instance.get(i).equals(cardList.subList(i, cardList.size()))) {
                    fail("The received list of cards is not equal to the expected one!");
                }
            } catch (OffenseException | IndexOutOfBoundsException ex) {
                fail(String.format("Unexpected %s! Message: %s", ex.getClass().getSimpleName(), ex.getMessage()));
            }
        }
    }

    /**
     * Test of apply method, of class Tableau.
     */
    @Test
    public void testApply() {
        List<Card> cardList = new ArrayList<>();
        Card.RANK ranks[] = Card.RANK.values();
        for (int i = ranks.length - 1; i >= 0; i--) {
            try {
                Card card = new Card(i % 2 == 0 ? Card.FOUNDATION.CLUB : Card.FOUNDATION.HEART, ranks[i]);
                cardList.add(card);
                instance.add(new ArrayList<>(Arrays.asList(card)));
            } catch (OffenseException | IndexOutOfBoundsException ex) {
                fail(String.format("Unexpected %s! Message: %s", ex.getClass().getSimpleName(), ex.getMessage()));
            }
        }
        try {
            instance.get(5);
            instance.apply();
            if (!instance.getStoredCards().equals(cardList.subList(0, 5))) {
                fail("The received list of cards is not equal to the expected one!");
            }
        } catch (OffenseException | IndexOutOfBoundsException ex) {

        }
    }

    /**
     * Test of undo method, of class Tableau.
     */
    @Test
    public void testUndo() {
        try {
            List<Card> listKing = Arrays.asList(new Card(Card.FOUNDATION.CLUB, Card.RANK.KING));
            List<Card> listQueenJack = Arrays.asList(new Card(Card.FOUNDATION.HEART, Card.RANK.QUEEN), new Card(Card.FOUNDATION.CLUB, Card.RANK.JACK));
            instance.add(listKing);
            instance.add(listQueenJack);
            assertTrue(instance.undo());
            if (!instance.getStoredCards().equals(listKing)) {
                fail("The received list of cards is not equal to the expected one!");
            }
        } catch (OffenseWhileAddingException ex) {
            fail(String.format("Unexpected OffenseWhileAddingException! Message: %s", ex.getMessage()));
        }
    }

}
