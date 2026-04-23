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
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/piggydoom/index.fxml"));
        Parent root = loader.load();

        Controller controller = loader.getController();

        stage.setResizable(false);
        scene = new Scene(root, 800, 550);

        scene.setOnKeyPressed(event -> {

            if (controller.gameStarted == false) {
                System.out.println("gameStart");
                controller.gameStarted = true;
                controller.pipeTimeline.play();
                controller.timeline.play();
            } else {

                switch (event.getCode()) {
                    case SPACE:

                        controller.jump();

                        break;
                    case TAB:

                        // for (Controller.Pipe pipe : controller.pipesArray){
                        // System.out.println(pipe);
                        // }
                        // System.out.println(controller.pipesArray.size());
                        controller.showPopup();
                        break;
                    case CONTROL:
                        controller.createNewPipe();
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