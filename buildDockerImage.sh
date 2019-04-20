rm dockerBuild/*.jar
rm dockerBuild/discordBot.config
rm dockerBuild/dockerbuild.dab
rm -r dockerBuild/plugins
mvn install
cp target/DiscordBot-0.0.1-SNAPSHOT.jar dockerBuild/
cp discordBot.config dockerBuild/
cp wordlist.txt dockerBuild/
mkdir dockerBuild/plugins

cd /home/zorro/git/DiscordBotCommands/
mvn install
cp target/DiscordDefaultCommands-0.1.jar /home/zorro/git/DiscordBot/dockerBuild/plugins

cd /home/zorro/git/DiscordBot/dockerBuild
