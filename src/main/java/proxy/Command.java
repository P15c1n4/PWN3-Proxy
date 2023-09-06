
package proxy;

import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.ExemptionMechanismException;


public class Command extends Thread{
  
    ArrayList<String> fila;
    
    public Command(ArrayList<String> fila){
        this.fila = fila;
    }
   
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        
        while (true){

            String comando = scanner.nextLine();

            String[] valida = comando.split(" ");
            if(valida.length > 1 && (valida[0].equals("C_") || valida[0].equals("S_"))){

                switch (valida[1]) {
                    case "mana":
                        
                        ManaUpDown();                        
                        fila.clear();
                        break;
                    case "setmana":
                        
                        SetMana(valida[2]);
                        fila.clear();
                        break;
                    default:
                        try {
                            fila.add(comando);
                            sleep(800);
                        } catch (InterruptedException ex) {
                            System.out.println(ex);
                        }

                        fila.clear();
                        break;

                }
            }
        }
    }
 
    
    private void SetMana(String mana){
        try {
            fila.add(C_ManaSetUpdate(Integer.parseInt(mana)));
            sleep(800);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
    }
    
  
    private void ManaUpDown(){
        for (int i = 10; i < 60; i += 20) {
            if(i == 50){
                try {
                    fila.add(C_ManaSetUpdate(i));
                    sleep(800);
                    fila.add(C_ManaSetUpdate(100));
                    sleep(800);
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }

            }else{

                try {
                    fila.add(C_ManaSetUpdate(i));                                        
                    sleep(800);
                    fila.add(C_ManaSetUpdate(100 - i));
                    sleep(800);
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }
        }        
    }
    
    
    
    
    
    // pacotes
    
    public String C_ManaSetUpdate(int mpTotal){
        StringBuilder result = new StringBuilder();

        result.insert(0, "6D61")
              .insert(result.length(), ReverseString(IntToHex(mpTotal, "8")))
              .insert(result.length(), "0000");

        return "S_ "+result.toString();
    }


    //traduções basicas

    private String IntToHex(int num, String size){
        String hexadecimal = String.format("%0"+size+"X", num);

        return hexadecimal;
    }

    private String ReverseString(String Hex){
        String result = "";

        for (int i = Hex.length() - 3; i >= -2; i-=2) {
            result += Hex.charAt(i+1);
            result += Hex.charAt(i+2);
        }

        return result;
    }

}
