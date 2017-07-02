wget "https://www.ifortuna.sk/sk/stavkovanie/futbal?nolimit"
mv futbal\?nolimit fortuna.txt
wget "https://tipkurz.etipos.sk/Odds.aspx?g=0&i=14888&v=0"
mv Odds.aspx\?g\=0\&i\=14888\&v\=0 tipos.txt
wget --random-wait -r -np -nd -P nike -e robots=off -U mozilla https://www.nike.sk/kurzovaPonuka/iframeRemember/ponukaPodlaCasu/
cat nike/* > nike.txt
rm -rf nike/
