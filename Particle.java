package asteroids;

import java.awt.Color;

import javax.vecmath.Vector2f;

import processing.core.PApplet;

public class Particle {
	
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
	
	public Particle(PApplet aParent, Vector2f aPosition, float aLife, Color aColor)
	{
		position = aPosition;
		life = aLife;
		maxLife = aLife;
		delete = false;
		color = aColor;
		parent = aParent;
		line = false;
	}
	public Particle(PApplet aParent, Vector2f aCenterPoint, float aRadius, float aRotation, float aRotationSpeed, float aLife, float aDirection)
	{
		position = aCenterPoint;
		life = aLife;
		maxLife = aLife;
		delete = false;
		parent = aParent;
		line = true;
		radius = aRadius;
		rotation = aRotation;
		rotationSpeed = aRotationSpeed * 10;
		direction = aDirection;
	}
	
	public void update(double aDelta)
	{
		life -= aDelta;
		if (life <= 0)
		{
			delete = true;
		}
		
		
		
		if (line)
		{
			rotation += rotationSpeed * aDelta;
			position = new Vector2f(position.x + (float)((Math.cos(Math.toRadians(direction)) * 60) * aDelta), 
									position.y + (float)((Math.sin(Math.toRadians(direction)) * 60) * aDelta));
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
		if (!line)
			parent.rect(position.x, position.y, 5, 5);
		
		if (line)
		{
			Vector2f point1 = new Vector2f((float)(position.x + (Math.cos(Math.toRadians(rotation)) * radius)), (float)(position.y + (Math.sin(Math.toRadians(rotation)) * radius)));
			Vector2f point2 = new Vector2f((float)(position.x + (Math.cos(Math.toRadians(rotation+180)) * radius)), (float)(position.y + (Math.sin(Math.toRadians(rotation+180)) * radius)));
			parent.line(point1.x, point1.y, point2.x, point2.y);
		}
		
	}
}
