package org.leozis.hw3;

import java.util.Set;
import org.leozis.hw2.StateChangerImpl;
import org.shared.chess.Color;
import org.shared.chess.GameResult;
import org.shared.chess.Move;
import org.shared.chess.Piece;
import org.shared.chess.PieceKind;
import org.shared.chess.Position;
import org.shared.chess.State;
import org.shared.chess.StateChanger;
import org.shared.chess.StateExplorer;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;

public class Presenter {
	public interface View {
		/**
		 * Renders the piece at this position.
		 * If piece is null then the position is empty.
		 */
		void setPiece(int row, int col, Piece piece);
		/**
		 * Turns the highlighting on or off at this cell.
		 * Cells that can be clicked should be highlighted.
		 */
		void setHighlighted(int row, int col, boolean highlighted);
		/**
		 * Indicate whose turn it is.
		 */
		void setWhoseTurn(Color color);
		/**
		 * Indicate whether the game is in progress or over.
		 */
		void setGameResult(GameResult gameResult);
		/**
		 * Indicate which piece is selected
		 */
		void setSelected(int row, int col, boolean selected);

		void setPromoteVisible(boolean b);
		
		void setHistory(String hist);
	}

	private State state;
	private StateChanger stateChanger;
	private StateExplorer stateExplorer;
	private Set<Move> possibleMovesFromPosition;
	private Position selected;
	private Position pawnPromotedToPosition;
	private View view;

	public Presenter(State state, StateChanger stateChanger, StateExplorer stateExplorer){
		this.state = state;
		this.stateChanger = stateChanger;
		this.stateExplorer = stateExplorer;
	}

	public Position getPawnPromotedToPosition() {
		return pawnPromotedToPosition;
	}

	public void setPawnPromotedToPosition(Position pawnPromotedToPosition) {
		this.pawnPromotedToPosition = pawnPromotedToPosition;
	}

	public void setView(View view) {
		this.view = view;
	}

	public void setSelected(Position p){
		this.selected = p;
	}

	public Position getSelected(){
		return this.selected;
	}
	
	
	
	/**
	 * Respond to when a grid cell is clicked on.
	 * @param row
	 * @param col
	 */
	public void clickedOn(int row, int col) {		
		int invertR = 7 - row;
		//if a pawn needs to be promoted, we ignore clicks on the board
		if(this.getPawnPromotedToPosition() == null){
			//A piece has been selected
			if(this.getSelected() == null){
				possibleMovesFromPosition = stateExplorer.getPossibleMovesFromPosition(state, new Position(invertR,col));

				if(!possibleMovesFromPosition.isEmpty()){
					for(Move m : possibleMovesFromPosition){
						view.setHighlighted(7 - m.getTo().getRow(), m.getTo().getCol(), true);
					}
					this.setSelected(new Position(invertR,col));
					view.setSelected(row, col, true);
				}
				//A "to" location has been chosen for the piece	
			}else{
				Piece selectedPiece = this.state.getPiece(this.getSelected());
				//if a pawn needs to be promoted, we do not make a move yet.
				//this checks if it is a legal promote move
				if(selectedPiece.getKind().equals(PieceKind.PAWN) &&
						invertR == (selectedPiece.getColor().isWhite() ? 7 : 0) &&
						((StateChangerImpl) stateChanger).isPossibleMove(state,
								new Move(this.getSelected(),new Position(invertR,col),PieceKind.QUEEN))){
					view.setPromoteVisible(true);
					this.setPawnPromotedToPosition(new Position(invertR,col));
				}else{ //if it is a non promote move, we simply see if it is legal and make the move.
					Move m = new Move(this.getSelected(),new Position(invertR,col),null);
					if(possibleMovesFromPosition.contains(m)){
						stateChanger.makeMove(state, m);
						this.setState(state);
						view.setHistory(StateEncoder.encode(state));
					}
					this.setSelected(null);
					this.clearHighlighted();
					view.setSelected(7 - m.getFrom().getRow(),m.getFrom().getCol(),false);
				}
			}
		}
	}

	/**
	 * Respond to when a promote cell is clicked on. Promote grid only should appear when
	 * a pawn needs to be promoted.
	 * @param col
	 */
	public void clickedOnPromote(int col) {

		PieceKind pk = null;

		switch(col){
		case 0: pk = PieceKind.QUEEN; break;
		case 1: pk = PieceKind.ROOK; break;
		case 2: pk = PieceKind.KNIGHT; break;
		case 3: pk = PieceKind.BISHOP; break;
		}

		Move m = new Move(this.getSelected(),this.getPawnPromotedToPosition(),pk);
		if(possibleMovesFromPosition.contains(m)){
			stateChanger.makeMove(state, m);
			this.setState(state);
			view.setHistory(StateEncoder.encode(state));
		}

		this.setSelected(null);
		this.clearHighlighted();
		this.clearSelected();
		view.setPromoteVisible(false);
		this.setPawnPromotedToPosition(null);
	}

	public void setState(State state) {
		view.setWhoseTurn(state.getTurn());
		view.setGameResult(state.getGameResult());
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				int invertR = 7 - r;
				view.setPiece(invertR, c, state.getPiece(r, c));
			}
		} 
		this.state = state;
	}

	public void clearHighlighted(){
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				view.setHighlighted(r, c, false);
			}
		} 
	}
	
	public void clearSelected(){
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				view.setSelected(r, c, false);
			}
		} 
	}
}
