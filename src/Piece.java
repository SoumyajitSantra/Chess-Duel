package piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Board;
import main.GamePanel;
import main.Type;

public class Piece {
 
	public Type type;
	public BufferedImage image;
	public int x, y;
	public int col , row, preCol, preRow;
	public int color;
	public Piece hittingP;
	public boolean moved = false;
	public boolean twoStepped;
	
	public Piece(int color,int row , int col ) {
   
		this.color=color;
		this.row=row;
		this.col=col;
		x = getX(col);
		y = getY(row);
		preCol = col;
		preRow = row;
		
}
	public BufferedImage getImage(String imagePath) {   // access image
		
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
			
			}catch(IOException e) {
				System.out.println(e);
			}
		return image;
	}
	
	public int getX(int col) {                     // TO acess x point we need to mul with col;
		return col*Board.SQUARE_SIZE;
	}
	public int getY(int row) {
		return row*Board.SQUARE_SIZE;
	}
	
	// PIECE X AND Y CORDINATE 
	public int getCol(int x) {
		//in java it takes the to corner position as main 
		return (x + Board.HALF_SQUARE_SIZE )/Board.SQUARE_SIZE;
	}
	public int getRow(int y) {
		return (y + Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
	}
	public int getIndex() {
		for(int index = 0; index < GamePanel.simpieces.size(); index++) {
			if(GamePanel.simpieces.get(index) == this) {
				return index;
			}
		}
		return 0;
	}
	
	/// Update piece
	public void updatePosition() {
		
		//  cheacking Em passant
		if(type == Type.PAWN) {
			if(Math.abs(row - preRow) ==  2) {
				twoStepped = true;
			}
		}
		
		x = getX(col);
		y = getY(row);
		
		preCol = getCol(x);                        // *2
		preRow = getRow(y);
		moved = true;
	}
	public void resetPosition() {
		col = preCol;
		row = preRow;
		x = getX(col);
		y = getY(row);
	}
	public boolean canMove(int targetCol , int targetRow) {
		return false;
	}
	public boolean isWithinBoard(int targetCol ,int targetRow) {
		if(targetCol >= 0 && targetCol <= 7 && targetRow >=0 && targetRow <= 7) {
			return true;
		}
		return false;
	}
	public boolean isSameSquare(int targetCol , int targetRow) {
		if(targetCol == preCol && targetRow == preRow) {
			return true;
		}
		return false ;
	}
	public Piece getHittingP(int targetCol , int targetRow) {
		for(Piece p : GamePanel.simpieces) {
			if(targetCol == p.col && targetRow == p.row && p != this) {
				return p;
			}
			
		}
		return null;
}
	public boolean isValidSquare(int targetCol , int targetRow) {
		
		hittingP = getHittingP(targetCol , targetRow);
		
		if(hittingP == null) {   // square is vacent
			return true;
		}
		else {                   // square is occupied
			if(hittingP.color != this.color) {
				return true;
			}
			else {
				 hittingP =null;
				 return false;
			}
			
		}
		
	}
	public boolean pieceIsOnStrightLine(int targetCol , int targetRow) {
		//when the piece is moving to the left
		for(int c = preCol-1; c > targetCol; c--) {
			for(Piece p: GamePanel.simpieces) {
				if(p.col == c && p.row == targetRow) {
					hittingP = p;
					return true;
				}
			}

}
		//when the piece is moving to the Right
		for(int c = preCol+1; c < targetCol; c++) {
			for(Piece p: GamePanel.simpieces) {
				if(p.col == c && p.row == targetRow) {
					hittingP = p;
					return true;
				}
			}

}
		//when the piece is moving to the Top
		for(int r = preRow-1; r > targetRow; r--) {
			for(Piece p: GamePanel.simpieces) {
				if(p.col == targetCol && p.row == r) {
					hittingP = p;
					return true;
				}
			}

}
		//when the piece is moving to the Down
		for(int r = preRow+1; r < targetRow; r++) {
			for(Piece p: GamePanel.simpieces) {
				if(p.col == targetCol && p.row == r) {
					hittingP = p;
					return true;
				}
			}

}
			return false;
	}
	public boolean pieceIsOnDiagonalLine( int targetCol , int targetRow){
		
	if(targetRow < preRow) {
		//when piece move to the left_top
		for(int c = preCol-1; c > targetCol; c--) {
			int diff = Math.abs(c - preCol);
			for(Piece p: GamePanel.simpieces) {
				if(p.col == c && p.row == preRow -diff) {
					hittingP = p;
					return true;
				}
			}
		}
		//when piece move to the right_top
			for(int c = preCol+1; c < targetCol; c++) {
				int diff = Math.abs(c - preCol);
				for(Piece p: GamePanel.simpieces) {
					if(p.col == c && p.row == preRow - diff) {
						hittingP = p;
						return true;
				}
			}
	    }
	}
		if(targetRow > preRow ) {
			//when piece move to the left_Down
			for(int c = preCol-1; c > targetCol; c--) {
				int diff = Math.abs(c - preCol);
				for(Piece p: GamePanel.simpieces) {
					if(p.col == c && p.row == preRow + diff) {
						hittingP = p;
						return true;
					}
				}
			}
	    
		//when piece move to the right_Down
			for(int c = preCol+1; c < targetCol; c++) {
				int diff = Math.abs(c - preCol);
				for(Piece p: GamePanel.simpieces) {
					if(p.col == c && p.row == preRow + diff) {
						hittingP = p;
						return true;
				}
			}
	    }
	}
		
		return false;
	   
	}
	public void draw(Graphics2D g2) {
		g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
	}
}
