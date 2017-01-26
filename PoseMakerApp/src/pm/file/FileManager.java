package pm.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import pm.data.DataManager;
import pm.gui.Workspace;
import pm.shapes.PoseMakerEllipse;
import pm.shapes.PoseMakerRectangle;
import saf.AppTemplate;
import saf.components.AppDataComponent;
import saf.components.AppFileComponent;

/**
 * This class serves as the file management component for this application,
 * providing all I/O services.
 *
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class FileManager implements AppFileComponent {

    /**
     * This method is for saving user work, which in the case of this
     * application means the data that constitutes the page DOM.
     * 
     * @param data The data management component for this application.
     * 
     * @param filePath Path (including file name/extension) to where
     * to save the data to.
     * 
     * @throws IOException Thrown should there be an error writing 
     * out data to the file.
     */
    
    AppTemplate app;
    
    @Override
    public void saveData(AppDataComponent data, String filePath) throws IOException {
        OutputStream os = null;
        DataManager dataManager = (DataManager) data;      
        ArrayList<Shape> shapeList = dataManager.getShapeList();
        Workspace workspace = (Workspace) dataManager.getApp().getWorkspaceComponent();
        
        try {        
            File file = new File(filePath);
            
            StringWriter sw = new StringWriter();
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for(Shape s : shapeList){
                if(s instanceof PoseMakerRectangle){
                    PoseMakerRectangle rect = (PoseMakerRectangle) s;
                    JsonObject jso = Json.createObjectBuilder()
                            .add("Index", rect.getIndex())
                            .add("X_Value", rect.getX())
                            .add("Y_Value", rect.getY())
                            .add("Width", rect.getWidth())
                            .add("Height", rect.getHeight())
                            .add("Fill_Color", rect.getFillColor().toString())
                            .add("Outline_Color", rect.getOutlineColor().toString())
                            .add("Outline_Width", rect.getOutlineWidth())
                            .add("Shape_Type", rect.getSHAPE_TYPE())
                            .build();
                    arrayBuilder.add(jso);
                }else if(s instanceof PoseMakerEllipse){
                    PoseMakerEllipse ellipse = (PoseMakerEllipse) s;
                    JsonObject jso = Json.createObjectBuilder()
                            .add("Index", ellipse.getIndex())
                            .add("Center_X_Value", ellipse.getCenterX())
                            .add("Center_Y_Value", ellipse.getCenterY())
                            .add("Starting_X", ellipse.getStartingX())
                            .add("Starting_Y", ellipse.getStartingY())
                            .add("X_Radius", ellipse.getRadiusX())
                            .add("Y_Radius", ellipse .getRadiusY())
                            .add("Fill_Color", ellipse.getFillColor().toString())
                            .add("Outline_Color", ellipse.getOutlineColor().toString())
                            .add("Outline_Width", ellipse.getOutlineWidth())
                            .add("Shape_Type", ellipse.getSHAPE_TYPE())
                            .build();
                    arrayBuilder.add(jso);
                }
                    
                
            }   
            JsonObject jso = Json.createObjectBuilder()
                    .add("Background_Color", workspace.getBackgroundColor().toString())
            .build();
            
            JsonArray nodesArray = arrayBuilder.build();
            JsonObject dataManagerJSO = Json.createObjectBuilder()
                    .add("shapes", nodesArray)
                    .add("background", jso)
                    .build();
            Map<String, Object> properties = new HashMap<>(1);
            properties.put(JsonGenerator.PRETTY_PRINTING, true);
            JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
            JsonWriter jsonWriter = writerFactory.createWriter(sw);
            jsonWriter.writeObject(dataManagerJSO);
            jsonWriter.close();
            os = new FileOutputStream(file);
            JsonWriter jsonFileWriter = Json.createWriter(os);
            jsonFileWriter.writeObject(dataManagerJSO);
            String prettyPrinted = sw.toString();
            PrintWriter pw = new PrintWriter(file);
            pw.write(prettyPrinted);
            pw.close();
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
      
    /**
     * This method loads data from a JSON formatted file into the data 
     * management component and then forces the updating of the workspace
     * such that the user may edit the data.
     * 
     * @param data Data management component where we'll load the file into.
     * 
     * @param filePath Path (including file name/extension) to where
     * to load the data from.
     * 
     * @throws IOException Thrown should there be an error reading
     * in data from the file.
     */
    @Override
    public void loadData(AppDataComponent data, String filePath) throws IOException {
        JsonObject json = loadJSONFile(filePath);       
        JsonArray jsonShapesArray = json.getJsonArray("shapes"); 
        ArrayList<Shape> shapeList = loadShapes(jsonShapesArray);
        JsonObject jsonBackground = json.getJsonObject("background");
        Color backgroundColor = Color.web(jsonBackground.getString("Background_Color"));
        Workspace workspace = (Workspace) app.getWorkspaceComponent();
        workspace.loadNewFile(shapeList, backgroundColor);
                     
    }

    // HELPER METHOD FOR LOADING DATA FROM A JSON FORMAT
    private JsonObject loadJSONFile(String jsonFilePath) throws IOException {
	InputStream is = new FileInputStream(jsonFilePath);
	JsonReader jsonReader = Json.createReader(is);
	JsonObject json = jsonReader.readObject();
	jsonReader.close();
	is.close();
	return json;
    }
    
    private static ArrayList<Shape> loadShapes(JsonArray jsonArray){
        ArrayList<Shape> shapeList = new ArrayList();
        for(int i = 0; i < jsonArray.size(); i++){
            JsonObject nodeJso = jsonArray.getJsonObject(i);
            if(nodeJso.getString("Shape_Type").equals("Rectangle")){
                PoseMakerRectangle rect = new PoseMakerRectangle();
                rect.setIndex(nodeJso.getInt("Index"));
                rect.setX(nodeJso.getInt("X_Value"));
                rect.setY(nodeJso.getInt("Y_Value"));
                rect.setWidth(nodeJso.getInt("Width"));
                rect.setHeight(nodeJso.getInt("Height"));
                rect.setFillColor(Color.web(nodeJso.getString("Fill_Color")));
                rect.setFill(Color.web(nodeJso.getString("Fill_Color")));
                rect.setOutlineColor(Color.web(nodeJso.getString("Outline_Color")));
                rect.setStroke(Color.web(nodeJso.getString("Outline_Color")));
                rect.setStrokeWidth(nodeJso.getInt("Outline_Width"));
                rect.setOutlineWidth(nodeJso.getInt("Outline_Width"));
                shapeList.add(rect);
            }else if(nodeJso.getString("Shape_Type").equals("Ellipse")){
                PoseMakerEllipse ellipse = new PoseMakerEllipse();
                ellipse.setIndex(nodeJso.getInt("Index"));
                ellipse.setCenterX(nodeJso.getInt("Center_X_Value"));
                ellipse.setCenterY(nodeJso.getInt("Center_Y_Value"));
                ellipse.setStartingX(nodeJso.getInt("Starting_X"));
                ellipse.setStartingY(nodeJso.getInt("Starting_Y"));
                ellipse.setRadiusX(nodeJso.getInt("X_Radius"));
                ellipse.setRadiusY(nodeJso.getInt("Y_Radius"));
                ellipse.setFillColor(Color.web(nodeJso.getString("Fill_Color")));
                ellipse.setFill(Color.web(nodeJso.getString("Fill_Color")));
                ellipse.setOutlineColor(Color.web(nodeJso.getString("Outline_Color")));
                ellipse.setStroke(Color.web(nodeJso.getString("Outline_Color")));
                ellipse.setStrokeWidth(nodeJso.getInt("Outline_Width"));
                ellipse.setOutlineWidth(nodeJso.getInt("Outline_Width"));
                shapeList.add(ellipse);
            }          
        }
        return shapeList;     
    }
    
    /**
     * This method exports the contents of the data manager to a 
     * Web page including the html page, needed directories, and
     * the CSS file.
     * 
     * @param data The data management component.
     * 
     * @param filePath Path (including file name/extension) to where
     * to export the page to.
     * 
     * @throws IOException Thrown should there be an error writing
     * out data to the file.
     */
    @Override
    public void exportData(AppDataComponent data, String filePath) throws IOException {

    }
    
    /**
     * This method is provided to satisfy the compiler, but it
     * is not used by this application.
     */
    @Override
    public void importData(AppDataComponent data, String filePath) throws IOException {
	// NOTE THAT THE Web Page Maker APPLICATION MAKES
	// NO USE OF THIS METHOD SINCE IT NEVER IMPORTS
	// EXPORTED WEB PAGES
    }
    
    public void setApp(AppTemplate app){
        this.app = app;
    }
}
