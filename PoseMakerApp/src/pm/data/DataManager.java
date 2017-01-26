package pm.data;

import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.shape.Shape;
import pm.gui.Workspace;
import pm.shapes.PoseMakerEllipse;
import pm.shapes.PoseMakerRectangle;
import saf.components.AppDataComponent;
import saf.AppTemplate;

/**
 * This class serves as the data management component for this application.
 *
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class DataManager implements AppDataComponent {
    // THIS IS A SHARED REFERENCE TO THE APPLICATION
    private AppTemplate app;
    ArrayList<Shape> shapeList;
    /**
     * THis constructor creates the data manager and sets up the
     *
     *
     * @param initApp The application within which this data manager is serving.
     */
    public DataManager(AppTemplate initApp) throws Exception {
	// KEEP THE APP FOR LATER
	app = initApp;
        shapeList = new ArrayList();
    }

    /**
     * This function clears out the HTML tree and reloads it with the minimal
     * tags, like html, head, and body such that the user can begin editing a
     * page.
     */
    @Override
    public void reset() {

    }
    
    public void addShape(Shape shape){
        shapeList.add(shape);
    }
    
    public void removeShape(Shape shape){
        int shapeIndex;
        Workspace workspace = (Workspace) getApp().getWorkspaceComponent();       
       
        if(shape instanceof PoseMakerRectangle){
            PoseMakerRectangle rect = (PoseMakerRectangle) shape;
            shapeIndex = rect.getIndex();
            shapeList.remove(shape);
            workspace.getCanvasPane().getChildren().remove(shape);
            
            for(Shape s : shapeList){
                if(s instanceof PoseMakerRectangle){
                    PoseMakerRectangle rect2 = (PoseMakerRectangle) s;
                    if(rect2.getIndex() >= shapeIndex) rect2.setIndex(rect2.getIndex() - 1);
                }else if(s instanceof PoseMakerEllipse){
                    PoseMakerEllipse ellipse2 = (PoseMakerEllipse) s;
                    if(ellipse2.getIndex() >= shapeIndex) ellipse2.setIndex(ellipse2.getIndex() - 1);
                }
            }
            
        }else if(shape instanceof PoseMakerEllipse){
            PoseMakerEllipse ellipse = (PoseMakerEllipse) shape;
            shapeIndex = ellipse.getIndex();
            shapeList.remove(shape);
            workspace.getCanvasPane().getChildren().remove(shape);
            
            for(Shape s : shapeList){
                if(s instanceof PoseMakerRectangle){
                    PoseMakerRectangle rect2 = (PoseMakerRectangle) s;
                    if(rect2.getIndex() >= shapeIndex) rect2.setIndex(rect2.getIndex() - 1);
                }else if(s instanceof PoseMakerEllipse){
                    PoseMakerEllipse ellipse2 = (PoseMakerEllipse) s;
                    if(ellipse2.getIndex() >= shapeIndex) ellipse2.setIndex(ellipse2.getIndex() - 1);
                }
            }
        }      
        workspace.setIndex(workspace.getIndex() - 1);
    }
    
    public void moveShapeUp(Shape shape){
       //Remove children from canvas
        Workspace workspace = (Workspace) getApp().getWorkspaceComponent();
        workspace.getCanvasPane().getChildren().removeAll(shapeList);

        //Swap the two shapes (selected one and one above it)
        if(shape instanceof PoseMakerRectangle){
            PoseMakerRectangle rect = (PoseMakerRectangle) shape;
            int selectedOriginalIndex = rect.getIndex();
            Shape swapShape = shapeList.get(selectedOriginalIndex + 1);
            shapeList.set(selectedOriginalIndex + 1, rect);
            shapeList.set(selectedOriginalIndex, swapShape);
            
            //set new indexes associated for the shape
            rect.setIndex(selectedOriginalIndex + 1);
            if(swapShape instanceof PoseMakerRectangle){
                PoseMakerRectangle swapRect = (PoseMakerRectangle) swapShape;
                swapRect.setIndex(selectedOriginalIndex);
            }else if(swapShape instanceof PoseMakerEllipse){
                PoseMakerEllipse swapEllipse = (PoseMakerEllipse) swapShape;
                swapEllipse.setIndex(selectedOriginalIndex);
            }            
        }else if(shape instanceof PoseMakerEllipse){
            PoseMakerEllipse ellipse = (PoseMakerEllipse) shape;
            int selectedOriginalIndex = ellipse.getIndex();
            Shape swapShape = shapeList.get(selectedOriginalIndex + 1);
            shapeList.set(selectedOriginalIndex + 1, ellipse);
            shapeList.set(selectedOriginalIndex, swapShape);
            
            //set new indexes associated for the shape
            ellipse.setIndex(selectedOriginalIndex + 1);
            if(swapShape instanceof PoseMakerRectangle){
                PoseMakerRectangle swapRect = (PoseMakerRectangle) swapShape;
                swapRect.setIndex(selectedOriginalIndex);
            }else if(swapShape instanceof PoseMakerEllipse){
                PoseMakerEllipse swapEllipse = (PoseMakerEllipse) swapShape;
                swapEllipse.setIndex(selectedOriginalIndex);
            }           
        }   
        
        //Add newly ordered shapes to canvas
        workspace.getCanvasPane().getChildren().addAll(shapeList);
        
    }
    
    public void moveShapeDown(Shape shape){
        //Remove children from canvas
        Workspace workspace = (Workspace) getApp().getWorkspaceComponent();
        workspace.getCanvasPane().getChildren().removeAll(shapeList);

        //Swap the two shapes (selected one and one above it)
        if(shape instanceof PoseMakerRectangle){
            PoseMakerRectangle rect = (PoseMakerRectangle) shape;
            int selectedOriginalIndex = rect.getIndex();
            Shape swapShape = shapeList.get(selectedOriginalIndex - 1);
            shapeList.set(selectedOriginalIndex - 1, rect);
            shapeList.set(selectedOriginalIndex, swapShape);
            
            //set new indexes associated for the shape
            rect.setIndex(selectedOriginalIndex - 1);
            if(swapShape instanceof PoseMakerRectangle){
                PoseMakerRectangle swapRect = (PoseMakerRectangle) swapShape;
                swapRect.setIndex(selectedOriginalIndex);
            }else if(swapShape instanceof PoseMakerEllipse){
                PoseMakerEllipse swapEllipse = (PoseMakerEllipse) swapShape;
                swapEllipse.setIndex(selectedOriginalIndex);
            }            
        }else if(shape instanceof PoseMakerEllipse){
            PoseMakerEllipse ellipse = (PoseMakerEllipse) shape;
            int selectedOriginalIndex = ellipse.getIndex();
            Shape swapShape = shapeList.get(selectedOriginalIndex - 1);
            shapeList.set(selectedOriginalIndex - 1, ellipse);
            shapeList.set(selectedOriginalIndex, swapShape);
            
            //set new indexes associated for the shape
            ellipse.setIndex(selectedOriginalIndex - 1);
            if(swapShape instanceof PoseMakerRectangle){
                PoseMakerRectangle swapRect = (PoseMakerRectangle) swapShape;
                swapRect.setIndex(selectedOriginalIndex);
            }else if(swapShape instanceof PoseMakerEllipse){
                PoseMakerEllipse swapEllipse = (PoseMakerEllipse) swapShape;
                swapEllipse.setIndex(selectedOriginalIndex);
            }           
        }   
        
        //Add newly ordered shapes to canvas
        workspace.getCanvasPane().getChildren().addAll(shapeList);
    }
    
    public ArrayList<Shape> getShapeList(){
        return shapeList;
    }

    public void setShapeList(ArrayList<Shape> list){
        shapeList = list;
    }
    /**
     * @return the app
     */
    public AppTemplate getApp() {
        return app;
    }
}
