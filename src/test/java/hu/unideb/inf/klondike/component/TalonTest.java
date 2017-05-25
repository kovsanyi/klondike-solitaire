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
public class TalonTest {

    private static final Logger logger = LoggerFactory.getLogger(TalonTest.class);

    private Talon instance;
    private List<Card> cards;

    public TalonTest() {
    }

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        cards = new ArrayList<>();
        for (Card.RANK rank : Card.RANK.values()) {
            cards.add(new Card(Card.FOUNDATION.CLUB, rank));
        }
        instance = new Talon(cards);
    }

    @After
    public void tearDown() {
        instance = null;
    }

    /**
     * Test of next and getVisibleCards methods, of class Talon.
     */
    @Test
    public void testNextAndGetVisibleCards() {
        assertTrue(instance.getVisibleCards().isEmpty());
        for (int i = 0; i < 4; i++) {
            instance.next();
            assertEquals(instance.getVisibleCards(), cards.subList(i * 3, i * 3 + 3));
        }
        instance.next();
        assertEquals(instance.getVisibleCards(), cards.subList(12, cards.size()));
        instance.next();
        assertEquals(instance.getVisibleCards(), new ArrayList<>());
    }

    /**
     * Test of get method, of class Talon.
     */
    @Test
    public void testGet() {
        assertNull(instance.get());
        for (int i = 0; i < 4; i++) {
            instance.next();
            assertEquals(instance.get(), cards.get(i * 3 + 2));
        }
        instance.next();
        instance.next();
        for (int i = 0; i < 4; i++) {
            instance.next();
            assertEquals(instance.get(), cards.get(i * 3 + 2));
        }
    }

    /**
     * Test of apply method, of class Talon.
     */
    @Test
    public void testApply() {
        instance.next();
        assertEquals(instance.get(), cards.get(2));
        instance.apply();
        assertEquals(instance.get(), cards.get(1));
        instance.apply();
        assertEquals(instance.get(), cards.get(0));
        instance.apply();
        assertEquals(instance.get(), null);
    }

    /**
     * Test of undo method, of class Talon.
     */
    @Test
    public void testUndo() {
        instance.next();
        instance.next();
        instance.undo();
        assertEquals(instance.getVisibleCards(), cards.subList(0, 3));
        instance.get();
        instance.apply();
        instance.undo();
        assertEquals(instance.getVisibleCards(), cards.subList(0, 3));
    }

}
