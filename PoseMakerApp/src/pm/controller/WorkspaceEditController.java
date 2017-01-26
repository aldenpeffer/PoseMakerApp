/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pm.controller;

import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import pm.PoseMaker;
import pm.data.DataManager;
import pm.gui.Workspace;
import pm.shapes.PoseMakerEllipse;
import pm.shapes.PoseMakerRectangle;

/**
 *
 * @author Alden
 */
public class WorkspaceEditController {

    PoseMaker app;
    Scene mainScene;
    PoseMakerState state;
    DataManager dataManager;
    Color backgroundColor;
    Color outlineColor;
    Color fillColor;
    private int index;
    private boolean enabled;
    final int HEIGHT_OFFSET = 58;
    final int WIDTH_OFFSET = 243;
    
    public WorkspaceEditController(PoseMaker initApp){
        app = initApp;
        mainScene = app.getGUI().getPrimaryScene();
        dataManager = (DataManager) app.getDataComponent(); 
        index = 0;
        state = PoseMakerState.nullState;
        backgroundColor = Color.SEASHELL;
        outlineColor = Color.web("#99cc99");
        fillColor = Color.web("#ff6666");
    }
    
    public void handleSelectionRequest() {
        state = PoseMakerState.selectState;
        mainScene.setCursor((Cursor.DEFAULT));
    }

    public void handleRemoveRequest() {
        Workspace workspace = (Workspace) app.getWorkspaceComponent();
        app.getGUI().updateToolbarControls(false);
        Shape s = workspace.getSelectedShape();
        if(s != null) dataManager.removeShape(s);
        workspace.setSelectedShape(null);
        workspace.getRemoveButton().setDisable(true);
        workspace.getMoveUpButton().setDisable(true);
        workspace.getMoveDownButton().setDisable(true);
    }

    public void handleRectRequest() {
        state = PoseMakerState.drawRectState;
        Workspace workspace = (Workspace) app.getWorkspaceComponent();
        workspace.getRemoveButton().setDisable(true);
        workspace.getMoveUpButton().setDisable(true);
        workspace.getMoveDownButton().setDisable(true);
        workspace.deselect();
        mainScene.setCursor(Cursor.CROSSHAIR);
    }

    public void handleEllipseRequest() {
        state = PoseMakerState.drawEllipseState;
        Workspace workspace = (Workspace) app.getWorkspaceComponent();
        workspace.getRemoveButton().setDisable(true);
        workspace.getMoveUpButton().setDisable(true);
        workspace.getMoveDownButton().setDisable(true);
        workspace.deselect();
        mainScene.setCursor(Cursor.CROSSHAIR);
    }

    public void handleMoveRequest(int i) {
        Workspace workspace = (Workspace) app.getWorkspaceComponent();
        Shape s = workspace.getSelectedShape();       
        
        if(i == 1){
            dataManager.moveShapeUp(s);
            if(s instanceof PoseMakerRectangle){
                PoseMakerRectangle rect = (PoseMakerRectangle) s;
                if(rect.getIndex() == dataManager.getShapeList().size() - 1){
                    workspace.getMoveUpButton().setDisable(true);
                }
            }else if(s instanceof PoseMakerEllipse){
                PoseMakerEllipse ellipse = (PoseMakerEllipse) s; 
                if(ellipse.getIndex() == dataManager.getShapeList().size() - 1){
                    workspace.getMoveUpButton().setDisable(true);
                }
            }  
            workspace.getMoveDownButton().setDisable(false);
        }else if(i == -1){
            dataManager.moveShapeDown(s);
            if(s instanceof PoseMakerRectangle){
                PoseMakerRectangle rect = (PoseMakerRectangle) s;
                if(rect.getIndex() == 0){
                    workspace.getMoveDownButton().setDisable(true);
                }
            }else if(s instanceof PoseMakerEllipse){
                PoseMakerEllipse ellipse = (PoseMakerEllipse) s; 
                if(ellipse.getIndex() == 0){
                    workspace.getMoveDownButton().setDisable(true);
                }
            }  
            workspace.getMoveUpButton().setDisable(false);
        }
        app.getGUI().updateToolbarControls(false);
    }

    public void handleChangeColorRequest(Color color, int index) {
        if(index == 0){           
            //background color stuff
            Workspace workspace = (Workspace) app.getWorkspaceComponent();
            workspace.updateBackground(color);
        }else if(index == 1){
            fillColor = color;
            Workspace workspace = (Workspace) app.getWorkspaceComponent();
            workspace.updateSelectedShapeColor(fillColor, 1);
            
        }else if(index == 2){
            outlineColor = color;
            Workspace workspace = (Workspace) app.getWorkspaceComponent();
            workspace.updateSelectedShapeColor(outlineColor, 2);
        }
        app.getGUI().updateToolbarControls(false);
    }

    public void handleSnapshotRequest() {
        FileChooser fileChooser = new FileChooser();
        Workspace workspace = (Workspace) app.getWorkspaceComponent();       
        fileChooser.setTitle("Save Pose");
        WritableImage poseImage = workspace.getCanvasPane().snapshot(new SnapshotParameters(), null);
        fileChooser.setInitialFileName("Pose.png");
        FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(pngFilter);
        File file = fileChooser.showSaveDialog(app.getGUI().getWindow());
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(poseImage,
                    null), "png", file);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public PoseMakerState getState(){
        return state;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public Color getOutlineColor() {
        return outlineColor;
    }
    
}
