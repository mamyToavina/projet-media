/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlMedia;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author ITU
 */
public final class ServeurMedia {
    private ServerSocket ss;
    private Socket sc;
    private ArrayList<String> listeMusic=new ArrayList<>();
    private ArrayList<String> listeImage=new ArrayList<>();
    public ServeurMedia() throws FileNotFoundException, IOException{
        ss=new ServerSocket(9080);
        sc=ss.accept();
        String liste1="D:/music/07 Ndray andro any.mp3";
        String liste2="D:/music/Gasikara (Zaka Rajaonia).mp3";
        String liste3="D:/music/Fitiavako masiaka - Ny Ainga.mp3";
        String liste4="D:/music/AMBONDRONA - Antso ( CLIP GASY ).mp3";
        
        String im1="D:/image/Acer_Wallpaper_01_5000x2814.jpg";
        String im2="D:/image/Acer_Wallpaper_02_5000x2813.jpg";
        String im3="D:/image/Acer_Wallpaper_03_5000x2814.jpg";
        String im4="D:/image/Planet9_Wallpaper_5000x2813.jpg";
        
        listeMusic.add(liste1);
        listeMusic.add(liste2);
        listeMusic.add(liste3);
        listeMusic.add(liste4);
        
        listeImage.add(im1);
        listeImage.add(im2);
        listeImage.add(im3);
        listeImage.add(im4);
        
        /*this.sendListMusicToClient();
        int req=this.getChoice();
        this.sendMusicToPlay(req);*/
        
        this.sendListImageToClient();
        int req=this.getChoice();
        this.sendImage(req);
        
    }
    
    public void sendListMusicToClient() throws IOException{
        DataOutputStream listToSend=new DataOutputStream(getSc().getOutputStream());
        String liste="Tapez le numero de la music que vous voulez ecouter : ";
        for(int i=0;i<getListeMusic().size();i++){
            liste+="\n"+(i+1)+" - "+" "+getListeMusic().get(i);
        }
        
        listToSend.writeUTF(liste);
        listToSend.flush();
    }
    
    public void sendListImageToClient() throws IOException{
        DataOutputStream listToSend=new DataOutputStream(getSc().getOutputStream());
        String liste="Tapez le numero de l image que vous voulez afficher : ";
        for(int i=0;i<getListeImage().size();i++){
            liste+="\n"+(i+1)+" - "+" "+getListeImage().get(i);
        }
        
        listToSend.writeUTF(liste);
        listToSend.flush();
    }
    
    public void sendMusicToPlay(int num) throws FileNotFoundException, IOException, InterruptedException{
        File f=new File(getListeMusic().get(num-1));
        FileInputStream fis=new FileInputStream(f);
        DataInputStream dis=new DataInputStream(fis); 
        //byte[] data=dis.readAllBytes();
        byte[] data=new byte[4096];
        int buffer=0;
        int transfere=0;
        int pourcentage=0;
        /*DataOutputStream dout=new DataOutputStream(getSc().getOutputStream());
        dout.write(data, 0,data.length);
        dout.flush();*/
        DataOutputStream dout=new DataOutputStream(getSc().getOutputStream());

        while((buffer=dis.read(data))!=-1){
            Thread.sleep(150);
            dout.write(data, 0,buffer);
            transfere+=buffer;
            pourcentage=(int) (transfere*100/f.length());
            System.out.println(pourcentage+"%");
            dout.flush();
        }
        
    }
    
    public void sendImage(int num) throws IOException{
        ImageIcon imageIcon=new ImageIcon(getListeImage().get(num-1));
        
   
        try {
            OutputStream outputStream=sc.getOutputStream();
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
                Image image=imageIcon.getImage();
                BufferedImage bufferedImage=new BufferedImage(image.getWidth(null),image.getHeight(null),BufferedImage.TYPE_INT_RGB);

                Graphics graphics=bufferedImage.createGraphics();
                graphics.drawImage(image,0,0,null);
                graphics.dispose();

                ImageIO.write(bufferedImage,"png",bufferedOutputStream);
            }

        } catch (IOException ex) {

        }
 
    }
    
    public int getChoice() throws IOException{
        System.out.println("Waiting for request...");
        DataInputStream choiceClient=new DataInputStream(getSc().getInputStream());
        int num=choiceClient.readInt();
        System.out.println(num);
        return num;
    }
    
    public void addListMusic(String path){
        this.getListeMusic().add(path);
    }
    
    public ServerSocket getSs() {
        return ss;
    }

    public void setSs(ServerSocket ss) {
        this.ss = ss;
    }

    public Socket getSc() {
        return sc;
    }

    public void setSc(Socket sc) {
        this.sc = sc;
    }

    public ArrayList<String> getListeMusic() {
        return listeMusic;
    }

    public void setListeMusic(ArrayList<String> listeMusic) {
        this.listeMusic = listeMusic;
    }

    public ArrayList<String> getListeImage() {
        return listeImage;
    }

    public void setListeImage(ArrayList<String> listeImage) {
        this.listeImage = listeImage;
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        ServeurMedia media = new ServeurMedia();
        media.sendMusicToPlay(media.getChoice());
        
    }
    
     
}
