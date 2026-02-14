# PWN3-Proxy

## Proxy implementado usando engenharia reversa par mapear pacotes trafegados entre Cliente e Servidor no jogo PWN Adventure3.

>Este proxy é uma implementação rápida e simples em java 17 para maper os pacotes trafegados via rede com propósito de compreender, manipular e reconstruir parte do servidor como prova de conceito

<br />

**Uso**
<br /> 
Windows CMD:<br /> 
`java -jar Proxy_pwn3-vx.x.jar !IP DO SEU SERVIDOR!`


Alterar no arquivo "Tradutor.java" a seguinte linha: <br />
`if(!inputSocket.getInetAddress().getHostAddress().equals("!IP DO SEU SERVIDOR!")){`


**ATENÇÃO!**  
>As classes "Tradutor.java" e "AutoInject.java" devem ficar na mesma pasta do arquivo .jar já compilado, pois elas são compiladas em tempo de execução, permitindo alterações durante o uso.

### Proxy
![imagem do proxy em uso](https://github.com/P15c1n4/PWN3-Proxy/assets/93447442/73d7de22-e3f5-4166-b408-ecbf95ffce58)

### Pacotes mapeados
![imagem do proxy em uso](https://github.com/P15c1n4/PWN3-Proxy/assets/93447442/224298e1-99c8-4f80-8f22-cd784ef182b5)


### Implementação de modulos para trapaça (Auto-Loot)
![](https://github.com/P15c1n4/PWN3-Proxy/assets/93447442/19667031-8e2e-42d4-8c45-f40cf8bb50a1)

