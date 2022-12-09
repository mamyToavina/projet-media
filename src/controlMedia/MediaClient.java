/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlMedia;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
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
        this.sendTypeToServer();
        this.showList();
        this.sendChoiceToServer();
        this.startFile();
        
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
                                    Thread.sleep(700);
                                    jlPlayer.play();
                                    
                                } catch (JavaLayerException e) {
                                    System.out.println(e.getMessage());
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(MediaClient.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }.start();
                        
                    }
                } catch (IOException | JavaLayerException  ex) {
                    
                }
            }
        }.start();

    }
    
    public void playVideo(){
        byte[] data=new byte[4096];
        ByteArrayOutputStream bout=new ByteArrayOutputStream();
        
        new Thread() {
            @Override
            public void run() {
                try {
                    InputStream entree=k.getInputStream();
                    InputStream mus;
                    int byteRead = 0;
                    File temp;
                    FileOutputStream musicPlay;
                    while(entree.read()!=-1){
                        entree.read(data);
                        mus=new ByteArrayInputStream(data);
                        bout.write(data,0,byteRead);
                        byte[] byteMusic=bout.toByteArray();
                        temp=File.createTempFile("temporaire", "mp4");
                        temp.deleteOnExit();
                        musicPlay=new FileOutputStream(temp);
                        musicPlay.write(byteMusic);
                        
                   
                        video = new Media(temp.toURI().toString());
                        
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    //Thread.sleep(700);
                                    MediaPlayer media=new MediaPlayer(video);
                                    media.play();
                                    
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                        }.start();
                        
                    }
                } catch (IOException ex) {
                    
                }
            }
        }.start();
    }
    
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
    
    public void sendTypeToServer() throws IOException{
        System.out.println("type :1:MUSIC\n       2:IMAGES\n       3:VIDEOS\n       0:QUIT\n");
        Scanner sc=new Scanner(System.in);
        int num=sc.nextInt();
        DataOutputStream out=new DataOutputStream(k.getOutputStream());
        out.writeInt(num);
        out.flush();
        System.out.println("wait for a second...");
    }
    
    public void showList() throws IOException{
        DataInputStream listMusic=new DataInputStream(k.getInputStream());
        String list=listMusic.readUTF();
        System.out.println(list);
    }
    
    public void startFile() throws IOException, InterruptedException{
        DataInputStream listMusic=new DataInputStream(k.getInputStream());
        String list=listMusic.readUTF();
        switch (list) {
            case "send music..." -> this.playMusic();
            case "send photo..." -> this.showImage();
            case "send video..." -> this.playVideo();
            default -> this.sendTypeToServer();
        }
    }
    
    
    public static void main(String args[]) throws IOException, InterruptedException{
        MediaClient mClient=new MediaClient();
        
    }
    
}
