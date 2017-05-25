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

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/GameScene.fxml"));

        Scene scene = new Scene(root);
        //scene.getStylesheets().add("/styles/Styles.css");

        stage.setTitle("Klondike Solitaire");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Program entry point
     *
     * @param args command line args
     */
    public static void main(String[] args) {
        System.out.println("Welcome to Klondike Solitaire! Let's play!");
        System.out.println("Switching to window mode...");
        launch(args);
        Platform.exit();
        System.exit(0);
    }

}
