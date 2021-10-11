package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.util.stream.IntStream;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
//            ObservableList<String> data = FXCollections.observableArrayList();
//            IntStream.range(0, 1000).mapToObj(Integer::toString).forEach(data::add);
//
//            FilteredList<String> filteredData = new FilteredList<>(data, s -> true);
//
//            TextField filterInput = new TextField();
//            filterInput.textProperty().addListener(obs->{
//                String filter = filterInput.getText();
//                if(filter == null || filter.length() == 0) {
//                    filteredData.setPredicate(s -> true);
//                }
//                else {
//                    filteredData.setPredicate(s -> s.contains(filter));
//                }
//            });
//
//
//            BorderPane content = new BorderPane(new ListView<>(filteredData));
//            content.setBottom(filterInput);
//
//            Scene scene = new Scene(content, 500, 500);
//            stage.setScene(scene);
//            stage.show();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/LoginScene.fxml"));
            Parent root = loader.load();
            Scene loginScene = new Scene(root);

            stage.setTitle("Password Management");
            stage.setScene(loginScene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
