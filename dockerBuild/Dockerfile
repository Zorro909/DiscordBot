FROM openjdk:12-jdk-oracle
COPY wait-for-it.sh /bot/wait-for-it.sh
RUN chmod +x /bot/wait-for-it.sh
ADD application.properties /bot/
ADD wordlist.txt /bot/
ADD discordBot.config /bot/
ADD DiscordBot-0.0.1-SNAPSHOT.jar /bot/
ADD plugins/ /bot/plugins/
 
WORKDIR /bot
CMD ["./wait-for-it.sh", "mysql:3306", "--", "java", "-jar", "DiscordBot-0.0.1-SNAPSHOT.jar"]
