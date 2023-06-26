
import java.math.BigInteger;
import java.net.Socket;


public class AutoInject {
    
    
    public String AutoInject(String Hex, Socket inputSocket){
    
        
        //Auto loot!
        if(Hex.contains("6D6B")){
            
            StringBuilder stgAtuo = new StringBuilder();
            
            String newHex = Hex.substring(Hex.indexOf("6D6B"));
            
            int pos2 = (Integer.valueOf(TradutorInt(22, 26, newHex, true))*2)+26;
            
            String dropName = TradutorString(26, pos2, newHex);
            if(dropName.contains("Drop")){
                stgAtuo.insert(0,"C_ 6565"+newHex.substring(4,12));
            }
                       
            return stgAtuo.toString();
            
        }else{
          
            return null;
        }


        
    }
    
    //Funções basicas
    private String TradutorInt(int inicio, int fim, String Hex, boolean big){
        String result = "";
        
        if(inicio != 0 && fim != 0){
            result = Hex.substring(inicio, fim);
        }else{
            result = Hex;
        }
        
        if(big){
           result = ReverseString(result); 
        }
        
        int i = Integer.parseInt(result, 16);
        result = String.valueOf(i);
        return result;
   }
   //Inversor de String
   private String ReverseString(String Hex){
        String result = "";

        for (int i = Hex.length() - 3; i >= -2; i-=2) {
            result += Hex.charAt(i+1);
            result += Hex.charAt(i+2);
        }

        return result;
   }
   
   private String TradutorString(int inicio, int fim, String Hex){
        String result = "";
        
        result = Hex.substring(inicio, fim);
        result = new String(new BigInteger(result, 16).toByteArray());
        return result;
   }
}
