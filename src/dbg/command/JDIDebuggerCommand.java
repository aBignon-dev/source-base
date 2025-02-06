package dbg.command;

import com.sun.jdi.ThreadReference;

public interface JDIDebuggerCommand<T> {
  T execute();  // execute la commande
  String getName(); // retourne le nom


}