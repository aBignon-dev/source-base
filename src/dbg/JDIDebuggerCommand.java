package dbg;

public interface JDIDebuggerCommand<T> {
  T execute();  // execute la commande
  String getName(); // retourne le nom
}