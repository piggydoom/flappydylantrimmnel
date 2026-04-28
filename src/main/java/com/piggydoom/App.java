package com.piggydoom;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        Font.loadFont(getClass().getResource("/com/piggydoom/assets/flappy-bird-font.otf").toExternalForm(), 10);
        Font.loadFont(getClass().getResource("/com/piggydoom/assets/flappy-font.ttf").toExternalForm(), 10);
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/piggydoom/index.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();

        stage.setResizable(false);
        scene = new Scene(root, 800, 550);

        scene.setOnKeyPressed(event -> {

            if (controller.gameStarted == false && !controller.cooldown) {
                controller.score = -1;

                if (controller.sessionBegan) {
                    controller.sketchBackground();
                }

                controller.gameStarted = true;
                controller.gameOverCard.setVisible(false);
                controller.pipeTimeline.play();
                controller.timeline.play();
                controller.jump();
                controller.gameOverFlag = false;

                if (!controller.sessionBegan) {
                    controller.sessionBegan = true;
                }

            } else {

                switch (event.getCode()) {
                    case SPACE:

                        if (!controller.cooldown) {
                            controller.jump();
                        }
                        break;
                    case TAB:

                        // for (Controller.Pipe pipe : controller.pipesArray){
                        // System.out.println(pipe);
                        // }
                        // System.out.println(controller.pipesArray.size());
                        // controller.showPopup();
                        controller.gameOver();
                        break;
                    case CONTROL:
                        // controller.createNewPipe(controller.canvasP.getWidth());
                        // controller.moveBackground();
                        break;
                }
            }
        });

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();

    }

}