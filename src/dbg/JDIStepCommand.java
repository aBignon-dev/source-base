package dbg;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.StepRequest;

public class JDIStepCommand extends JDIAbstractDebuggerCommand<Void> {
  public JDIStepCommand(VirtualMachine vm, ThreadReference thread) {
    super(vm, thread);
  }

  @Override
  public Void execute() {
    try {
      clearStepRequests();
      StepRequest stepRequest = vm.eventRequestManager()
          .createStepRequest(currentThread,
              StepRequest.STEP_LINE,
              StepRequest.STEP_INTO);
      stepRequest.addClassFilter("dbg.*"); // On print que les classes de notre package.
      stepRequest.enable();
      vm.resume();
    } catch (Exception e) {
      System.out.println("Erreur step: " + e.getMessage());
    }
    return null;
  }

  @Override
  public String getName() {
    return "step";
  }
}