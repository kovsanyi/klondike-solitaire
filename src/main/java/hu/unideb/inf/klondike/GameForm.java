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
package hu.unideb.inf.klondike;

import hu.unideb.inf.klondike.model.Game;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author sanyi
 */
public class GameForm implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(GameForm.class);

    Game controller = new Game();

    private double mouseX;
    private double mouseY;
    private boolean isDragged;
    private ImageView dragged[];

    @FXML
    AnchorPane mainPane;

    @FXML
    ImageView deck;

    @FXML
    Pane reserveDeck, tableauPile1, tableauPile2, tableauPile3, tableauPile4,
            tableauPile5, tableauPile6, tableauPile7, foundations, tableauPiles[];

    @FXML
    Label score, moves, lblYouWon, lblCongratulations;

    public Pane getContainer(ImageView card) {
        switch (card.getId().charAt(0)) {
            case ('t'):
                return reserveDeck;
            case ('f'):
                return foundations;
            default:
                return tableauPiles[Integer.parseInt(card.getId().substring(0, 1))];
        }
    }

    int getFoundationIndex(ImageView card) {
        return Integer.parseInt(card.getId().substring(1, 2));
    }

    int getFoundationIndex(MouseEvent mouseEvent) {
        if (mouseEvent.getSceneX() < 450) {
            return 0;
        } else if (mouseEvent.getSceneX() < 560) {
            return 1;
        } else if (mouseEvent.getSceneX() < 670) {
            return 2;
        } else {
            return 3;
        }
    }

    int getTableauIndex(MouseEvent mouseEvent) {
        if (mouseEvent.getSceneX() < 120) {
            return 0;
        } else if (mouseEvent.getSceneX() < 230) {
            return 1;
        } else if (mouseEvent.getSceneX() < 340) {
            return 2;
        } else if (mouseEvent.getSceneX() < 450) {
            return 3;
        } else if (mouseEvent.getSceneX() < 560) {
            return 4;
        } else if (mouseEvent.getSceneX() < 670) {
            return 5;
        } else {
            return 6;
        }
    }

    public void updateLabels() {
        score.setText("Score: " + String.valueOf(controller.getScore()));
        moves.setText("Moves: " + String.valueOf(controller.getMoves()));
    }

    public void releaseCard(ImageView card, MouseEvent mouseEvent) {
        try {
            switch (card.getId().charAt(0)) {
                case 't':
                    if (mouseEvent.getSceneY() < 185) {
                        if (mouseEvent.getSceneX() < 340) {
                            LoadTalonContext();
                        } else {
                            controller.moveFromTalonToFoundation(getFoundationIndex(mouseEvent));
                            LoadTalonContext();
                            LoadFoundationsContext();
                        }
                    } else {
                        controller.moveFromTalonToTableau(getTableauIndex(mouseEvent));
                        LoadTalonContext();
                        LoadTableauPileContext(getTableauIndex(mouseEvent));
                    }
                    break;
                case 'f':
                    if (mouseEvent.getSceneY() < 185) {
                        if (mouseEvent.getSceneX() < 340) {
                            LoadFoundationsContext();
                        } else {
                            controller.moveFromFoundationToFoundation(getFoundationIndex(card), getFoundationIndex(mouseEvent));
                            LoadFoundationsContext();
                        }
                    } else {
                        controller.moveFromFoundationToTableau(getFoundationIndex(card), getTableauIndex(mouseEvent));
                        LoadFoundationsContext();
                        LoadTableauPileContext(getTableauIndex(mouseEvent));
                    }
                    break;
                default:
                    int fromIndex = Integer.parseInt(card.getId().substring(0, 1));
                    int cardIndex = Integer.parseInt(card.getId().substring(1, 3));
                    if (mouseEvent.getSceneY() < 185) {
                        if (mouseEvent.getSceneX() < 340) {
                            LoadTableauPileContext(fromIndex);
                        } else {
                            controller.moveFromTableauToFoundation(fromIndex, getFoundationIndex(mouseEvent), cardIndex);
                            LoadTableauPileContext(fromIndex);
                            LoadFoundationsContext();
                        }
                    } else {
                        controller.moveFromTableauToTableau(fromIndex, getTableauIndex(mouseEvent), cardIndex);
                        LoadTableauPileContext(fromIndex);
                        LoadTableauPileContext(getTableauIndex(mouseEvent));
                    }
                    break;
            }
            updateLabels();
            if (controller.isWon()) {
                lblYouWon.setDisable(false);
                lblYouWon.setVisible(true);
                lblCongratulations.setDisable(false);
                lblCongratulations.setVisible(true);
            }
        } catch (OffenseException ex) {
            LoadTalonContext();
            LoadFoundationsContext();
            for (int i = 0; i < 7; i++) {
                LoadTableauPileContext(i);
            }
            logger.warn("All container reloaded due to OffenseException!");
        }
    }

    public void setUpMoving(ImageView card, MouseEvent mouseEvent) {
        int pileIndex = Integer.parseInt(card.getId().substring(0, 1));
        int cardIndex = Integer.parseInt(card.getId().substring(1, 3));
        int sum = controller.getTableauCards(pileIndex).size() - cardIndex - 1;
        if (sum == 0) {
            isDragged = false;
            return;
        } else {
            isDragged = true;
        }
        dragged = new ImageView[sum];
        for (int i = 0; i < sum; i++) {
            dragged[i] = (ImageView) getContainer(card)
                    .lookup("#" + String.valueOf(pileIndex) + ((cardIndex + i + 1 < 10) ? "0" + String.valueOf(cardIndex + i + 1) : String.valueOf(cardIndex + i + 1)));
            getContainer(dragged[i]).getChildren().remove(dragged[i]);
            mainPane.getChildren().add(dragged[i]);
            dragged[i].relocate(card.getLayoutX(), card.getLayoutY() + (i + 1) * 30);
        }
    }

    public void moving(ImageView card, MouseEvent mouseEvent) {
        if (isDragged) {
            for (int i = 0; i < dragged.length; i++) {
                dragged[i].relocate(card.getLayoutX(), card.getLayoutY() + (i + 1) * 30);
            }
        }
    }

    public void terminateMoving(MouseEvent mouseEvent) {
        if (isDragged) {
            for (int i = 0; i < dragged.length; i++) {
                mainPane.getChildren().remove(dragged[i]);
            }
        }
    }

    public void LoadTalonContext() {
        reserveDeck.getChildren().clear();
        int cardIndex = 0;
        for (String cardValue : controller.getTalonCards()) {
            ImageView card = new ImageView(new Image(this.getClass().getClassLoader().getResourceAsStream("cards/" + cardValue.toLowerCase() + ".png")));
            card.relocate(cardIndex * 25, 0);
            if (cardIndex == controller.getTalonCards().size() - 1) {
                card.setId("t");
                card.setCursor(Cursor.OPEN_HAND);
                card.setOnMousePressed(mouseEvent -> {
                    mouseX = mouseEvent.getSceneX() - reserveDeck.getLayoutX() - card.getLayoutX();
                    mouseY = mouseEvent.getSceneY() - reserveDeck.getLayoutY();
                    reserveDeck.getChildren().remove(card);
                    mainPane.getChildren().add(card);
                    card.relocate(mouseEvent.getSceneX() - mouseX, mouseEvent.getSceneY() - mouseY);
                    card.setCursor(Cursor.CLOSED_HAND);
                });
                card.setOnMouseDragged(mouseEvent -> {
                    card.relocate(mouseEvent.getSceneX() - mouseX, mouseEvent.getSceneY() - mouseY);
                });
                card.setOnMouseReleased(mouseEvent -> {
                    releaseCard(card, mouseEvent);
                    mainPane.getChildren().remove(card);
                });
            };
            reserveDeck.getChildren().add(card);
            cardIndex++;
        }
    }

    public void LoadFoundationsContext() {
        foundations.getChildren().clear();
        for (int i = 0; i < 4; i++) {
            List<String> cards = controller.getFoundationCards(i);
            if (cards.size() > 1) {
                ImageView card = new ImageView(new Image(this.getClass().getClassLoader().getResourceAsStream("cards/" + cards.get(cards.size() - 2).toLowerCase() + ".png")));
                card.relocate(i * 110, 0);
                foundations.getChildren().add(card);
            }
            if (!cards.isEmpty()) {
                ImageView card = new ImageView(new Image(this.getClass().getClassLoader().getResourceAsStream("cards/" + cards.get(cards.size() - 1).toLowerCase() + ".png")));
                card.setId("f" + i);
                card.relocate(i * 110, 0);
                card.setCursor(Cursor.OPEN_HAND);
                card.setOnMousePressed(mouseEvent -> {
                    mouseX = mouseEvent.getSceneX() - foundations.getLayoutX() - card.getLayoutX();
                    mouseY = mouseEvent.getSceneY() - foundations.getLayoutY();
                    foundations.getChildren().remove(card);
                    mainPane.getChildren().add(card);
                    card.relocate(mouseEvent.getSceneX() - mouseX, mouseEvent.getSceneY() - mouseY);
                    card.setCursor(Cursor.CLOSED_HAND);
                });
                card.setOnMouseDragged(mouseEvent -> {
                    card.relocate(mouseEvent.getSceneX() - mouseX, mouseEvent.getSceneY() - mouseY);
                });
                card.setOnMouseReleased(mouseEvent -> {
                    releaseCard(card, mouseEvent);
                    mainPane.getChildren().remove(card);
                });
                foundations.getChildren().add(card);
            }
        }
    }

    public void LoadTableauPileContext(int pileIndex) {
        tableauPiles[pileIndex].getChildren().clear();
        String prevValue = "";
        int locY = 0;
        int cardIndex = 0;
        for (String cardValue : controller.getTableauCards(pileIndex)) {
            ImageView card = new ImageView(new Image(this.getClass().getClassLoader().getResourceAsStream("cards/" + cardValue.toLowerCase() + ".png")));
            if (prevValue.equals("downside")) {
                card.relocate(0, locY += 15);
            } else {
                if (cardIndex == 0) {
                    card.relocate(0, 0);
                } else {
                    card.relocate(0, locY += 30);
                }
            }
            if (!cardValue.toLowerCase().equals("downside")) {
                card.setId(String.valueOf(pileIndex) + (cardIndex < 10 ? "0" + String.valueOf(cardIndex) : String.valueOf(cardIndex)));
                card.setCursor(Cursor.OPEN_HAND);
                card.setOnMousePressed(mouseEvent -> {
                    mouseX = mouseEvent.getSceneX() - getContainer(card).getLayoutX();
                    mouseY = mouseEvent.getSceneY() - getContainer(card).getLayoutY() - card.getLayoutY();
                    getContainer(card).getChildren().remove(card);
                    mainPane.getChildren().add(card);
                    card.relocate(mouseEvent.getSceneX() - mouseX, mouseEvent.getSceneY() - mouseY);
                    card.setCursor(Cursor.CLOSED_HAND);
                    setUpMoving(card, mouseEvent);
                });
                card.setOnMouseDragged(mouseEvent -> {
                    card.relocate(mouseEvent.getSceneX() - mouseX, mouseEvent.getSceneY() - mouseY);
                    moving(card, mouseEvent);
                });
                card.setOnMouseReleased(mouseEvent -> {
                    releaseCard(card, mouseEvent);
                    mainPane.getChildren().remove(card);
                    terminateMoving(mouseEvent);
                });
            }
            tableauPiles[pileIndex].getChildren().add(card);
            cardIndex++;
            prevValue = cardValue.toLowerCase();
        }
    }

    @FXML
    public void stockOnMousePressed() {
        controller.nextTalon();
        LoadTalonContext();
        updateLabels();
    }

    @FXML
    public void newGame(ActionEvent event) {
        controller.newGame();
        reserveDeck.getChildren().clear();
        foundations.getChildren().clear();
        for (int i = 0; i < 7; i++) {
            LoadTableauPileContext(i);
        }
        updateLabels();
        lblYouWon.setDisable(true);
        lblYouWon.setVisible(false);
        lblCongratulations.setDisable(true);
        lblCongratulations.setVisible(false);
    }

    @FXML
    public void loadGame(ActionEvent event) {
        try {
            controller.loadGame();
            LoadTalonContext();
            LoadFoundationsContext();
            for (int i = 0; i < 7; i++) {
                LoadTableauPileContext(i);
            }
            updateLabels();
            if (controller.isWon()) {
                lblYouWon.setDisable(false);
                lblYouWon.setVisible(true);
                lblCongratulations.setDisable(false);
                lblCongratulations.setVisible(true);
            } else {
                lblYouWon.setDisable(true);
                lblYouWon.setVisible(false);
                lblCongratulations.setDisable(true);
                lblCongratulations.setVisible(false);
            }
        } catch (IOException ex) {
            logger.warn("Failed to load game due to {}: {}", ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    @FXML
    public void undo(ActionEvent event) throws Exception {
        controller.undo();
        LoadTalonContext();
        LoadFoundationsContext();
        for (int i = 0; i < 7; i++) {
            LoadTableauPileContext(i);
        }
        updateLabels();
    }

    @FXML
    public void exit(ActionEvent event) throws Exception {
        Platform.exit();
    }

    @FXML
    public void saveGame(ActionEvent event) throws Exception {
        controller.saveGame();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableauPiles = new Pane[]{tableauPile1, tableauPile2, tableauPile3, tableauPile4, tableauPile5, tableauPile6, tableauPile7};
        deck.setImage(new Image(this.getClass().getClassLoader().getResourceAsStream("cards/downside.png")));
        deck.setCursor(Cursor.HAND);
        mainPane.setStyle("-fx-background-color: #008000");
        score.setStyle("-fx-font-weight: bold; -fx-text-fill: #FFFFFF");
        moves.setStyle("-fx-font-weight: bold; -fx-text-fill: #FFFFFF");
    }

}
