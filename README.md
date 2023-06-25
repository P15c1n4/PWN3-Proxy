# PWN3-Proxy

## Proxy implementado usando engenharia reversa par mapear pacotes trafegados entre Cliente e Servidor do jogo PWN Adventure3.

>Este proxy é uma implementação em java 17 para maper os pacotes trafegados via rede, com proposito de reconstruir parte do servidor como prova de conceito para o TCC UNIP - 2023.

<br />

**Uso**
<br /> 
Windows CMD:<br /> 
`java -jar Proxy_pwn3-vx.x.jar !IP DO SEU SERVIDOR!`
<br />

Alterar no arquivo "Tradutor.java" a seguinte linha: <br />
`if(!inputSocket.getInetAddress().getHostAddress().equals("!IP DO SEU SERVIDOR!")){`
<br />

### Proxy
![imagem do proxy em uso](https://cdn.discordapp.com/attachments/1122605734115410144/1122645190922993694/Screenshot_1.png)


### Auto-Loot module
![](https://github.com/P15c1n4/PWN3-Proxy/assets/93447442/f776feb5-b496-4b91-8bd1-a0b8cfca9267)
