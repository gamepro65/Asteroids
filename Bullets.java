package asteroids;

import processing.core.PApplet;

public class Bullets {
	
	double bulletx, bullety, crosshairx, crosshairy;
	double speedx, speedy, bulletLife = 60;
	PApplet parent;
	
	public Bullets(PApplet papp, int x, int y, int crosshairx, int crosshairy, int speed)
	{
		parent = papp;
		bulletx = x;
		bullety = y;
		//finds missing sides and angles
		double xdis = crosshairx - x;
		double ydis = crosshairy - y;
		double angleAradians = Math.atan2(ydis,xdis);
		double rotation = Math.toDegrees(angleAradians);
		//creates the points to add to next frame
		speedx = (float)(speed * 60 * Math.cos(Math.toRadians(rotation)));
		speedy = (float)(speed * 60 * Math.sin(Math.toRadians(rotation)));
		
		
	}
	public void update(double aDelta)
	{
		bulletx += speedx * aDelta;
		bullety += speedy * aDelta;
	}
	
	public void draw()
	{
		parent.fill(155);
		parent.rect((float)bulletx, (float)bullety, 3, 3);
		
	}

}
