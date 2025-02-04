package dbg;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

public class JDIContinueCommand extends JDIAbstractDebuggerCommand {
  public JDIContinueCommand(VirtualMachine vm, ThreadReference thread) {
    super(vm, thread);
  }

  @Override
  public void execute() {
    vm.resume();
  }

  @Override
  public String getName() {
    return "continue";
  }
}