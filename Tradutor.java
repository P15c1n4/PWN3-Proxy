
import java.math.BigInteger;
import java.net.Socket;


public class Tradutor {
    //Valida que está enviando o pacote(alterado automaticamente)
    boolean isClient = false; //não alterar!!!
    
    //Resolver recusivamente todos os pacotes associados a um pacote inicial
    boolean cascatResolve = true;
    
    //Mostra coordenadas
    boolean showCoord = true;
    //Mostra pacotes do cliente
    boolean showClient = true;
    //Mostra pacotes do servidor
    boolean showServer = true;

    Socket inputSocket;
   
   public String Tradutor(String Hex, Socket inputSocket){
       this.inputSocket = inputSocket;
       
       //controle para sabe origen do pacote
       //142.44.191.189
       if(!inputSocket.getInetAddress().getHostAddress().equals("192.168.15.202")){
           isClient = true;
       }
       //Identificação do pacote
       String magicByte = Hex.substring(0,4);
       
       StringBuilder stgFinal = new StringBuilder();
       
       //Traducao dos pacotes                     !=====TODOS OS CAMPOS PODEM SER UTILIZADOS OU NÃO DE ACORDO COM O USUÁRIO=====!
       
       //Cliente
        if(isClient && showClient){
            //Client
            switch (magicByte){
                //Pacote Noop
                case "0000":
                    
                    return TradutorNoop(Hex);
                
                //Pacote de coordenada
                case "6D76": 
                    return "";
                    //return "\n"+TradutorCoordenada(Hex,true);
                
                //Pacote de inicio e fim de disparo
                case "6672":
                    
                    return "\n"+TradutorC_ShotStart(Hex);
                    
                    
                //Pacote de uso de habilidade
                case "2A69":
                    //return Hex;
                    return "\n"+TradutorC_UsoHabilidade(Hex);
                                       
                //pacote desconhecido   
                case "1703":
                    return "";
                    
                //Pacote de jump    
                case "6A70":
                    //return Hex;
                    return "\n"+TradutorC_Jump(Hex);
                    
                //Pacote de toca de arma
                case "733D":
                    
                    return "\n"+TradutorC_WeaponChange(Hex);                   
                
                //pacote de reload(arma)    
                case "726C":   
                    
                    return "\n"+TradutorReloadWep(Hex);
                
                //Pacote Respawn
                case "7273":
                    
                    return "\n"+TradutorC_Respawn(Hex);
                
                    
                //Pacote Pick Objeto
                case "6565":
                    return "\n"+TradutorC_PickObj(Hex);
                
                    
                //Pacote de Teleport   
                case "6674":
                    
                    return "\n"+TradutorC_Teleport(Hex);
                
                    
                //Pacote Identificação
                case "0100":
                    
                    return "\n"+TradutorC_Identificador(Hex);
                
                //Pacote chat
                case "232A":
                    
                    return "\n"+TradutorC_Chat(Hex);
                    
                default:
                    //return "";
                    return "\n"+DataSplit(Hex);
            
            }   
        }
        //Server
        else if(!isClient && showServer){
            //return "";
            
            switch (magicByte){
                //Pacote noop
                case "0000": 
            
                    return "";
                    //return "\n"+TradutorNoop(Hex);
                
                //Pacote de toca de arma
                case "733D":
                    
                    return "\n"+TradutorS_WeaponChange(Hex);
                    
                
                //Pacote Coordenada de Objeto
                case "6D76":
                    //return "";
                    return "\n"+TradutorS_CoordSObj(Hex);
                
                //Tradutor Finalização de Objeto
                case "7878":
                    
                    return "\n"+TradutorS_FinalObj(Hex);
                    
                //Pacotes Mob possição
                case "7073":
                    return "";
                    //return "\n"+TradutorS_MobPosition(Hex);
                  
                    
                //Pacotes Mana Recovery 
                case "6D61":
                    //return "";
                    return "\n"+TradutorS_ManaRecover(Hex);
                
                //Pacote de Spawn de objetos/skill   
                case "6D6B":
                    
                    return "\n"+TradutorS_ObjSpawn(Hex);
                
                //Pacote HP Total 
                case "2B2B":
                    
                    return "\n"+TradutorS_HpTotal(Hex);                    
                    
                
                //Pacote Tiro com arma 
                case "6C61":
                    
                    return "\n"+TradutorS_ShotWep(Hex);

                //Pacote Recarda de Munição
                case "726C":
                    
                    return "\n"+TradutorS_WepReload(Hex);
                
                //Pacote de Aggro
                case "7374":
                    
                    return "\n"+TradutorS_Aggro(Hex);
                
                //Pacote de Respawn    
                case "7273":
                    
                    return Hex;//"\n"+TradutorS_Respawn(Hex);
                
                    
                //Pacote de skill Mobs(7472)
                case "7472":
                    
                    return "\n"+TradutorS_MobSkill(Hex);
                
                    
                //Pacote de Drop (6370)
                case "6370":
                    
                    return "\n"+TradutorS_Drop(Hex);
                 
                    
                //Pacote desconhecido    
                case "6C68":
                    return "";
                
                    
                //Pacote de Timer    
                case "6364":
                    
                   return "\n"+TradutorS_Timer(Hex);
                    
      
                //Pacote Dead
                case "2D39":
                    
                    return "\n"+TradutorS_Dead(Hex);
                
                    
                    
                //Pacote chat
                case "232A":
                    
                    return "\n"+TradutorS_Chat(Hex);
                    
                    
                default:
                    //return Hex;
                    return "\n"+DataSplit(Hex);

                    
            }
            
        }else{
            return "";
        }
            
   }  
        

   
   //Tradução de pacotes

   //Tradutor Chat(Server)(232A)
   private String TradutorS_Chat(String Hex){
        StringBuilder stgChat  = new StringBuilder();
        
        int posStg = (Integer.valueOf(TradutorInt(12, 16, Hex, true))*2)+16;
        
        stgChat.insert(0, Hex.substring(0, 4)+" |")
               .insert(stgChat.length(), "PlayerID:"+DataSplit(Hex.substring(4,12))+" |")
               .insert(stgChat.length(), "Mensage:"+TradutorString(16, posStg, Hex)+" |");
        
        if(cascatResolve && Hex.length() > posStg){
           stgChat.insert(stgChat.length(), Tradutor(Hex.substring(posStg), inputSocket));
        }
        
        return stgChat.toString();
   }
   
   
   
   //Tradutor Chat(Cliente)(232A)
   private String TradutorC_Chat(String Hex){
        StringBuilder stgChat  = new StringBuilder();
        
        int posStg = (Integer.valueOf(TradutorInt(4, 8, Hex, true))*2)+8;
        
        stgChat.insert(0, Hex.substring(0, 4)+" |")
               .insert(stgChat.length(), "Mensage:"+TradutorString(8, posStg, Hex)+" |");
        
        if(cascatResolve && Hex.length() > posStg){
           stgChat.insert(stgChat.length(), Tradutor(Hex.substring(posStg), inputSocket));
       }
        
        return stgChat.toString();
        
   }
   
   
   
   //Tradutor Morte(Server)(2D39)
   private String TradutorS_Dead(String Hex){
       StringBuilder stgDead  = new StringBuilder();
       
       stgDead.insert(0, Hex.substring(0, 4)+" |")
              .insert(stgDead.length(), "ObjIdDead:"+Hex.substring(8,12)+" |")
              .insert(stgDead.length(), "MobId:"+DataSplit(Hex.substring(12,20))+" |")
              .insert(stgDead.length(), Hex.substring(20,24)+" |");
       
       if(cascatResolve && Hex.length() > 24){
           stgDead.insert(stgDead.length(), Tradutor(Hex.substring(24), inputSocket));
       }
       
       return stgDead.toString();
   }
   
   
   
   //Tradutor Identificador
   private String  TradutorC_Identificador(String Hex){
       StringBuilder stgId  = new StringBuilder();
       
       int posStg = (Integer.valueOf(TradutorInt(8, 12, Hex, true))*2)+12;
       
       stgId.insert(0, Hex.substring(0, 4)+" |")
            .insert(stgId.length(), Hex.substring(4,8)+" |")
            .insert(stgId.length(), TradutorString(12, posStg, Hex)+" |");
       
       if(cascatResolve && Hex.length() > posStg){
           stgId.insert(stgId.length(), Tradutor(Hex.substring(posStg), inputSocket));
       }
       
       return stgId.toString();
   }
   
   
   //Tradutor Teleporte(Client)(6674)
   private String TradutorC_Teleport(String Hex){
       StringBuilder stgTele  = new StringBuilder();
       
       int posStg1 = (Integer.valueOf(TradutorInt(4, 8, Hex, true))*2)+8;
       int posStg2 = (Integer.valueOf(TradutorInt(posStg1, posStg1+4, Hex, true))*2)+posStg1+4;
       
       stgTele.insert(0, Hex.substring(0, 4)+" |")
              .insert(stgTele.length(), "INF:Teleport |Mapa:"+TradutorString(8, posStg1, Hex)+" |")
              .insert(stgTele.length(), "Local:"+TradutorString(posStg1+4, posStg2, Hex)+" |");
       
       if(cascatResolve && Hex.length() > posStg2){
           stgTele.insert(stgTele.length(), Tradutor(Hex.substring(posStg2), inputSocket));
           
       }
       
       return stgTele.toString();
       
   }
   
   
   //Tradutor de Timer(Server)(6364)
   private String TradutorS_Timer(String Hex){
       StringBuilder stgTimer  = new StringBuilder();
       
       stgTimer.insert(0, Hex.substring(0, 4)+" |")
               .insert(stgTimer.length(), "TempoRestante:"+TradutorInt(4, 12, Hex, true)+" ms |");
       
       if(cascatResolve && Hex.length() > 12 ){
           stgTimer.insert(stgTimer.length(), Tradutor(Hex.substring(12), inputSocket));
       }
       
       return stgTimer.toString();
   }
   
   
   //Tradutor Pick Drop/Item/Obj(Client)(6565)
   private String TradutorC_PickObj(String Hex){
       StringBuilder stgPickObj  = new StringBuilder();
       
       stgPickObj.insert(0, Hex.substring(0, 4)+" |")
                 .insert(stgPickObj.length(), "INF:Pick |ObjId:"+DataSplit(Hex.substring(4,12))+" |");
       
       if(cascatResolve && Hex.length() > 12){
           stgPickObj.insert(stgPickObj.length(), Tradutor(Hex.substring(12), inputSocket));
           
       }
       
       return stgPickObj.toString();
   }
   
   //Tradutor Drop(Server)(6370)
   private String TradutorS_Drop(String Hex){
       StringBuilder stgDrop  = new StringBuilder();
       
       int posStg = (Integer.valueOf(TradutorInt(4, 8, Hex, true))*2)+8;
       
       stgDrop.insert(0, Hex.substring(0, 4)+" |")
                  .insert(stgDrop.length(), "ItemName:"+TradutorString(8,posStg,Hex)+" |")
                  .insert(stgDrop.length(), "ItemTotal:"+TradutorInt(posStg, posStg+4, Hex, true)+" |")
                  .insert(stgDrop.length(), Hex.substring(posStg+4, posStg+8)+" |");
       
       if(cascatResolve && Hex.length() > posStg+8){
           stgDrop.insert(stgDrop.length(), Tradutor(Hex.substring(posStg+8), inputSocket));
       }
       
       return stgDrop.toString();
   }
   
   
   //Tradutor Mob Skill(Server)(7472)
   private String TradutorS_MobSkill(String Hex){

       StringBuilder stgMobSkill  = new StringBuilder();
   
       int posStg = (Integer.valueOf(TradutorInt(12, 16, Hex, true))*2)+16;
       
       stgMobSkill.insert(0, Hex.substring(0, 4)+" |")
                  .insert(stgMobSkill.length(), "MobId:"+DataSplit(Hex.substring(4,12))+" |")
                  .insert(stgMobSkill.length(), "SkillName:"+TradutorString(16, posStg, Hex)+" |")
                  .insert(stgMobSkill.length(), "TargetId:"+DataSplit(Hex.substring(posStg, posStg+8))+" |");
       
       if(cascatResolve && Hex.length() > posStg+8){
           stgMobSkill.insert(stgMobSkill.length(), Tradutor(Hex.substring(posStg+8), inputSocket));
       }
       
       return stgMobSkill.toString();
    }
   //Tradutor Respawn(Servidor)(7273)
   private String TradutorS_Respawn(String Hex){

       StringBuilder stgRes  = new StringBuilder();
       
       stgRes.insert(0, Hex.substring(0, 4)+" |INF:RespawnCoord |")
             .insert(stgRes.length(), TradutorCoordenadaNoPack(Hex.substring(4,28))+" |")
             .insert(stgRes.length(), DataSplit(Hex.substring(28,40))+" |");
       
       if(cascatResolve && Hex.length() > 40){
           stgRes.insert(stgRes.length(), Tradutor(Hex.substring(40), inputSocket));
       }
       
       return stgRes.toString();
       
   }
   
   //Tradutor Possição de Mobs(Server)(7073)
   private String TradutorS_MobPosition(String Hex){
       StringBuilder stgMobPos  = new StringBuilder();
       
       stgMobPos.insert(0, Hex.substring(0, 4)+" |")
                .insert(stgMobPos.length(), "MobId:"+DataSplit(Hex.substring(4,12))+" |")
                .insert(stgMobPos.length(), TradutorCoordenadaNoPack(Hex.substring(12,36))+" |")
                .insert(stgMobPos.length(), DataSplit(Hex.substring(36,60))+" |");
       
       if(cascatResolve && Hex.length() > 60){
           stgMobPos.insert(stgMobPos.length(), Tradutor(Hex.substring(60), inputSocket));
       }
       
       return stgMobPos.toString();
   }
   
   
   //Tradutor de aggro(Server)(7374)
   private String TradutorS_Aggro(String Hex){
       StringBuilder stgAggro  = new StringBuilder();
       
       int posStg = (Integer.valueOf(TradutorInt(12, 16, Hex, true))*2)+16;
       
       stgAggro.insert(0, Hex.substring(0,4)+" |")
               .insert(stgAggro.length(), "AggroMobId:"+DataSplit(Hex.substring(4,12))+" |")
               .insert(stgAggro.length(), "AggroStatus:"+TradutorString(16, posStg, Hex)+" |")
               .insert(stgAggro.length(), DataSplit(Hex.substring(posStg, posStg+2))+" |");
       
       if(cascatResolve && Hex.length() > posStg+2){
           stgAggro.insert(stgAggro.length(), Tradutor(Hex.substring(posStg+2), inputSocket));
       }
      
       return stgAggro.toString();
       
   }
   
   
   //Tradutor de Recarda de Arma(Server)(726C)
   private String TradutorS_WepReload(String Hex){
       
       StringBuilder stgWepRe = new StringBuilder();
       
       int posName = (Integer.valueOf(TradutorInt(4, 8, Hex, true))*2)+8;
       int posTipeBu = (Integer.valueOf(TradutorInt(posName, posName+4, Hex, true))*2)+posName+4;
       
       stgWepRe.insert(0, Hex.substring(0,4)+" |")
               .insert(stgWepRe.length(), "WepName:"+TradutorString(8, posName, Hex)+" |")
               .insert(stgWepRe.length(), "BulletAmmo:"+TradutorString(posName+4, posTipeBu, Hex)+" |")
               .insert(stgWepRe.length(), "TotalRecaregado:"+TradutorInt(posTipeBu, posTipeBu+4, Hex, true)+" |")
               .insert(stgWepRe.length(),Hex.substring(posTipeBu+4,posTipeBu+8)+" |");
       
       //Resolver Em Cascata
       if(cascatResolve && Hex.length() > posTipeBu+8){
           stgWepRe.insert(stgWepRe.length(), Tradutor(Hex.substring(posTipeBu+8), inputSocket));
       }
       
       return stgWepRe.toString();
       
   }
   
   
   //Tadutor Munição Total(Server)(6C61)
   private String TradutorS_ShotWep(String Hex){
       
       StringBuilder stgTotalBul = new StringBuilder();
       int pos = (Integer.valueOf(TradutorInt(4, 8, Hex, true))*2)+8;
       
            stgTotalBul.insert(0, Hex.substring(0,4)+" |")
                       .insert(stgTotalBul.length(), "WepName:"+TradutorString(8, pos, Hex)+" |")
                       .insert(stgTotalBul.length(), "TotalBullet:"+TradutorInt(pos, pos+4, Hex, true)+" |")
                       .insert(stgTotalBul.length(), Hex.substring(pos+4,pos+8)+" |");
        
        //Resolver Em Cascata
        if(cascatResolve && Hex.length() > pos+8){
            stgTotalBul.insert(stgTotalBul.length(),Tradutor(Hex.substring(pos+8), inputSocket));
        }
         
        return stgTotalBul.toString();
   }
   
   //Tradutor HP Total(server)(2B2B)
   private String TradutorS_HpTotal(String Hex){

       StringBuilder stgHP = new StringBuilder();
       String HpFinal;
       
       if(Hex.substring(16,20).equals("FFFF")){
           HpFinal = "0";
       }else{
           HpFinal = TradutorInt(12, 20, Hex, true);
       }
            stgHP.insert(0, Hex.substring(0,4)+" |")
                 .insert(stgHP.length(), "ObjId:"+DataSplit(Hex.substring(4,12))+" |")
                 .insert(stgHP.length(), "HpTotal:"+HpFinal+" |");
        
        //Resolver Em Cascata    
        if(cascatResolve && Hex.length() > 20){
            stgHP.insert(stgHP.length(), Tradutor(Hex.substring(20) ,inputSocket));
        }
            
        return stgHP.toString();
   }
   
   //Tradutor Recovery
   private String TradutorS_ManaRecover(String Hex){

       
       StringBuilder stgMana = new StringBuilder();
       
        stgMana.insert(0, Hex.substring(0,4)+" |");

                 stgMana.insert(stgMana.length(), "ManaTotal:"+TradutorInt(4, 8, Hex, true)+" |");
                 stgMana.insert(stgMana.length(), Hex.substring(8,12)+" |");
        
        //Resolver em Cascata         
        if(cascatResolve && Hex.length() > 12){
            stgMana.insert(stgMana.length(),(Tradutor(Hex.substring(12), inputSocket)));
            
        }
                 
        return stgMana.toString();
   }
   
   //Tradutor pacote Spawn de objeto/skill (Server)(6D6B)
   private String TradutorS_ObjSpawn(String Hex){
        
        StringBuilder stgUsoHab = new StringBuilder();
        int pos = (Integer.parseInt(TradutorInt(22, 26, Hex, true))*2)+4+22;
        int posFinal = 20+pos+24;

        stgUsoHab.insert(0, Hex.substring(0,4)+" |");

                 stgUsoHab.insert(stgUsoHab.length(),"ObjIdSpawn:"+DataSplit(Hex.substring(4, 12))+" |")
                      
                    //Bloco com byte unico!
                   .insert(stgUsoHab.length(), "OrigenId:"+Hex.substring(12,16)+" |")
                   .insert(stgUsoHab.length(), Hex.substring(16,18)+" ")//continuação com byte unico!
                   .insert(stgUsoHab.length(), Hex.substring(18,22)+" |")//continuação com byte unico!

                    //Fim do bloco
                   .insert(stgUsoHab.length(), "ObjName:"+TradutorString(26, pos, Hex)+" |")
                   .insert(stgUsoHab.length(), TradutorCoordenadaNoPack(Hex.substring(pos,pos+24))+" |")
                   .insert(stgUsoHab.length(),DataSplit(Hex.substring(pos+24, posFinal))+" |");

        //Resolver em Cascata       
        if(cascatResolve && Hex.length() > posFinal){
            
            stgUsoHab.insert(stgUsoHab.length(),Tradutor(Hex.substring(posFinal), inputSocket));
        }
                 
                 
        return stgUsoHab.toString();

   }
   
   
   //Tradutor Finalização de Objeto(Server)(7878)
    private String TradutorS_FinalObj(String Hex){

        StringBuilder stgFinalObj = new StringBuilder();
        
        stgFinalObj.insert(0, Hex.substring(0,4)+" |")
                   .insert(stgFinalObj.length(), "ObjFinID:"+DataSplit(Hex.substring(4,12))+" |");
            
        if(cascatResolve && Hex.length() > 12){
            stgFinalObj.insert(stgFinalObj.length(), Tradutor(Hex.substring(12), inputSocket));
        }
 
        return stgFinalObj.toString();
    }
   
    //Tradutor Noop(Cliente-Server)(0000)
    private String TradutorNoop(String Hex){
        StringBuilder stgNoop = new StringBuilder();
        
        stgNoop.insert(0, "Noop |");
        
        //Resolve em cascata
        if(cascatResolve && Hex.length() > 4){
            stgNoop.insert(stgNoop.length(), Tradutor(Hex.substring(4), inputSocket));
        }
        
        
        return stgNoop.toString();
    }
   
   //Tradutor Coordenada Skill/Obj (Server) (6D76)
   private String TradutorS_CoordSObj(String Hex){

        StringBuilder stgCoord = new StringBuilder();

            stgCoord.insert(stgCoord.length(), Hex.substring(0,4)+" |")
                    .insert(stgCoord.length(), "ObjMovID:"+DataSplit(Hex.substring(4,12))+" |")
                    .insert(stgCoord.length(), TradutorCoordenadaNoPack(Hex.substring(12,36))+" |")
                    .insert(stgCoord.length(), DataSplit(Hex.substring(36,48))+" |");
                    
                    //.insert(stgCoord.length(), TradutorCoordenada(Hex.substring(0,4)+Hex.substring(12,48),false));
       

       if(cascatResolve && Hex.length() > 48){
           stgCoord.insert(stgCoord.length(), Tradutor(Hex.substring(48), inputSocket));
       } 
        
       return stgCoord.toString();
   }
   
   
   //Tadutor Respawn(Client)(7273)
   private String TradutorC_Respawn(String Hex){
       StringBuilder stgResp = new StringBuilder();
       
       stgResp.insert(0, Hex.substring(0,4)+" |INF:Respawn |");
               
       if(cascatResolve && Hex.length() > 4){
           stgResp.insert(stgResp.length(), Tradutor(Hex.substring(4), inputSocket));
       }
       
       return stgResp.toString();
   }
   
   
   //Tadutor Resload(arma)(Client)(726C)
   private String TradutorReloadWep(String Hex){
       
       StringBuilder stgReloWep = new StringBuilder();
       
       stgReloWep.insert(0, Hex.substring(0,4)+" |INF:WeaponReload |");
       
       
       if(cascatResolve && Hex.length() > 4){
           stgReloWep.insert(stgReloWep.length(), Tradutor(Hex.substring(4), inputSocket));
       }
       
       
       return stgReloWep.toString();
   }
   
   
   //Tradutor Troca de Arma(Cliente)(733D)
   private String TradutorC_WeaponChange(String Hex){
       

       StringBuilder stgCWep = new StringBuilder();
       
       stgCWep.insert(0, Hex.substring(0,4)+" |")
               .insert(stgCWep.length(), "WepNum:"+TradutorInt(4,6,Hex,false)+" |");
       
       if(cascatResolve && Hex.length() > 6){
           stgCWep.insert(stgCWep.length(), Tradutor(Hex.substring(6), inputSocket));
       }
       
       return stgCWep.toString();
   }
   
   //Tradutor Troca de Arma(Server)(733D)
   private String TradutorS_WeaponChange(String Hex){
       
       StringBuilder stgCWep = new StringBuilder();
       
       stgCWep.insert(0, Hex.substring(0,4)+" |")
               .insert(stgCWep.length(), "WepNum:"+TradutorInt(4,6,Hex,false)+" |");
       
       
       if(cascatResolve && Hex.length() > 6){
           stgCWep.insert(stgCWep.length(), Tradutor(Hex.substring(6), inputSocket));
       }
       
       
       return stgCWep.toString();
   }   

   
   //Tradutor pulo(Client)(6A70)
   private String TradutorC_Jump(String Hex){
       StringBuilder stgJump = new StringBuilder();
       
       String status = "JumpBTM:DOWN";
       if(Hex.substring(4,6).equals("01")){
           status = "JumpBTM:UP";
       }
      
       stgJump.insert(0, Hex.substring(0,4)+" |")
               .insert(stgJump.length(), status+" |");
       
       if(cascatResolve && Hex.length() > 6){
           stgJump.insert(stgJump.length(), Tradutor(Hex.substring(6), inputSocket));
       }
       
       return stgJump.toString();
   }
   
   
   //Traduçaõ pacotes de uso de habilidade(Client)(2A69)
   private String TradutorC_UsoHabilidade(String Hex){

        StringBuilder stgUHab = new StringBuilder();
        int posStg = (Integer.parseInt(TradutorInt(4,8,Hex,true))*2)+8;

        stgUHab.insert(0, Hex.substring(0,4)+" |")
               .insert(stgUHab.length(),"SkillName:"+TradutorString(8,posStg,Hex)+" |")
               .insert(stgUHab.length(), DataSplit(Hex.substring(posStg,posStg+24))+" |");

        if(cascatResolve && Hex.length() > posStg+24){
            stgUHab.insert(stgUHab.length(), Tradutor(Hex.substring(posStg+24), inputSocket));
        }
        
        return stgUHab.toString();
   }
   
   //Tradutor Inicio e Fim de disparo
   private String TradutorC_ShotStart(String Hex){
       StringBuilder stgShotS = new StringBuilder();
       
       String result = TradutorInt(4, 6, Hex, false);
       String text;
       
       
       if(result.equals("1")){
           text = "ShotStatus: Start |";
       }else{
           text = "ShotStatus: Stop |";
       }
       
       stgShotS.insert(0, Hex.substring(0,4)+" |")
               .insert(stgShotS.length(), text);
       
       if(cascatResolve && Hex.length() > 6){
           stgShotS.insert(stgShotS.length(), Tradutor(Hex.substring(6), inputSocket));
       }
       
       return stgShotS.toString();
       
   }
   
   
   //Tradutor do pacote de coordenada(Client)(6D76)
   private String TradutorCoordenada(String Hex, boolean pack){
        if(showCoord){  
            String result;
            
            StringBuilder stgCoo = new StringBuilder();
            stgCoo.insert(0, Hex.substring(0,4)+" |")
                .insert(stgCoo.length(),"X:"+TradutorFloat(4,12,Hex,true)+" |")
                .insert(stgCoo.length(),"Y:"+TradutorFloat(12,20,Hex,true)+" |")
                .insert(stgCoo.length(),"Z:"+TradutorFloat(20,28,Hex,true)+" |")


                .insert(stgCoo.length(),DataSplit(Hex.substring(28, 44))+" |");
            
            //Resolve em cascata
            if(cascatResolve && Hex.length() > 44){
                stgCoo.insert(stgCoo.length(), Tradutor(Hex.substring(44), inputSocket));
            }
            
            result = stgCoo.toString();
            
            if(!pack){
                //corta magicByte + " |"
                result = stgCoo.substring(6,stgCoo.length());
            }
            return result;
        }else{
            return "CoordHidden! |";
        }
        
        
   }
   
   //Tradutor do pacote de coordenada (sem Pack)
    private String TradutorCoordenadaNoPack(String Hex){
        if(showCoord){  

            StringBuilder stgCoo = new StringBuilder();
                stgCoo.insert(0,"X:"+TradutorFloat(0,8,Hex,true)+" |")
                      .insert(stgCoo.length(),"Y:"+TradutorFloat(8,16,Hex,true)+" |")
                      .insert(stgCoo.length(),"Z:"+TradutorFloat(16,24,Hex,true));

            return stgCoo.toString();
        }else{
            return "CoordHidden! |";
        }


       }
   
   
   //Funções de convesão basicas
   
   //Conversor de Hexa para Float
   private String TradutorFloat(int inicio, int fim, String Hex, boolean big){
        String result = "";
        
        result = Hex.substring(inicio, fim);
        
        if(big){
           result = ReverseString(result); 
        }
               
        Long i = Long.parseLong(result, 16);
        Float f = Float.intBitsToFloat(i.intValue());
        result = f.toString();
        return result;
   }
   
   //Conversor de Hexa Para Inteiro
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
   
   private String TradutorString(int inicio, int fim, String Hex){
        String result = "";
        
        result = Hex.substring(inicio, fim);
        result = new String(new BigInteger(result, 16).toByteArray());
        return result;
   }
   
   
   //Separador de pacote (separa de 2 em 2 bytes)
   private String DataSplit(String Hex){
       String result = ""; 
       
        if(Hex.length() % 4 == 0){
            for(int i = 4; i <= Hex.length(); i += 4){
                result = result + " "+Hex.substring(i-4,i);

            }
         }else if(Hex.length() % 2 == 0){
            for(int i = 2; i <= Hex.length(); i += 2){
                result = result + " "+Hex.substring(i-2,i);
            } 
         }else{
             result = Hex;  
         }
       
       return result.trim();
       
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
   
}