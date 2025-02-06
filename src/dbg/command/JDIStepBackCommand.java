package dbg.command;

import com.sun.jdi.*;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.StepRequest;

import java.util.*;

public class JDIStepBackCommand extends JDIAbstractDebuggerCommand<Void> {
  private final Deque<DebugState> stateHistory;
  private static final int MAX_HISTORY_SIZE = 100;
  private boolean isFirstStepBack = true;

  public JDIStepBackCommand(VirtualMachine vm) {
    super(vm);
    this.stateHistory = new LinkedList<>();
  }

  public void recordState() {
    try {
      if (!currentThread.isSuspended()) {
        return;
      }

      StackFrame currentFrame = currentThread.frame(0);
      Map<LocalVariable, Value> variables = new HashMap<>();
      for (LocalVariable var : currentFrame.visibleVariables()) {
        variables.put(var, currentFrame.getValue(var));
      }

      DebugState state = new DebugState(variables, currentFrame.location());

      if (stateHistory.isEmpty() || !state.equals(stateHistory.peekFirst())) {
        stateHistory.addFirst(state);

        if (stateHistory.size() > MAX_HISTORY_SIZE) {
          stateHistory.removeLast();
        }
      }

    } catch (IncompatibleThreadStateException | AbsentInformationException e) {
      System.out.println("Impossible de capturer l'état: " + e.getMessage());
    }
  }

  @Override
  public Void execute() {
    return stepBack(1);
  }

  public Void stepBack(int steps) {
    if (stateHistory.isEmpty()) {
      System.out.println("Pas d'historique d'état disponible");
      return null;
    }

    try {
      if (isFirstStepBack) {
        stateHistory.removeFirst();
        isFirstStepBack = false;
      }

      if (stateHistory.isEmpty()) {
        System.out.println("Pas de point de retour disponible");
        return null;
      }

      DebugState targetState = stateHistory.removeFirst();

      restoreThreadState(targetState);

      System.out.println("Retour à la position: " + targetState.getStoredLocation());
      printStateDetails(targetState);

    } catch (Exception e) {
      System.out.println("Erreur lors du retour en arrière: " + e.getMessage());
      isFirstStepBack = true;
    }

    return null;
  }

  private void restoreThreadState(DebugState state) throws Exception {
    try {
      currentThread.suspend();

      List<StackFrame> frames = currentThread.frames();
      if (!frames.isEmpty()) {
        StackFrame currentFrame = frames.get(0);

        for (Map.Entry<LocalVariable, Value> entry : state.getStoredVariables().entrySet()) {
          LocalVariable var = entry.getKey();
          Value value = entry.getValue();
          currentFrame.setValue(var, value);
        }

        Location targetLocation = state.getStoredLocation();
        if (targetLocation != null) {
          EventRequestManager erm = currentThread.virtualMachine().eventRequestManager();
          StepRequest stepRequest = erm.createStepRequest(currentThread,
              StepRequest.STEP_LINE,
              StepRequest.STEP_INTO);
          stepRequest.addClassFilter(targetLocation.declaringType());
          stepRequest.enable();
          currentThread.resume();
        }
      }
    } catch (Exception e) {
      throw new Exception("Erreur lors de la restauration de l'état du thread: " + e.getMessage(), e);
    }
  }

  private void printStateDetails(DebugState state) {
    System.out.println("État restauré:");
    System.out.println("  Position: " + state.getStoredLocation());
    System.out.println("  Variables:");
    for (Map.Entry<LocalVariable, Value> var : state.getStoredVariables().entrySet()) {
      System.out.println("    " + var.getKey() + " = " + var.getValue());
    }
  }

  @Override
  public String getName() {
    return "step-back";
  }

  public class DebugState {
    private final Map<LocalVariable, Value> storedVariables;
    private final Location storedLocation;

    public DebugState(Map<LocalVariable, Value> variables, Location location) {
      this.storedVariables = variables;
      this.storedLocation = location;
    }

    public Map<LocalVariable, Value> getStoredVariables() {
      return storedVariables;
    }

    public Location getStoredLocation() {
      return storedLocation;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      DebugState that = (DebugState) o;
      return Objects.equals(storedLocation, that.storedLocation) &&
          Objects.equals(storedVariables, that.storedVariables);
    }

    @Override
    public int hashCode() {
      return Objects.hash(storedLocation, storedVariables);
    }
  }
}