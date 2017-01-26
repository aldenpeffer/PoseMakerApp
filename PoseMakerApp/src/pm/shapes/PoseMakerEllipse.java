/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pm.shapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

/**
 *
 * @author Alden
 */
public class PoseMakerEllipse extends Ellipse{
    
    private int index;
    private double startingX, startingY;
    private int outlineWidth;
    private Color fillColor;    
    private Color outlineColor;    
    private final String SHAPE_TYPE = "Ellipse";
    
    public PoseMakerEllipse(){
        //For loading purposes
    }
    
    public PoseMakerEllipse(double x, double y, double width, double height,
            int index, Color fillColor, Color outlineColor){
        setCenterX(x); 
        setCenterY(y);
        setStartingX(getCenterX());
        setStartingY(getCenterY());
        setRadiusX(width);
        setRadiusY(height);
        this.startingX = startingX;
        this.startingY = startingY;
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
     * @return the startingX
     */
    public double getStartingX() {
        return startingX;
    }

    /**
     * @param startingX the startingX to set
     */
    public void setStartingX(double startingX) {
        this.startingX = startingX;
    }

    /**
     * @return the startingY
     */
    public double getStartingY() {
        return startingY;
    }

    /**
     * @param startingY the startingY to set
     */
    public void setStartingY(double startingY) {
        this.startingY = startingY;
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