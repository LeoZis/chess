package org.leozis.hw3;

import org.leozis.hw3.Presenter.View;
import org.shared.chess.Color;
import org.shared.chess.GameResult;
import org.shared.chess.Piece;
import org.shared.chess.State;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
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
				image.setHeight("100%");
				final int i = row; 
				final int j = col;
				image.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						presenter.clickedOn(i, j);
					}
				});



				FlowPanel panel = new FlowPanel();
				if (row % 2 == 0 && col % 2 == 1 || row % 2 == 1 && col % 2 == 0) {
					panel.setStylePrimaryName("blackTile");
				} else {
					panel.setStylePrimaryName("whiteTile");
				}
				panel.add(image);

				gameGrid.setWidget(row, col, panel);
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

		//History
		History.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String token = event.getValue();

				if (token.isEmpty()) {
					presenter.setState(new State());
				} else {
					presenter.setState(StateEncoder.decode(token));
				}

				presenter.setSelected(null);
				presenter.clearHighlighted();
				presenter.clearSelected();
			}
		});
	}

	@Override
	public void setPiece(int row, int col, Piece p) {
		final Image image = board[row][col];
		
		this.attachDragOverHandler(image);
		this.attachDragDropHandler(image,row,col);
		this.attachDragStartHandler(image,row,col);

		
		if (p == null) {
			image.setUrl("");
			image.setWidth("100%");
			image.setHeight("100%");
			return;
		}
		switch (p.getKind()) {
		case KING:
			if (p.getColor().isWhite()) {
				image.setResource(gameImages.whiteK());
			} else {				
				image.setResource(gameImages.blackK());
			}
			break;
		case PAWN:
			if (p.getColor().isWhite()) {
				image.setResource(gameImages.whiteP());
			} else {
				image.setResource(gameImages.blackP());
			}
			break;
		case ROOK:
			if (p.getColor().isWhite()) {
				image.setResource(gameImages.whiteR());
			} else {
				image.setResource(gameImages.blackR());
			}
			break;
		case KNIGHT:
			if (p.getColor().isWhite()) {
				image.setResource(gameImages.whiteN());
			} else {
				image.setResource(gameImages.blackN());
			}
			break;
		case BISHOP:
			if (p.getColor().isWhite()) {
				image.setResource(gameImages.whiteB());

			} else {
				image.setResource(gameImages.blackB());
			}
			break;
		case QUEEN:
			if (p.getColor().isWhite()) {
				image.setResource(gameImages.whiteQ());
			} else {
				image.setResource(gameImages.blackQ());
			}
			break;
		default:
			break;
		}
		

		image.getElement().setDraggable(Element.DRAGGABLE_TRUE);
		
		this.attachDragOverHandler(image);
		this.attachDragStartHandler(image,row,col);
		this.attachDragDropHandler(image,row,col);
		
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

	@Override
	public void setPromoteVisible(boolean visible) {
		this.promoteGrid.setVisible(visible);
	}

	@Override
	public void setHistory(String hist){
		History.newItem(hist);
	}
	
	public void attachDragOverHandler(final Image image){
		//drag over
		image.addDragOverHandler(new DragOverHandler() {
			public void onDragOver(DragOverEvent event) {
				resetDragHighlighting();
				image.getElement().setClassName(css.dragover());
			}
		});
	}
	
	public void attachDragStartHandler(final Image image, final int row, final int col){		
		// Add a DragStartHandler.
		image.addDragStartHandler(new DragStartHandler() {
			public void onDragStart(DragStartEvent event) {
				// Required: set data for the event.
				event.setData("fromRow", row + "");
				event.setData("fromCol", col + "");

				// Optional: show a copy of the widget under cursor.
				event.getDataTransfer().setDragImage(image.getElement(),
						10, 10);
			}
		});
	}
	
	public void attachDragDropHandler(final Image image, final int row, final int col){		
		// Add a DropHandler.
		image.addDropHandler(new DropHandler() {
			public void onDrop(DropEvent event) {
				// Prevent the native text drop.
				event.preventDefault();
				// Get the data out of the event.
				String fromRow = event.getData("fromRow");
				String fromCol = event.getData("fromCol");
				
				if(!presenter.isPossibleMove(row, col, Integer.parseInt(fromRow), Integer.parseInt(fromCol))){
					resetDragHighlighting();
				}

			}
		});
	}
	
	private void resetDragHighlighting(){
		//remove all previous drag over classes
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				board[r][c].getElement().removeClassName(css.dragover());
			}
		}
	}

	@Override
	public void doPieceAnimation(int row, int col, int toRow, int toCol) {
		//		Element piece = board[toRow][toCol].getElement();
		//		piece.getStyle().setOpacity(1);
		//		PieceAnimation animation = new PieceAnimation(piece);
		//		//Element toSquare = board[toRow][toCol].getElement();
		//        animation.scrollTo(2000);
		//        
		//		//

	}
}
