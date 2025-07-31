package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.Timer;
import java.awt.Cursor;


import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

public class GamePanel extends JPanel implements Runnable{

	public static final int WIDTH =1100;
	public static final int HEIGHT =800;
	final  int FPS=60;
	Thread gameThread;
	Board board = new Board();
	Mouse mouse = new Mouse();
	
	// COLOR
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	int currentColor=WHITE;
	
	// Pieces
	public static ArrayList<Piece> pieces = new ArrayList<>();
	public static ArrayList<Piece> simpieces = new ArrayList<>();
	public static ArrayList<Piece> promoPieces = new ArrayList<>();
	
	Piece activeP , checkingP;
	public static Piece castlingP;
	
	// BOOLEANS
	boolean canMove;
	boolean validSquare;
	boolean promotion;
	boolean gameover;
	boolean stalemate;
	
	//timer
	private Timer whiteTimer, blackTimer;
	private static int whiteTimeLeft;
	private static int blackTimeLeft;
	private boolean useTimer;


	
	public GamePanel () {
		// main panel size declaration
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		setBackground(Color.black);
		setBorder(BorderFactory.createLineBorder(Color.cyan,4));
		
		//asking player timer need or not if needed then add time
		  useTimer = JOptionPane.showConfirmDialog(null, "Play with timer?", "Timer", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

		int timeLimit = 0;
		if (useTimer) {
		    String input = JOptionPane.showInputDialog("Enter time per player (in seconds):");
		    try {
		        timeLimit = Integer.parseInt(input);
		    } catch (NumberFormatException e) {
		        JOptionPane.showMessageDialog(null, "Invalid number! Defaulting to 300 seconds.");
		        timeLimit = 300;
		    }
		}
		 whiteTimeLeft = timeLimit;
		 blackTimeLeft = timeLimit;
		

		setPieces();
		copyPieces(pieces,simpieces);
		
		//mouse
	    addMouseMotionListener(mouse);
	    addMouseListener(mouse);
	    
	    setLayout(null); // Required to use setBounds()

	    
	    //button
	    //Save button
	    JButton saveButton = new JButton("Save");
	     saveButton.setBackground(new Color(255, 87, 34));  //orange
	     saveButton.setForeground(Color.BLACK);
	     saveButton.setFont(new Font("Arial", Font.BOLD, 16));
	     saveButton.setFocusPainted(false);
	     saveButton.setBorderPainted(false);
	     saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
	     saveButton.setBounds(833, 740, 100, 40);
	     saveButton.addActionListener(e -> {
	    	    DatabaseManager.saveGame(pieces, currentColor, whiteTimeLeft, blackTimeLeft, useTimer); 
	    	});
	    add(saveButton);

	    //Load button
	    JButton loadButton = new JButton("Load");
	    loadButton.setBackground(new Color(50, 205, 50)); //green
	    loadButton.setForeground(Color.BLACK);
	    loadButton.setFont(new Font("Arial", Font.BOLD, 16));
	    loadButton.setFocusPainted(false);
	    loadButton.setBorderPainted(false);
	    loadButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    loadButton.setBounds(966, 740, 100, 40);
	    loadButton.addActionListener(e -> {
	        int[] colorRef = new int[1];
	        ArrayList<Piece> loaded = DatabaseManager.loadGame(colorRef, useTimer);
	        if (!loaded.isEmpty()) {
	            pieces = loaded;
	            simpieces.clear();
	            simpieces.addAll(pieces);
	            currentColor = colorRef[0];
	            activeP = null;
	            promotion = false;
	            gameover = false;
	            stalemate = false;
	            repaint();
	        }
	    });

	    add(loadButton);
	    
	    
	    //Timer button
	    JButton pauseButton = new JButton("Pause");
	    pauseButton.setForeground(Color.BLACK);
	    pauseButton.setFont(new Font("Arial", Font.BOLD, 16));
	    pauseButton.setFocusPainted(false);
	    pauseButton.setBorderPainted(false);
	    pauseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    pauseButton.setBounds(833, 40, 100, 40);
	    pauseButton.addActionListener(e -> {
	        if (currentColor == 0) whiteTimer.stop();
	        else blackTimer.stop();
	    });
	    

	    JButton resumeButton = new JButton("Resume");
	    resumeButton.setForeground(Color.BLACK);
	    resumeButton.setFont(new Font("Arial", Font.BOLD, 16));
	    resumeButton.setFocusPainted(false);
	    resumeButton.setBorderPainted(false);
	    resumeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    resumeButton.setBounds(966, 40, 100, 40);
	    resumeButton.addActionListener(e -> {
	        if (currentColor == 0) whiteTimer.start();
	        else blackTimer.start();
	    });
	    
	    if(useTimer) {
	    add(pauseButton);
	    add(resumeButton);
	    }


			
		
	}
	
	public void launchGame() {
		if(useTimer) {
			startTimers();
			
		}
		gameThread = new Thread(this);         //*1
		gameThread.start();
		
		
	}
	
	public void setPieces() {
		
		//white team
		pieces.add(new Pawn(WHITE,6,0));
		pieces.add(new Pawn(WHITE,6,1));
		pieces.add(new Pawn(WHITE,6,2));
		pieces.add(new Pawn(WHITE,6,3));
		pieces.add(new Pawn(WHITE,6,4));
		pieces.add(new Pawn(WHITE,6,5));
		pieces.add(new Pawn(WHITE,6,6));
		pieces.add(new Pawn(WHITE,6,7));
		pieces.add(new Rook(WHITE,7,0));
		pieces.add(new Rook(WHITE,7,7));
		pieces.add(new Knight(WHITE,7,1));
		pieces.add(new Knight(WHITE,7,6));
		pieces.add(new Bishop(WHITE,7,2));
		pieces.add(new Bishop(WHITE,7,5));
		pieces.add(new Queen(WHITE,7,3));
		pieces.add(new King(WHITE,7,4));
	
		
		//Black team
		pieces.add(new Pawn(BLACK,1,0));
		pieces.add(new Pawn(BLACK,1,1));
		pieces.add(new Pawn(BLACK,1,2));
		pieces.add(new Pawn(BLACK,1,3));
		pieces.add(new Pawn(BLACK,1,4));
		pieces.add(new Pawn(BLACK,1,5));
		pieces.add(new Pawn(BLACK,1,6));
		pieces.add(new Pawn(BLACK,1,7));
		pieces.add(new Rook(BLACK,0,0));
		pieces.add(new Rook(BLACK,0,7));
		pieces.add(new Knight(BLACK,0,1));
		pieces.add(new Knight(BLACK,0,6));
		pieces.add(new Bishop(BLACK,0,2));
		pieces.add(new Bishop(BLACK,0,5));
		pieces.add(new Queen(BLACK,0,3));
		pieces.add(new King(BLACK,0,4));
		
		
	}
	private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
		target.clear();
		for(int i=0; i<source.size(); i++) {
		  target.add(source.get(i));
		}
		
	}
	@Override
	public void run() {
		
		// Game Loop
		double  drawInterval = 1000000000/FPS;
		double delta =0;
		long lastTime =System.nanoTime();
		long currentTime;
		
		while(gameThread != null) {
			
			currentTime = System.nanoTime();
			
			delta += (currentTime - lastTime)/drawInterval;
			lastTime = currentTime;
			
			if(delta >= 1) {
				update();
				repaint();
				delta--;
			}
			
		}

		
	}
	

	private void update() {
		
		//promotion 
		if(promotion) {
			promoting();
		}
		else if(gameover == false && stalemate == false){
			// MOUSE BUTTON PRESSED //
			if(mouse.pressed) {
				if(activeP == null) {
					// IF the  activeP is null, Check if you can pick up a piece  
					
					for(Piece p :simpieces) {
						// check if the mouse is in any piece then it up as the activeP
						
					   if(p.color == currentColor &&
							   p.col == mouse.x/Board.SQUARE_SIZE &&
							   p.row == mouse.y/Board.SQUARE_SIZE) {
						   activeP = p;
						   
					   }
					}
				}
				else {
					// if the player is holding a piece , simulate the  move
					simulate();
					
				}
			}
			
			/// Mouse button released  ///
			if(mouse.pressed == false) {
				if(activeP != null) {
					
					if(validSquare) {
						
						//MOVE confirmed
						
						// update the piece list in case a piece has been captured and removed during the simulation
						copyPieces(simpieces, pieces);
					activeP.updatePosition();
					if(castlingP != null) {
						castlingP.updatePosition();
					}
					
					//checking if king is in check
					if(IsKingInCheck() && isCheckmate()) {
						gameover = true;
					}
					else if(isStalemate() && IsKingInCheck()==false) {
						stalemate = true;
					}
					
					else {//the game is still going on 
						if(canPromote()) {
							promotion = true;
						}
						else {
							 changePlayer();
						}
					}
				}
					else {
						// The move is not valid so reset everything
						copyPieces(pieces,simpieces);
					activeP.resetPosition();
					activeP = null;
					}
				}
			}
			
		}
		
	}
	private void simulate() {
		
		canMove = false;
		validSquare = false;
		//Reset the pieces list in every loop
		// this is basically fore restoring the removed piece during the simulation
		// every time the gameloop run the simpiceses copy element  from the pieces
		copyPieces(pieces,simpieces);
		
		//Reset the castling pieces position
		if(castlingP != null) {
			castlingP.col = castlingP.preCol;
			castlingP.x = castlingP.getX(castlingP.col);
			castlingP = null;
		}
		
		//if a piece is being  held, update its position
		// in there left top corner point is (0,0), for mouse pointer at the middle of piece
		activeP.x=mouse.x - Board.HALF_SQUARE_SIZE;
		activeP.y=mouse.y - Board.HALF_SQUARE_SIZE;
		
		activeP.col=activeP.getCol(activeP.x);
		activeP.row=activeP.getRow(activeP.y);
		
		
		// check if the piece is  hovering  over a rechable square or not
		if(activeP.canMove(activeP.col , activeP.row)) {
			canMove = true;
			
			// if hitting piece, remove it from reachable square
			if(activeP.hittingP != null) {
				simpieces.remove(activeP.hittingP.getIndex());
			}
			checkCastling();
			
			if(isIllegal(activeP) == false && opponentCanCaptureKing() == false) {
				validSquare = true;
			}
			
		}
		
	}
	// when king moves  to the  worng box
	private boolean  isIllegal(Piece king) {
		if(king.type ==Type.KING) {
			for(Piece p: simpieces) {
				if(p.color != king.color && p.canMove(king.col,king.row) && p != king ) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean opponentCanCaptureKing() {
		Piece king = getKing(false);
		
		for(Piece p: simpieces) {
			if(p.color != king.color && p.canMove(king.col, king.row)){
				return true;
			}
		}
		return false;
	}
	
	private boolean IsKingInCheck() {
		Piece king = getKing(true);
		
		if(activeP.canMove(king.col , king.row)) {
			checkingP = activeP;
			return true;
		}
		else {
			checkingP = null;
		}
		return false;
	}
	
	private Piece getKing(boolean opponent){
		
		Piece king = null;
		for(Piece p : simpieces) {
			if(opponent) {
				if(p.type == Type.KING && p.color != currentColor) {
					king = p;
				}
			}
			else {
					if(p.type == Type.KING && p.color == currentColor) {
						king = p;
					}
			}
		}
		return king;
		
	}
	
	//for detect checkmate
     private  boolean isCheckmate(){
		
		Piece king = getKing(true);
		if(kingCanMove(king)) {
			return false;
		}
		else {
			
			// but  you still have a chance !!!!
			//check if you can block the attack with your piece
			// Check the position of the checking piece and the king in check
			int colDiff = Math.abs(checkingP.col - king.col);
			int rowDiff = Math.abs(checkingP.row - king.row);
			
			if(colDiff == 0) {
				// The checking piece is attacking vertically
				if(checkingP.row < king.row) {
					//The checking piece is above the king
					for(int row = checkingP.row; row < king.row; row++) {
						for(Piece piece : simpieces) {
							if(piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
								return false;
								
							}
						}
					}
				}
				if(checkingP.row > king.row) {
					//The checking piece is below the king
					for(int row = checkingP.row; row > king.row; row--) {
						for(Piece piece : simpieces) {
							if(piece != king && piece.color !=currentColor && piece.canMove(checkingP.col, row)) {
								return false;
								
							}
						}
					}
				}
			}
			else if(rowDiff == 0) {
				//the checking piece is attacking horizontally
				if(checkingP.col < king.col) {
					
					//the checking piece is to the left
					for(int col = checkingP.col; col < king.col; col++) {
						for(Piece piece : simpieces) {
							if(piece != king && piece.color !=currentColor && piece.canMove(col, checkingP.row)) {
								return false;
								
							}
						}
					}
				}
				if(checkingP.col > king.col) {
					//the checking piece is to the right
					for(int col = checkingP.col; col > king.col; col--) {
						for(Piece piece : simpieces) {
							if(piece != king && piece.color !=currentColor && piece.canMove(col, checkingP.row)) {
								return false;
								
							}
						}
					}
				}
				
			}
			else if(colDiff == rowDiff) {
				
				
				// The checking piece is attacking diagonally
				if(checkingP.row < king.row) {
					//the checking piece is above the king 
					
					if(checkingP.col < king.col) {
						//The checking piece is in the upper left
						for(int col = checkingP.col, row = checkingP.row; col < king.col; col++,row++) {
							for(Piece piece : simpieces) {
								if(piece != king && piece.color !=currentColor && piece.canMove(col,row)) {
									return false;
									
								}
						    }
					    }
					}
					if(checkingP.col > king.col) {
						//The checking piece is in the upper right
						for(int col = checkingP.col, row = checkingP.row; col > king.col; col--,row++) {
							for(Piece piece : simpieces) {
								if(piece != king && piece.color !=currentColor && piece.canMove(col,row)) {
									return false;
									
								}
						  }
					}
				}
					
				}
				
	            if(checkingP.row > king.row) {
	            	//the checking piece is below the king

					if(checkingP.col < king.col) {
						//The checking piece is in the lower left
						for(int col = checkingP.col, row = checkingP.row; col < king.col; col++,row--) {
							for(Piece piece : simpieces) {
								if(piece != king && piece.color !=currentColor && piece.canMove(col,row)) {
									return false;
								}
							}
						}
					}
					if(checkingP.col > king.col) {
						//The checking piece is in the lower right
						for(int col = checkingP.col, row = checkingP.row; col > king.col; col--,row--) {
							for(Piece piece : simpieces) {
								if(piece != king && piece.color !=currentColor && piece.canMove(col,row)) {
									return false;
									
								}
						   }
					   }
				   }
					
	            }
	            
			}
//			else {
//				//The checking piece is a knight
//				// no need
//			}
			
			
		}
		return true;
	}
	private boolean kingCanMove(Piece king) {
		//simulate if there is any square where the king can move
		if(isValidMove(king,-1,-1)) {return true;}
		if(isValidMove(king,0,-1)) {return true;}
		if(isValidMove(king,1,-1)) {return true;}
		if(isValidMove(king,-1,0)) {return true;}
		if(isValidMove(king,1,0)) {return true;}
		if(isValidMove(king,-1,1)) {return true;}
		if(isValidMove(king,0,1)) {return true;}
		if(isValidMove(king,1,1)) {return true;}
		
		return false;
		
	}
	private boolean isValidMove(Piece king,int colPlus,int rowPlus) {
		
		boolean isValidMove=false;
		//update the king's position for a second
		king.col+=colPlus;
		king.row+=rowPlus;
		
	if(king.canMove(king.col, king.row)) {
		if(king.hittingP!=null) {
			simpieces.remove(king.hittingP.getIndex());
		}
		if(isIllegal(king)==false) {
			isValidMove=true;
			
		}
	}
		
		//Reset the king's position and restore the removed piece
		king.resetPosition();
		copyPieces(pieces,simpieces);
		
		
		
		return isValidMove;
		
	}
	private boolean isStalemate() {
		
		int count = 0;
		//count  the number of pieces
		for(Piece piece : simpieces) {
			if(piece.color != currentColor) {
				count++;
			}
		}
		
		//if only one piece (the king) is left
		if(count == 1) {
			if(kingCanMove(getKing(true)) == false) {
				return true;
			}
		}
		return false;
	}
	private void checkCastling() {
		if(castlingP != null){
			//left castling , left rook
			if(castlingP.col == 0 && castlingP.row == 7) {
			    castlingP.col = castlingP.col+3;
			}
			//right rook
			if(castlingP.col == 7 && castlingP.row ==7) {
				castlingP.col = castlingP.col-2;
			}
			castlingP.x = castlingP.getX(castlingP.col);
		}
	}
	private void changePlayer() {
		if(currentColor == WHITE) {
			currentColor = BLACK;
			// reset all black pieces  twostepped status    // *4
			for(Piece p : pieces) {
				if(p.color == BLACK) {
					p.twoStepped = false;
				}
			}
		}
		else {
			currentColor = WHITE;
			// reset all White pieces  twostepped status
						for(Piece p : pieces) {
							if(p.color == WHITE) {
								p.twoStepped = false;
							}
						}
		}
		activeP = null;
		
		if (useTimer) {
		    if (currentColor == 0) {
		        blackTimer.stop();
		        whiteTimer.start();
		    } else {
		        whiteTimer.stop();
		        blackTimer.start();
		    }
		}

	}
	
private boolean canPromote() {
		
		if(activeP.type == Type.PAWN) {
			if(currentColor == WHITE && activeP.row == 0 || currentColor == BLACK && activeP.row  == 7) {
				promoPieces.clear();
				promoPieces.add(new Rook(currentColor,2,9));
				promoPieces.add(new Knight(currentColor,3,9));
				promoPieces.add(new Bishop(currentColor,4,9));
				promoPieces.add(new Queen(currentColor,5,9));
				
				return true;
				}
		
			
		}
	
		return false;
	}
	
public void promoting() {
	if(mouse.pressed) {
		for(Piece p: promoPieces) {
			if(p.col == mouse.x/Board.SQUARE_SIZE && p.row == mouse.y/Board.SQUARE_SIZE) {
				switch(p.type) {
				
				case ROOK :simpieces.add(new Rook(currentColor , activeP.row,activeP.col)); break;
				case BISHOP :simpieces.add(new Bishop(currentColor , activeP.row,activeP.col)); break;
				case KNIGHT :simpieces.add(new Knight(currentColor , activeP.row,activeP.col)); break;
				case QUEEN :simpieces.add(new Queen(currentColor , activeP.row,activeP.col)); break;
				default:break;
				}
				simpieces.remove(activeP.getIndex());
				copyPieces(simpieces , pieces);
				activeP = null;
				promotion = false;
				changePlayer();
				
			}
			
		}
	}
	
}
private void startTimers() {
    whiteTimer = new Timer(1000, e -> {
        whiteTimeLeft--;
        repaint();
        if (whiteTimeLeft <= 0) {
            JOptionPane.showMessageDialog(this, "White ran out of time!");
            gameover = true;
        }
    });

    blackTimer = new Timer(1000, e -> {
        blackTimeLeft--;
        repaint();
        if (blackTimeLeft <= 0) {
            JOptionPane.showMessageDialog(this, "Black ran out of time!");
            gameover = true;
        }
    });

    if (currentColor == 0) whiteTimer.start();
    else blackTimer.start();
}

public static void setTimerFromDB(int whiteTime, int blackTime) {
    whiteTimeLeft = whiteTime;
    blackTimeLeft = blackTime;
}



	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		//BOARD
		board.draw(g2);
		
		//PIECES
		for(Piece p : simpieces) {
			p.draw(g2);
		}
		
		//
		if(activeP != null) {          //*3
			if(canMove) {
				if(isIllegal(activeP) || opponentCanCaptureKing()) {
					
					g2.setColor(Color.red);
//					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.4f));
					g2.fillRect(activeP.col*Board.SQUARE_SIZE , activeP.row*Board.SQUARE_SIZE,
							Board.SQUARE_SIZE, Board.SQUARE_SIZE);
//					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
				}
				else {
					g2.setColor(Color.black);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.4f));
					g2.fillRect(activeP.col*Board.SQUARE_SIZE , activeP.row*Board.SQUARE_SIZE,
							Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
				}
			
			}
			
			//Draw the active piece in the end so it won't be hidden by the board or the square
 
			activeP.draw(g2);
		}
		
		// Turn heading
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    g2.setFont(new Font("Monospaced",Font.PLAIN,35));
	    g2.setColor(Color.white);
	    
	    if(promotion) {
	    	g2.drawString("Promote to:",820,150);
	    	for(Piece p:promoPieces){
	    		g2.drawImage(p.image, p.getX(p.col), p.getY(p.row),Board.SQUARE_SIZE ,Board.SQUARE_SIZE, null);
	    	}
	    }
	    else {
	    	 if(currentColor == WHITE) {
	 	    	g2.drawString("White's turn",820,600);
	 	    	if(checkingP != null && checkingP.color == BLACK) {
	 	    		g2.setColor(Color.red);
	 	    		g2.drawString("The white king",820,400);
	 	    		g2.drawString("is in check!",820,450);
	 	    		
	 	    	}
	 	    }
	 	    else {
	 	    	g2.drawString("Black's turn",820,200);
	 	    	if(checkingP != null && checkingP.color == WHITE) {
	 	    		g2.setColor(Color.red);
	 	    		g2.drawString("The Black king",820,400);
	 	    		g2.drawString("is in check!",820,450);
	 	    		
	 	    	}
	 	    }
	    }
	    
	    if(gameover) {
	    	String s="";
	    	if(currentColor == WHITE) {
	    		s="WHITE WINS";
	    	}
	    	else {
	    		s="BLACK WINS";
	    	}
	    	g2.setFont(new Font("Arial",Font.PLAIN,90));
	    	g2.setColor(Color.BLUE);
	    	g2.drawString(s,200,420);
	   
	    }
	    if(stalemate) {
	    	g2.setFont(new Font("Arial",Font.PLAIN,90));
	    	g2.setColor(Color.lightGray);
	    	g2.drawString("STALEMATE",200,420);
	    }
	    
	    
	    //Timer
	    if(useTimer) {
	    g2.setColor(Color.WHITE);
	    g2.setFont(new Font("Arial", Font.BOLD, 30));
	    g2.drawString("White Time: " + whiteTimeLeft + "s", 815, 450);
	    g2.drawString("Black Time: " + blackTimeLeft + "s", 815, 350);
	    }

	   
	}
	
	

}
