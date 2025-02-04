package dbg;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.StepRequest;

public class JDIStepCommand extends JDIAbstractDebuggerCommand {
  public JDIStepCommand(VirtualMachine vm, ThreadReference thread) {
    super(vm, thread);
  }

  @Override
  public void execute() {
    StepRequest stepRequest = vm.eventRequestManager()
        .createStepRequest(currentThread,
            StepRequest.STEP_MIN,
            StepRequest.STEP_INTO);
    stepRequest.enable();
  }

  @Override
  public String getName() {
    return "step";
  }
}