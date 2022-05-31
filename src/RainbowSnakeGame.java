import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;


public class RainbowSnakeGame
{

    public static void main(String[] args)
    {
    	String osName = System.getProperty("os.name").toLowerCase();
		if (osName.startsWith("mac os x")) 
		{
			//Makes Command+Q activate the windowClosing windowEvent
			System.setProperty("apple.eawt.quitStrategy","CLOSE_ALL_WINDOWS");
		}
    	
    	Game game = new Game();
		
		game.setSize(1000+8,650+30);
		game.setResizable(false);
		game.setVisible(true);
				
		game.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
			public void componentShown(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
		
		game.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
    }
}

@SuppressWarnings("serial")
class Game extends JFrame implements MouseListener, MouseMotionListener, KeyListener {
	
	static Image offscreen = null;
    static Graphics g2;
	static BufferedImage backgroundPic = null;
    static Graphics bg;
    int offsetX = 8;
    int offsetY = 30;
    int screenWidth = 1000+offsetX;
    int screenHeight = 650+offsetY;
    
    boolean up = false;
	boolean down = false;
	boolean left = false;
	boolean right = false;
    
    int mousexClick = -100;
	int mouseyClick = -100;
	int mousexHover = -100;
	int mouseyHover = -100;
	boolean mousePressed = false;
	Random r = new Random();
	//////////////////////////////
	
	int worldSize = 25;//25;
	double gameSpeedMultiplier = 1.0;//effectively game speed control
	int posx = screenWidth/worldSize/2;
	int posy = screenHeight/worldSize/2;
	int worldW = screenWidth/worldSize;
	int worldH = screenHeight/worldSize;
	int[][] world = new int[worldW][worldH];
	boolean gameOver = false;
	int length = 1;
	boolean food = false;
	ArrayList<Block> snake = new ArrayList<Block>();
	int shift = 75;
	boolean flip = false;
	boolean easy = true;
	int counter = 0;
	
	public Game()
	{
		super("RainbowSnakeGame");
		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);
		
		//Prepare game
		genBackground();
	}
	
	/*** Main Methods ***/
	
	public void runGame() throws ArrayIndexOutOfBoundsException
	{
		g2.setColor(Color.black);
		g2.fillRect(0,0,screenWidth-offsetX,screenHeight-offsetY);
		if(posx > worldW || posx < 0 || posy > worldH || posy < 0)
		{
			gameOver = true;
		}
		else if(up)
		{
			world[posx][posy] = 0;
			posy -= 1;
			if(world[posx][posy] == 2)
			{
				length++;
				food = false;
			}
			world[posx][posy] = 1;
		}
		else if(down)
		{
			world[posx][posy] = 0;
			posy += 1;
			if(world[posx][posy] == 2)
			{
				length++;
				food = false;
			}
			world[posx][posy] = 1;
		}
		else if(left)
		{
			world[posx][posy] = 0;
			posx -= 1;
			if(world[posx][posy] == 2)
			{
				length++;
				food = false;
			}
			world[posx][posy] = 1;
		}
		else if(right)
		{
			world[posx][posy] = 0;
			posx += 1;
			if(world[posx][posy] == 2)
			{
				length++;
				food = false;
			}
			world[posx][posy] = 1;
		}
			
		snake.add(new Block(new Dimension(posx,posy),new Color(r.nextInt(255),r.nextInt(255),r.nextInt(255))));
		
		if(snake.size() > length)
			snake.remove(0);

		g2.drawImage(backgroundPic,0,0,null);
		
		for(int k = 0; k < worldW; k++)
		{
			for(int j = 0; j < worldH; j++)
			{
				if(world[k][j] == 0)
				{
					if(!food)
					{
						if(r.nextInt(worldW*worldH*10) == 0)
						{
							world[k][j] = 2;
							food = true;
						}
					}
				}
				
				if(world[k][j] == 2)
				{
					g2.setColor(new Color(r.nextInt(255),r.nextInt(255),r.nextInt(255)));
					g2.fillRect(k*worldSize,j*worldSize,worldSize,worldSize);
				}
			}
		}
		
		for(int k = 0; k < snake.size(); k++)
		{
			g2.setColor(snake.get(k).color);
			g2.fillRect((int)snake.get(k).dim.getWidth()*worldSize,(int)snake.get(k).dim.getHeight()*worldSize,worldSize,worldSize);
			
			if(snake.get(k).dim.getWidth() == posx && snake.get(k).dim.getHeight() == posy && k != snake.size()-1)
			{
				gameOver = true;
			}
		}
		
		if(counter*5 < 255 && !(up || down || left || right))
		{
			String difficulty = "";
			
			if(easy)
				difficulty = "Easy";
			else
				difficulty = "Hard";
			
			g2.setColor(new Color(255,255,255,255-(counter*5)));
			g2.setFont(new Font("Ariel",Font.BOLD,50));
			g2.drawString("SPACEBAR TO TOGGLE DIFFICULTY",50,300);
			g2.drawString("ARROW KEYS TO BEGIN",200,410);
			g2.drawString("Difficulty: "+difficulty,200,520);
		}
		
		world[posx][posy] = 1;
		
		if(easy)
		{
			shift = 50;
		}
		else
		{
			if(shift >= 135)
			{
				shift = 135;
				flip = true;
			}
			else if(shift <= 50)
			{
				shift = 50;
				flip = false;
			}
		
			if(flip)
				shift -= 1;
			else
				shift += 1;
		}
		
		genBackground();
		counter++;
	}
	
	public void genBackground()
	{
		backgroundPic = new BufferedImage(screenWidth,screenHeight,BufferedImage.TYPE_INT_ARGB);
		bg = backgroundPic.getGraphics();
		
		for(int k = 0; k < worldW; k++)
		{
			for(int j = 0; j < worldH; j++)
			{
				bg.setColor(new Color(r.nextInt(shift),r.nextInt(shift),r.nextInt(shift)));
				bg.fillRect(k*worldSize,j*worldSize,worldSize,worldSize);
			}
		}
	}
	
	/*** No-touch/Util Methods ***/
	
	public void update(Graphics g)
	{
		offscreen = createImage(screenWidth,screenHeight);
		g2 = offscreen.getGraphics();
		g2.translate(offsetX,offsetY);
		
		try{
			runGame();
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			gameOver = true;
		}
		
		g.drawImage(offscreen,0,0,this);
		
		for(int k = 0; k < (int)((worldSize*2.68f)/gameSpeedMultiplier); k++)//separate to enable picking up the keyboard easier
			delay(1);										  				 //delay length is relative to world size
		
		if(!gameOver)
			repaint();
		else
		{
			for(int k = 0; k < 100; k++)
			{
				if(k % (int)(((worldSize*2.68f)/gameSpeedMultiplier)/10) == 0)
					genBackground();
				g2.drawImage(backgroundPic,0,0,null);
				g2.setColor(new Color(0,0,0,(int)(2.5*k)));
				g2.fillRect(0,0,screenWidth,screenHeight);
				g2.setColor(Color.white);
				g2.setFont(new Font("Ariel",Font.BOLD,50));
				g2.drawString("GAME OVER",300,350);
				g.drawImage(offscreen,0,0,this);
				delay(10);
			}
			System.exit(0);
		}
	}
	
	public void paint(Graphics g)
	{
		update(g);
	}
	
	public void delay(long delay)
	{
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void mouseDragged(MouseEvent e) 
	{
		mousexClick = mousexHover = e.getX();
		mouseyClick = mouseyHover = e.getY();
	}
	
	public void keyPressed(KeyEvent e) 
	{
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_SPACE:
			{
				easy = !easy;
				break;
			}
			case KeyEvent.VK_UP:
			{
				up = true;
				down = false;
				left = false;
				right = false;
				break;
			}
			case KeyEvent.VK_DOWN:
			{
				up = false;
				down = true;
				left = false;
				right = false;
				break;
			}
			case KeyEvent.VK_LEFT:
			{
				up = false;
				down = false;
				left = true;
				right = false;
				break;
			}
			case KeyEvent.VK_RIGHT:
			{
				up = false;
				down = false;
				left = false;
				right = true;
				break;
			}
		}
	}

	public void mouseMoved(MouseEvent e) 
	{
		mousexHover = e.getX();
		mouseyHover = e.getY();
	}
	
	public void mousePressed(MouseEvent e) 
	{
		mouseDragged(e);
		mousePressed = true;
	}
	
	public void mouseClicked(MouseEvent e) 
	{
		mouseDragged(e);
	}

	public void mouseReleased(MouseEvent e) 
	{
		mousePressed = false;
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void keyTyped(KeyEvent e) {}

	public void keyReleased(KeyEvent e) {}
}

class Block
{
	public Color color = Color.black;
	public Dimension dim = new Dimension();
	
	public Block(Dimension dim, Color color)
	{
		this.dim = dim;
		this.color = color;
	}
}




