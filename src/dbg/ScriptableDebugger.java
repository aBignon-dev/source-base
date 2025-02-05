package dbg;

import com.sun.jdi.*;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.*;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.StepRequest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Scanner;

public class ScriptableDebugger {
    private Class debugClass; // classe a debugger
    private VirtualMachine vm; // machine virtuelle
    private int startLineNumber; // ligne de depart
    private boolean stepMode = false; // mode pas a pas
    private JDICommandManager commandManager; // gestionnaire de commandes
    private ThreadReference currentThread; // thread actuel

    // constructeur basique
    public ScriptableDebugger(int startLineNumber) {
        this.startLineNumber = startLineNumber;
    }

    // initialise toutes les commandes dispo
    public void initializeCommands() {
        commandManager = new JDICommandManager();
        commandManager.registerCommand(new JDIStepCommand(vm, currentThread));
        commandManager.registerCommand(new JDIStepOverCommand(vm, currentThread));
        commandManager.registerCommand(new JDIContinueCommand(vm, currentThread));
        commandManager.registerCommand(new JDIFrameCommand(vm, currentThread));
        commandManager.registerCommand(new JDITemporariesCommand(vm, currentThread));
        commandManager.registerCommand(new JDIStackCommand(vm, currentThread));
    }

    // connexion a la vm de debug
    public VirtualMachine connectAndLaunchVM() throws IOException, IllegalConnectorArgumentsException, VMStartException {
        LaunchingConnector launchingConnector = Bootstrap.virtualMachineManager().defaultConnector();
        Map<String, Connector.Argument> arguments = launchingConnector.defaultArguments();
        arguments.get("main").setValue(debugClass.getName());
        VirtualMachine vm = launchingConnector.launch(arguments);
        return vm;
    }

    // gere les événements qui arrivent
    private void handleEvent(Event event) throws AbsentInformationException {
        if (event instanceof ClassPrepareEvent) {
            handleClassPrepareEvent((ClassPrepareEvent) event);
        } else if (event instanceof BreakpointEvent || event instanceof StepEvent) {
            LocatableEvent locatableEvent = (LocatableEvent) event;
            currentThread = locatableEvent.thread();
            initializeCommands();
            waitForUserCommand();
        } else if (event instanceof VMDisconnectEvent) {
            handleVMDisconnectEvent((VMDisconnectEvent) event);
        }
    }

    // attend que l'utilisateur tape une commande
    private void waitForUserCommand() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Entrez une commande (step/step-over/continue/frame/temporaries/stack): ");
        String input = scanner.nextLine().trim();

        try {
            commandManager.executeCommand(input);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            waitForUserCommand(); // redemande si erreur
        }
    }

    // attache le debugger a une classe
    public void attachTo(Class debuggeeClass) {
        this.debugClass = debuggeeClass;
        try {
            vm = connectAndLaunchVM();
            enableClassPrepareRequest(vm);
            startDebugger();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalConnectorArgumentsException e) {
            e.printStackTrace();
        } catch (VMStartException e) {
            e.printStackTrace();
            System.out.println(e.toString());
        } catch (VMDisconnectedException e) {
            System.out.println("Machine virtuelle déconnectée: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // active la détection de chargement de classe
    public void enableClassPrepareRequest(VirtualMachine vm) {
        ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
        classPrepareRequest.addClassFilter(debugClass.getName());
        classPrepareRequest.enable();
    }

    // lance le debugger et traite les événements
    public void startDebugger() throws VMDisconnectedException, InterruptedException, AbsentInformationException {
        EventSet eventSet;
        while ((eventSet = vm.eventQueue().remove()) != null) {
            for (Event event : eventSet) {
                handleEvent(event);
                System.out.println(event.toString());
                vm.resume();
            }
        }
    }

    // gere la déconnexion de la vm
    private void handleVMDisconnectEvent(VMDisconnectEvent event) {
        System.out.println("Fin du programme");
        InputStreamReader reader = new InputStreamReader(vm.process().getInputStream());
        OutputStreamWriter writer = new OutputStreamWriter(System.out);
        try {
            reader.transferTo(writer);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Erreur lecture flux VM cible.");
        }
    }

    // gere le chargement d'une classe
    private void handleClassPrepareEvent(ClassPrepareEvent event) throws AbsentInformationException {
        setBreakPoint(debugClass.getName(), startLineNumber);
    }

    // met un point d'arrêt sur une ligne
    public void setBreakPoint(String className, int lineNumber) throws AbsentInformationException {
        for (ReferenceType targetClass : vm.allClasses()) {
            if (targetClass.name().equals(className)) {
                Location location = targetClass.locationsOfLine(lineNumber).get(0);
                BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
                bpReq.enable();
            }
        }
    }
}