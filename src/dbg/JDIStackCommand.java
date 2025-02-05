package dbg;

import com.sun.jdi.*;
import java.util.ArrayList;
import java.util.List;

public class JDIStackCommand extends JDIAbstractDebuggerCommand<List<Location>> {

  public JDIStackCommand(VirtualMachine vm, ThreadReference thread) {
    super(vm, thread);
  }

  @Override
  public List<Location> execute() {
    List<Location> stackTrace = new ArrayList<>();

    try {
      if (!currentThread.isSuspended()) {
        System.out.println("Impossible d'obtenir la pile: thread pas suspendu");
        return stackTrace;
      }

      List<StackFrame> frames = currentThread.frames();

      if (frames.isEmpty()) {
        System.out.println("Pile vide");
        return stackTrace;
      }

      System.out.println("Pile d'appels (plus récent en premier):");
      for (int i = 0; i < frames.size(); i++) {
        StackFrame frame = frames.get(i);
        Location location = frame.location();
        stackTrace.add(location);

        String methodName = location.method().name();
        String className = location.declaringType().name();
        int lineNumber = location.lineNumber();

        StringBuilder args = new StringBuilder();
        try {
          List<LocalVariable> parameters = location.method().arguments();
          for (int j = 0; j < parameters.size(); j++) {
            if (j > 0) args.append(", ");
            LocalVariable param = parameters.get(j);
            Value value = frame.getValue(param);
            args.append(param.name()).append("=").append(value != null ? value : "null");
          }
        } catch (AbsentInformationException e) {
          args.append("<info debug pas dispo>");
        }

        System.out.printf("#%d: %s.%s(%s) à la ligne %d%n",
            i, className, methodName, args, lineNumber);
      }

    } catch (IncompatibleThreadStateException e) {
      System.out.println("Erreur accès frames: " + e.getMessage());
    }

    return stackTrace;
  }

  @Override
  public String getName() {
    return "stack";
  }
}