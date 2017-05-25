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
import org.hamcrest.core.Is;
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
public class FoundationTest {

    private static final Logger logger = LoggerFactory.getLogger(FoundationTest.class);

    private Foundation instance;

    public FoundationTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new Foundation();
    }

    @After
    public void tearDown() {
        instance = null;
    }

    /**
     * Test of add method, of class Foundation.
     */
    @Test
    public void testAdd() throws Exception {
        boolean ace = true;
        for (Card.FOUNDATION foundation : Card.FOUNDATION.values()) {
            for (Card.RANK rank : Card.RANK.values()) {
                try {
                    if (rank == Card.RANK.ACE) {
                        continue;
                    }
                    instance.add(new Card(foundation, rank));
                    fail("Expected an OffenseWhileAddingException to be thrown!");
                } catch (OffenseWhileAddingException ex) {
                    assertThat(ex.getMessage(), Is.is("The first card of the foundation must be an Ace one!"));
                }
            }
        }
        try {
            instance.add(new Card(Card.FOUNDATION.CLUB, Card.RANK.ACE));
        } catch (OffenseWhileAddingException | NullPointerException ex) {
            fail("Unexpected " + ex.getClass().getSimpleName() + "!");
        }
    }

    /**
     * Test of get method, of class Foundation.
     */
    @Test
    public void testGet() {
        for (Card.RANK rank : Card.RANK.values()) {
            try {
                Card card = new Card(Card.FOUNDATION.CLUB, rank);
                instance.add(card);
                if (!instance.get().equals(card)) {
                    fail("The received card is not equal to the expected one!");
                }
            } catch (OffenseWhileAddingException ex) {
                fail(String.format("Unexpected OffenseWhileAddingException! Message: %s", ex.getMessage()));
            }
        }
    }

    /**
     * Test of apply method, of class Foundation.
     */
    @Test
    public void testApply() {
        for (Card.RANK rank : Card.RANK.values()) {
            try {
                Card card = new Card(Card.FOUNDATION.CLUB, rank);
                instance.add(card);
            } catch (OffenseWhileAddingException ex) {
                fail(String.format("Unexpected OffenseWhileAddingException! Message: %s", ex.getMessage()));
            }
        }
        Card.RANK ranks[] = Card.RANK.values();
        for (int i = ranks.length - 1; i >= 0; i--) {
            if (!instance.get().equals(new Card(Card.FOUNDATION.CLUB, ranks[i]))) {
                fail("The received card is not equal to the expected one!");
            }
            instance.apply();
        }
    }

    /**
     * Test of undo method, of class Foundation.
     */
    @Test
    public void testUndo() {
        try {
            Card ace = new Card(Card.FOUNDATION.CLUB, Card.RANK.ACE);
            Card two = new Card(Card.FOUNDATION.CLUB, Card.RANK.TWO);
            instance.add(ace);
            instance.add(two);
            assertTrue(instance.undo());
            if (!instance.getStoredCards().get(0).equals(ace)) {
                fail("The received card is not equal to the expected one!");
            }
        } catch (OffenseWhileAddingException ex) {
            fail(String.format("Unexpected OffenseWhileAddingException! Message: %s", ex.getMessage()));
        }
    }

    /**
     * Test of isKing method, of class Foundation.
     */
    @Test
    public void testIsKing() {
        for (Card.RANK rank : Card.RANK.values()) {
            try {
                Card card = new Card(Card.FOUNDATION.CLUB, rank);
                instance.add(card);
            } catch (OffenseWhileAddingException ex) {
                fail(String.format("Unexpected OffenseWhileAddingException! Message: %s", ex.getMessage()));
            }
        }
        assertTrue(instance.isKing());
    }

}
