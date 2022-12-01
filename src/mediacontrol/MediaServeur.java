/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mediacontrol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author ITU
 */
public final class MediaServeur {
    private ServerSocket ss;
    private Socket sc;
    private ArrayList<String> listeMusic=new ArrayList<>();
    private ArrayList<String> listeImage=new ArrayList<>();
    public MediaServeur() throws FileNotFoundException, IOException{
        ss=new ServerSocket(9080);
        sc=ss.accept();
        String liste1="E:/PROJET S3/JAVA MR NAINA/music/HOSANA EKEKO.mp3";
        String liste2="E:/PROJET S3/JAVA MR NAINA/music/RONAN KEATING When you say Nothing at all - Copie.mp3";
        String liste3="E:/PROJET S3/JAVA MR NAINA/music/mitonia-official-lyrics-video-fy-rasolofoniaina - Copie.mp3";
        String liste4="E:/PROJET S3/JAVA MR NAINA/music/Ed Sheeran - Shape of You - MP3 320.mp3";
        
        String im1="E:/PROJET S3/JAVA MR NAINA/image/feature-1.jpg";
        String im2="E:/PROJET S3/JAVA MR NAINA/image/feature-2.jpg";
        String im3="E:/PROJET S3/JAVA MR NAINA/image/feature-3.jpg";
        String im4="E:/PROJET S3/JAVA MR NAINA/image/feature-4.jpg";
        
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
    
    public void sendMusicToPlay(int num) throws FileNotFoundException, IOException{
        File f=new File(getListeMusic().get(num-1));
        FileInputStream fis=new FileInputStream(f);
        DataInputStream dis=new DataInputStream(fis); 
        byte[] data=dis.readAllBytes();
        DataOutputStream dout=new DataOutputStream(getSc().getOutputStream());
        dout.write(data, 0,data.length);
        dout.flush();
        
    }
    
    public void sendImage(int num) throws IOException{
        File f=new File(getListeImage().get(num-1));
        FileInputStream fis=new FileInputStream(f);
        DataInputStream dis=new DataInputStream(fis); 
        byte[] data=dis.readAllBytes();
        DataOutputStream dout=new DataOutputStream(getSc().getOutputStream());
        dout.write(data, 0,data.length);
        dout.flush();
 
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
    
    public static void main(String[] args) throws IOException{
        MediaServeur media = new MediaServeur();
    }
        
}
