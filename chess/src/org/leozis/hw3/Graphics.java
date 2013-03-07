package org.leozis.hw3;

import org.leozis.hw3.Presenter.View;
import org.shared.chess.Color;
import org.shared.chess.GameResult;
import org.shared.chess.Piece;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Graphics extends Composite implements View {
	private static GameImages gameImages = GWT.create(GameImages.class);
	private static GraphicsUiBinder uiBinder = GWT.create(GraphicsUiBinder.class);

	interface GraphicsUiBinder extends UiBinder<Widget, Graphics> {
	}

	@UiField GameCss css;
	@UiField Label gameStatus;
	@UiField Grid gameGrid;
	@UiField Grid promoteGrid;
	private Image[][] board = new Image[8][8];
	private Image[] promote = new Image[4];

	private Presenter presenter;
	public Graphics(final Presenter presenter) {
		this.presenter = presenter;
		initWidget(uiBinder.createAndBindUi(this));
		gameGrid.resize(8, 8);
		gameGrid.setCellPadding(0);
		gameGrid.setCellSpacing(0);
		gameGrid.setBorderWidth(0);
		promoteGrid.resize(1, 4);
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				final Image image = new Image();
				board[row][col] = image;
				image.setWidth("100%");
				final int i = row; 
				final int j = col;
				image.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						presenter.clickedOn(i, j);
					}
				});
				gameGrid.setWidget(row, col, image);
			}
		}
		
		for (int i = 0; i < 4; i++) {
			final Image image = new Image();
			promote[i] = image;
			final int col = i;
			image.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					presenter.clickedOnPromote(col);
				}
			});

			promoteGrid.setWidget(0, i, image);
			promoteGrid.setVisible(false);
			
		}
		
		promote[0].setResource(gameImages.whiteQ());
		promote[1].setResource(gameImages.whiteR()); 
		promote[2].setResource(gameImages.whiteN()); 
		promote[3].setResource(gameImages.whiteB()); 
		
	}

	@Override
	public void setPiece(int row, int col, Piece p) {
		if (p == null) {
			if (this.isWhiteTile(row, col)) {
				board[row][col].setResource(gameImages.blackTile());
			} else {
				board[row][col].setResource(gameImages.whiteTile()); 
			}
			return;
		}
		switch (p.getKind()) {
		case KING:
			if (p.getColor().isWhite()) {
				if(this.isWhiteTile(row, col)){
					board[row][col].setResource(gameImages.whiteK_blkbg());				
				}else{
					board[row][col].setResource(gameImages.whiteK());
				}
			} else {				
				if(this.isWhiteTile(row, col)){
					board[row][col].setResource(gameImages.blackK_blkbg());
				}else{
					board[row][col].setResource(gameImages.blackK());
				}
			}
			break;
		case PAWN:
			if (p.getColor().isWhite()) {
				if(this.isWhiteTile(row, col)){
					board[row][col].setResource(gameImages.whiteP_blkbg());
				}else{
					board[row][col].setResource(gameImages.whiteP());
				}
			} else {
				if(this.isWhiteTile(row, col)){
					board[row][col].setResource(gameImages.blackP_blkbg());
				}else{
					board[row][col].setResource(gameImages.blackP());
				}
			}
			break;
		case ROOK:
			if (p.getColor().isWhite()) {
				if(this.isWhiteTile(row, col)){
					board[row][col].setResource(gameImages.whiteR_blkbg());
				}else{
					board[row][col].setResource(gameImages.whiteR());
				}
			} else {
				if(this.isWhiteTile(row, col)){
					board[row][col].setResource(gameImages.blackR_blkbg());
				}else{
					board[row][col].setResource(gameImages.blackR());
				}
			}
			break;
		case KNIGHT:
			if (p.getColor().isWhite()) {
				if(this.isWhiteTile(row, col)){
					board[row][col].setResource(gameImages.whiteN_blkbg());

				}else{
					board[row][col].setResource(gameImages.whiteN());
				}
			} else {
				if(this.isWhiteTile(row, col)){
					board[row][col].setResource(gameImages.blackN_blkbg());
				}else{
					board[row][col].setResource(gameImages.blackN());
				}
			}
			break;
		case BISHOP:
			if (p.getColor().isWhite()) {
				if(this.isWhiteTile(row, col)){
					board[row][col].setResource(gameImages.whiteB_blkbg());
				}else{
					board[row][col].setResource(gameImages.whiteB());
				}
			} else {
				if(this.isWhiteTile(row, col)){
					board[row][col].setResource(gameImages.blackB_blkbg());
				}else{
					board[row][col].setResource(gameImages.blackB());
				}
			}
			break;
		case QUEEN:
			if (p.getColor().isWhite()) {
				if(this.isWhiteTile(row, col)){
					board[row][col].setResource(gameImages.whiteQ_blkbg());
				}else{
					board[row][col].setResource(gameImages.whiteQ());
				}
			} else {
				if(this.isWhiteTile(row, col)){
					board[row][col].setResource(gameImages.blackQ_blkbg());
				}else{
					board[row][col].setResource(gameImages.blackQ());
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void setHighlighted(int row, int col, boolean highlighted) {

		Element element = board[row][col].getElement();
		if (highlighted) {
			element.setClassName(css.highlighted());
		} else {
			element.removeClassName(css.highlighted());
		}
	}

	@Override
	public void setSelected(int row, int col, boolean selected) {
		Element element = board[row][col].getElement();
		if (selected) {
			element.setClassName(css.selected());
		} else {
			element.removeClassName(css.selected());
		}
	}

	@Override
	public void setWhoseTurn(Color color) {
		if(color.isWhite()){
			gameStatus.setText("Whites Turn!");
		}else{
			gameStatus.setText("Blacks Turn!");
		}
	}

	@Override
	public void setGameResult(GameResult gameResult) {
		if(gameResult != null ){
			switch (gameResult.getGameResultReason()) {
			case CHECKMATE:
				if(gameResult.getWinner().isWhite()){
					gameStatus.setText("White Wins By Checkmate!");
				}else{
					gameStatus.setText("Black Wins By Checkmate!");
				}
				break;
			case STALEMATE:
				gameStatus.setText("DRAW By Stalemate!");
				break;
			case FIFTY_MOVE_RULE:
				gameStatus.setText("DRAW By fifty moves!");
				break;
			default:
				break;
			}
		}
	}
	
	private boolean isWhiteTile(int row, int col){
		
		if (row % 2 == 0 && col % 2 == 1 || row % 2 == 1 && col % 2 == 0) {
			return true;
		}else return false;

	}

	@Override
	public void setPromoteVisible(boolean visible) {
		this.promoteGrid.setVisible(visible);
	}
}
