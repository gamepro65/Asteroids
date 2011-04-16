package asteroids;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Vector2f;
import processing.core.PApplet;



public abstract class AbstractEntity {
	protected ArrayList verticiesSizes;
	protected Vector2f position;
	protected double rotation;
	protected int numberOfSides;
	protected Polygon verticies;
	protected PApplet parent;
	protected int health;
	Color objectColor;
	
	public AbstractEntity(PApplet aParent, Vector2f aPosition)
	{
		Random ran = new Random();
		position = aPosition;
		rotation = 0;
		parent = aParent;
		verticies = new Polygon();
		verticiesSizes = new ArrayList();
		objectColor = new Color(ran.nextFloat(),ran.nextFloat(),ran.nextFloat(),1);
	}
	
	public void updatePolygon()
	{
		verticies = new Polygon();
		for (int x = 0; x < verticiesSizes.size(); x++)
		{
			Double size1 = (Double)verticiesSizes.get(x);
			double angle1 = ((360/numberOfSides)*x) + rotation;
			Vector2f point1 = new Vector2f((float)(Math.cos(Math.toRadians(angle1))*size1.doubleValue()), (float)(Math.sin(Math.toRadians(angle1))*size1.doubleValue()));
			verticies.addPoint((int)point1.x+(int)position.x, (int)point1.y+(int)position.y);
		}
	}
	
	public void update(double aDelta)
	{
		updatePolygon();
		
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
		if (Main.useColor)
		{
			parent.fill(objectColor.getRed(), objectColor.getGreen(), objectColor.getBlue(), objectColor.getAlpha());
		}
		else
		{
			parent.noFill();
		}
		
		parent.beginShape();
	    for(int i = 0; i < verticies.npoints; i++)
	    {
	      parent.vertex(verticies.xpoints[i],verticies.ypoints[i]);
	    }
	    parent.vertex(verticies.xpoints[0], verticies.ypoints[0]);
	    parent.endShape();
		
		
	}

}
