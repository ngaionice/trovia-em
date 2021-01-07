package ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Arrays;

public class View extends Application {

    int viewWidth = 1280;
    int viewHeight = 720;

    int navButtonWidth = 75;
    int viewButtonWidth = 150;

    DesignProperties p = new DesignProperties();
    Presenter pr = new Presenter(p);

    @Override
    public void start(Stage primaryStage) {


        BorderPane root = new BorderPane();

        JFXDecorator decorator = new JFXDecorator(primaryStage, root);
        decorator.setCustomMaximize(true);
        Scene scene = new Scene(decorator, viewWidth, viewHeight);

        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        sceneInitSetUp(root);


        primaryStage.setTitle("Trovia - Database Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    void sceneInitSetUp(BorderPane root) {
        root.setLeft(navInitSetUp(root));

        // set up landing screen
        Text start = new Text("Select an option on the navigation bar to begin.");
        start.setFill(Paint.valueOf("#424242"));
        start.setFont(Font.font("Roboto Light"));

        StackPane startPane = new StackPane();
        startPane.setBackground(new Background(new BackgroundFill(Color.rgb(27,27,27), CornerRadii.EMPTY, Insets.EMPTY)));
        startPane.getChildren().add(start);

        root.setCenter(startPane);

        // set up dynamic resizing
        root.autosize();
    }

    VBox navInitSetUp(BorderPane root) {

        // the overarching VBox
        VBox nav = new VBox(80);
        nav.setBackground(p.backgroundMainSidebar);
        nav.setPadding(new Insets(80, 0,0,0));
//        nav.prefWidthProperty().bind(root.widthProperty().multiply(0.155));


        // sub-VBox 1
        VBox mainNav = new VBox();
        mainNav.setBackground(p.backgroundMainSidebar);

        JFXButton createBtn = new JFXButton("Create");
        JFXButton viewBtn = new JFXButton("View/Modify");
        JFXButton syncBtn = new JFXButton("Synchronize");

        Button[] options = new Button[] {createBtn, viewBtn, syncBtn};

        pr.initSetUp();
        pr.setPropVBox(mainNav, options, "#fafafa");

        // sub-VBox 2
        VBox typeNav = new VBox();

        createBtn.setOnAction(event -> pr.callCreate(root, nav, mainNav));
        viewBtn.setOnAction(event -> pr.callView(root, nav, mainNav));
        syncBtn.setOnAction(event -> pr.callSync(root, nav, mainNav));

        // add the VBoxes to the main VBox
        nav.getChildren().addAll(Arrays.asList(mainNav, typeNav));

        return nav;
    }










}
