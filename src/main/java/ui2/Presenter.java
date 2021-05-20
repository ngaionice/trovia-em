package ui2;

import com.jfoenix.controls.*;
import com.jfoenix.effects.JFXDepthManager;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import datamodel.parser.Parser;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Arrays;
import java.util.List;

public class Presenter {

    Stage stage;
    Scene scene;
    BorderPane root;
    UIController controller;
    TextArea logger;

    public Presenter(Stage stage, Scene sc, BorderPane root) {
        this.stage = stage;
        this.scene = sc;
        this.root = root;
        this.controller = new UIController();
        this.logger = getLogger();
    }

    public VBox getNavBar() {
        VBox navBox = new VBox();
        Region spacer = new Region();
        JFXButton parseButton = new JFXButton("Parse");
        JFXButton editButton = new JFXButton("Edit");
        JFXButton syncButton = new JFXButton("Sync");
        JFXButton reviewButton = new JFXButton("Review");
        JFXButton loadButton = new JFXButton("Load");
        JFXButton createButton = new JFXButton("Create");
        JFXButton logsButton = new JFXButton("Logs");
        JFXButton quitButton = new JFXButton("Quit");
        Separator separator = new Separator();
        Separator separator2 = new Separator();

        List<Button> actionButtons = Arrays.asList(parseButton, editButton, syncButton, reviewButton);
        List<Button> databaseButtons = Arrays.asList(loadButton, createButton);
        List<JFXButton> buttonList = Arrays.asList(parseButton, editButton, syncButton, reviewButton, loadButton, createButton, logsButton, quitButton);
        String[] buttonIds = new String[]{"button-parse", "button-edit", "button-sync", "button-review", "button-load", "button-create", "button-logs", "button-quit"};

        spacer.prefHeightProperty().bind(scene.heightProperty().multiply(0.125));
        for (int i = 0; i < buttonIds.length; i++) {
            buttonList.get(i).getStyleClass().add("nav-button");
            buttonList.get(i).setGraphic(new FontIcon());
            buttonList.get(i).setId(buttonIds[i]);
        }
        navBox.getChildren().addAll(buttonList);
        navBox.getChildren().add(4, separator);
        navBox.getChildren().add(7, separator2);
        navBox.getChildren().add(0, spacer);
        navBox.setId("nav-box");
        separator.getStyleClass().add("nav-separator");
        separator2.getStyleClass().add("nav-separator");

        // set button actions
        parseButton.setOnAction(e -> setParseScreen());
        editButton.setOnAction(e -> setEditScreen());
        syncButton.setOnAction(e -> setSyncScreen());
        reviewButton.setOnAction(e -> setReviewScreen());

        loadButton.setOnAction(e -> {
            boolean enable = controller.loadDatabase(stage, logger);
            if (enable) {
                controller.enableActionButtons(actionButtons);
                controller.disableActionButtons(databaseButtons);
            }
        });
        createButton.setOnAction(e -> {
            boolean enable = controller.createDatabase(stage, logger);
            if (enable) {
                controller.enableActionButtons(actionButtons);
                controller.disableActionButtons(databaseButtons);
            }
        });

//        logsButton.setOnAction(e -> controller.dumpLogs(stage, logger));
        logsButton.setOnAction(e -> setLogsScreen());
        quitButton.setOnAction(e -> runQuitSequence());

        controller.disableActionButtons(actionButtons);

        return navBox;
    }

    private void setParseScreen() {
        BorderPane screenRoot = new BorderPane();
        HBox header = new HBox();
        Text headerText = new Text("Parse");

        header.getChildren().add(headerText);

        JFXTabPane tabs = new JFXTabPane();
        Tab benches = getParseTab("Benches", Parser.ObjectType.BENCH);
        Tab collections = getParseTab("Collections", Parser.ObjectType.COLLECTION);
        Tab items = getParseTab("Items", Parser.ObjectType.ITEM);
        Tab placeables = getParseTab("Placeables", Parser.ObjectType.PLACEABLE);
        Tab professions = getParseTab("Professions", Parser.ObjectType.PROFESSION);
        Tab recipes = getParseTab("Recipes", Parser.ObjectType.RECIPE);
        Tab strings = getParseTab("Strings", Parser.ObjectType.STRING);
        tabs.getTabs().addAll(benches, collections, items, placeables, professions, recipes, strings);

        header.getStyleClass().add("header");
        headerText.getStyleClass().add("header-text");

        screenRoot.setTop(header);
        screenRoot.setCenter(tabs);
        screenRoot.setBottom(getLogsRegion());
        root.setCenter(screenRoot);
    }

    private Tab getParseTab(String name, Parser.ObjectType type) {
        Tab tab = new Tab(name);

        StackPane root = new StackPane();
        AnchorPane anchor = new AnchorPane();
        GridPane grid = new GridPane();
        JFXTextField directory = new JFXTextField();
        JFXButton dirButton = new JFXButton();
        JFXTextField filter = new JFXTextField();
        JFXButton filterButton = new JFXButton();
        TreeView<String> tree = new TreeView<>();
        JFXButton startButton = new JFXButton();
        JFXProgressBar progressBar = new JFXProgressBar();
        Text progressText = new Text("Status");
        VBox progressBox = new VBox();

        directory.setPromptText("Directory");
        directory.setDisable(true);
        filter.setPromptText("Filter");
        filter.setText(controller.getFilterText(type));
        progressBar.setProgress(0);

        dirButton.setOnAction(e -> controller.setParseDirectory(stage, directory, tree, type));
        filterButton.setOnAction(e -> controller.updateParseDirectory(filter, directory, tree, type));
        startButton.setOnAction(e -> controller.parse(progressBar, progressText, logger, type));

        root.getStyleClass().add("pane-background");
        anchor.getStyleClass().add("card-backing");
        grid.getStyleClass().add("grid-content");
        directory.getStyleClass().add("text-field-dir");
        filter.getStyleClass().add("text-field-filter");
        tree.getStyleClass().add("dir-view");
        dirButton.getStyleClass().addAll("button-inline", "color-subtle");
        filterButton.getStyleClass().addAll("button-inline", "color-subtle");
        startButton.getStyleClass().addAll("floating-button", "button-start");
        dirButton.setId("button-set-dir");
        filterButton.setId("button-update");
        progressText.getStyleClass().add("text-normal");
        progressText.setId("text-progress");

        dirButton.setRipplerFill(Color.valueOf("#FAFAFA"));
        filterButton.setRipplerFill(Color.valueOf("#FAFAFA"));
        startButton.setRipplerFill(Color.valueOf("#FAFAFA"));

        dirButton.setGraphic(new FontIcon());
        filterButton.setGraphic(new FontIcon());
        startButton.setGraphic(new FontIcon());
        tree.setCellFactory(CheckBoxTreeCell.forTreeView());
        tree.prefWidthProperty().bind(root.widthProperty().multiply(0.80));
        tree.prefHeightProperty().bind(root.heightProperty().multiply(0.68));
        progressBar.prefWidthProperty().bind(root.widthProperty().multiply(0.80));

        tab.setContent(root);
        root.getChildren().add(anchor);
        anchor.getChildren().add(grid);
        anchor.getChildren().add(startButton);
        progressBox.getChildren().addAll(progressText, progressBar);
        grid.add(directory, 0, 0);
        grid.add(dirButton, 1, 0);
        grid.add(filter, 2, 0);
        grid.add(filterButton, 3, 0);
        grid.add(progressBox, 0, 1, 4, 1);
        grid.add(tree, 0, 2, 4, 1);

        setMaxAnchor(grid);
        setFabAnchor(startButton);

        JFXDepthManager.setDepth(anchor, 1);
        return tab;
    }

    private void setEditScreen() {
        BorderPane screenRoot = new BorderPane();
        HBox header = new HBox();
        Text headerText = new Text("Edit");

        header.getChildren().add(headerText);

        JFXTabPane tabs = new JFXTabPane();
        Tab benches = new Tab("Benches");
        Tab collections = new Tab("Collections");
        Tab items = new Tab("Items");
        Tab placeables = new Tab("Placeables");
        Tab recipes = new Tab("Recipes");
        Tab strings = new Tab("Strings");
        tabs.getTabs().addAll(benches, collections, items, placeables, recipes, strings);

        header.getStyleClass().add("header");
        headerText.getStyleClass().add("header-text");

        screenRoot.setTop(header);
        screenRoot.setCenter(tabs);
        root.setCenter(screenRoot);
    }

    private void setSyncScreen() {

    }

    private void setReviewScreen() {

    }

    private void setLogsScreen() {
        BorderPane screenRoot = new BorderPane();
        StackPane stack = new StackPane();
        AnchorPane anchor = new AnchorPane();
        HBox header = new HBox();
        Text headerText = new Text("Logs");
        JFXButton dumpButton = new JFXButton();

        dumpButton.setOnAction(e -> controller.dumpLogs(stage, logger));

        header.getChildren().add(headerText);
        stack.getChildren().add(anchor);
        anchor.getChildren().add(logger);
        anchor.getChildren().add(dumpButton);

        header.getStyleClass().add("header");
        headerText.getStyleClass().add("header-text");
        stack.getStyleClass().add("pane-background");
        anchor.getStyleClass().add("card-backing");
        dumpButton.getStyleClass().add("floating-button");
        dumpButton.setId("button-dump");

        dumpButton.setGraphic(new FontIcon());
        dumpButton.setRipplerFill(Color.valueOf("#FAFAFA"));

        setMaxAnchor(logger);
        setFabAnchor(dumpButton);

        JFXDepthManager.setDepth(anchor, 1);

        screenRoot.setTop(header);
        screenRoot.setCenter(stack);
        root.setCenter(screenRoot);
    }

    private StackPane getLogsRegion() {
        StackPane root = new StackPane();
        StackPane loggerWrapper = new StackPane();
        root.getStyleClass().add("pane-background-no-top");
        root.getChildren().add(loggerWrapper);
        loggerWrapper.getChildren().add(logger);
        JFXDepthManager.setDepth(loggerWrapper, 1);

        return root;
    }

    private TextArea getLogger() {
        TextArea logger = new JFXTextArea();
        logger.setEditable(false);
        logger.setWrapText(true);
        logger.setId("logger");
        return logger;
    }

    private void runQuitSequence() {
        // need to add more stuff such as checking data is saved, etc
        Platform.exit();
    }

    // HELPER METHODS

    private void setMaxAnchor(Node node) {
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
    }

    private void setFabAnchor(Node node) {
        AnchorPane.setRightAnchor(node, 36.0);
        AnchorPane.setBottomAnchor(node, 36.0);
    }
}
