package main;

import java.awt.Image;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
       JFrame window= new JFrame("Simple chess");
       
       //load icon
       try {
               Image icon = ImageIO.read(main.class.getResource("/piece/icon.jpg"));
               window.setIconImage(icon);
        
       } catch (Exception e) {
           System.out.println("Icon loading failed!");
           e.printStackTrace();
       }

       window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//       window.setResizable(false);
     
       
       // Add GamePanel to the window
       GamePanel gp= new GamePanel();
         window.add(gp);
         JButton saveBtn = new JButton("Save");
         JButton loadBtn = new JButton("Load");
       
         window.pack();
         
         window.setLocationRelativeTo(null);
         window.setVisible(true);
         
         
         gp.launchGame();
         
	}

}
