package dbg.command;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.StepRequest;

public abstract class JDIAbstractDebuggerCommand<T> implements JDIDebuggerCommand<T> {
  protected final VirtualMachine vm;
  protected ThreadReference currentThread; // Référence au thread actuel en cours de débogage
  public void setCurrentThread(ThreadReference currentThread){
    this.currentThread = currentThread;
  }
  public JDIAbstractDebuggerCommand(VirtualMachine vm) {
    this.vm = vm;
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
