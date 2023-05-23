package com.wetin.ChatServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

/**
   @Auther: xiao wetin
   @DateTime: 2023/5/22 - 05 - 22 - 14:01
   @Description: com.wetin.ChatServer-----
   @version: 1.0.1
 */
/*说明:如果一个类,需要有界面的显示,那么该类就需要继承自JFrame,此时,该类就被称为一个'窗体类'
1.定义JFrame窗体中的组件
2.在构造方法中初始化窗体中组件
3.使用网络编程完成数据的传输(TCP,UDP 协议)
4.实现'发送'按钮的监听点击事件
5.实现回车键发送数据
*/
class ServerChatMain extends JFrame implements ActionListener,KeyListener {
    public static void main(String[] args){
        new ServerChatMain();//调用构造方法
    }

    //属性
    private JTextArea jta;//文本域
    private JScrollPane jsp;//滚动条
    private JPanel jp;//面板
    private JTextField jtf;//文本框
    private JButton jb;//按钮
    private BufferedWriter bw = null;//输出流
    private static int serverPort;

    //使用static 静态代码块读取外部配置文件
    //特点1:在类加载的时候,自动执行,
    //特点2:一个类只会被加载一次,因此静态代码块在程序中仅会被执行一次
    static {
        Properties prop = new Properties();
        try {
            //加载
            prop.load(new FileReader("chat.properties"));
            //给属性赋值
            serverPort = Integer.parseInt(prop.getProperty("serverPort"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //构造方法
    public ServerChatMain(){
        jta = new JTextArea();//初始化文本域
        jta.setEditable(false);
        jsp = new JScrollPane(jta);//将文本域添加到滚动条中，实现滚动效果
        jp = new JPanel();//面板
        jtf =new JTextField(15);//文本框
        jb = new JButton("发送");
        jp.add(jtf);
        jp.add(jb);

        this.add(jsp, BorderLayout.CENTER);
        this.add(jp,BorderLayout.SOUTH);

        this.setTitle("QQ聊天 服务器");
        this.setSize(300,300);
        this.setLocation(300,300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        /*         TCP 服务端start        */
        //给发送按钮绑定一个监听点击事件
        jb.addActionListener(this);
        //给文本绑定一个键盘点击事件
        jtf.addKeyListener(this);
        try {
            //1.创建一个服务端的套接字
            ServerSocket serverSocket = new ServerSocket(serverPort);
            //2.等待客户端的连接
            Socket socket = serverSocket.accept();
            //3.获取socket 通道的输入流
            //InputStream in = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //4.获取socket 通道的输出流
            //问题:什么时候需要写出数据???当用户点击'发送'按钮的时候才需要写出数据
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //循环读取数据,并拼接到文本域中
            String line;
            while ((line = br.readLine()) != null){
                jta.append(line + System.lineSeparator());
            }


            //5.关闭socket 通道
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        /*     TCP 服务端 end    */


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        sendDataToSocket();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            sendDataToSocket();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    //定义一个方法,实现将数据发送到socket通道中
    private void sendDataToSocket(){
        //System.out.println("send button click");
        //1.获取文本框内容
        String text =jtf.getText();
        //2.拼接需要发送的数据
        text = "服务端对客户端说:" + text;
        //3.自己也需要显示
        jta.append(text+System.lineSeparator());
        //4.发送
        try {
            bw.write(text);
            bw.newLine();
            bw.flush();
            //5.清空文本框内容
            jtf.setText("");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
