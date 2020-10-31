package SpireBot.networking;

import SpireBot.info.CommandDatabase;
import SpireBot.info.DefaultCommands;
import SpireBot.info.InfoFinder;
import SpireBot.utils.Credentials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TwitchBot {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

//    private String prefix = "!spire ";
    private Credentials credentials;

    // --------------------------------------------------------------------------------

    public TwitchBot(Credentials credentials) {
        this.credentials = credentials;
        connect();
    }

    // --------------------------------------------------------------------------------

//    private void handleCommand(String command) {
//        System.out.println("Handling command '" + command + "'");
//        String response;
//
//        if (command.equals("help"))
//            response = InfoFinder.getHelp();
//        else if (command.equals("list"))
//            response = InfoFinder.getList();
//        else if (command.equals("info"))
//            response = InfoFinder.getInfo();
//        else if (command.equals("seed"))
//            response = InfoFinder.getSeed();
//        else
//            response = InfoFinder.getError();
//
//        sendMsg(response);
//    }

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

    public void sendMsg(String data) {
        String msg = "PRIVMSG #" + credentials.channel + " :" + data + "\r\n";
        out.write(msg);
        out.flush();

        System.out.println("Sent msg: " + msg);
    }

}
