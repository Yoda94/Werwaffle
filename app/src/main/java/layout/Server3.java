package layout;
        import java.io.IOException;
        import java.io.ObjectOutputStream;
        import java.io.OutputStream;
        import java.io.PrintStream;
        import java.net.InetAddress;
        import java.net.NetworkInterface;
        import java.net.ServerSocket;
        import java.net.Socket;
        import java.net.SocketException;
        import java.util.ArrayList;
        import java.util.Enumeration;



public class Server3 {
    playground activity;
    ServerSocket serverSocket;
    String message = "";
    static final int socketServerPORT = 8080;
    ArrayList<player_model> persons;
    ArrayList<Socket> clients = new ArrayList<>();

    public Server3(playground activity) {
        this.activity = activity;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
        persons = addPlayer.getPlayerlist();
    }

    public int getPort() {
        return socketServerPORT;
    }

    public void onDestroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    void sendJsonToClients(){
        for(Socket client : clients){
            SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                    client);
            socketServerReplyThread.run();
        }
    }

    private class SocketServerThread extends Thread {

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(socketServerPORT);

                while (true) {
                    Socket socket = serverSocket.accept();
                    clients.add(socket);

                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                            socket);
                    socketServerReplyThread.run();

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private class SocketServerReplyThread extends Thread {

        public Socket hostThreadSocket;

        SocketServerReplyThread(Socket socket) {
            hostThreadSocket = socket;
            persons = addPlayer.getPlayerlist();
        }


        @Override
        public void run() {
            OutputStream outputStream;

            //old stuff
            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                String send = addPlayer.getJsonArray().toString();
                printStream.print(send);
                printStream.close();
                byte[] b = send.getBytes("UTF-8");
                System.out.println("Size:"+b.length);
                System.out.println("Datei:"+send);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }
        }

    }

    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Server running at : "
                                + inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }
}