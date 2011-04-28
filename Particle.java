package asteroids;
/**
Name: Chris Drury
Class: CSc 2310: Introduction to programming
Filename: Particle.java
Date written: April, 26, 2011

Description:
This class holds a particle. This can either be a
square or a line, both types are defined however
many more could be made from this base class.
*/


import java.awt.Color;
import javax.vecmath.Vector2f;
import processing.core.PApplet;

public class Particle {
	
	//Particle variables
	Vector2f position;
	float life;
	float maxLife;
	boolean delete;
	Color color;
	PApplet parent;
	boolean line;
	float radius;
	float rotation;
	float rotationSpeed;
	float direction;
	float size;
	float speed;
	
	//Default constructor for making a square shaped particle
	public Particle(PApplet aParent, Vector2f aPosition, float aLife, Color aColor, float aSize)
	{
		//Basic setup
		position = aPosition;
		life = aLife;
		maxLife = aLife;
		delete = false;
		color = aColor;
		size = aSize;
		parent = aParent;
		//tell the class we are not a line
		line = false;
	}
	
	//Default constructor for making a line
	public Particle(PApplet aParent, Vector2f aCenterPoint, float aRadius, float aRotation, float aRotationSpeed, float aLife, float aDirection, float aSpeed)
	{
		
		position = aCenterPoint;
		life = aLife;
		maxLife = aLife;
		delete = false;
		parent = aParent;
		radius = aRadius;
		rotation = aRotation;
		rotationSpeed = aRotationSpeed;
		direction = aDirection;
		speed = aSpeed;
		//tell the class that we have a line
		line = true;
	}
	
	public void update(double aDelta)
	{
		//subtract time alive from current life
		life -= aDelta;
		if (life <= 0)
		{
			//if we have no life then
			//set our delete flag
			delete = true;
		}
		
		
		//if we are a line particle
		if (line)
		{
			//rotate the particle based on rotate speed
			rotation += rotationSpeed * aDelta;
			//update its position with its told direction with a speed
			//of 60 pixel/s
			position = new Vector2f(position.x + (float)((Math.cos(Math.toRadians(direction)) * speed) * aDelta), 
									position.y + (float)((Math.sin(Math.toRadians(direction)) * speed) * aDelta));
		}
	}
	
	public void draw()
	{
		
		//Get current life and set the color with an alpha
		//percent of life to maxLife end. This makes our nice alpha out
		//Instead of an abrupt end to the smoke it dissiapates.
		if (Main.useColor && !line)
			parent.fill(color.getRed(), color.getGreen(), color.getBlue(), (life/maxLife)*255);				
		else
		{
			parent.fill(66);
		}
		//if we are not a line then render our rectangle
		if (!line)
			parent.rect(position.x, position.y, size, size);
		
		//if we are a line then lets do some math.
		if (line)
		{
			//We create new points from the center point of our line to our radius
			//at both the current angle and 180 degrees in the other direction
			//this creates out 2 points to make our line
			Vector2f point1 = new Vector2f((float)(position.x + (Math.cos(Math.toRadians(rotation)) * radius)), (float)(position.y + (Math.sin(Math.toRadians(rotation)) * radius)));
			Vector2f point2 = new Vector2f((float)(position.x + (Math.cos(Math.toRadians(rotation+180)) * radius)), (float)(position.y + (Math.sin(Math.toRadians(rotation+180)) * radius)));
			//tell processing to render our line
			parent.line(point1.x, point1.y, point2.x, point2.y);
		}
		
	}
}
