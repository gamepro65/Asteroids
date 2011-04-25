package asteroids;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Vector2f;
import processing.core.PApplet;



public abstract class AbstractEntity {
	//Set-up our parent protected variables
	protected ArrayList verticiesSizes;
	protected Vector2f position;
	protected double rotation;
	protected int numberOfSides;
	protected Polygon verticies;
	protected PApplet parent;
	protected int health;
	protected Color objectColor;
	
	//Default constructor used to create an entity
	//Auto initializes Entity.
	public AbstractEntity(PApplet aParent, Vector2f aPosition)
	{
		//Math wont supply next float so well just make a new random object
		//for our random object color.
		Random ran = new Random();
		objectColor = new Color(ran.nextFloat(),ran.nextFloat(),ran.nextFloat(),1);
		position = aPosition;
		rotation = 0;
		parent = aParent;
		verticies = new Polygon();
		verticiesSizes = new ArrayList();
		
	}
	
	public void updatePolygon()
	{
		//Reinitializes the polygon from the distance away from the center
		//of the current position. It uses the fact that 360 degrees will
		//be divided by the number of points required. This will give us a circular polygon
		//with varying distances around the center to create our object. We also are
		//able to rotate our polygon here by adding our object rotation into the equation.
		verticies = new Polygon();
		for (int x = 0; x < verticiesSizes.size(); x++)
		{
			//current distance from the center
			Double size1 = (Double)verticiesSizes.get(x);
			//current angle from a 360 standpoint divided by the number of 
			//sides and adding our current rotation.
			double angle1 = ((360/numberOfSides)*x) + rotation;
			//Creates new point for the vector
			Vector2f point1 = new Vector2f((float)(Math.cos(Math.toRadians(angle1))*size1.doubleValue()), (float)(Math.sin(Math.toRadians(angle1))*size1.doubleValue()));
			//Inserts the point into our newly created polygon.
			verticies.addPoint((int)point1.x+(int)position.x, (int)point1.y+(int)position.y);
		}
	}
	
	public void update(double aDelta)
	{
		//Rotate the polygon by reinitializing it so it can reupdate its points
		//based on the current rotation of the object.
		updatePolygon();
		
		//Gets the bounds of the polygon then checks if the polygon is outside of
		//The screen. If so then it will move the objects position to the other side
		//of the map for our wrap around effect.
		Rectangle2D tempRect = verticies.getBounds2D();
		if (position.x < -tempRect.getWidth())
			position.x = (float)(parent.screenWidth+tempRect.getWidth());
		
		if (position.x > (float)(parent.screenWidth+tempRect.getWidth()))
			position.x = -(float)tempRect.getWidth();
		
		if (position.y < -tempRect.getHeight())
			position.y = (float)(parent.screenHeight+tempRect.getHeight());
		
		if (position.y > (parent.screenHeight+tempRect.getHeight()))
			position.y = -(float)tempRect.getHeight();
		
	}
		
	public void draw()
	{	
		//If color is enabled lets set our polygon color. If not
		//Then make it not have any fill.
		if (Main.useColor)
		{
			parent.fill(objectColor.getRed(), objectColor.getGreen(), objectColor.getBlue(), objectColor.getAlpha());
		}
		else
		{
			parent.noFill();
		}
		
		//Begins drawing our new shape.
		//Processing shapes are created by giving them the verticies points
		//from the polygon and then links the points with lines for us
		//all while using our current fill color to figure out what pixels to color
		//for our object.
		parent.beginShape();
	    for(int i = 0; i < verticies.npoints; i++)
	    {
	      parent.vertex(verticies.xpoints[i],verticies.ypoints[i]);
	    }
	    parent.vertex(verticies.xpoints[0], verticies.ypoints[0]);
	    parent.endShape();		
	}

}
