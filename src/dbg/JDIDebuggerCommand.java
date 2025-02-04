package dbg;

public interface JDIDebuggerCommand {
  void execute();
  String getName();
}
