package dbg;

public interface JDIDebuggerCommand<T> {
  T execute();
  String getName();
}
