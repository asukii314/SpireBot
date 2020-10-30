package SpireBot.utils;

import SpireBot.SpireBot;
import SpireBot.networking.TwitchBot;
import SpireBot.utils.Credentials;

public class ThreadHelper {
    public static class BotThread implements Runnable {
        private Thread thread;
        private Credentials credentials;

        public BotThread(Credentials credentials) {
            this.credentials = credentials;
        }

        @Override
        public void run() {
            TwitchBot bot = new TwitchBot(credentials);
            // TODO: make the twitch bot respond to interrupts
            System.out.println("Thread successfully shutdown");
        }

        public void start() {
            if (thread == null) {
                thread = new Thread(this);
                thread.start();
            }
        }

        public void kill() {
            if (thread != null) {
                thread.interrupt();
                thread = null;
            }
        }

    }

    private static BotThread botThread;

    // TODO: figure out a way to call this join when the game is killed (Gdx.app.exit() called)
    //   as the thread continues to run when the game is dead.
    public static boolean start(Credentials credentials) {
        if (credentials != null && botThread == null) {
            botThread = new BotThread(credentials);
            botThread.start();
            return true;
        }

        return false;
    }
    public static void kill() {
        if (botThread != null) {
            botThread.kill();
            botThread = null;
        }
    }
}
