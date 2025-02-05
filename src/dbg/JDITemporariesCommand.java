package dbg;

import com.sun.jdi.*;
import java.util.HashMap;
import java.util.Map;

public class JDITemporariesCommand extends JDIAbstractDebuggerCommand<Map<String, Value>> {

  public JDITemporariesCommand(VirtualMachine vm, ThreadReference thread) {
    super(vm, thread);
  }

  @Override
  public Map<String, Value> execute() {
    Map<String, Value> temporaries = new HashMap<>();

    try {
      if (!currentThread.isSuspended()) {
        System.out.println("Cannot get temporaries: thread not suspended");
        return temporaries;
      }

      StackFrame currentFrame = currentThread.frame(0);

      try {
        // Récupère toutes les variables locales visibles
        Map<LocalVariable, Value> visibleVariables = currentFrame.getValues(currentFrame.visibleVariables());

        System.out.println("Temporary variables in current frame:");
        for (Map.Entry<LocalVariable, Value> entry : visibleVariables.entrySet()) {
          String name = entry.getKey().name();
          Value value = entry.getValue();
          temporaries.put(name, value);

          // Affichage formaté des variables
          String valueStr = (value != null) ? value.toString() : "null";
          System.out.printf("  %s → %s%n", name, valueStr);
        }

      } catch (AbsentInformationException e) {
        System.out.println("Debug information not available for local variables");
      }

    } catch (IncompatibleThreadStateException e) {
      System.out.println("Error accessing frame: " + e.getMessage());
    }

    return temporaries;
  }

  @Override
  public String getName() {
    return "temporaries";
  }
}