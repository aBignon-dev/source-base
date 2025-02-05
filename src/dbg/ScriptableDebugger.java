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

    private Class debugClass;
    private VirtualMachine vm;
    private int startLineNumber;
    private boolean stepMode = false;
    private JDICommandManager commandManager;
    private ThreadReference currentThread;
    public ScriptableDebugger(int startLineNumber) {
        this.startLineNumber = startLineNumber;
    }
    // ... autres méthodes inchangées ...
    public void initializeCommands() {
        commandManager = new JDICommandManager();
        commandManager.registerCommand(new JDIStepCommand(vm, currentThread));
        commandManager.registerCommand(new JDIStepOverCommand(vm, currentThread));
        commandManager.registerCommand(new JDIContinueCommand(vm, currentThread));
        commandManager.registerCommand(new JDIFrameCommand(vm, currentThread));
        commandManager.registerCommand(new JDITemporariesCommand(vm, currentThread)); // Ajout de la nouvelle commande
    }
    public VirtualMachine connectAndLaunchVM() throws IOException, IllegalConnectorArgumentsException, VMStartException {
        LaunchingConnector launchingConnector = Bootstrap.virtualMachineManager().defaultConnector();
        Map<String, Connector.Argument> arguments = launchingConnector.defaultArguments();
        arguments.get("main").setValue(debugClass.getName());
        VirtualMachine vm = launchingConnector.launch(arguments);
        return vm;
    }

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

    private void waitForUserCommand() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter command (step/step-over/continue): ");
        String input = scanner.nextLine().trim();

        try {
            commandManager.executeCommand(input);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            waitForUserCommand(); // Redemande une commande en cas d'erreur
        }
    }

    private void disableAllStepRequests() {
        for (StepRequest request : vm.eventRequestManager().stepRequests()) {
            request.disable();
            vm.eventRequestManager().deleteEventRequest(request);
        }
    }

    public void enableStepRequest(LocatableEvent event) {
        // Disable existing step requests for the thread
        for (StepRequest request : vm.eventRequestManager().stepRequests()) {
            if (request.thread().equals(event.thread())) {
                request.disable();
                vm.eventRequestManager().deleteEventRequest(request);
            }
        }

        // Create and enable a new step request
        StepRequest stepRequest = vm.eventRequestManager()
            .createStepRequest(event.thread(),
                StepRequest.STEP_MIN,
                StepRequest.STEP_OVER);
        stepRequest.enable();
    }
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
            System.out.println("Virtual Machine is disconnected: " + e.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enableClassPrepareRequest(VirtualMachine vm) {
        ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
        classPrepareRequest.addClassFilter(debugClass.getName());
        classPrepareRequest.enable();
    }


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

    private void handleVMDisconnectEvent(VMDisconnectEvent event) {
        System.out.println("End of program");
        InputStreamReader reader = new InputStreamReader(vm.process().getInputStream());
        OutputStreamWriter writer = new OutputStreamWriter(System.out);
        try {
            reader.transferTo(writer);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Target VM input stream reading error.");
        }
    }

    private void handleClassPrepareEvent(ClassPrepareEvent event) throws AbsentInformationException {
        setBreakPoint(debugClass.getName(), startLineNumber);
    }

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
