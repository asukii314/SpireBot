package SpireBot.networking;

import SpireBot.info.CommandDatabase;
import SpireBot.info.DefaultCommands;
import SpireBot.utils.Credentials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class TwitchBot {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private Credentials credentials;

    // --------------------------------------------------------------------------------

    public TwitchBot(Credentials credentials) {
        this.credentials = credentials;
        connect();
    }

    // --------------------------------------------------------------------------------

    private void handleMsg(String user, String msg) {
        String response = CommandDatabase.handleMessage(user, msg);
        if (!response.isEmpty()) {
            sendMsg(response);
        }
    }

    // --------------------------------------------------------------------------------

    public void connect() {
        try {
            socket = new Socket("irc.chat.twitch.tv", 6667);

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            sendRaw("PASS " + credentials.oauth);
            sendRaw("NICK " + credentials.username);
            sendRaw("JOIN #" + credentials.channel);

            // Other classes should register their commands at any time before users start inputting to the chat
            DefaultCommands.setup();

            System.out.println("Finished connecting to the channel, attempting to send arrival msg to chat");
            sendMsg("/me has entered the chat");

            String res;
            while (true) {
                res = in.readLine();
                System.out.println("RESPONSE: " + res);

                if (res.contains("PRIVMSG")) {
                    String[] x = res.split("!", 2);
                    String[] y = x[1].split(":", 2);

                    String username = x[0].substring(1);
                    String msg = y[1];

                    //send_msg("Hello @" + username);

                    handleMsg(username, msg);

                }
                else if (res.contains("PING :tmi.twitch.tv")) {
                    System.out.println("Recv a ping, should pong now");
                    sendRaw("PONG :tmi.twitch.tv");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --------------------------------------------------------------------------------

    public void sendRaw(String data) {
        String msg = data + "\r\n";
        out.write(msg);
        out.flush();
    }

    // Actual limit is 500, but let there be some space for boilerplate
    private static final int MAX_PACKET_SIZE = 450;

    private ArrayList<String> smartSplit(String data) {
        ArrayList<String> res = new ArrayList<>();

        int length = data.length();
        if (length > MAX_PACKET_SIZE) {
            // need to split
            String[] x = data.split("\\. ");
            System.out.println("Split on periodspace into " + x.length + " chunks");

            StringBuilder sb = new StringBuilder();

            for (String s : x) {
                int subLen = s.length() + 2;
                System.out.println("Checking sub of size "+ subLen + ": '" + s + "'");

                // Can't smart split (this sentence is too long on its own!)
                if (subLen > MAX_PACKET_SIZE)
                    return null;

                // If this new string would make the string builder too big, start a new one
                if (sb.length() + subLen > MAX_PACKET_SIZE) {
                    res.add(sb.toString());
                    sb = new StringBuilder(s);
                    sb.append(". ");
                }
                else {
                    sb.append(s);
                    sb.append(". ");
                }
            }

            res.add(sb.toString());
        }
        else {
            res.add(data);
        }

        System.out.println("ORIGINAL SIZE: " + data.length() + ": '" + data + "'");
        System.out.println("Split original data of length " + data.length() + " into " + res.size() + " chunks.");
        for (String s : res) {
            System.out.println("\tSIZE " + s.length() + ": '" + s + "'");
        }
        System.out.println("----");

        return res;
    }

    private void splitAndSend(String data) {
        if (data.length() > MAX_PACKET_SIZE) {
            ArrayList<String> split = smartSplit(data);
            if (split == null) {
                System.out.println("Failed to split, doing nothing!");
            }
            else {
                System.out.print("Successfully split into chunks: [");
                for (String s : split)
                    System.out.print(s.length() + " ");
                System.out.println("]");

                // Time to sendMsg on each chunk
                for (String s : split)
                    sendMsg(s);
            }
//            System.out.println("Need to split data: " + data);
//            System.out.println("It's size is " + data.length());
//
//            String s1 = data.substring(0, MAX_PACKET_SIZE);
//            System.out.println("Part 1 is size " + s1.length() + ": " + s1);
//
//            String s2 = data.substring(MAX_PACKET_SIZE);
//            System.out.println("Part 2 is size " + s2.length() + ": " + s2);
//
//            // TODO: send s1 for real
//            // TODO: recurse on s2
        }
    }

    public void sendMsg(String data) {
        if (data.length() > 450) {
            System.out.println("OJB WARNING: data too long! -- attempting to split");
            splitAndSend(data);
        }
        else {
            String msg = "PRIVMSG #" + credentials.channel + " :" + data + "\r\n";
            out.write(msg);
            out.flush();

            System.out.println("Sent msg: " + msg);
        }
    }

}
