package asteroids;

/**
Name: Chris Drury
Class: CSc 2310: Introduction to programming
Filename: AbstractEntity.java
Date written: April, 19, 2011

Description:
This class is the parent that all my entities are 
based off of. It controls our base polygon for drawing
and some basic general update information about it.
*/

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
	protected ArrayList verticieParticles;
	public boolean destroied;
	
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
		verticieParticles = new ArrayList();
		destroied = false;
	}
	
	//destroies the current polygon by creating particles that mimic
	//the current outline of our polygon
	public void destroyObject()
	{		
		//loop throught the points in our object
		for (int x = 0; x < verticies.npoints-1; x++)
		{
			//grab both points and then use the mid-point formula
			//to find the center of the line
			Vector2f point1 = new Vector2f(verticies.xpoints[x], verticies.ypoints[x]);
			Vector2f point2 = new Vector2f(verticies.xpoints[x+1], verticies.ypoints[x+1]);
			Vector2f center = new Vector2f((point2.x + point1.x)/2, (point2.y + point1.y)/2);
			//find the triangle sides
			float xdis = point2.x - point1.x;
			float ydis = point2.y - point1.y;
			//get the angle of the line
			double angleAradians = Math.atan2(ydis,xdis);	
			//get the length of the line
			double hypot = Math.hypot(point2.x - point1.x, point2.y - point1.y);					
			double currentRotation;
			//convert to 360 degrees to make it easier to figure out.
			if (angleAradians < 0)
			{
				currentRotation = Math.toDegrees(angleAradians) + 360;
			}
			else
			{
				currentRotation = Math.toDegrees(angleAradians);
			}
			//Create a new particle using the information we have found out about the points
			Particle p = new Particle(parent, center, (float)(hypot/2), (float)currentRotation, (float)(Math.random()*100), 2, (float)(Math.random()*360), (float)(Math.random()*30 + 45));
			//add it to our particle emitter array
			verticieParticles.add(p);
		}
		
		//grab the first and last points to 
		//get the side of the polygon
		//and then use the mid-point formula
		//to find the center of the line
		Vector2f point1 = new Vector2f(verticies.xpoints[verticies.npoints-1], verticies.ypoints[verticies.npoints-1]);
		Vector2f point2 = new Vector2f(verticies.xpoints[0], verticies.ypoints[0]);
		Vector2f center = new Vector2f((point2.x + point1.x)/2, (point2.y + point1.y)/2);
		//find the triangle sides
		float xdis = point2.x - point1.x;
		float ydis = point2.y - point1.y;
		//get the angle of the line
		double angleAradians = Math.atan2(ydis,xdis);	
		//get the length of the line
		double hypot = Math.hypot(point2.x - point1.x, point2.y - point1.y);					
		double currentRotation;
		//convert to 360 degrees to make it easier to figure out.
		if (angleAradians < 0)
		{
			currentRotation = Math.toDegrees(angleAradians) + 360;
		}
		else
		{
			currentRotation = Math.toDegrees(angleAradians);
		}
		//Create a new particle using the information we have found out about the points
		Particle p = new Particle(parent, center, (float)(hypot/2), (float)currentRotation, (float)(Math.random()*100), 2, (float)(Math.random()*360), (float)(Math.random()*30 + 45));
		//add it to our particle emitter array
		verticieParticles.add(p);
		
		//tell us that the ship is now destroied
		destroied = true;
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
		
		//if our ship is destroied
		if (destroied)
		{
			ArrayList discardedParticles = new ArrayList();
			
			//loop the particle emitter to update its
			//particles and delete dead ones.
			for (int x = 0; x < verticieParticles.size(); x++)
			{
				Particle p = (Particle)verticieParticles.get(x);
				p.update(aDelta);
				if (p.delete)
				{
					discardedParticles.add(p);
				}				
			}
			verticieParticles.removeAll(discardedParticles);
		}
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
			//make the stroke white
			parent.stroke(255);
		}
		//if were alive draw us
		if (!destroied)
		{
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
		else
		{
			//if we are not alive then display my particles in my
			//emitter because thats the outline of our polygon
			for (int x = 0; x < verticieParticles.size(); x++)
			{
				Particle p = (Particle)verticieParticles.get(x);
				p.draw();
			}
		}
	}

}
