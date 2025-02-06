package dbg.command;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.StepRequest;

public abstract class JDIAbstractDebuggerCommand<T> implements JDIDebuggerCommand<T> {
  protected final VirtualMachine vm;
  protected final ThreadReference currentThread; // Référence au thread actuel en cours de débogage

  public JDIAbstractDebuggerCommand(VirtualMachine vm, ThreadReference thread) {
    this.vm = vm;
    this.currentThread = thread;
  }

  // nettoie les requêtes d'étape pour ce thread
  protected void clearStepRequests() {
    EventRequestManager erm = vm.eventRequestManager();
    for (StepRequest req : erm.stepRequests()) {
      if (req.thread().equals(currentThread)) {
        erm.deleteEventRequest(req);
      }
    }
  }
}
