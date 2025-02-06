package dbg.command;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.StepRequest;

public class JDIStepOverCommand extends JDIAbstractDebuggerCommand<Void> {
  public JDIStepOverCommand(VirtualMachine vm ) {
    super(vm);
  }

  @Override
  public Void execute() {
    try {
      clearStepRequests();
      StepRequest stepRequest = vm.eventRequestManager()
          .createStepRequest(currentThread,
              StepRequest.STEP_LINE,
              StepRequest.STEP_OVER);
      stepRequest.addClassFilter("dbg.*"); // reste dans notre package
      stepRequest.enable();
      vm.resume();
    } catch (Exception e) {
      System.out.println("Erreur step-over: " + e.getMessage());
    }
    return null;
  }

  @Override
  public String getName() {
    return "step-over";
  }
}
