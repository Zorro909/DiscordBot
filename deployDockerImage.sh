rsync -r dockerBuild/* root@jectrum.de:DiscordBot/
ssh -t root@jectrum.de 'cd DiscordBot; docker-compose down;docker-compose build; docker-compose up -d'
