package org.yoavzibin.hw2;

import org.shared.chess.AbstractStateChangerAllTest;
import org.shared.chess.StateChanger;


public class StateChangerImplAllTest extends AbstractStateChangerAllTest {
  @Override
  public StateChanger getStateChanger() {
    return new StateChangerImpl();
  }
}
