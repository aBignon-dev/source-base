package dbg;

import java.util.HashMap;
import java.util.Map;

public class JDICommandManager {
  private final Map<String, JDIDebuggerCommand> commands = new HashMap<>(); // stocke les commandes

  public void registerCommand(JDIDebuggerCommand command) {
    commands.put(command.getName(), command); // ajoute une commande
  }

  // lance la commande si elle existe
  public void executeCommand(String commandName) {
    JDIDebuggerCommand command = commands.get(commandName);
    if (command != null) {
      command.execute();
    } else {
      throw new IllegalArgumentException("Commande inconnue: " + commandName);
    }
  }
}