package dbg;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.StepRequest;

public class JDIStepOverCommand extends JDIAbstractDebuggerCommand {
  public JDIStepOverCommand(VirtualMachine vm, ThreadReference thread) {
    super(vm, thread);
  }

  @Override
  public void execute() {
    StepRequest stepRequest = vm.eventRequestManager()
        .createStepRequest(currentThread,
            StepRequest.STEP_LINE,
            StepRequest.STEP_OVER);
    stepRequest.enable();
  }

  @Override
  public String getName() {
    return "step-over";
  }
}
