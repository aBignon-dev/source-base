package dbg;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

public class JDIContinueCommand extends JDIAbstractDebuggerCommand<Void> {
  public JDIContinueCommand(VirtualMachine vm, ThreadReference thread) {
    super(vm, thread);
  }

  @Override
  public Void execute() {
    clearStepRequests(); //Supprime les requêtes de pas à pas pour passer au prochain point d'arrêt
    vm.resume(); // continue l'exécution
    return null;
  }

  @Override
  public String getName() {
    return "continue";
  }
}
