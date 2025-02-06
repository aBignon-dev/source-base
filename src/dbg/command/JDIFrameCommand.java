package dbg.command;

import com.sun.jdi.*;

public class JDIFrameCommand extends JDIAbstractDebuggerCommand<StackFrame> {
  public JDIFrameCommand(VirtualMachine vm, ThreadReference thread) {
    super(vm, thread);
  }

  @Override
  public StackFrame execute() {
    try {
      StackFrame currentFrame = currentThread.frame(0);
      System.out.println("Frame actuelle:");
      System.out.println("  Position: " + currentFrame.location());
      try {
        System.out.println("  Numéro de ligne: " + currentFrame.location().lineNumber());
        System.out.println("  Méthode: " + currentFrame.location().method());
        System.out.println("  Variables locales:");
        for (LocalVariable var : currentFrame.visibleVariables()) {
          System.out.println("    " + var.name() + " = " + currentFrame.getValue(var));
        }
      } catch (AbsentInformationException e) {
        System.out.println("  (Info de debug pas dispo)");
      }
      return currentFrame;
    } catch (IncompatibleThreadStateException e) {
      System.out.println("Impossible d'obtenir la frame: thread pas suspendu");
      return null;
    }
  }

  @Override
  public String getName() {
    return "frame";
  }
}