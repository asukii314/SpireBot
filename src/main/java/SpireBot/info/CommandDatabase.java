package SpireBot.info;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Supplier;

public class CommandDatabase {
    private HashMap<String, HashMap<String, Supplier<String>>> commands2 = new HashMap<>();
    //private HashMap<Pair<String, String>, Supplier<String>> commands = new HashMap<>();

    //private HashSet<String> prefixes = new HashSet<>();
    private HashMap<String, Supplier<String>> prefixErrorMessages = new HashMap<>();

    // Singleton pattern
    private static class CommandDatabaseHolder { private static final CommandDatabase INSTANCE = new CommandDatabase(); }
    private static CommandDatabase getInstance() { return CommandDatabaseHolder.INSTANCE; }

    // --------------------------------------------------------------------------------

    public static void register(String prefix, String command, Supplier<String> fn) {
        CommandDatabase instance = getInstance();

        HashMap<String, Supplier<String>> inner;
        if (instance.commands2.containsKey(prefix)) {
            inner = instance.commands2.get(prefix);
        }
        else {
            inner = new HashMap<>();
            instance.commands2.put(prefix, inner);
        }

        inner.put(command, fn);

    }

    public static void registerPrefixErrorMessage(String prefix, Supplier<String> fn) {
        CommandDatabase instance = getInstance();
        instance.prefixErrorMessages.put(prefix, fn);
    }

    public static boolean hasCommand(String prefix, String command) {
        CommandDatabase instance = getInstance();

        if (instance.commands2.containsKey(prefix)) {
            HashMap<String, Supplier<String>> inner = instance.commands2.get(prefix);
            return inner.containsKey(command);
        }
        else {
            return false;
        }
    }

    public static boolean hasPrefix(String prefix) {
        return getInstance().commands2.containsKey(prefix);
    }

    public static String getErrorMessage(String prefix) {
        CommandDatabase instance = getInstance();
        if (instance.prefixErrorMessages.containsKey(prefix)) {
            return instance.prefixErrorMessages.get(prefix).get();
        }
        else {
            return "";
        }
//        if (instance.showFullErrorMessage)
//            return "ERROR: " + prefix + " " + command + " is not a valid command. Try " + prefix + " help for more details about this bot.";
//        else
//            return "";
    }

    public static String get(String prefix, String command) {
        if (hasCommand(prefix, command)) {
            return getInstance().commands2.get(prefix).get(command).get();
        }
        else {
            return getErrorMessage(prefix);
        }
    }

    // --------------------------------------------------------------------------------

    public static Pair<Boolean, String> testPrefix(String msg) {
        for (String prefix : getInstance().commands2.keySet()) {
            if (msg.startsWith(prefix)) {
                return Pair.of(true, prefix);
            }
        }

        return Pair.of(false, "");
    }

    public static String handleMessage(String username, String msg) {
        Pair<Boolean, String> msgIsCommand = testPrefix(msg);

        if (msgIsCommand.getLeft())
            return get(msgIsCommand.getRight(), msg.substring(msgIsCommand.getRight().length())); //handleCommand(username, msgIsCommand.getRight(), msg.substring(msgIsCommand.getRight().length()));
        else return "";
    }

    // --------------------------------------------------------------------------------

    // TODO: oh no making the key of the map pairs was a terrible mistake. Need to probably redo it to a map of maps
    public static String getAllCommandsByPrefix(String prefix) {
        CommandDatabase instance = getInstance();
        if (instance.commands2.containsKey(prefix)) {
            HashMap<String, Supplier<String>> inner = instance.commands2.get(prefix);

            ArrayList<String> all = new ArrayList(inner.keySet());
            all.sort(String::compareTo);

            StringBuilder sb = new StringBuilder("The list of commands starting with " + prefix + "are: [ ");
            int curr = 0;
            for (String command : all) {
                sb.append(command);

                if (curr++ < all.size() - 1)
                    sb.append(", ");
            }

            sb.append(" ]");
            return sb.toString();
        }
        else {
            return "ERROR: There are no commands with the " + prefix + "prefix.";
        }
    }

    // --------------------------------------------------------------------------------

    // DEBUG
//    public static void print() {
//        CommandDatabase instance = getInstance();
//        System.out.println("CommandDatabase contains the following " + instance.commands.size() + " commands:");
//        for (Pair<String, String> key : instance.commands.keySet()) {
//            System.out.println("\t" + key.getLeft() + key.getRight());
//        }
//        System.out.println();
//    }
}
