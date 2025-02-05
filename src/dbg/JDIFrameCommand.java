package dbg;

import com.sun.jdi.*;

public class JDIFrameCommand extends JDIAbstractDebuggerCommand<StackFrame> {
  public JDIFrameCommand(VirtualMachine vm, ThreadReference thread) {
    super(vm, thread);
  }

  @Override
  public StackFrame execute() {
    try {
      StackFrame currentFrame = currentThread.frame(0);
      // Affiche les informations sur la frame
      System.out.println("Current frame:");
      System.out.println("  Location: " + currentFrame.location());
      try {
        System.out.println("  Line number: " + currentFrame.location().lineNumber());
        System.out.println("  Method: " + currentFrame.location().method());
        // Affiche les variables locales si possible
        System.out.println("  Local variables:");
        for (LocalVariable var : currentFrame.visibleVariables()) {
          System.out.println("    " + var.name() + " = " + currentFrame.getValue(var));
        }
      } catch (AbsentInformationException e) {
        System.out.println("  (Debug information not available)");
      }
      return currentFrame;
    } catch (IncompatibleThreadStateException e) {
      System.out.println("Cannot get frame: thread not suspended");
      return null;
    }
  }

  @Override
  public String getName() {
    return "frame";
  }
}