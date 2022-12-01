/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mediacontrol;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

/**
 *
 * @author ITU
 */
public final class MediaClient {
    private Player jlPlayer;
    private final Socket s;

    public MediaClient() throws IOException {
        this.s=new Socket("192.168.43.94",9080);
        this.showListMusic();
        this.sendChoiceToServer();
        this.playMusic();
        
        /*this.showListImage();
        this.sendChoiceToServer();
        this.showImage();*/
    }

    public void playMusic() throws IOException {
        List<Byte> byteMusic;
        byte[] musicToPlay;
        
        try {
            DataInputStream musicInput = new DataInputStream(this.s.getInputStream());
            byte music=musicInput.readByte();
            byteMusic=new ArrayList<>();
            
            while(musicInput.available()!=0){
                byteMusic.add(music);
                music=musicInput.readByte();
            }
            
            musicToPlay=new byte[byteMusic.size()];
            for(int i=0;i<byteMusic.size();i++){
                musicToPlay[i]=byteMusic.get(i);
                jlPlayer = new Player(new ByteArrayInputStream(musicToPlay));
                        new Thread() {
                        @Override
                            public void run() {
                                try {
                                    jlPlayer.play();
                                } catch (JavaLayerException e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                        }.start();
            }
              
            } catch (JavaLayerException e){
                System.out.println("une erreur a ete rencontree lors du streaming");
        }

    }
    
    public void close() {
        if (jlPlayer != null){
            jlPlayer.close();
        }
    }
    
    public void showImage(){
        List<Byte> byteImage;
        byte[] imageToShow;
        JFrame fenetre=new JFrame();
        JLabel label=new JLabel();
        fenetre.setSize(200,200);
        fenetre.setLocationRelativeTo(null);
        fenetre.setVisible(true);
        fenetre.add(label);
        
        try {
            DataInputStream ImageInput = new DataInputStream(this.s.getInputStream());
            byte image=ImageInput.readByte();
            byteImage=new ArrayList<>();
            
            while(ImageInput.available()!=0){
                byteImage.add(image);
                image=ImageInput.readByte();
            }
            
            imageToShow=new byte[byteImage.size()];
            for(int i=0;i<byteImage.size();i++){
                imageToShow[i]=byteImage.get(i);
                ImageIcon img = new ImageIcon(imageToShow);
                
                new Thread() {
                @Override
                    public void run() {
                        label.setIcon(img);
                    }
                }.start();
                
            }
        }catch(IOException e){
            System.out.println(e);
        }
    }
    
    public void sendChoiceToServer() throws IOException{
        System.out.println("\nyour choice: ");
        Scanner sc=new Scanner(System.in);
        int num=sc.nextInt();
        if(num==0 && jlPlayer!=null){
            jlPlayer.close();
        }
        else{
            DataOutputStream out=new DataOutputStream(s.getOutputStream());
            out.writeInt(num);
            out.flush();
            System.out.println("wait for a second...");
        }
    }
    
    public void showListMusic() throws IOException{
        DataInputStream listMusic=new DataInputStream(s.getInputStream());
        String list=listMusic.readUTF();
        System.out.println(list);
    }
    
    public void showListImage() throws IOException{
        DataInputStream listImage=new DataInputStream(s.getInputStream());
        String list=listImage.readUTF();
        System.out.println(list);
    }
    
    public static void main(String[] args) throws IOException {
        MediaClient mediaClient = new MediaClient();
        

    }
    
}
