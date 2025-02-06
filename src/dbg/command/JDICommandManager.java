package dbg.command;

import com.sun.jdi.ThreadReference;

import java.util.HashMap;
import java.util.Map;

public class JDICommandManager {
  private final Map<String, JDIAbstractDebuggerCommand> commands = new HashMap<>(); // stocke les commandes
  //Stocking All command executed with their name and the result of the command and in key = programCounter

  public void registerCommand(JDIAbstractDebuggerCommand command) {
    commands.put(command.getName(), command); // ajoute une commande
  }

  // lance la commande si elle existe
  public void executeCommand(String commandName, ThreadReference currentThread) {
    JDIAbstractDebuggerCommand command = commands.get(commandName);
    if (command != null) {
      command.setCurrentThread(currentThread);
       command.execute();
    } else {
      throw new IllegalArgumentException("Commande inconnue: " + commandName);
    }
  }

  public Map<String, JDIAbstractDebuggerCommand> getCommands() {
    return this.commands;
  }
}