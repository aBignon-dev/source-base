package dbg;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

public abstract class JDIAbstractDebuggerCommand implements JDIDebuggerCommand {
  protected final VirtualMachine vm;
  protected final ThreadReference currentThread;

  public JDIAbstractDebuggerCommand(VirtualMachine vm, ThreadReference thread) {
    this.vm = vm;
    this.currentThread = thread;
  }
}

