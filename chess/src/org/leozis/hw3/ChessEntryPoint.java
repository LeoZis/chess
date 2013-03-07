package org.leozis.hw3;

import org.leozis.hw2.StateChangerImpl;
import org.leozis.hw2_5.StateExplorerImpl;
import org.leozis.hw3.Graphics;
import org.leozis.hw3.Presenter;
import org.leozis.*;
import org.shared.chess.State;
import org.shared.chess.StateChanger;
import com.google.gwt.user.client.History;
import org.shared.chess.StateExplorer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class ChessEntryPoint implements EntryPoint {

	  @Override
	  public void onModuleLoad() {
		State state;
		StateChanger stateChanger = new StateChangerImpl();
		StateExplorer stateExplorer = new StateExplorerImpl();
	    state = History.getToken().isEmpty() ? new State() : StateEncoder.decode(History.getToken());
		Presenter presenter = new Presenter(state, stateChanger, stateExplorer);
	    final Graphics graphics = new Graphics(presenter);
	    
	    presenter.setView(graphics);
	    presenter.setState(state);
	    RootPanel.get().add(graphics);
	   
	  }

}
