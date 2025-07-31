package piece;

import main.GamePanel;
import main.Type;

public class King extends Piece {
 
	
	public King(int color, int col, int row) {
		super(color, col, row);
		
		type = Type.KING;
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/wk");
		}
		else {
			image = getImage("/piece/bk");
		}
	
	}
	public boolean canMove(int targetCol , int targetRow) {
		if(isWithinBoard(targetCol , targetRow)){
			
			if(Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1 ||
					Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) ==1){
				if(isValidSquare(targetCol , targetRow)) {
				return true;
				}
			}
			// castling
			if(moved == false) {
				// Right castling
				if(targetCol == preCol+2 && targetRow == preRow && 
						pieceIsOnStrightLine(targetCol , targetRow)== false) {
					for(Piece p:GamePanel.simpieces) {
						if(p.col == preCol+3 && p.row == preRow && moved == false) {
							GamePanel.castlingP = p;
							return true;
						}
					}
					
				}
				// Left castling
                if(targetCol == preCol-2 && targetRow == preRow &&
                		pieceIsOnStrightLine(targetCol , targetRow)== false) {
                	
                	Piece p1[]=new Piece[2];
                	// checking if the left rook right positin is vacent or not
                	for(Piece p:GamePanel.simpieces) {
                		if(p.col == preCol-3 && p.row == preRow) {
                			p1[0]=p;
                		}
                		if(p.col == preCol-4 && p.row == preRow) {
                			p1[1]=p;
                		}
                	}
                	if(p1[0]== null && p1[1] !=null && p1[1].moved == false) {
                		GamePanel.castlingP =p1[1];
                		return true;
                	}
                	
                }
			}
		}
		return false;
	}

	

}
