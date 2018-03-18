var translate = require("google-translate-api");
var readline = require('readline');
var rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
  terminal: false
});

var to = "";
rl.on('line', function(line){
if(to == ""){
to = line;
}else{
translate(line, {"to":to}).then(res => {
console.log(res.text + "\n$FINISHED$");
});
to = "";
}
})