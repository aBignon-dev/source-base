package dbg.command;

import com.sun.jdi.*;

import java.util.HashMap;
import java.util.Map;

public class JDITemporariesCommand extends JDIAbstractDebuggerCommand<Map<String, Value>> {

  public JDITemporariesCommand(VirtualMachine vm ) {
    super(vm);
  }

  @Override
  public Map<String, Value> execute() {
    Map<String, Value> temporaries = new HashMap<>();

    try {
      if (!currentThread.isSuspended()) {
        System.out.println("Impossible d'obtenir les variables temporaires: thread pas suspendu");
        return temporaries;
      }

      StackFrame currentFrame = currentThread.frame(0);

      try {
        Map<LocalVariable, Value> visibleVariables = currentFrame.getValues(currentFrame.visibleVariables());

        System.out.println("Variables temporaires dans la frame actuelle:");
        for (Map.Entry<LocalVariable, Value> entry : visibleVariables.entrySet()) {
          String name = entry.getKey().name();
          Value value = entry.getValue();
          temporaries.put(name, value);
          String valueStr = (value != null) ? value.toString() : "null";
          System.out.printf("  %s → %s%n", name, valueStr);
        }

      } catch (AbsentInformationException e) {
        System.out.println("Info debug pas dispo pour les variables locales");
      }

    } catch (IncompatibleThreadStateException e) {
      System.out.println("Erreur accès frame: " + e.getMessage());
    }

    return temporaries;
  }

  @Override
  public String getName() {
    return "temporaries";
  }
}