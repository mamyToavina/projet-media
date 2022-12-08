/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlMedia;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;


public final class MediaClient {
    private Player jlPlayer;
    private final Socket k;
    private ImageIcon img;
    private BufferedImage bufferedImage;

    public MediaClient() throws IOException, InterruptedException {
        this.k=new Socket("localhost",9080);
        this.showListMusic();
        this.sendChoiceToServer();
        this.playMusic();
        
        /*this.showListImage();
        this.sendChoiceToServer();
        this.showImage();*/
    }

    public void playMusic() throws IOException, InterruptedException {
        byte[] data=new byte[4096];
        new Thread() {
            @Override
            public void run() {
                try {
                    DataInputStream entree=new DataInputStream(k.getInputStream());
                    DataInputStream mus;
                    while(entree.read()!=-1){
                        entree.read(data);
                        mus=new DataInputStream(new ByteArrayInputStream(data));
                        jlPlayer = new Player(mus);
                        
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    //Thread.sleep(700);
                                    jlPlayer.play();
                                    
                                } catch (JavaLayerException e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                        }.start();
                        
                    }
                } catch (IOException | JavaLayerException  ex) {
                    
                }
            }
        }.start();

    }
    
    /*public void playVideo(){
        JFrame f=new JFrame();
        f.setLocationRelativeTo(null);
        f.setSize(1000,600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        
        Canvas c=new Canvas();
        c.setBackground(Color.black);
        JPanel p=new JPanel();
        p.setLayout(new BorderLayout());
        p.add(c);
        p.add(p);
        
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),"C:/Program Files/VideoLAN/VLC");
        Native.LoadLibrary(RuntimeUtil.getLibVlcLibraryName(),LibVlc.class);
        MediaPlayerFactory mpf=new MediaPlayerFactory();
        EmbeddedMediaPlayer emp =mpf.newEmbeddedMediaPlayer(new Win32FullScreenStrategy(f));
        emp.setVideoSurface(mpf.newVideoSurface(c));
        emp.toggleFullScreen();
        emp.setEnableMouseInputHandlig(false);
        emp.setEnableKeyInputHandling(false);
        
        String file="";
        emp.prepareMedia(file);
        emp.play();
    }*/
    
    public void showImage() throws IOException{
        JFrame jFrame=new JFrame("image recu");
        jFrame.setSize(400,400);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JLabel jLabelText=new JLabel("waiting for image from client...");
        jFrame.add(jLabelText,BorderLayout.SOUTH);
        jFrame.setVisible(true);
        
        
        BufferedInputStream bufferedInputStream;
        InputStream inputStream = k.getInputStream();
            bufferedInputStream = new BufferedInputStream(inputStream);
        
        
        new Thread(){
            
            @Override
            public void run(){
                
                try {
                    bufferedImage = ImageIO.read(bufferedInputStream);
                } catch (IOException ex) {
                    Logger.getLogger(MediaClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                JLabel jlabelPic=new JLabel(new ImageIcon(bufferedImage));
                jLabelText.setText("image received");
                jFrame.add(jlabelPic,BorderLayout.CENTER);
            }
        }.start();
    }
    
    public void sendChoiceToServer() throws IOException{
        System.out.println("\nyour choice: ");
        Scanner sc=new Scanner(System.in);
        int num=sc.nextInt();
        if(num==0 && jlPlayer!=null){
            jlPlayer.close();
        }
        else{
            DataOutputStream out=new DataOutputStream(k.getOutputStream());
            out.writeInt(num);
            out.flush();
            System.out.println("wait for a second...");
        }
    }
    
    public void showListMusic() throws IOException{
        DataInputStream listMusic=new DataInputStream(k.getInputStream());
        String list=listMusic.readUTF();
        System.out.println(list);
    }
    
    public void showListImage() throws IOException{
        DataInputStream listImage=new DataInputStream(k.getInputStream());
        String list=listImage.readUTF();
        System.out.println(list);
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        MediaClient mediaClient = new MediaClient();
        
    }
    
}
