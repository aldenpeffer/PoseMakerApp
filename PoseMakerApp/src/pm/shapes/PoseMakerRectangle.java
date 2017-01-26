/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pm.shapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Alden
 */
public class PoseMakerRectangle extends Rectangle{
    
    private int index;
    private int outlineWidth;
    private Color fillColor;    
    private Color outlineColor;    
    private final String SHAPE_TYPE = "Rectangle";
    
    public PoseMakerRectangle(){
        //For loading purposes
    }
    
    public PoseMakerRectangle(double x, double y, double width, double height,
            int index, Color fillColor, Color outlineColor){
        setX(x); 
        setY(y);
        setWidth(width);
        setHeight(height);
        this.index = index;
        this.fillColor = fillColor;
        this.outlineColor = outlineColor;       
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
     * @return the fillColor
     */
    public Color getFillColor() {
        return fillColor;
    }

    /**
     * @param fillColor the fillColor to set
     */
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * @return the outlineColor
     */
    public Color getOutlineColor() {
        return outlineColor;
    }

    /**
     * @param outlineColor the outlineColor to set
     */
    public void setOutlineColor(Color outlineColor) {
        this.outlineColor = outlineColor;
    }

    /**
     * @return the outlineWidth
     */
    public int getOutlineWidth() {
        return outlineWidth;
    }

    /**
     * @param outlineWidth the outlineWidth to set
     */
    public void setOutlineWidth(int outlineWidth) {
        this.outlineWidth = outlineWidth;
    }

    /**
     * @return the SHAPE_TYPE
     */
    public String getSHAPE_TYPE() {
        return SHAPE_TYPE;
    }
}