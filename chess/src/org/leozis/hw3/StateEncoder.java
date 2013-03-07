package org.leozis.hw3;

import org.shared.chess.Color;
import org.shared.chess.GameResult;
import org.shared.chess.GameResultReason;
import org.shared.chess.Piece;
import org.shared.chess.PieceKind;
import org.shared.chess.Position;
import org.shared.chess.State;

public class StateEncoder {

	public static State decode(String stateStr){
		Piece[][] board = null;
		Color turn = Color.WHITE;
		boolean whiteKS = false;
		boolean whiteQS = false;
		boolean blackKS = false;
		boolean blackQS = false;
		Position enpassant = null;
		int numberOfMovesWoCap = 0;
		Color winner = null;
		GameResult result = null;
		GameResultReason reason = null;


		for(String token : stateStr.split("&")){
			String[] stateVars = token.split("=");

			String key = stateVars[0];
			String val = stateVars[1];

			if(key.equals("castle")){
				char[] castleVals = val.toCharArray();
				whiteKS = (castleVals[0] == 'T' ? true: false);
				blackKS = (castleVals[1] == 'T' ? true: false);
				whiteQS = (castleVals[2] == 'T' ? true: false);
				blackQS = (castleVals[3] == 'T' ? true: false);
			}else if(key.equals("turn")){
				turn = val.equals("W") ? Color.WHITE : Color.BLACK;
			}else if(key.equals("moveswocap")){
				numberOfMovesWoCap = Integer.parseInt(val);
			}else if(key.equals("board")){
				board = boardDecoder(val);
			}else if(key.equals("enpassant")){
				char[] enVals = val.toCharArray();
				enpassant = new Position(enVals[0], enVals[1]);
			}else if(key.equals("winner")){
				winner =  val.equals("W") ? Color.WHITE : Color.BLACK;
			}else if(key.equals("reason")){
				if (val.equals("CHECKMATE")) {
					reason = GameResultReason.CHECKMATE;
				}  else if (val.equals("STALEMATE")) {
					reason = GameResultReason.STALEMATE;
				} else if (val.equals("FIFTY_MOVE_RULE")) {
					reason = GameResultReason.FIFTY_MOVE_RULE;
				} else if (val.equals("THREEFOLD_REPETITION_RULE")) {
					reason = GameResultReason.THREEFOLD_REPETITION_RULE;
				}
			}

		}
		
		if(reason != null) {
			result = new GameResult(winner, reason);
		}
		
		

		return new State(turn, board, new boolean[] { whiteKS, blackKS },
				new boolean[] { whiteQS, blackQS }, enpassant,
				numberOfMovesWoCap, result);
	}

	private static Piece[][] boardDecoder(String board){
		char[] boardPositions =  board.toCharArray();
		int row;
		int col;
		char pcolor;
		char kind;
		
		
		Piece boardDecoded[][] = new Piece[8][8];

		//set everything to null to initialize
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				boardDecoded[r][c] = null;
			}
		} 
		
		for(int i = 0; i < boardPositions.length; i = i + 4){
			row = Integer.parseInt(boardPositions[i] + "");
			col = Integer.parseInt(boardPositions[i+1] + "");
			pcolor = boardPositions[i+2];
			kind = boardPositions[i+3];

			Color color;
			PieceKind pk = null;

			color = pcolor == 'W' ? Color.WHITE : Color.BLACK;

			switch(kind){
			case 'Q': pk = PieceKind.QUEEN;
			break;
			case 'K': pk = PieceKind.KING;
			break;
			case 'R': pk = PieceKind.ROOK;
			break;
			case 'B': pk = PieceKind.BISHOP;
			break;
			case 'N': pk = PieceKind.KNIGHT;
			break;
			case 'P': pk = PieceKind.PAWN;
			break;
			}

			boardDecoded[row][col] = new Piece(color, pk);
		}

		return boardDecoded;

	}

	public static String encode(State state){
		String stateStr = "";
		String pieceLetter = null;

		stateStr += "castle=";

		stateStr += state.isCanCastleKingSide(Color.WHITE) ? "T" : "F";
		stateStr += state.isCanCastleKingSide(Color.BLACK) ? "T" : "F";
		stateStr += state.isCanCastleQueenSide(Color.WHITE) ? "T" : "F";
		stateStr += state.isCanCastleQueenSide(Color.BLACK) ? "T" : "F";

		if(state.getEnpassantPosition() != null){
			stateStr += "&enpassant=" + state.getEnpassantPosition().getRow(); 
			stateStr += state.getEnpassantPosition().getCol();
		}

		stateStr += "&turn=" + (state.getTurn() == Color.WHITE ? "W" : "B");

		stateStr += "&moveswocap=" + state.getNumberOfMovesWithoutCaptureNorPawnMoved();

		if (state.getGameResult() != null) {
			stateStr += "&winner=";
			stateStr += state.getGameResult().getWinner().isWhite() ? "W" : "B";
			stateStr += "&reason=" + state.getGameResult().getGameResultReason();
		}

		stateStr += "&board=";

		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				Piece piece = state.getPiece(r, c);
				if(piece != null){
					stateStr += r;
					stateStr += c;
					stateStr += piece.getColor().equals(Color.WHITE) ? "W" : "B";

					switch(piece.getKind()){
					case QUEEN: pieceLetter =  "Q";
					break;
					case KNIGHT: pieceLetter =  "N";
					break;
					case ROOK: pieceLetter =  "R";
					break;
					case BISHOP: pieceLetter =  "B";
					break;
					case PAWN: pieceLetter =  "P";
					break;
					case KING: pieceLetter =  "K";
					break;
					}

					stateStr += pieceLetter;
				}
			} 
		}

		return stateStr;
	}

}
