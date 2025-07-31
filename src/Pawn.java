package piece;

import main.GamePanel;
import main.Type;

public class Pawn extends Piece {

	public Pawn(int color, int col, int row) {
		super(color, col, row);
		type = Type.PAWN;
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/wp");
		}
		else {
			image = getImage("/piece/bp");
		}
	
	}

	public boolean canMove(int targetCol , int targetRow) {
		if(isWithinBoard(targetCol , targetRow) && isSameSquare(targetCol , targetRow) == false) {
			//DEfine the move value if it is black or white
			int moveValue;
			if(color == GamePanel.WHITE) {
				moveValue = -1;
			}
			else {
				moveValue = 1;
			}
			
			// Check the hitting piece
			hittingP = getHittingP(targetCol , targetRow);
			
			// 1 square movment
			if(targetCol == preCol && targetRow == preRow + moveValue && hittingP == null) {
				return true;
			}
			
			// 2 square movment
			if(targetCol == preCol && targetRow == preRow + moveValue*2 && hittingP == null && 
					moved == false && pieceIsOnStrightLine(targetCol , targetRow)== false) {
				return true;
			}
			
           // Diagonal movment & capture(if there is an oponents piece or not)
			if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && 
					hittingP != null && hittingP.color != color){
						return true;
					}
			//Em passant
			if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue) {
				for(Piece p : GamePanel.simpieces) {
					if(p.col == targetCol && p.row == preRow && p.twoStepped == true) {
						hittingP = p;
						return true;
						
					}
				}
			}
			
		}
			
		return false;
	}

}
