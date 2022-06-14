
import java.awt.List;
import java.awt.TextArea;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.rmi.RemoteException;

public class ChatFrame extends Frame {

    private ChatServer server;
    private ChatClient client;
    private String name;
    private TextArea entryArea = new TextArea(5, 70);
    private Button sendButton = new Button("Send");
    private Button logoutButton = new Button("Close");
    private ProjectForkJoin pj;
    public List clientList = new List(20, true);
    public TextArea chatArea = new TextArea(20, 70);
    
    public ChatFrame(ChatServer server, String clientName) throws RemoteException {
        super("Chat Client - " + clientName);
        this.server = server;
        this.name = clientName;
        this.setBounds(0, 0, 700, 500);
        
        this.client = new ChatClientGUIImpl(clientName, clientList, chatArea);
        
        String[] clientNames = server.login(client);
        for (String client : clientNames) {
            clientList.add(client);
        }
        //Call all the methods
        this.setupComponents();
        this.setupEvents();
    }

    private void setupComponents() {
        this.setLayout(new FlowLayout());
        this.chatArea.setEditable(false);
        this.add(chatArea);
        this.add(clientList);
        this.add(entryArea);
        Panel p = new Panel();
        p.add(sendButton);
        p.add(logoutButton);
        this.add(p);
    }

    private void setupEvents() {
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                setVisible(false);
            }
        });
        this.logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                setVisible(false);
            }
        ;
        });
        this.sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] clientNames = clientList.getSelectedItems();
                for (String clientName : clientNames) {
                    try {
                        server.sendMessage(name, clientName, entryArea.getText());
                        chatArea.append("Sent message to: " + clientName + "\r\n");
                    } catch (RemoteException re) {
                        re.printStackTrace();
                        chatArea.append("Send message failes for: " + clientName + "\r\n");
                    }
                }
                entryArea.setText("");
            }
        });
    }
}
