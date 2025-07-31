package piece;

import main.GamePanel;
import main.Type;

public class Rook extends Piece {

	public Rook(int color, int col, int row) {
		super(color, col, row);
		
		type = Type.ROOK;
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/wr");
		}
		else {
			image = getImage("/piece/br");
		}
	
	}
	public boolean canMove(int targetCol , int targetRow) {
		if(isWithinBoard(targetCol , targetRow)) {
		//Rook can go as long either its col or row is same
			if(targetCol == preCol || targetRow == preRow) {
				if(isValidSquare(targetCol , targetRow)) {
					
				if(isSameSquare(targetCol , targetRow) == false && pieceIsOnStrightLine(targetCol , targetRow)== false) {
					return true;
				}
				}
			}
		}
		return false;
	}

	

}
