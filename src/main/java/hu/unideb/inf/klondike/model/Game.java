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
package hu.unideb.inf.klondike.model;

import hu.unideb.inf.klondike.component.*;
import hu.unideb.inf.klondike.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages all the components to build up a game.
 */
public class Game {
    
    class Movement {
        
        private final String from;
        private final String to;
        private final int fromIndex;
        private final int toIndex;
        private final int score;
        
        Movement(String from, String to, int fromIndex, int toIndex, int score) {
            this.from = from;
            this.to = to;
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
            this.score = score;
        }
        
        String getFrom() {
            return from;
        }
        
        String getTo() {
            return to;
        }
        
        int getFromIndex() {
            return fromIndex;
        }
        
        int getToIndex() {
            return toIndex;
        }
        
        int getScore() {
            return score;
        }
        
    }
    
    private static final Logger logger = LoggerFactory.getLogger(Game.class);
    
    private Tableau[] tableauPiles;
    private Foundation[] foundationPiles;
    private Talon talon;
    private int score, moves;
    private List<Movement> prevMovements;
    private List<Character> prevOps;

    /**
     * Constructor for creating a {@code Game} instance.
     */
    public Game() {
        tableauPiles = new Tableau[7];
        foundationPiles = new Foundation[4];
        score = 0;
        moves = 0;
        prevMovements = new ArrayList<>();
        prevOps = new ArrayList<>();
    }

    /**
     * This method returns the collected score.
     *
     * @return the collected score
     */
    public int getScore() {
        return score;
    }

    /**
     * This method returns the number of steps in the game.
     *
     * @return the number of steps in the game
     */
    public int getMoves() {
        return moves;
    }

    /**
     * This method returns true if the game is won.
     *
     * @return true if the game is won
     */
    public boolean isWon() {
        int kings = 0;
        for (Foundation fnd : foundationPiles) {
            if (fnd.isKing()) {
                kings++;
            }
        }
        return kings == 4;
    }
    
    private void updateScore(int score) {
        this.score += score;
        if (this.score < 0) {
            this.score = 0;
        }
        moves++;
    }

    /**
     * This method moves the specified list of {@code Card} objects on the
     * specified {@code Tableau} pile to the specified {@code Tableau} pile.
     *
     * @see hu.unideb.inf.klondike.component.Card
     * @see hu.unideb.inf.klondike.component.Tableau
     * @param fromIndex index of the {@code Tableau} pile to gather in
     * @param toIndex index of the {@code Tableau} pile to put
     * @param cardIndex index of the grabbed card
     * @throws OffenseException if the operation can not be performed due to
     * rule offense
     */
    public void moveFromTableauToTableau(int fromIndex, int toIndex, int cardIndex) throws OffenseException {
        if (fromIndex == toIndex) {
            return;
        }
        tableauPiles[toIndex].add(tableauPiles[fromIndex].get(cardIndex));
        prevOps.add('m');
        prevMovements.add(new Movement(Tableau.class.getTypeName(), Tableau.class.getTypeName(), fromIndex, toIndex, score));
        tableauPiles[fromIndex].apply();
        updateScore(5);
        logger.info("Card(s) moved from tableau to tableau.");
    }

    /**
     * This method moves the specified list of {@code Card} objects on the
     * specified {@code Tableau} pile to the specified {@code Foundation} pile.
     *
     * @see hu.unideb.inf.klondike.component.Card
     * @see hu.unideb.inf.klondike.component.Tableau
     * @see hu.unideb.inf.klondike.component.Foundation
     * @param fromIndex index of the {@code Tableau} pile to gather in
     * @param toIndex index of the {@code Foundation} pile to put
     * @param cardIndex index of the grabbed card
     * @throws OffenseException if the operation can not be performed due to
     * rule offense
     */
    public void moveFromTableauToFoundation(int fromIndex, int toIndex, int cardIndex) throws OffenseException {
        if (Arrays.asList(tableauPiles[fromIndex].get(cardIndex)).size() != 1) {
            throw new OffenseWhileAddingException("Only one card can be added!");
        }
        foundationPiles[toIndex].add(tableauPiles[fromIndex].get(cardIndex).get(0));
        prevOps.add('m');
        prevMovements.add(new Movement(Tableau.class.getTypeName(), Foundation.class.getTypeName(), fromIndex, toIndex, score));
        tableauPiles[fromIndex].apply();
        updateScore(10);
        logger.info("Card moved from tableau to foundation.");
    }

    /**
     * This method moves the top {@code Card} of the specified
     * {@code Foundation} pile to the specified {@code Foundation} pile.
     *
     * @see hu.unideb.inf.klondike.component.Card
     * @see hu.unideb.inf.klondike.component.Foundation
     * @param fromIndex index of the {@code Foundation} pile to gather in
     * @param toIndex index of the {@code Foundation} pile to put
     * @throws OffenseException if the operation can not be performed due to
     * rule offense
     */
    public void moveFromFoundationToFoundation(int fromIndex, int toIndex) throws OffenseException {
        if (fromIndex == toIndex) {
            return;
        }
        foundationPiles[toIndex].add(foundationPiles[fromIndex].get());
        prevOps.add('m');
        prevMovements.add(new Movement(Foundation.class.getTypeName(), Foundation.class.getTypeName(), fromIndex, toIndex, score));
        foundationPiles[fromIndex].apply();
        logger.info("Card moved from foundation to foundation.");
    }

    /**
     * This method moves the top {@code Card} of the specified
     * {@code Foundation} pile to the specified {@code Tableau} pile.
     *
     * @see hu.unideb.inf.klondike.component.Card
     * @see hu.unideb.inf.klondike.component.Foundation
     * @see hu.unideb.inf.klondike.component.Tableau
     * @param fromIndex index of the {@code Foundation} pile to gather in
     * @param toIndex index of the {@code Tableau} pile to put
     * @throws OffenseException if the operation can not be performed due to
     * rule offense
     */
    public void moveFromFoundationToTableau(int fromIndex, int toIndex) throws OffenseException {
        tableauPiles[toIndex].add(Arrays.asList(foundationPiles[fromIndex].get()));
        prevOps.add('m');
        prevMovements.add(new Movement(Foundation.class.getTypeName(), Tableau.class.getTypeName(), fromIndex, toIndex, score));
        foundationPiles[fromIndex].apply();
        updateScore(-15);
        logger.info("Card moved from foundation to tableau.");
    }

    /**
     * This method moves the available {@code Card} of the {@code Talon} to the
     * specified {@code Tableau} pile.
     *
     * @see hu.unideb.inf.klondike.component.Card
     * @see hu.unideb.inf.klondike.component.Talon
     * @see hu.unideb.inf.klondike.component.Tableau
     * @param toIndex index of the {@code Tableau} pile to put
     * @throws OffenseException if the operation can not be performed due to
     * rule offense
     */
    public void moveFromTalonToTableau(int toIndex) throws OffenseException {
        tableauPiles[toIndex].add(Arrays.asList(talon.get()));
        prevOps.add('m');
        prevMovements.add(new Movement(Talon.class.getTypeName(), Tableau.class.getTypeName(), 0, toIndex, score));
        talon.apply();
        updateScore(5);
        logger.info("Card moved from talon to tableau.");
    }

    /**
     * This method moves the available {@code Card} on the {@code Talon} to the
     * specified {@code Foundation} pile.
     *
     * @param toIndex index of the {@code Foundation} pile to put
     * @throws OffenseException if the operation can not be performed due to
     * rule offense
     */
    public void moveFromTalonToFoundation(int toIndex) throws OffenseException {
        foundationPiles[toIndex].add(talon.get());
        prevOps.add('m');
        prevMovements.add(new Movement(Talon.class.getTypeName(), Foundation.class.getTypeName(), 0, toIndex, score));
        talon.apply();
        updateScore(10);
        logger.info("Card moved from talon to foundation.");
    }

    /**
     * This method returns a list of {@code String} representation of
     * {@code Card} objects build up for <i>FOUNDATION_RANK</i> pattern that are
     * stored on the specified {@code Tableau} pile.
     * <br>
     * For example: "HEART_KING", "DIAMOND_ACE".
     *
     * @see hu.unideb.inf.klondike.component.Card.FOUNDATION
     * @see hu.unideb.inf.klondike.component.Card.RANK
     * @see hu.unideb.inf.klondike.component.Tableau
     * @param pileIndex index of the {@code Tableau} pile
     * @return a list of {@code String} representation of {@code Card} objects
     * build up for <i>FOUNDATION_RANK</i> pattern that are stored on the
     * specified {@code Tableau} pile
     */
    public List<String> getTableauCards(int pileIndex) {
        List<String> list = new ArrayList<>();
        if (tableauPiles[pileIndex] != null) {
            for (Card card : tableauPiles[pileIndex].getStoredCards()) {
                if (card.getState() == Card.STATE.DOWNSIDE) {
                    list.add("DOWNSIDE");
                } else {
                    list.add(card.getFoundation() + "_" + card.getRank());
                }
            }
        }
        return list;
    }

    /**
     * This method returns a list of {@code String} representation of
     * {@code Card} objects build up for <i>FOUNDATION_RANK</i> pattern that are
     * stored on the specified {@code Foundation} pile.
     * <br>
     * For example: "HEART_KING", "DIAMOND_ACE".
     *
     * @see hu.unideb.inf.klondike.component.Card.FOUNDATION
     * @see hu.unideb.inf.klondike.component.Card.RANK
     * @see hu.unideb.inf.klondike.component.Foundation
     * @param pileIndex index of the {@code Foundation} pile
     * @return a list of {@code String} representation of {@code Card} objects
     * build up for <i>FOUNDATION_RANK</i> pattern that are stored on the
     * specified {@code Foundation} pile
     */
    public List<String> getFoundationCards(int pileIndex) {
        List<String> list = new ArrayList<>();
        if (foundationPiles[pileIndex] != null) {
            for (Card card : foundationPiles[pileIndex].getStoredCards()) {
                list.add(card.getFoundation() + "_" + card.getRank());
            }
        }
        return list;
    }

    /**
     * This method returns a list of {@code String} representation of
     * {@code Card} objects build up for <i>FOUNDATION_RANK</i> pattern up to
     * max 3 items which items are visible on the {@code Talon}.
     * <br>
     * For example: "HEART_KING", "DIAMOND_ACE".
     *
     * @see hu.unideb.inf.klondike.component.Card.FOUNDATION
     * @see hu.unideb.inf.klondike.component.Card.RANK
     * @see hu.unideb.inf.klondike.component.Talon
     * @return a list of {@code String} representation of {@code Card} objects
     * build up for <i>FOUNDATION_RANK</i> pattern up to max 3 items which items
     * are visible on the {@code Talon}.
     */
    public List<String> getTalonCards() {
        List<String> list = new ArrayList<>();
        if (talon != null) {
            for (Card card : talon.getVisibleCards()) {
                list.add(card.getFoundation() + "_" + card.getRank());
            }
        }
        return list;
    }

    /**
     * This method moves the {@code Talon} to the next state and makes the next
     * triple group of {@code Card} objects visible. If the next state contains
     * less than 3 {@code Card}s, only the remaining {@code Card} objects will
     * be visible. When there is a turning point, no {@code Card} is available.
     *
     * @see hu.unideb.inf.klondike.component.Card
     * @see hu.unideb.inf.klondike.component.Talon
     */
    public void nextTalon() {
        if ((talon != null)) {
            prevOps.add('n');
            talon.next();
            if (!talon.getVisibleCards().isEmpty()) {
                updateScore(0);
            }
        }
    }

    /**
     * This method starts a new game.
     */
    public void newGame() {
        score = 0;
        moves = 0;
        prevMovements = new ArrayList<>();
        prevOps = new ArrayList<>();
        List<Card> cards = new ArrayList<>();
        List<Card> cardsToTableau = new ArrayList<>();
        int cardPointer = 0;
        
        for (Card.FOUNDATION foundation : Card.FOUNDATION.values()) {
            for (Card.RANK rank : Card.RANK.values()) {
                cards.add(new Card(foundation, rank));
            }
        }
        Collections.shuffle(cards);
        
        for (int i = 0; i < tableauPiles.length; i++) {
            for (int j = 0; j <= i; j++) {
                cardsToTableau.add(cards.get(cardPointer++));
            }
            tableauPiles[i] = new Tableau(cardsToTableau);
            cardsToTableau.clear();
        }
        
        for (int i = 0; i < foundationPiles.length; i++) {
            foundationPiles[i] = new Foundation();
        }
        
        talon = new Talon(cards.subList(cardPointer, cards.size() - 1));
        
        logger.info("A new game started.");
    }

    /**
     * This method loads the previously saved state of the game <b>from the
     * user's home directory</b>.
     *
     * @throws IOException if an I/O error occurs
     * @throws FileNotFoundException if the file exists but is a directory
     * rather than a regular file, does not exist but cannot be created, or
     * cannot be opened for any other reason
     */
    public void loadGame() throws IOException, FileNotFoundException {
        Gson gson = new Gson();
        Game controller = gson.fromJson(loadSaveFileContext(), Game.class);
        tableauPiles = controller.getTableauPiles();
        foundationPiles = controller.getFoundationPiles();
        talon = controller.getTalon();
        score = controller.getScore();
        moves = controller.getMoves();
        prevMovements = controller.getPrevMovements();
        prevOps = controller.getPrevOps();
        logger.info("Game loaded succesfully.");
    }

    /**
     * This method saves the actual state of the game <b>to the user's home
     * directory</b>.
     *
     * @throws IOException if an I/O error occurs
     * @throws FileNotFoundException if the file exists but is a directory
     * rather than a regular file, does not exist but cannot be created, or
     * cannot be opened for any other reason
     */
    public void saveGame() throws IOException, FileNotFoundException {
        if (talon == null) {
            logger.warn("To save, start a new game first!");
            return;
        }
        String file_loc = (System.getProperty("user.home") + System.getProperty("file.separator") + "save.k");
        FileOutputStream file_out = new FileOutputStream(file_loc);
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(file_out, "UTF-8"))) {
            Gson gson = new GsonBuilder().create();
            writer.write(encrypt(gson.toJson(this).toCharArray()));
        }
        if (!new File(file_loc).exists()) {
            logger.error("Failed to save game data!");
            throw new FileNotFoundException("file does not exists");
        }
        logger.info("Game saved to user's home directory succesfully.");
    }

    /**
     * This method restores the game to the previous state. If there is no
     * previous state, it will not do anything.
     */
    public void undo() {
        if (!prevOps.isEmpty()) {
            if (prevOps.get(prevOps.size() - 1).equals('n')) {
                talon.undo();
            } else {
                String from = prevMovements.get(prevMovements.size() - 1).getFrom();
                String to = prevMovements.get(prevMovements.size() - 1).getTo();
                int fromIndex = prevMovements.get(prevMovements.size() - 1).getFromIndex();
                int toIndex = prevMovements.get(prevMovements.size() - 1).getToIndex();
                if (from.equals(Tableau.class.getTypeName())) {
                    tableauPiles[fromIndex].undo();
                    if (to.equals(Tableau.class.getTypeName())) {
                        tableauPiles[toIndex].undo();
                    }
                    if (to.equals(Foundation.class.getTypeName())) {
                        foundationPiles[toIndex].undo();
                    }
                }
                if (from.equals(Foundation.class.getTypeName())) {
                    foundationPiles[fromIndex].undo();
                    if (to.equals(Tableau.class.getTypeName())) {
                        tableauPiles[toIndex].undo();
                    }
                    if (to.equals(Foundation.class.getTypeName())) {
                        foundationPiles[toIndex].undo();
                    }
                }
                if (from.equals(Talon.class.getTypeName())) {
                    talon.undo();
                    if (to.equals(Tableau.class.getTypeName())) {
                        tableauPiles[toIndex].undo();
                    }
                    if (to.equals(Foundation.class.getTypeName())) {
                        foundationPiles[toIndex].undo();
                    }
                }
                score = prevMovements.get(prevMovements.size() - 1).getScore();
                prevMovements.remove(prevMovements.size() - 1);
            }
            moves--;
            prevOps.remove(prevOps.size() - 1);
            logger.info("The previous state of the game has been restored successfully.");
        }
        logger.info("The previous state of the game has not been restored: there is nothing to restore.");
    }
    
    private Tableau[] getTableauPiles() {
        return tableauPiles;
    }
    
    private Foundation[] getFoundationPiles() {
        return foundationPiles;
    }
    
    private Talon getTalon() {
        return talon;
    }
    
    private List<Movement> getPrevMovements() {
        return prevMovements;
    }
    
    private List<Character> getPrevOps() {
        return prevOps;
    }
    
    private String loadSaveFileContext() throws IOException {
        String file_loc = System.getProperty("user.home") + System.getProperty("file.separator") + "save.k";
        if (!new File(file_loc).exists()) {
            logger.warn("Failed to load game data!");
            throw new FileNotFoundException("file does not exists");
        }
        
        FileReader file = new FileReader(file_loc);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(file)) {
            int r;
            while ((r = br.read()) != -1) {
                sb.append((char) r);
            }
        }
        return encrypt(sb.toString().toCharArray());
    }
    
    private String encrypt(char[] bytefile) {
        char[] key = {'K', 'L', 'O', 'N', 'D', 'I', 'K', 'E'};
        char state[] = bytefile;
        for (int i = 0; i < state.length; i++) {
            state[i] = (char) (state[i] ^ key[i % key.length]);
        }
        return String.valueOf(state);
    }
    
}
