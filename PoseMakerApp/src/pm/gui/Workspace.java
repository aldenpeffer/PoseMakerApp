package pm.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import pm.PoseMaker;
import pm.controller.PoseMakerState;
import pm.controller.WorkspaceEditController;
import pm.data.DataManager;
import pm.file.FileManager;
import pm.shapes.PoseMakerRectangle;
import pm.shapes.PoseMakerEllipse;
import saf.ui.AppGUI;
import saf.AppTemplate;
import saf.components.AppWorkspaceComponent;

/**
 * This class serves as the workspace component for this application, providing
 * the user interface controls for editing work.
 *
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class Workspace extends AppWorkspaceComponent {

    // HERE'S THE APP
    AppTemplate app;

    // IT KNOWS THE GUI IT IS PLACED INSIDE
    AppGUI gui;
    Scene mainScene;
    
    WorkspaceEditController workspaceEditController;
    
    SplitPane splitWorkSpace;
    BorderPane containerPane;
    VBox optionPane;
    Pane canvasPane;
    
    DataManager dataManager;
    Color backgroundColor;
    Color outlineColor;
    Color fillColor;
    private int index;
    
    private Shape selectedShape;
    
    //VBox Arraylist for all sections of left pane
    ArrayList<VBox> vboxList;
    ArrayList<HBox> hboxList;
    
    //All images used for GUI
    ArrayList<Image> shapeEditImages;
    ArrayList<Image> alterShapeIndexImages;
    Image snapshotImage;
    
    //All in left pane that interact with workspace
    ArrayList<Button> shapeEditButtons;
    ArrayList<Button> alterShapeIndexButtons;
    Button snapshotButton;
    private Button removeButton;
    private Button moveUpButton;
    private Button moveDownButton;
    
    final Color[] STARTING_COLORS = {Color.web("#fbd784"), Color.web("#ff6666"), Color.web("#99cc99")};
    ColorPicker backgroundColorPicker, fillColorPicker, outlineColorPicker;
    
    //Constants for button dimensions
    final int SHAPE_EDIT_WIDTH = 45;  
    final int ALTER_SHAPE_INDEX_WIDTH = 80;
    final int SNAPSHOT_WIDTH = 210;
    
    final int HEIGHT_OFFSET = 58;
    final int WIDTH_OFFSET = 243;
    
    Slider outlineSlider;
    
    int outlineWidth;
    boolean moveable;
    double xdist, ydist;
    
    
    /**
     * Constructor for initializing the workspace, note that this constructor
     * will fully setup the workspace user interface for use.
     *
     * @param initApp The application this workspace is part of.
     *
     * @throws IOException Thrown should there be an error loading application
     * data for setting up the user interface.
     */
    public Workspace(AppTemplate initApp) throws IOException {
        outlineWidth = 10;
        hboxList = new ArrayList();     
        shapeEditImages = new ArrayList();
        alterShapeIndexImages = new ArrayList();      
        shapeEditButtons = new ArrayList();
        alterShapeIndexButtons = new ArrayList();
        boolean moveable = false;

        // KEEP THIS FOR LATER
	app = initApp;

	// KEEP THE GUI FOR LATER
	gui = app.getGUI();
        mainScene = gui.getPrimaryScene();
        workspaceEditController = new WorkspaceEditController((PoseMaker) app);
        
        workspace = new BorderPane();       
        FileManager fileManager = (FileManager) app.getFileComponent();
        fileManager.setApp(app);
	dataManager = (DataManager) app.getDataComponent();          
        
        initStyle();        
        initializeButtonImages();
        initializeButtons();    
               
        backgroundColor = Color.SEASHELL;
        index = 0;
        optionPane = new VBox();
        for(HBox h : hboxList){
            h.getStyleClass().add("hbox");
            optionPane.getChildren().add(h);     
        }
        
        optionPane.getStyleClass().add("option_style");

        canvasPane = new Pane();
        
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();     
        canvasPane.setPrefSize(bounds.getWidth(), bounds.getHeight());         
        canvasPane.getStyleClass().add("max_pane");
        canvasPane.setStyle("-fx-background-color:#fbd784;");
        
        splitWorkSpace = new SplitPane();
        splitWorkSpace.getItems().add(optionPane);
        splitWorkSpace.getItems().add(canvasPane);
        splitWorkSpace.setDividerPosition(0, 0.2f);
        optionPane.maxWidthProperty().bind(splitWorkSpace.widthProperty().multiply(0.2));
        /*containerPane = new BorderPane();
        containerPane.setLeft(optionPane);
        containerPane.setCenter(canvasPane);       
        */
        
        workspace = new Pane();
        //workspace.getChildren().add(containerPane);
        workspace.getChildren().add(splitWorkSpace);
        
        initClickListener();
        initDragListener();
        initPressedListener();
        initReleasedListener();
        
        removeButton.setDisable(true);
        moveUpButton.setDisable(true);
        moveDownButton.setDisable(true);
        
        workspaceActivated = false;  
    }
    
    /**
     * This function specifies the CSS style classes for all the UI components
     * known at the time the workspace is initially constructed. Note that the
     * tag editor controls are added and removed dynamicaly as the application
     * runs so they will have their style setup separately.
     */
    @Override
    public void initStyle() {
    }

    /**
     * This function reloads all the controls for editing tag attributes into
     * the workspace.
     */
    @Override
    public void reloadWorkspace() {
    }   

    //Initialized Button Images
    private void initializeButtonImages() {
        //Shape edit images
        shapeEditImages.add(new Image("file:./images/SelectionTool.png"));
        shapeEditImages.add(new Image("file:./images/Remove.png"));
        shapeEditImages.add(new Image("file:./images/Rect.png"));
        shapeEditImages.add(new Image("file:./images/Ellipse.png"));
        //Images for changing the index of a shape
        alterShapeIndexImages.add(new Image("file:./images/MoveToFront.png"));
        alterShapeIndexImages.add(new Image("file:./images/MoveToBack.png"));
        //Image for snapshot button
        snapshotImage = new Image("file:./images/Snapshot.png");     
    }
    
    private void initializeButtons(){
        //ROW 1: SHAPE EDIT BUTTONS
        HBox hbox = new HBox(10);
        for(int i = 0; i < shapeEditImages.size(); i++){
            Button button = new Button();
            button.setMaxWidth(SHAPE_EDIT_WIDTH);
	    button.setMinWidth(SHAPE_EDIT_WIDTH);          
	    button.setPrefWidth(SHAPE_EDIT_WIDTH);
            button.setGraphic(new ImageView(shapeEditImages.get(i)));
            
            if(i == 0){
                button.setOnAction(e -> {                   
                    workspaceEditController.handleSelectionRequest();
                });
            }else if(i == 1){
                removeButton = button;
                button.setOnAction(e -> {                   
                    workspaceEditController.handleRemoveRequest();
                });
            }else if(i == 2){
                button.setOnAction(e -> {                   
                    workspaceEditController.handleRectRequest();
                });
            }else if(i == 3){
                button.setOnAction(e -> {                   
                    workspaceEditController.handleEllipseRequest();
                });
            }
            hbox.getChildren().add(button);          
        }
        hboxList.add(hbox);        
      
        //ROW 2: ALTER SHAPE INDEX BUTTONS
        hbox = new HBox(10);
        for(int i = 0; i < alterShapeIndexImages.size(); i++){
            Button button = new Button();
            button.setMaxWidth(ALTER_SHAPE_INDEX_WIDTH);
	    button.setMinWidth(ALTER_SHAPE_INDEX_WIDTH);          
	    button.setPrefWidth(ALTER_SHAPE_INDEX_WIDTH);
            button.setGraphic(new ImageView(alterShapeIndexImages.get(i)));         
            if(i == 0){
                moveUpButton = button;
                button.setOnAction(e -> {                   
                    workspaceEditController.handleMoveRequest(1);
                });
            }else if(i == 1){
                moveDownButton = button;
                button.setOnAction(e -> {                   
                    workspaceEditController.handleMoveRequest(-1);
                });
            }
            hbox.getChildren().add(button); 
        }
        hboxList.add(hbox);
         
        //ROW 3 - 5: COLOR PICKERS
        String[] colorOptionText = {"Background Color", "Fill Color", "Outline Color"};
        backgroundColorPicker = new ColorPicker(STARTING_COLORS[0]);
        fillColorPicker = new ColorPicker(STARTING_COLORS[1]);
        outlineColorPicker = new ColorPicker(STARTING_COLORS[2]);
        for(int i = 0; i < 3; i++){
            HBox tempHBox = new HBox(10);
            VBox subVBox1 = new VBox();
            VBox subVBox2 = new VBox();

            Text currentColorText = new Text(colorOptionText[i]);
            currentColorText.getStyleClass().add("subheading_label");       
            subVBox1.getChildren().add(currentColorText);         
            
            int index = i;
            
            //Only doing this clunky code
            //because nothing else worked after many attempts...
            if(i == 0){
                backgroundColorPicker.setOnAction((ActionEvent e) -> {
                workspaceEditController.handleChangeColorRequest(backgroundColorPicker.valueProperty().get(), index);
                });
                subVBox2.getChildren().add(backgroundColorPicker);
            }else if(i == 1){
                fillColorPicker.setOnAction((ActionEvent e) -> {
                workspaceEditController.handleChangeColorRequest(fillColorPicker.valueProperty().get(), index);
                });
                subVBox2.getChildren().add(fillColorPicker);
            }else if(i == 2){
                outlineColorPicker.setOnAction((ActionEvent e) -> {
                workspaceEditController.handleChangeColorRequest(outlineColorPicker.valueProperty().get(), index);
                });
                subVBox2.getChildren().add(outlineColorPicker);
            }
            
            subVBox1.getStyleClass().add("vbox");
            subVBox2.getStyleClass().add("vbox");          
            subVBox1.getChildren().add(subVBox2);
            tempHBox.getChildren().addAll(subVBox1);
            hboxList.add(tempHBox);
        }
        
        //ROW 6: OUTLINE THICKNESS
        hbox = new HBox(10);
        VBox subVBox1 = new VBox();
        VBox subVBox2 = new VBox();
        
        Text outlineText = new Text("Outline Thickness");
        outlineText.getStyleClass().add("subheading_label");
        subVBox1.getChildren().add(outlineText);
        
        outlineSlider = new Slider(0, 20, 10);
        outlineSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                outlineWidth = new_val.intValue();
                if(getSelectedShape() != null){
                    if(getSelectedShape() instanceof PoseMakerRectangle){
                        PoseMakerRectangle rect = (PoseMakerRectangle) getSelectedShape();
                        rect.setOutlineWidth(outlineWidth);
                        rect.setStrokeWidth(outlineWidth);
                    }else if(getSelectedShape() instanceof PoseMakerEllipse){
                        PoseMakerEllipse ellipse = (PoseMakerEllipse) getSelectedShape();
                        ellipse.setOutlineWidth(outlineWidth);
                        ellipse.setStrokeWidth(outlineWidth);
                    }
            }
            }
        });
        subVBox2.getChildren().add(outlineSlider);
        
        subVBox1.getStyleClass().add("vbox");
        subVBox2.getStyleClass().add("vbox");          
        subVBox1.getChildren().add(subVBox2);
        hbox.getChildren().addAll(subVBox1);
        hboxList.add(hbox);
        
        //ROW 7: SNAPSHOT
        hbox = new HBox(10);
        snapshotButton = new Button();
        snapshotButton.setMinWidth(SNAPSHOT_WIDTH);
        snapshotButton.setMaxWidth(SNAPSHOT_WIDTH);
        snapshotButton.setPrefWidth(SNAPSHOT_WIDTH);
        snapshotButton.setGraphic(new ImageView(snapshotImage));
        snapshotButton.setOnAction(e -> {
            if(getSelectedShape() != null) getSelectedShape().setStroke(outlineColor);
            selectedShape = null;
            moveUpButton.setDisable(true);
            moveDownButton.setDisable(true);
            removeButton.setDisable(true);
            workspaceEditController.handleSnapshotRequest();
        });
        hbox.getChildren().add(snapshotButton);
        hboxList.add(hbox);

    }

    /**
     * @return the canvasPane
     */
    public Pane getCanvasPane() {
        return canvasPane;
    }
    
    public void updateSelectedShapeColor(Color clr, int i) {
        if(i == 1){
            if(getSelectedShape() != null){
                if(getSelectedShape() instanceof PoseMakerRectangle){
                        PoseMakerRectangle rect = (PoseMakerRectangle) getSelectedShape();
                        rect.setFillColor(clr);
                        rect.setFill(clr);
                    }else if(getSelectedShape() instanceof PoseMakerEllipse){
                        PoseMakerEllipse ellipse = (PoseMakerEllipse) getSelectedShape();
                        ellipse.setFillColor(clr);
                        ellipse.setFill(clr);
                    }
            }
        }else if(i == 2){
            if(getSelectedShape() != null){
                if(getSelectedShape() instanceof PoseMakerRectangle){
                        PoseMakerRectangle rect = (PoseMakerRectangle) getSelectedShape();
                        rect.setOutlineColor(clr);                        
                    }else if(getSelectedShape() instanceof PoseMakerEllipse){
                        PoseMakerEllipse ellipse = (PoseMakerEllipse) getSelectedShape();
                        ellipse.setOutlineColor(clr);
                    }
            }
        }
    }
    
    public void select(Shape selected){
        setSelectedShape(selected);
        selected.setStroke(Color.YELLOW);
    }
    
    /*EVERYTHING BELOW HERE IS
    SETTING UP MOUSE LISTENERS FOR
    THE CANVAS PANE */
    
    private void initClickListener() {
        canvasPane.setOnMouseClicked((MouseEvent event) -> {
            if(workspaceEditController.getState() == PoseMakerState.selectState){                           
                //for any state do this stuff
                //this handles disabling remove button
                if(getSelectedShape() != null){
                    getRemoveButton().setDisable(false);
                }else{
                    getRemoveButton().setDisable(true);
                }
                //this handles disabling move index buttons
                if(getSelectedShape() == null){
                    getMoveUpButton().setDisable(true);
                    getMoveDownButton().setDisable(true);               
                }else{
                    if(getSelectedShape() instanceof PoseMakerRectangle){
                        PoseMakerRectangle rect = (PoseMakerRectangle)getSelectedShape();
                        //check for moveup button
                        if(dataManager.getShapeList().size() - 1 >= rect.getIndex() + 1){
                            getMoveUpButton().setDisable(false);
                        }else{
                            getMoveUpButton().setDisable(true);
                        }
                        //check for movedown button
                        if(rect.getIndex() > 0 ){
                            getMoveDownButton().setDisable(false);
                        }else{
                            getMoveDownButton().setDisable(true);
                        }                   
                    }else if(getSelectedShape() instanceof PoseMakerEllipse){
                        PoseMakerEllipse ellipse = (PoseMakerEllipse)getSelectedShape();
                        //check for moveup button
                        if(dataManager.getShapeList().size() - 1 >= ellipse.getIndex() + 1){
                            getMoveUpButton().setDisable(false);
                        }else{
                            getMoveUpButton().setDisable(true);
                        }
                        //check for movedown button
                        if(ellipse.getIndex() > 0 ){
                            getMoveDownButton().setDisable(false);
                        }else{
                            getMoveDownButton().setDisable(true);
                        }
                    }   
                }
          
            }
        });
    }

    private void initDragListener() {
        canvasPane.setOnMouseDragged((MouseEvent event) -> {
            if(workspaceEditController.getState() == PoseMakerState.selectState){
                //MOVE SELECTED SHAPE
                double x = event.getX();
                double y = event.getY();
                if(moveable){
                    app.getGUI().updateToolbarControls(false);
                    if(selectedShape instanceof PoseMakerRectangle){
                        PoseMakerRectangle rect = (PoseMakerRectangle) selectedShape;
                        rect.setX(x - xdist);
                        rect.setY(y - ydist);
                    }else if(selectedShape instanceof PoseMakerEllipse){
                        PoseMakerEllipse ellipse = (PoseMakerEllipse) selectedShape; 
                        ellipse.setCenterX(x - xdist);
                        ellipse.setCenterY(y - ydist);
                    } 
                }
                
            }else if(workspaceEditController.getState() == PoseMakerState.drawRectState){
                //Draw the rectangle
                app.getGUI().updateToolbarControls(false);
                PoseMakerRectangle rect;               
                rect = (PoseMakerRectangle) dataManager.getShapeList().get(getIndex());       
                double width = event.getX() - rect.getX();
                rect.setWidth(width);
                double height = event.getY() - rect.getY();
                rect.setHeight(height);
                
            }else if(workspaceEditController.getState() == PoseMakerState.drawEllipseState){          
                //Draw the ellipse
                app.getGUI().updateToolbarControls(false);
                PoseMakerEllipse ellipse;
                ellipse = (PoseMakerEllipse) dataManager.getShapeList().get(getIndex());
                
                ellipse.setCenterX(ellipse.getStartingX() + ((event.getX() - ellipse.getStartingX()) / 2));
                ellipse.setCenterY(ellipse.getStartingY() + ((event.getY() - ellipse.getStartingY()) / 2));
                ellipse.setRadiusX((event.getX() - ellipse.getStartingX())/2);
                ellipse.setRadiusY((event.getY() - ellipse.getStartingY())/2);
            }
        });   
    }

    //React to a mouse pressed event
    private void initPressedListener() {
        canvasPane.setOnMousePressed((MouseEvent event) -> {
            //clears highlight from last selected shape
            if(workspaceEditController.getState() == PoseMakerState.selectState){
                if(getSelectedShape() != null){
                    if(getSelectedShape() instanceof PoseMakerRectangle){
                        PoseMakerRectangle rect = (PoseMakerRectangle) getSelectedShape();
                        rect.setStroke(rect.getOutlineColor());
                    }else if(getSelectedShape() instanceof PoseMakerEllipse){
                        PoseMakerEllipse ellipse = (PoseMakerEllipse) getSelectedShape();
                        ellipse.setStroke(ellipse.getOutlineColor());
                    }
                }         
                
                //gets the new selectedshape if it exists
                ArrayList<Shape> shapeList = dataManager.getShapeList();
                int highestIndex = 0;
                Shape highestShape = null;

                for(Shape s : shapeList){
                    double x = event.getX();
                    double y = event.getY();                   
                    if(s.intersects(x, y, 0, 0)){
                        if(s instanceof PoseMakerRectangle){
                            PoseMakerRectangle rect = (PoseMakerRectangle)s;
                            if(rect.getIndex() >= highestIndex){
                                highestIndex = rect.getIndex();
                                highestShape = rect;
                            }
                        }else if(s instanceof PoseMakerEllipse){
                            PoseMakerEllipse ellipse = (PoseMakerEllipse)s;
                            if(ellipse.getIndex() >= highestIndex){
                                highestIndex = ellipse.getIndex();
                                highestShape = ellipse;
                            }
                        }  
                    }
                }
                
                //if a shape was clicked on, set up the
                //side options and highlight it (with yellow)
                setSelectedShape(highestShape);               
                if(getSelectedShape() != null){
                    if(getSelectedShape() instanceof PoseMakerRectangle){
                        PoseMakerRectangle rect = (PoseMakerRectangle)getSelectedShape();
                        outlineSlider.setValue(rect.getOutlineWidth());
                        fillColor = rect.getFillColor();
                        fillColorPicker.setValue(fillColor);
                        outlineColor = rect.getOutlineColor();
                        outlineColorPicker.setValue(outlineColor);
                    }else if(getSelectedShape() instanceof PoseMakerEllipse){
                        PoseMakerEllipse ellipse = (PoseMakerEllipse)getSelectedShape();
                        outlineSlider.setValue(ellipse.getOutlineWidth()); 
                        fillColor = ellipse.getFillColor();
                        fillColorPicker.setValue(ellipse.getFillColor());
                        outlineColor = ellipse.getOutlineColor();
                        outlineColorPicker.setValue(outlineColor);
                    } 
                    getSelectedShape().setStroke(Color.YELLOW);
                }
                
                //if the shape exists, get the mousepressed location
                //for dragging it if the user chooses to
                if(selectedShape != null){
                    if(selectedShape.intersects(event.getX(), event.getY(), 0, 0)){
                        moveable = true;                        
                        double x = event.getX();
                        double y = event.getY();
                        if(selectedShape instanceof PoseMakerRectangle){
                            PoseMakerRectangle rect = (PoseMakerRectangle) selectedShape;
                            xdist = x - rect.getX();
                            ydist = y - rect.getY();                  
                        }else if(selectedShape instanceof PoseMakerEllipse){
                            PoseMakerEllipse ellipse = (PoseMakerEllipse) selectedShape; 
                            xdist = x - ellipse.getCenterX();
                            ydist = y - ellipse.getCenterY();                               
                        }
                    }
                }
            }else if(workspaceEditController.getState() == PoseMakerState.drawRectState){
                //Make the rectangle
                app.getGUI().updateToolbarControls(false);
                setSelectedShape(null);
                PoseMakerRectangle rect;
                fillColor = fillColorPicker.getValue();
                outlineColor = outlineColorPicker.getValue();
                rect = new PoseMakerRectangle(event.getX(), event.getY(), 0, 0, getIndex(), fillColor, outlineColor);
                rect.setFill(fillColor);
                rect.setFillColor(fillColor);
                rect.setOutlineColor(outlineColor);
                rect.setStroke(outlineColor);
                rect.setStrokeWidth(outlineWidth);
                rect.setOutlineWidth(outlineWidth);
                dataManager.addShape(rect);       
                canvasPane.getChildren().add(rect);
            }else if(workspaceEditController.getState() == PoseMakerState.drawEllipseState){          
                //Make the ellipse
                app.getGUI().updateToolbarControls(false);
                setSelectedShape(null);
                PoseMakerEllipse ellipse;
                fillColor = fillColorPicker.getValue();
                outlineColor = outlineColorPicker.getValue();
                ellipse = new PoseMakerEllipse(event.getX(), event.getY(), 0, 0, getIndex(), fillColor, outlineColor);
                ellipse.setFill(fillColor);
                ellipse.setFillColor(fillColor);
                ellipse.setOutlineColor(outlineColor);
                ellipse.setStroke(outlineColor);
                ellipse.setStrokeWidth(outlineWidth);
                ellipse.setOutlineWidth(outlineWidth);
                dataManager.addShape(ellipse);        
                canvasPane.getChildren().add(ellipse);
            }
        });  
    }

    private void initReleasedListener() {
        canvasPane.setOnMouseReleased((MouseEvent event) -> {           
            if(workspaceEditController.getState() == PoseMakerState.selectState){
                moveable = false;
            }else if(workspaceEditController.getState() == PoseMakerState.drawRectState){
                setIndex(getIndex() + 1);
            }else if(workspaceEditController.getState() == PoseMakerState.drawEllipseState){          
                setIndex(getIndex() + 1);
            }
        });   
    }

    public void deselect() {
        if(getSelectedShape() != null){
            if(getSelectedShape() instanceof PoseMakerRectangle){
                PoseMakerRectangle rect = (PoseMakerRectangle) getSelectedShape();
                rect.setStroke(rect.getOutlineColor());
            }else if(getSelectedShape() instanceof PoseMakerEllipse){
                PoseMakerEllipse ellipse = (PoseMakerEllipse) getSelectedShape();
                ellipse.setStroke(ellipse.getOutlineColor());
            }
        }
        setSelectedShape(null);
    }

    /**
     * @return the selectedShape
     */
    public Shape getSelectedShape() {
        return selectedShape;
    }

    /**
     * @param selectedShape the selectedShape to set
     */
    public void setSelectedShape(Shape selectedShape) {
        this.selectedShape = selectedShape;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the removeButton
     */
    public Button getRemoveButton() {
        return removeButton;
    }

    /**
     * @return the moveUpButton
     */
    public Button getMoveUpButton() {
        return moveUpButton;
    }

    /**
     * @return the moveDownButton
     */
    public Button getMoveDownButton() {
        return moveDownButton;
    }

    public void updateBackground(Color color) {
        app.getGUI().updateToolbarControls(false);
        backgroundColor = color;
        String colorHexCode = String.format( "#%02X%02X%02X",(int)(color.getRed()*255), (int)(color.getGreen()*255), (int)(color.getBlue()*255));
        canvasPane.setStyle("-fx-background-color:" + colorHexCode + ";");
    }
    
    public Color getBackgroundColor(){
        return backgroundColor;
    }
    
    public void loadNewFile(ArrayList<Shape> sList, Color bckGround){
        ArrayList<Shape> newShapeList = sList;
        
        System.out.println("we did it boys1");
        Iterator<Node> iter = canvasPane.getChildren().iterator();        
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
            
        }
        System.out.println("we did it boys2");
        Iterator<Shape> shapeIter = dataManager.getShapeList().iterator();
        while (shapeIter.hasNext()) {
            shapeIter.next();
            shapeIter.remove();
        }        
        System.out.println("we did it boys3");
        
        for(Shape s : newShapeList){
            dataManager.getShapeList().add(s);
        }
        System.out.println("we did it boys4");
        
        for(Shape s: dataManager.getShapeList()){
            canvasPane.getChildren().add(s);
            if(s instanceof PoseMakerRectangle){
                PoseMakerRectangle rect = (PoseMakerRectangle) s;
                System.out.println("List index: " + dataManager.getShapeList().indexOf(rect) + ", Shape index: " + rect.getIndex());
            }else if(s instanceof PoseMakerEllipse){
                PoseMakerEllipse ellipse = (PoseMakerEllipse) s;
                System.out.println("List index: " + dataManager.getShapeList().indexOf(ellipse) + ", Shape index: " + ellipse.getIndex());              
            }  
        }
        
        updateBackground(bckGround);
        index = dataManager.getShapeList().size();
    }
    
}
